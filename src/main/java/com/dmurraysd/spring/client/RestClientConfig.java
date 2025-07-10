package com.dmurraysd.spring.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(RestClientConfig.class);

    @Bean
    public InternalMatchScoreClient internalMatchScoreClient(@Value("${internal.match.score.client.url:http://localhost:8085/}") String url,
                                                             final RestClient.Builder restClientBuilder) {

        return getClient(restClientBuilder, url, InternalMatchScoreClient.class);
    }

    private <S> S getClient(final RestClient.Builder restClientBuilder,
                            final String url,
                            final Class<S> clazz) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(configureBuilder(restClientBuilder, url)))
                .build()
                .createClient(clazz);
    }

    private RestClient configureBuilder(final RestClient.Builder restClientBuilder, final String url) {
        return restClientBuilder
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError,
                        (request, response) -> logger.error("Client Error Status {} [{}]", response.getStatusCode().value(), request.getURI())
                ).defaultStatusHandler(HttpStatusCode::is5xxServerError,
                        (request, response) -> {
                            logger.error("Internal Server Error Status {} [{}]", response.getStatusCode().value(), request.getURI());
                            throw new ServerException(String.format("Internal Server Error Status %s [%s]", response.getStatusCode().value(), request.getURI()));
                        }
                ).build();
    }
}
