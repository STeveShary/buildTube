package org.buildTube.tc.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StepBuilds implements Comparable<StepBuilds>{
  private String buildStepName;
  private int count;
  private List<Build> build = new ArrayList<>();

  @Override
  public int compareTo(StepBuilds o) {
    return buildStepName.compareTo(o.getBuildStepName());
  }
}
