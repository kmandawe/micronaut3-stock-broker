package com.kensbunker.mn.broker.watchlist;

import com.kensbunker.mn.broker.Symbol;
import com.kensbunker.mn.broker.data.InMemoryAccountStore;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.rxjava3.http.client.Rx3HttpClient;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.reactivex.rxjava3.core.Single;
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
class WatchListControllerReactiveTest {

  private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerReactiveTest.class);
  private static final UUID TEST_ACCOUNT_ID = WatchListControllerReactive.ACCOUNT_ID;
  public static final String ACCOUNT_WATCHLIST_REACTIVE = "/account/watchlist-reactive";

  @Inject
  @Client("/")
  Rx3HttpClient client;

  @Inject InMemoryAccountStore inMemoryAccountStore;

  @BeforeEach
  void setup() {
    inMemoryAccountStore.deleteWatchList(TEST_ACCOUNT_ID);
  }

  @Test
  void returnsEmptyWatchListForTestAccount() {
    BearerAccessRefreshToken token = givenMyUserIsLoggedIn();
    var request =
        GET(ACCOUNT_WATCHLIST_REACTIVE)
            .bearerAuth(token.getAccessToken())
            .accept(MediaType.APPLICATION_JSON);

    final Single<WatchList> result = client.retrieve(request, WatchList.class).singleOrError();
    assertNull(result.blockingGet().symbols());
    assertTrue(inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());
  }

  @Test
  void returnsWatchListForTestAccountAsSingle() {
    givenWatchListForAccountExists();
    BearerAccessRefreshToken token = givenMyUserIsLoggedIn();
    var request =
        GET(ACCOUNT_WATCHLIST_REACTIVE + "/single")
            .bearerAuth(token.getAccessToken())
            .accept(MediaType.APPLICATION_JSON);

    var response = client.toBlocking().retrieve(request, WatchList.class);
    assertEquals(3, response.symbols().size());
    assertEquals(3, inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols().size());
  }

  @Test
  void canUpdateWatchListForTestAccount() {
    var symbols = Stream.of("AAPL", "GOOGL", "MSFT").map(Symbol::new).toList();

    BearerAccessRefreshToken token = givenMyUserIsLoggedIn();
    var request =
        PUT(ACCOUNT_WATCHLIST_REACTIVE, new WatchList(symbols))
            .bearerAuth(token.getAccessToken())
            .accept(MediaType.APPLICATION_JSON);

    final HttpResponse<Object> added = client.toBlocking().exchange(request);
    assertEquals(HttpStatus.OK, added.getStatus());
    assertEquals(symbols, inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols());
  }

  @Test
  void canDeleteWatchListForTestAccount() {
    givenWatchListForAccountExists();
    assertFalse(inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());

    BearerAccessRefreshToken token = givenMyUserIsLoggedIn();
    var request =
        DELETE(ACCOUNT_WATCHLIST_REACTIVE)
            .bearerAuth(token.getAccessToken())
            .accept(MediaType.APPLICATION_JSON);

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
    final UsernamePasswordCredentials credentials =
        new UsernamePasswordCredentials("my-user", "secret");
    var login = HttpRequest.POST("/login", credentials);
    var response = client.toBlocking().exchange(login, BearerAccessRefreshToken.class);
    assertEquals(HttpStatus.OK, response.getStatus());
    var token = response.getBody().get();
    assertEquals("my-user", token.getUsername());
    LOG.debug("Login Bearer Token: {} expires in {}", token.getAccessToken(), token.getExpiresIn());
    return token;
  }
}
