package org.buildTube.tc.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AllProjects {
  private int count;
  private List<Project> project = new ArrayList<>();
}
