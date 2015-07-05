package org.buildTube.services;

import org.buildTube.tc.models.*;
import org.buildTube.util.AsyncUtil;
import org.buildTube.util.RestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class TeamCityService {

  private final EnvironmentService env;
  private final RestUtil restUtil;
  private final AsyncUtil asyncUtil;

  @Autowired
  public TeamCityService(EnvironmentService env, RestUtil restUtil, AsyncUtil asyncUtil) {
    this.env = env;
    this.restUtil = restUtil;
    this.asyncUtil = asyncUtil;
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
    project.addCallback(result -> populateBuilds(getFirstBuildStep(result), projectBuilds),
        projectBuilds::setException);
    return projectBuilds;
  }

  public ListenableFuture<List<Build>> getBuilds(String projectId, String baseBuildNumber) {
    SettableListenableFuture<List<Build>> builds = new SettableListenableFuture<>();
    ListenableFuture<List<BuildStep>> projectBuildSteps = getProjectBuildSteps(projectId);
    projectBuildSteps.addCallback(
        buildSteps -> {
          List<ListenableFuture<StepBuilds>> allBuilds = new ArrayList<>();
          buildSteps.forEach(buildStep -> {
            allBuilds.add(fetchBuildsForStep(buildStep));
          });
          ListenableFuture<List<StepBuilds>> buildsOnFuture = asyncUtil.flatMapCommands(allBuilds);
          buildsOnFuture.addCallback(
              buildsList -> {
                Collections.sort(buildsList);
                List<Build> relatedBuilds = new ArrayList<>();
                buildsList.forEach(currentBuilds -> {
                  addRelatedBuildStep(currentBuilds.getBuild(), relatedBuilds, baseBuildNumber);
                });
                builds.set(relatedBuilds);
              },
              builds::setException);
        },
        builds::setException);
    return builds;
  }

  private void addRelatedBuildStep(List<Build> buildsInStep, List<Build> relatedBuilds, String baseBuildNumber) {
    Optional<Build> relatedBuild = buildsInStep.stream().filter(build -> isBuildRelated(baseBuildNumber, build)).findFirst();
    if (relatedBuild.isPresent()) {
      relatedBuilds.add(relatedBuild.get());
    }
  }

  private boolean isBuildRelated(String baseBuildNumber, Build build) {
    return build.getNumber().startsWith(baseBuildNumber);
  }

  private String buildGetAllProjectsUrl() {
    return String.format("%s/teamcity/httpAuth/app/rest/projects/", env.getTeamcityServerUrl());
  }

  private String buildGetProjectUrl(String projectId) {
    return String.format("%s/teamcity/httpAuth/app/rest/projects/id:%s", env.getTeamcityServerUrl(), projectId);
  }

  private void populateBuilds(BuildStep buildStep, SettableListenableFuture<List<Build>> returnFuture) {
    fetchBuildsForStep(buildStep).addCallback(
        result -> returnFuture.set(result.getBuild()),
        returnFuture::setException);
  }

  private ListenableFuture<StepBuilds> fetchBuildsForStep(BuildStep buildStep) {
    ListenableFuture<StepBuilds> stepBuildsListenableFuture = restUtil.doAsyncGet(buildGetBuildsForStepUrl(buildStep), StepBuilds.class);
    stepBuildsListenableFuture.addCallback(result -> result.setBuildStepName(buildStep.getName()), ex -> {
    });
    return stepBuildsListenableFuture;
  }

  private String buildGetBuildsForStepUrl(BuildStep firstBuildStep) {
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
