package com.microsoft.graph.notify;

import static java.util.function.Predicate.not;

import java.util.Objects;
import java.util.stream.Stream;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import com.microsoft.graph.MsGraph;
import com.microsoft.graph.planer.MicrosoftGraphPlannerTask;
import com.microsoft.graph.planer.MicrosoftGraphPlannerTaskDetails;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.process.call.SubProcessCall;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.ISecurity;
import ch.ivyteam.ivy.security.ISecurityMember;
import ch.ivyteam.ivy.security.ISession;
import ch.ivyteam.ivy.security.IUser;
import ch.ivyteam.ivy.security.internal.User;
import ch.ivyteam.ivy.workflow.ITask;
import ch.ivyteam.ivy.workflow.IWorkflowManager;

public class PlannerNotifier extends NewTaskAssignmentListener {

  public PlannerNotifier() {
    super(IWorkflowManager.instance());
    taskHandler(this::notifyGraph);
  }

  public void notifyGraph(ITask newTask) {
    Ivy.log().info("notify ms-teams clients on new teask "+newTask);

    // exec as system session: avoid token clash!
    ISecurityMember activator = newTask.getActivator();
    getUsers(activator).map(IUser::getExternalId)
      .filter(Objects::nonNull)
      .filter(not(String::isBlank))
      .forEach(userId -> notify(userId, newTask));
  }

  public static void test() {
    ch.ivyteam.ivy.security.internal.User usr = (User) ISession.current().getSessionUser();
    //usr.setExternalId("fdf686ff-05a1-4c1d-89f3-b89f1f7d51eb");
    var heinz = "db1f0166-4dfe-449a-a2fe-d0a0dfe16b38";
    usr.setExternalId(heinz);
  }

  private void notify(String azureUserId, ITask newTask) {
    try {
       var plannerTask = new MicrosoftGraphPlannerTask();
       plannerTask.setPlanId("DZbuRHazR0yFxcyB8DyRKJgAGF33");
       plannerTask.setBucketId("YF8ekbxvAkmAoXX4hfGlpJgANIh4");
       plannerTask.setTitle(newTask.getName());
       var detail = new MicrosoftGraphPlannerTaskDetails();
       detail.setDescription(toHtml(newTask));
       plannerTask.setDetails(detail);

       var created =  new MsGraph().client().path("/planner/tasks").request()
         .post(Entity.entity(plannerTask, MediaType.APPLICATION_JSON));

       Ivy.log().info("task "+created);
    } catch (Exception ex) {
      Ivy.log().error("Failed to notify user "+azureUserId+" on new task "+newTask, ex);
    }
  }

  private void callSub(ITask newTask) {
    Ivy.log().info("planning..."+newTask.getId());
    MicrosoftGraphPlannerTask result = SubProcessCall.withPid("184C0096C0F3E50A")//.withPath("msPlanner")
      .withParam("taskId", newTask.getId())
      .call()
      .get("plannerTask", MicrosoftGraphPlannerTask.class);
    Ivy.log().info("notified planner "+result);
  }

  private static String toHtml(ITask newTask) {
    var html = new StringBuilder();
    html.append("<h1>New Task ").append(newTask.getName()).append("</h1>");
    html.append("<a href='").append(newTask.getStartLink().getAbsolute()).append("'>Run Task</a>");
    return html.toString();
  }

  private static Stream<IUser> getUsers(ISecurityMember activator) {
    if (activator.isUser()) {
      return Stream.of(ISecurity.current().users().findById(activator.getSecurityMemberId()));
    }
    IRole role = ISecurity.current().roles().findById(activator.getSecurityMemberId());
    return role.users().allPaged().stream();
  }

}
