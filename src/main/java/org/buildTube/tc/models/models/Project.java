package org.buildTube.tc.models.models;

import lombok.Data;

@Data
public class Project {
  private String id;
  private String name;
  private String parentProjectId;
  private String href;
  private String webUrl;

  private BuildType buildType;
}
