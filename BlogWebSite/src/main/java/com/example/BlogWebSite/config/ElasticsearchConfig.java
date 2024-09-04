package com.example.BlogWebSite.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Bean
    public RestClient restClient() {
        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "http"))
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                        .setConnectTimeout(10000)
                        .setSocketTimeout(60000))
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                        .setMaxConnTotal(100)
                        .setMaxConnPerRoute(10));

        return builder.build();
    }

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        RestClientTransport transport = new RestClientTransport(
                restClient(),
                new JacksonJsonpMapper());

        return new ElasticsearchClient(transport);
    }
}
