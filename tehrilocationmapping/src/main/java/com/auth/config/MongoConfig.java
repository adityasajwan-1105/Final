package com.auth.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.Collection;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener(LocalValidatorFactoryBean validator) {
        return new ValidatingMongoEventListener(validator);
    }

    @Bean
    public GridFsTemplate gridFsTemplate() throws Exception {
        MongoDatabaseFactory mongoDbFactory = mongoDbFactory();
        MongoMappingContext mappingContext = new MongoMappingContext();
        mappingContext.setAutoIndexCreation(true);
        
        MappingMongoConverter converter = new MappingMongoConverter(
            new DefaultDbRefResolver(mongoDbFactory), 
            mappingContext
        );
        converter.setCustomConversions(customConversions());
        converter.afterPropertiesSet();
        
        return new GridFsTemplate(mongoDbFactory, converter);
    }

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(new ArrayList<>());
    }

    @Override
    protected Collection<String> getMappingBasePackages() {
        Collection<String> mappingBasePackages = new ArrayList<>();
        mappingBasePackages.add("com.auth.model");
        return mappingBasePackages;
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
} 