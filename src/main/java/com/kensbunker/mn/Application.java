package com.kensbunker.mn;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {

  public static void main(String[] args) {
    final ApplicationContext context = Micronaut.run(Application.class, args);
    final HelloWorldService service = context.getBean(HelloWorldService.class);
    log.info(service.sayHi() + " with lombok!");
  }
}
