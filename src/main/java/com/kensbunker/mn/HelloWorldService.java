package com.kensbunker.mn;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class HelloWorldService {

  @Property(name = "hello.service.greeting", defaultValue = "default value")
  private String greeting;

  @EventListener
  public void onStartup(StartupEvent startupEvent) {
    log.debug("Startup: {}", HelloWorldService.class.getSimpleName());
  }

  public String sayHi() {
    return greeting;
  }
}
