package org.buildTube.services;

import org.buildTube.tc.models.*;
import org.buildTube.util.RestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.Collections;
import java.util.List;

@Component
public class TeamCityService {

  private final EnvironmentService env;
  private final RestUtil restUtil;

  @Autowired
  public TeamCityService(EnvironmentService env, RestUtil restUtil) {
    this.env = env;
    this.restUtil = restUtil;
  }

  public ListenableFuture<Project> getProject(String projectId) {
    return restUtil.doAsyncGet(buildGetProjectUrl(projectId), Project.class);
  }

  public ListenableFuture<AllProjects> getAllProjects() {
    return restUtil.doAsyncGet(buildGetAllProjectsUrl(), AllProjects.class);
  }

  public ListenableFuture<List<Build>> getProjectBuilds(String projectId) {
    final SettableListenableFuture<List<Build>> projectBuilds = new SettableListenableFuture<>();

    ListenableFuture<Project> project = getProject(projectId);
    project.addCallback(result -> fetchBuilds(getFirstBuildStep(result), projectBuilds),
        projectBuilds::setException);
    return projectBuilds;
  }

  private String buildGetAllProjectsUrl() {
    return String.format("%s/teamcity/httpAuth/app/rest/projects/", env.getTeamcityServerUrl());
  }

  private String buildGetProjectUrl(String projectId) {
    return String.format("%s/teamcity/httpAuth/app/rest/projects/id:%s", env.getTeamcityServerUrl(), projectId);
  }

  private void fetchBuilds(BuildStep firstBuildStep, SettableListenableFuture<List<Build>> returnFuture) {
    ListenableFuture<StepBuilds> buildsFuture = restUtil.doAsyncGet(
        buildGetBuildStepBuild(firstBuildStep), StepBuilds.class);

    buildsFuture.addCallback(
        result -> returnFuture.set(result.getBuild()),
        returnFuture::setException);
  }

  private String buildGetBuildStepBuild(BuildStep firstBuildStep) {
    return String.format("%s/teamcity/httpAuth/app/rest/buildTypes/id:%s/builds/",
        env.getTeamcityServerUrl(), firstBuildStep.getId());
  }

  private BuildStep getFirstBuildStep(Project project) {
    List<BuildStep> buildSteps = getBuildSteps(project);
    if (buildSteps == null) return null;
    return buildSteps.get(0);
  }

  private List<BuildStep> getBuildSteps(Project project) {
    List<BuildStep> buildSteps = project.getBuildTypes().getBuildType();
    if (buildSteps == null ||
        buildSteps.size() == 0) {
      return null;
    }
    Collections.sort(buildSteps);
    return buildSteps;
  }

  public ListenableFuture<List<BuildStep>> getProjectBuildSteps(String projectId) {
    SettableListenableFuture<List<BuildStep>> returnFuture = new SettableListenableFuture<>();
    ListenableFuture<Project> project = getProject(projectId);
    project.addCallback(
        result -> returnFuture.set(getBuildSteps(result)),
        returnFuture::setException);

    return returnFuture;
  }
}
