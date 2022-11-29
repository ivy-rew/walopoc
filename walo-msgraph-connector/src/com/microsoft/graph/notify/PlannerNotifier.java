package com.microsoft.graph.notify;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.graph.MsGraph;
import com.microsoft.graph.planer.MicrosoftGraphPlannerAssignments;
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
      .filter(Predicate.not(String::isBlank))
      .forEach(userId -> notify(userId, newTask));
  }

  public static void test() {
    ch.ivyteam.ivy.security.internal.User usr = (User) ISession.current().getSessionUser();
    //usr.setExternalId("fdf686ff-05a1-4c1d-89f3-b89f1f7d51eb");
    var heinz = "db1f0166-4dfe-449a-a2fe-d0a0dfe16b38"; // myid
    usr.setExternalId(heinz);
  }

  private void notify(String azureUserId, ITask newTask) {
    try {
       var plannerTask = toPayload(azureUserId, newTask);
       var rawTask = rawPayload(azureUserId, newTask);

       var created =  new MsGraph().client().path("/planner/tasks").request()
         .post(Entity.entity(rawTask, MediaType.APPLICATION_JSON));

       Ivy.log().info("task "+created);
    } catch (Exception ex) {
      Ivy.log().error("Failed to notify user "+azureUserId+" on new task "+newTask, ex);
    }
  }

  private JsonNode rawPayload(String azureUserId, ITask newTask) {
    ObjectNode pTask = JsonNodeFactory.instance.objectNode();
    pTask.set("planId", pTask.textNode("DZbuRHazR0yFxcyB8DyRKJgAGF33"));
    pTask.set("bucketId", pTask.textNode("YF8ekbxvAkmAoXX4hfGlpJgANIh4"));
    pTask.set("title", pTask.textNode(newTask.getName()));
    pTask.set("assignments", rawAssign(azureUserId, newTask));
    var detail = JsonNodeFactory.instance.objectNode();
    detail.set("description", pTask.textNode(toHtml(newTask)));
    pTask.set("details", detail);
    return pTask;
  }

  private JsonNode rawAssign(String azureUserId, ITask newTask) {
    var assign = JsonNodeFactory.instance.objectNode();
    var inner = JsonNodeFactory.instance.objectNode();
    inner.set("@odata.type", inner.textNode("#microsoft.graph.plannerAssignment"));
    inner.set("orderHint", inner.textNode(" !"));
    assign.set(azureUserId, inner);
    return assign;
  }

  private MicrosoftGraphPlannerTask toPayload(String azureUserId, ITask newTask) {
    var plannerTask = new MicrosoftGraphPlannerTask();
     plannerTask.setPlanId("DZbuRHazR0yFxcyB8DyRKJgAGF33");
     plannerTask.setBucketId("YF8ekbxvAkmAoXX4hfGlpJgANIh4");
     plannerTask.setTitle(newTask.getName());
     var detail = new MicrosoftGraphPlannerTaskDetails();
     detail.setDescription(toHtml(newTask));
     plannerTask.setAssignments(null);
     plannerTask.setDetails(detail);

     var assignments = new MicrosoftGraphPlannerAssignments();
     var member = new com.microsoft.graph.planer.MsGraphAssins.PlanerAssignment();
     //assignments.put("f626c06e-b450-4eeb-8062-534984fbac20", member);
     assignments.put(azureUserId, member);
     plannerTask.setAssignments(assignments);
    return plannerTask;
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
