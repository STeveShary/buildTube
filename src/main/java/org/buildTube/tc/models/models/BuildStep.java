package org.buildTube.tc.models.models;

import lombok.Data;

@Data
public class BuildStep {
  private String id;
  private String name;
  private String projectName;
  private String projectId;
  private String href;
  private String webUrl;
}
