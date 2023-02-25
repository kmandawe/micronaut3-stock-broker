package com.kensbunker.mn;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import lombok.extern.slf4j.Slf4j;

@OpenAPIDefinition(
    info =
        @Info(
            title = "mn-stock-broker",
            version = "0.1",
            description = "Udemy Micronaut Course",
            license = @License(name = "MIT")))
@Slf4j
public class Application {

  public static void main(String[] args) {
    final ApplicationContext context = Micronaut.run(Application.class, args);
    final HelloWorldService service = context.getBean(HelloWorldService.class);
    log.info(service.sayHi() + " with lombok!");
  }
}
