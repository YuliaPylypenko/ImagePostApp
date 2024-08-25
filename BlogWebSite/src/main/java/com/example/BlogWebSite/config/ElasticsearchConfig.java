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
    public ElasticsearchClient elasticsearchClient(){
        RestClientBuilder builder = RestClient.builder(new HttpHost("172.19.0.3", 9200, "http"))
                .setRequestConfigCallback(requestConfigBuilder ->
                        requestConfigBuilder
                                .setConnectTimeout(10000) // Increase connection timeout
                                .setSocketTimeout(60000)) // Increase socket timeout
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setMaxConnTotal(100) // Max total connections
                                .setMaxConnPerRoute(10)); // Max connections per route

        RestClientTransport transport = new RestClientTransport(
                builder.build(),
                new JacksonJsonpMapper());

        return new ElasticsearchClient(transport);
    }
}
