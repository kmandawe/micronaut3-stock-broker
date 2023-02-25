package com.kensbunker.mn.broker.watchlist;

import com.fasterxml.jackson.databind.JsonNode;
import com.kensbunker.mn.broker.Symbol;
import com.kensbunker.mn.broker.data.InMemoryAccountStore;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.stream.Stream;

import static io.micronaut.http.HttpRequest.*;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class WatchListControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerTest.class);
  private static final UUID TEST_ACCOUNT_ID = WatchListController.ACCOUNT_ID;
    public static final String ACCOUNT_WATCHLIST = "/account/watchlist";

    @Inject
  @Client("/")
  HttpClient client;

  @Inject
  InMemoryAccountStore inMemoryAccountStore;
  
  @BeforeEach
  void setup() {
    inMemoryAccountStore.deleteWatchList(TEST_ACCOUNT_ID);
  }
  
  @Test
  void unauthorizedAccessIsForbidden() {
      try{
          client.toBlocking().retrieve(ACCOUNT_WATCHLIST);
          fail("Should fail if no exception is thrown");
      } catch (HttpClientResponseException e) {
          assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
      }
  }
  
  @Test
  void returnsEmptyWatchListForTestAccount() {
      BearerAccessRefreshToken token = givenMyUserIsLoggedIn();
      var request = GET(ACCOUNT_WATCHLIST).bearerAuth(token.getAccessToken()).accept(MediaType.APPLICATION_JSON);
      final WatchList result = client.toBlocking().retrieve(request, WatchList.class);
      assertNull(result.symbols());
      assertTrue(inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());
  }

    

    @Test
  void returnsWatchListForTestAccount() {
    givenWatchListForAccountExists();
    BearerAccessRefreshToken token = givenMyUserIsLoggedIn();
    var request = GET("/account/watchlist").bearerAuth(token.getAccessToken()).accept(MediaType.APPLICATION_JSON);
    var response = client.toBlocking().exchange(request, JsonNode.class);
    assertEquals(HttpStatus.OK, response.getStatus());
    assertEquals("""
            {
              "symbols" : [ {
                "value" : "AAPL"
              }, {
                "value" : "GOOGL"
              }, {
                "value" : "MSFT"
              } ]
            }""", response.getBody().get().toPrettyString());
  }
  
  @Test
  void canUpdateWatchListForTestAccount() {
      BearerAccessRefreshToken token = givenMyUserIsLoggedIn();
      var symbols = Stream.of("AAPL", "GOOGL", "MSFT").map(Symbol::new).toList();
      var request = PUT("/account/watchlist", new WatchList(symbols)).bearerAuth(token.getAccessToken()).accept(MediaType.APPLICATION_JSON);
      final HttpResponse<Object> added = client.toBlocking().exchange(request);
      assertEquals(HttpStatus.OK, added.getStatus());
      assertEquals(symbols, inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols());
  }
  
  @Test
  void canDeleteWatchListForTestAccount() {
    givenWatchListForAccountExists();
    BearerAccessRefreshToken token = givenMyUserIsLoggedIn();
    assertFalse(inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());
    var request = DELETE("/account/watchlist").bearerAuth(token.getAccessToken()).accept(MediaType.APPLICATION_JSON);
    var deleted = client.toBlocking().exchange(request);
    assertEquals(HttpStatus.NO_CONTENT, deleted.getStatus());
    assertTrue(inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());
  }

  private void givenWatchListForAccountExists() {
    inMemoryAccountStore.updateWatchList(
            TEST_ACCOUNT_ID,
            new WatchList(Stream.of("AAPL", "GOOGL", "MSFT").map(Symbol::new).toList()));
  }

    private BearerAccessRefreshToken givenMyUserIsLoggedIn() {
        final UsernamePasswordCredentials credentials =  new UsernamePasswordCredentials("my-user", "secret");
        var login = HttpRequest.POST("/login", credentials);
        var response = client.toBlocking().exchange(login, BearerAccessRefreshToken.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        var token = response.getBody().get();
        assertEquals("my-user", token.getUsername());
        LOG.debug("Login Bearer Token: {} expires in {}", token.getAccessToken(), token.getExpiresIn() );
        return token;
    }
}
