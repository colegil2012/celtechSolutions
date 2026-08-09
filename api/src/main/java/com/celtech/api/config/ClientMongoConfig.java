package com.celtech.api.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "com.celtech.api.clients.repository",
        mongoTemplateRef = "clientMongoTemplate")
public class ClientMongoConfig {

    @Bean
    public MongoDatabaseFactory clientsMongoDbFactory(
            @Value("${spring.data.mongodb.uri}") String uri,
            @Value("${app.clients.mongodb-database}") String database) {
        MongoClient mongoClient = MongoClients.create(uri);
        return new SimpleMongoClientDatabaseFactory(mongoClient, database);
    }

    @Bean
    public MongoTemplate clientMongoTemplate(@Qualifier("clientsMongoDbFactory")
                                                 MongoDatabaseFactory clientsMongoDbFactory) {
        return new MongoTemplate(clientsMongoDbFactory);
    }
}
