package com.practice.WebclientExamples.config;


import com.practice.WebclientExamples.constants.UrlConstants;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {


    @Bean(name = "webClientAPI1")
    public WebClient webClientForApi1() {
        HttpClient httpClient = createWebClientWithTimeOut(Duration.ofMillis(2000), Duration.ofMillis(2000), Duration.ofMillis(2000));
        return getWebClientConfigurations(httpClient, UrlConstants.RANDOM_USER_API_URL);
    }

    @Bean(name = "webClientAPI2")
    public WebClient webClientForApi2() {
        HttpClient httpClient = createWebClientWithTimeOut(Duration.ofMillis(1000), Duration.ofMillis(1000), Duration.ofMillis(1000));
        return getWebClientConfigurations(httpClient, UrlConstants.NATIONALIZE_USER_API_URL);
    }

    @Bean(name = "webClientAPI3")
    public WebClient webClientForApi3() {
        HttpClient httpClient = createWebClientWithTimeOut(Duration.ofMillis(1000), Duration.ofMillis(1000), Duration.ofMillis(1000));
        return getWebClientConfigurations(httpClient, UrlConstants.USER_GENDER_API_URL);
    }

    private HttpClient createWebClientWithTimeOut(Duration connectionTimeout, Duration readTimeout, Duration writeTimeout) {

        return HttpClient.create()
                .responseTimeout(readTimeout)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(readTimeout.toMillis(), TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(writeTimeout.toMillis(), TimeUnit.MILLISECONDS)))
                .responseTimeout(connectionTimeout);
    }

    private WebClient getWebClientConfigurations(HttpClient httpClient, String baseUrl) {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(baseUrl)
                .defaultHeader("Accept", MediaType.ALL_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "Agent")
                .build();
    }
}
