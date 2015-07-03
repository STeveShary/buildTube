package org.buildTube.services;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.buildTube.tc.models.models.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureAdapter;
import org.springframework.web.client.AsyncRestTemplate;

import java.net.URI;
import java.util.concurrent.ExecutionException;

@Component
public class TcService {

  private final EnvironmentService env;
  private final AsyncRestTemplate asyncRestTemplate;

  @Autowired
  public TcService(EnvironmentService env) {
    this.env = env;
    asyncRestTemplate = buildBasicAuthTemplate();
  }

  public AsyncRestTemplate buildBasicAuthTemplate() {
    HttpAsyncClientBuilder clientBuilder = HttpAsyncClientBuilder.create();
    if (env.useBasicAuth()) {
      clientBuilder.setDefaultCredentialsProvider(
          buildBasicAuthProvider(env.getBasicAuthUserName(), env.getBasicAuthUserPassword()));
    }
    CloseableHttpAsyncClient client = clientBuilder.build();
    AsyncRestTemplate restClient = new AsyncRestTemplate(new HttpComponentsAsyncClientHttpRequestFactory(client));
    return restClient;
  }

  private CredentialsProvider buildBasicAuthProvider(String authUserName, String authPassword) {
    BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(
        new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM),
        new UsernamePasswordCredentials(authUserName, authPassword));
    return credentialsProvider;
  }

  public ListenableFuture<Project> getProject(String projectId) {
    return adaptResponseEntityFuture(asyncRestTemplate.getForEntity(buildGetProjectUrl(projectId), Project.class));
  }

  protected <T> ListenableFutureAdapter<T, ResponseEntity<T>> adaptResponseEntityFuture(
      final ListenableFuture<ResponseEntity<T>> responseEntity) {
    ListenableFutureAdapter<T, ResponseEntity<T>> returnFuture;
    returnFuture = new ListenableFutureAdapter<T, ResponseEntity<T>>(responseEntity) {
      @Override
      protected T adapt(ResponseEntity<T> adapteeResult) throws ExecutionException {
        return adapteeResult.getBody();
      }
    };
    return returnFuture;
  }


  private URI buildGetProjectUrl(String projectId) {
    return URI.create(
        String.format("%s/teamcity/httpAuth/app/rest/projects/id:%s", env.getTeamcityServerUrl(), projectId));

  }


}
