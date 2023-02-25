package com.kensbunker.mn.broker.watchlist;

import com.kensbunker.mn.broker.data.InMemoryAccountStore;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.scheduling.exceptions.TaskExecutionException;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Slf4j
@Controller("/account/watchlist-reactive")
public class WatchListControllerReactive {

  static final UUID ACCOUNT_ID = UUID.randomUUID();
  private final InMemoryAccountStore store;
  private final Scheduler scheduler;

  public WatchListControllerReactive(
      @Named(TaskExecutors.IO) ExecutorService executorService, InMemoryAccountStore store) {
    this.store = store;
    this.scheduler = Schedulers.from(executorService);
  }

  @Get(produces = MediaType.APPLICATION_JSON)
  @ExecuteOn(TaskExecutors.IO)
  public WatchList get() {
    log.debug("getWatchList - {}", Thread.currentThread().getName());
    WatchList watchList = store.getWatchList(ACCOUNT_ID);
    log.debug("Retrieved watchlist symbols: {}", watchList.symbols());
    return watchList;
  }

  @Get(value = "/single", produces = MediaType.APPLICATION_JSON)
  public Single<WatchList> getAsSingle() {
    return Single.fromCallable(
        () -> {
          log.debug("getAsSingle - {}", Thread.currentThread().getName());
          return store.getWatchList(ACCOUNT_ID);
        }).subscribeOn(scheduler);
  }

  @Put(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
  @ExecuteOn(TaskExecutors.IO)
  public WatchList update(@Body WatchList watchList) {
    return store.updateWatchList(ACCOUNT_ID, watchList);
  }

  @Status(HttpStatus.NO_CONTENT)
  @Delete(value = "/{accountId}",produces = MediaType.APPLICATION_JSON)
  @ExecuteOn(TaskExecutors.IO)
  public void delete(@PathVariable String accountId) {
    store.deleteWatchList(UUID.fromString(accountId));
  }
}
