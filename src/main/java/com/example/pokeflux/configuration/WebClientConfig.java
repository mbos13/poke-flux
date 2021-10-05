package com.example.pokeflux.configuration;

import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Configuration
@Slf4j
public class WebClientConfig {

    @Bean
    public WebClient localApiClient() {
        var httpClient = HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE);
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient))
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(logRequest());
//                    exchangeFilterFunctions.add(logResponse());
                }).build();
    }

    ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            if (log.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder("Request: \n");
                clientRequest
                        .headers()
                        .forEach((name, values) -> values.forEach(value ->{
                            log.debug(sb.toString());
                        }));
            }
            return Mono.just(clientRequest);
        });
    }

//    ExchangeFilterFunction logResponse() {
//        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
//            if (log.isDebugEnabled()) {
//                StringBuilder sb = new StringBuilder("Response: \n");
//                clientResponse
//                        .headers()
//                        .forEach((name, values) -> values.forEach(value -> /* append header key/value */));
//                log.debug(sb.toString());
//            }
//            return Mono.just(clientResponsec);
//        });
//    }
}
