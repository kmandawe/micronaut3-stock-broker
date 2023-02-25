package com.kensbunker.mn.auth.jwt;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.*;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;

import java.util.ArrayList;

@Slf4j
@Singleton
public class AuthenticationProviderUserPassword implements AuthenticationProvider {
  @Override
  public Publisher<AuthenticationResponse> authenticate(
          @Nullable HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {
    return Flowable.create(
        emitter -> {
          final Object identity = authenticationRequest.getIdentity();
          final Object secret = authenticationRequest.getSecret();
          log.debug("User {} tries to login..", identity);

          if (identity.equals("my-user") && secret.equals("secret")) {
            // pass
            emitter.onNext(AuthenticationResponse.success((String) identity, new ArrayList<>()));
            emitter.onComplete();
            log.debug("Successful login!");
            return;
          }
          emitter.onError(
              new AuthenticationException(new AuthenticationFailed("Wrong username or password")));
        },
        BackpressureStrategy.ERROR);
  }
}
