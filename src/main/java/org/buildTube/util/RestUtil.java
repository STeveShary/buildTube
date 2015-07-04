package org.buildTube.util;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.buildTube.services.EnvironmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureAdapter;
import org.springframework.web.client.AsyncRestTemplate;

import java.util.concurrent.ExecutionException;

@Component
public class RestUtil {

  private final AsyncRestTemplate asyncRestTemplate;

  @Autowired
  public RestUtil(EnvironmentService env) {
    if (env.useBasicAuth()) {
      asyncRestTemplate = buildRestTemplateWithBasicAuth(
          env.getBasicAuthUserName(),
          env.getBasicAuthUserPassword());
    } else {
      asyncRestTemplate = buildRestTemplate();
    }
  }

  public AsyncRestTemplate buildRestTemplate() {
    return new AsyncRestTemplate();
  }

  public <T> ListenableFuture<T> doAsyncGet(String url, Class<T> responseType) {
    return adaptResponseEntityFuture(asyncRestTemplate.getForEntity(url, responseType));
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

  private AsyncRestTemplate buildRestTemplateWithBasicAuth(String username, String password) {
    HttpAsyncClientBuilder clientBuilder = HttpAsyncClientBuilder.create();
    clientBuilder.setDefaultCredentialsProvider(buildBasicAuthProvider(username, password));
    CloseableHttpAsyncClient client = clientBuilder.build();
    return new AsyncRestTemplate(new HttpComponentsAsyncClientHttpRequestFactory(client));
  }

  private CredentialsProvider buildBasicAuthProvider(String authUserName, String authPassword) {
    BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(
        new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM),
        new UsernamePasswordCredentials(authUserName, authPassword));
    return credentialsProvider;
  }
}
