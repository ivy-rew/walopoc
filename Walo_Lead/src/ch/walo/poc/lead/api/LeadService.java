package ch.walo.poc.lead.api;

import java.util.Date;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.process.model.value.SignalCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Simple RESTful service. The REST interface is defined by the JAX-RS
 * annotations on the methods and its path.<br/>
 *
 * <p>
 * <b>URL</b><br/>
 * The simplest external URL of this service will be: <br/>
 * - designer: <code>http://localhost:8081/designer/api/persons</code><br/>
 * - engine:
 * <code>http://localhost:8081/myApplicationName/api/persons</code>
 * </p>
 *
 * <p>
 * <b>Authentication</b><br/>
 * - Consumers of this service must be authenticated with HTTP-BASIC. In the
 * Designer any 'Test User' of the application is valid.
 * </p>
 *
 */
@Singleton
@Path("lead")
@Tag(name = "pocathon")
public class LeadService{

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Operation(description = "creates a new lead")
  public Response create(LeadInit lead)
  {
    Ivy.wf().signals().send(new SignalCode("createLead"), lead);
    // use subprocess-call api + trigger: if created task/case-id is important
    return Response.status(Status.CREATED)
      .entity("lead created!")
      .build();
  }

  public static class LeadInit {
    @JsonProperty("Initiator")
    public String initiator;

    @JsonProperty("ProjectId")
    public Long projectId;
    @JsonProperty("ProjectName")
    public String projectName;

    @JsonProperty("CustomerId")
    public Long customerId;
    @JsonProperty("CustomerName")
    public String customerName;

    @JsonProperty("Budget")
    public Number budget;

    @JsonProperty("Division")
    public String division;

    @JsonProperty("Projectstart")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS")
    public Date projectStart;

    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
  }

}
