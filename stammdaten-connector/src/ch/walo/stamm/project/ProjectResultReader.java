package ch.walo.stamm.project;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ivyteam.ivy.environment.Ivy;

public class ProjectResultReader {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static List<ProjectBase> convert(JsonNode result) {
    List<ProjectBase> projects = new ArrayList<>();
    var res2 = result.get("result");
    Ivy.log().debug("result = "+res2);
    Iterator<JsonNode> it = res2.iterator();
    while(it.hasNext()) {
      JsonNode project = it.next();
      Ivy.log().debug("reading "+project);
      ProjectBase base = new ProjectBase();
      base.setProjectName(project.get("projectBase").get("projectName").asText());
      projects.add(base);
    }
    return projects;
  }

}
