package com.microsoft.graph.planer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MsGraphAssins extends MicrosoftGraphPlannerAssignments {

  public static class PlanerAssignment {

    @JsonProperty("@odata.type")
    String type = "#microsoft.graph.plannerAssignment";

    @JsonProperty("orderHint")
    String orderHint = " !";
  }

}
