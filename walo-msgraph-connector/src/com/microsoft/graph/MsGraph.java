package com.microsoft.graph;

import java.util.UUID;

import javax.ws.rs.client.WebTarget;

import ch.ivyteam.ivy.environment.Ivy;

public class MsGraph {

  private final WebTarget client;
  private final WebTarget backend;

  public MsGraph() {
    client = Ivy.rest().client(UUID.fromString("007036dc-72d1-429f-88a7-ba5d5cf5ae58"));
    backend = Ivy.rest().client(UUID.fromString("bc2023ff-7579-4638-8099-3ec21308ca1f"));
  }

  public WebTarget client() {
    return client;
  }

  public WebTarget appClient() {
    return backend;
  }

}
