package com.celtech.api.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "com.celtech.api.repository",
        mongoTemplateRef = "siteMongoTemplate"
)
public class SiteMongoConfig {

    @Bean
    @Primary
    public MongoDatabaseFactory siteMongoDbFactory(
            @Value("${spring.data.mongodb.uri}") String uri,
            @Value("${spring.data.mongodb.database:celtech-solutions}") String database) {
        MongoClient mongoClient = MongoClients.create(uri);
        return new SimpleMongoClientDatabaseFactory(mongoClient, database);
    }

    @Bean
    @Primary
    public MongoTemplate siteMongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}
