package org.buildTube.tc.models.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BuildType {

  private int count;
  private List<BuildStep> buildType = new ArrayList<>();
}
