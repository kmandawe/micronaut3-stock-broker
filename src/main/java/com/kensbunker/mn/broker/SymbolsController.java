package com.kensbunker.mn.broker;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
  
  @Get("/filter{?max,offset}")
  public List<Symbol> getSymbols(@QueryValue Optional<Integer> max, @QueryValue Optional<Integer> offset) {
    return inMemoryStore.getSymbols().values()
            .stream()
            .skip(offset.orElse(0))
            .limit(max.orElse(0))
            .toList();
  }
  
}
