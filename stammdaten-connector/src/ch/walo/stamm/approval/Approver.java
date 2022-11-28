
package ch.walo.stamm.approval;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "prename",
    "name",
    "mail",
    "function",
    "profitCenterId",
    "level",
    "companyId",
    "projectId",
    "amount",
    "procurementStructureId",
    "subWorkflowPre",
    "subWorkflowPost"
})
@Generated("jsonschema2pojo")
public class Approver {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("prename")
    private String prename;
    @JsonProperty("name")
    private String name;
    @JsonProperty("mail")
    private String mail;
    @JsonProperty("function")
    private String function;
    @JsonProperty("profitCenterId")
    private Integer profitCenterId;
    @JsonProperty("level")
    private Integer level;
    @JsonProperty("companyId")
    private Integer companyId;
    @JsonProperty("projectId")
    private Integer projectId;
    @JsonProperty("amount")
    private Integer amount;
    @JsonProperty("procurementStructureId")
    private Integer procurementStructureId;
    @JsonProperty("subWorkflowPre")
    private List<SubWorkflowPre> subWorkflowPre = new ArrayList<SubWorkflowPre>();
    @JsonProperty("subWorkflowPost")
    private List<SubWorkflowPost> subWorkflowPost = new ArrayList<SubWorkflowPost>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("prename")
    public String getPrename() {
        return prename;
    }

    @JsonProperty("prename")
    public void setPrename(String prename) {
        this.prename = prename;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("mail")
    public String getMail() {
        return mail;
    }

    @JsonProperty("mail")
    public void setMail(String mail) {
        this.mail = mail;
    }

    @JsonProperty("function")
    public String getFunction() {
        return function;
    }

    @JsonProperty("function")
    public void setFunction(String function) {
        this.function = function;
    }

    @JsonProperty("profitCenterId")
    public Integer getProfitCenterId() {
        return profitCenterId;
    }

    @JsonProperty("profitCenterId")
    public void setProfitCenterId(Integer profitCenterId) {
        this.profitCenterId = profitCenterId;
    }

    @JsonProperty("level")
    public Integer getLevel() {
        return level;
    }

    @JsonProperty("level")
    public void setLevel(Integer level) {
        this.level = level;
    }

    @JsonProperty("companyId")
    public Integer getCompanyId() {
        return companyId;
    }

    @JsonProperty("companyId")
    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    @JsonProperty("projectId")
    public Integer getProjectId() {
        return projectId;
    }

    @JsonProperty("projectId")
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    @JsonProperty("amount")
    public Integer getAmount() {
        return amount;
    }

    @JsonProperty("amount")
    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @JsonProperty("procurementStructureId")
    public Integer getProcurementStructureId() {
        return procurementStructureId;
    }

    @JsonProperty("procurementStructureId")
    public void setProcurementStructureId(Integer procurementStructureId) {
        this.procurementStructureId = procurementStructureId;
    }

    @JsonProperty("subWorkflowPre")
    public List<SubWorkflowPre> getSubWorkflowPre() {
        return subWorkflowPre;
    }

    @JsonProperty("subWorkflowPre")
    public void setSubWorkflowPre(List<SubWorkflowPre> subWorkflowPre) {
        this.subWorkflowPre = subWorkflowPre;
    }

    @JsonProperty("subWorkflowPost")
    public List<SubWorkflowPost> getSubWorkflowPost() {
        return subWorkflowPost;
    }

    @JsonProperty("subWorkflowPost")
    public void setSubWorkflowPost(List<SubWorkflowPost> subWorkflowPost) {
        this.subWorkflowPost = subWorkflowPost;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Approver.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("prename");
        sb.append('=');
        sb.append(((this.prename == null)?"<null>":this.prename));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("mail");
        sb.append('=');
        sb.append(((this.mail == null)?"<null>":this.mail));
        sb.append(',');
        sb.append("function");
        sb.append('=');
        sb.append(((this.function == null)?"<null>":this.function));
        sb.append(',');
        sb.append("profitCenterId");
        sb.append('=');
        sb.append(((this.profitCenterId == null)?"<null>":this.profitCenterId));
        sb.append(',');
        sb.append("level");
        sb.append('=');
        sb.append(((this.level == null)?"<null>":this.level));
        sb.append(',');
        sb.append("companyId");
        sb.append('=');
        sb.append(((this.companyId == null)?"<null>":this.companyId));
        sb.append(',');
        sb.append("projectId");
        sb.append('=');
        sb.append(((this.projectId == null)?"<null>":this.projectId));
        sb.append(',');
        sb.append("amount");
        sb.append('=');
        sb.append(((this.amount == null)?"<null>":this.amount));
        sb.append(',');
        sb.append("procurementStructureId");
        sb.append('=');
        sb.append(((this.procurementStructureId == null)?"<null>":this.procurementStructureId));
        sb.append(',');
        sb.append("subWorkflowPre");
        sb.append('=');
        sb.append(((this.subWorkflowPre == null)?"<null>":this.subWorkflowPre));
        sb.append(',');
        sb.append("subWorkflowPost");
        sb.append('=');
        sb.append(((this.subWorkflowPost == null)?"<null>":this.subWorkflowPost));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
