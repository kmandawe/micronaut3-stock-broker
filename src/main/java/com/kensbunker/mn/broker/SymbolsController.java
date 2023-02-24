package com.kensbunker.mn.broker;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Controller("/symbols")
public class SymbolsController {

  private final InMemoryStore inMemoryStore;

  public SymbolsController(InMemoryStore inMemoryStore) {
    this.inMemoryStore = inMemoryStore;
  }

  @Get
  public List<Symbol> getAll() {
    return new ArrayList<>(inMemoryStore.getSymbols().values());
  }
  
  @Get("{value}")
  public Symbol getSymbolByValue(@PathVariable String value) {
    return inMemoryStore.getSymbols().get(value);
  }
}
