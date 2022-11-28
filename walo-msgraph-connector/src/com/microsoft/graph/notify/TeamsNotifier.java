package com.microsoft.graph.notify;

import static java.util.function.Predicate.not;

import java.util.Objects;
import java.util.stream.Stream;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.graph.MicrosoftGraphBodyType;
import com.microsoft.graph.MicrosoftGraphChat;
import com.microsoft.graph.MicrosoftGraphChatMessage;
import com.microsoft.graph.MicrosoftGraphItemBody;
import com.microsoft.graph.MsGraph;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.ISecurity;
import ch.ivyteam.ivy.security.ISecurityMember;
import ch.ivyteam.ivy.security.ISession;
import ch.ivyteam.ivy.security.IUser;
import ch.ivyteam.ivy.security.internal.User;
import ch.ivyteam.ivy.workflow.ITask;
import ch.ivyteam.ivy.workflow.IWorkflowManager;

public class TeamsNotifier extends NewTaskAssignmentListener {

  public TeamsNotifier() {
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
    var diego = "aef70065-bd75-4c42-b79a-ab9f64388ae2";
    usr.setExternalId(diego);
  }

  private void notify(String azureUserId, ITask newTask) {
    try {
      var chat = createChat(azureUserId);

      var msg = new MicrosoftGraphChatMessage();
      var body = new MicrosoftGraphItemBody();
      msg.setBody(body);
      body.setContentType(MicrosoftGraphBodyType.HTML);
      body.setContent(toHtml(newTask));

      Ivy.log().info("sending "+msg);
      new MsGraph().appClient().path("/chats/{chat-id}/messages")
        .resolveTemplate("chat-id", chat.getId())
        .request()
        .post(Entity.entity(msg, MediaType.APPLICATION_JSON));
    } catch (Exception ex) {
      Ivy.log().error("Failed to notify user "+azureUserId+" on new task "+newTask, ex);
    }
  }

  private static String toHtml(ITask newTask) {
    var html = new StringBuilder();
    html.append("<h1>New Task ").append(newTask.getName()).append("</h1>");
    html.append("<a href='").append(newTask.getStartLink().getAbsolute()).append("'>Start Task</a>");
    return html.toString();
  }

  private MicrosoftGraphChat createChat(String azureUserId) {
    ObjectNode initChat = JsonNodeFactory.instance.objectNode();
    initChat.set("chatType", initChat.textNode("oneOnOne"));
    ArrayNode members = initChat.putArray("members");
    String sender = "126263ee-04c4-4238-bfb2-61d38edc9b45";
    members.add(createOwner(sender));
    members.add(createOwner(azureUserId));
    Ivy.log().info(initChat);

    var result = new MsGraph().appClient().path("/chats")
      .request(MediaType.APPLICATION_JSON)
      .post(Entity.entity(initChat, MediaType.APPLICATION_JSON));
    Ivy.log().info(result.getStatus());
    return result.readEntity(MicrosoftGraphChat.class);
  }

  private static ObjectNode createOwner(String userId) {
    ObjectNode member = JsonNodeFactory.instance.objectNode();
    member.set("@odata.type", member.textNode("#microsoft.graph.aadUserConversationMember"));
    member.putArray("roles").add("owner");
    member.set("user@odata.bind", member.textNode("https://graph.microsoft.com/v1.0/users('"+userId+"')"));
    return member;
  }

  private static Stream<IUser> getUsers(ISecurityMember activator) {
    if (activator.isUser()) {
      return Stream.of(ISecurity.current().users().findById(activator.getSecurityMemberId()));
    }
    IRole role = ISecurity.current().roles().findById(activator.getSecurityMemberId());
    return role.users().allPaged().stream();
  }

}
