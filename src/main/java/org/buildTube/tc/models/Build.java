package org.buildTube.tc.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY)
public class Build {
  private String id;
  private String buildTypeId;
  private String number;
  private String status;
  private String state;
  private String href;
  private String webUrl;
  private String statusText;

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
