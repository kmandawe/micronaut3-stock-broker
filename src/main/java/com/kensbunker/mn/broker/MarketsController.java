package com.kensbunker.mn.broker;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.reactivex.rxjava3.core.Single;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/markets")
public class MarketsController {
  private final InMemoryStore store;

  public MarketsController(InMemoryStore store) {
    this.store = store;
  }

  @Operation(summary = "Returns all available markets")
  @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON))
  @Tag(name = "markets")
  @Get("/")
  public Single<List<Symbol>> index() {
    return Single.just(store.getSymbols().values().stream().toList());
  }
}
