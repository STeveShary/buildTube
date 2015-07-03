package org.buildTube.tc.models.models;

import lombok.Data;

@Data
public class Build {
  private String id;
  private String buildTypeId;
  private String number;
  private String status;
  private String state;
  private String href;
  private String webUrl;
  private String statusText;
  private SnapshotDependency dependency;

  public boolean wasSuccessFullBuild() {
    return "SUCCESS".equalsIgnoreCase(status);
  }

  public boolean isStarted() {
    return "running".equalsIgnoreCase(state);
  }

  public boolean isFinished() {
    return "finished".equalsIgnoreCase(state);
  }
}
