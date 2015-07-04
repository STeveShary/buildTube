package org.buildTube.tc.models;

import lombok.Data;

@Data
public class BuildStep implements Comparable<BuildStep> {
  private String id;
  private String name;
  private String projectName;
  private String projectId;
  private String href;
  private String webUrl;

  @Override
  public int compareTo(BuildStep o) {
    return name.compareTo(o.getName());
  }
}
