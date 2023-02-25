package com.kensbunker.mn.broker.hello;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class HelloWorldControllerTest {

  private static final Logger LOG = LoggerFactory.getLogger(HelloWorldControllerTest.class);

  @Inject
  @Client("/")
  HttpClient client;

  @Test
  void helloWorldEndpointRespondsWithProperContent() {
    var response = client.toBlocking().retrieve("/hello");
    assertEquals("Hello from HelloService!", response);
  }

  @Test
  void helloWorldEndpointRespondsWithProperStatusCodeAndContent() {
    var response = client.toBlocking().exchange("/hello", String.class);
    assertEquals(HttpStatus.OK, response.getStatus());
    assertEquals("Hello from HelloService!", response.getBody().get());
  }

  @Test
  void helloFromConfigEndpointReturnsMessageFromConfigFile() {
    var response = client.toBlocking().exchange("/hello/config", String.class);
    assertEquals(HttpStatus.OK, response.getStatus());
    assertEquals("Hello from application.yml", response.getBody().get());
  }

  @Test
  void helloFromTranslationEndpointReturnsContentFromConfigFile() {
    var response = client.toBlocking().exchange("/hello/translation", JsonNode.class);
    assertEquals(HttpStatus.OK, response.getStatus());
    assertEquals(
        "{\"de\":\"Hello Welt\",\"en\":\"Hello World\"}", response.getBody().get().toString());
  }

  @Test
  void returnsGreetingsAsJson() {
    final ObjectNode result = client.toBlocking().retrieve("/hello/json", ObjectNode.class);
    LOG.debug(result.toString());
  }
}
