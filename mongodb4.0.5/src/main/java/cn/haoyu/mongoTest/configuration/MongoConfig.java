package cn.haoyu.mongoTest.configuration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

/**
 * Created by haoyu on 2019/1/7.
 */
@Configuration
public class MongoConfig extends AbstractMongoConfiguration {


    @Override
    public MongoClient mongoClient() {
        return new MongoClient(new MongoClientURI("mongodb://localhost:27117,localhost:27217,localhost:27317/teamis?replicaSet=haoyuSet"));
    }

    @Override
    protected String getDatabaseName() {
        return "teamis";
    }

    @Bean
    MongoTransactionManager getMongoTranactionManage(MongoDbFactory mongoDbFactory) {
        return new MongoTransactionManager(mongoDbFactory);
    }
}
