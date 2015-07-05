package org.buildTube.controllers;

import org.buildTube.services.TeamCityService;
import org.buildTube.tc.models.AllProjects;
import org.buildTube.tc.models.Build;
import org.buildTube.tc.models.BuildStep;
import org.buildTube.tc.models.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ProjectController {

  private final TeamCityService teamCityService;

  @Autowired
  public ProjectController(TeamCityService teamCityService) {
    this.teamCityService = teamCityService;
  }

  @RequestMapping("/project/{projectId}")
  @ResponseBody
  public ListenableFuture<Project> getProject(@PathVariable String projectId) {
    return teamCityService.getProject(projectId);
  }

  @RequestMapping("/project/all")
  @ResponseBody
  public ListenableFuture<AllProjects> getAllProjects() {
    return teamCityService.getAllProjects();
  }

  @RequestMapping("/project/{projectId}/builds")
  @ResponseBody
  public ListenableFuture<List<Build>> getProjectBuilds(@PathVariable String projectId) {
    return teamCityService.getProjectBuilds(projectId);
  }

  @RequestMapping("/project/{projectId}/buildSteps")
  @ResponseBody
  public ListenableFuture<List<BuildStep>> getProjectBuildSteps(@PathVariable String projectId) {
    return teamCityService.getProjectBuildSteps(projectId);
  }

  @RequestMapping("/project/{projectId}/build/{buildId}")
  @ResponseBody
  public ListenableFuture<List<Build>> getBuildsForBuildId(@PathVariable String projectId, @PathVariable String buildId) {
    return teamCityService.getBuilds(projectId, buildId);
  }

}
