package com.kensbunker.mn.hello;

import io.micronaut.context.annotation.ConfigurationProperties;

import javax.validation.constraints.NotBlank;

@ConfigurationProperties("hello.world.translation")
public interface HelloWorldTranslationConfig {
    @NotBlank
    String getDe();
    String getEn();
}
