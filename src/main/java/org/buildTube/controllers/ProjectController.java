package org.buildTube.controllers;

import org.buildTube.services.TcService;
import org.buildTube.tc.models.models.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ProjectController {

  private final TcService tcService;

  @Autowired
  public ProjectController(TcService tcService) {
    this.tcService = tcService;
  }


  @RequestMapping("/project/{projectId}")
  public ListenableFuture<Project> getProject(@PathVariable String projectId) {
    return tcService.getProject(projectId);
  }

}
