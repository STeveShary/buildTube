package org.buildTube.tc.models.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SnapshotDependency {
  private int count;
  private List<Build> build = new ArrayList<>();
}
