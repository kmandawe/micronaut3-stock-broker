package com.kensbunker.mn.broker;

import com.kensbunker.mn.broker.watchlist.WatchList;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import java.util.UUID;

@Client("/")
public interface JWTWatchListClient {
  @Post("/login")
  BearerAccessRefreshToken login(@Body UsernamePasswordCredentials credentials);

  @Get("/account/watchlist-reactive")
  Flowable<WatchList> retrieveWatchList(@Header String authorization);

  @Get("/account/watchlist-reactive/single")
  Single<WatchList> retrieveWatchListAsSingle(@Header String authorization);

  @Put("/account/watchlist-reactive")
  HttpResponse<Object> updateWatchList(@Header String authorization, @Body WatchList watchList);

  @Delete("/account/watchlist-reactive/{accountId}")
  HttpResponse<Object> deleteWatchList(@Header String authorization, @PathVariable UUID accountId);
}
