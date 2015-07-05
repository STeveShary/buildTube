package org.buildTube.tc.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BuildDependency {
  private int count;
  private List<Build> build = new ArrayList<>();
}
