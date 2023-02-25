package com.kensbunker.mn.broker.watchlist;

import com.kensbunker.mn.broker.Symbol;
import com.kensbunker.mn.broker.data.InMemoryAccountStore;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.rxjava3.http.client.Rx3HttpClient;
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

  @Inject
  @Client("/account/watchlist-reactive")
  Rx3HttpClient client;

  @Inject InMemoryAccountStore inMemoryAccountStore;

  @BeforeEach
  void setup() {
    inMemoryAccountStore.deleteWatchList(TEST_ACCOUNT_ID);
  }

  @Test
  void returnsEmptyWatchListForTestAccount() {
    final Single<WatchList> result = client.retrieve(GET("/"), WatchList.class).singleOrError();
    assertNull(result.blockingGet().symbols());
    assertTrue(inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());
  }

  @Test
  void returnsWatchListForTestAccountAsSingle() {
    givenWatchListForAccountExists();

    var response = client.toBlocking().retrieve("/single", WatchList.class);
    assertEquals(3, response.symbols().size());
    assertEquals(3, inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols().size());
  }

  @Test
  void canUpdateWatchListForTestAccount() {
    var symbols = Stream.of("AAPL", "GOOGL", "MSFT").map(Symbol::new).toList();

    final var request = PUT("/", new WatchList(symbols)).accept(MediaType.APPLICATION_JSON);
    final HttpResponse<Object> added = client.toBlocking().exchange(request);
    assertEquals(HttpStatus.OK, added.getStatus());
    assertEquals(symbols, inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols());
  }

  @Test
  void canDeleteWatchListForTestAccount() {
    givenWatchListForAccountExists();
    assertFalse(inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());

    var deleted = client.toBlocking().exchange(DELETE("/"));
    assertEquals(HttpStatus.NO_CONTENT, deleted.getStatus());
    assertTrue(inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());
  }

  private void givenWatchListForAccountExists() {
    inMemoryAccountStore.updateWatchList(
        TEST_ACCOUNT_ID,
        new WatchList(Stream.of("AAPL", "GOOGL", "MSFT").map(Symbol::new).toList()));
  }
}
