package org.gaung.wiwokdetok.kapsulkeaslian.config;

import org.gaung.wiwokdetok.kapsulkeaslian.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

public class TestConfig {

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return mock(JwtTokenProvider.class);
    }
}
