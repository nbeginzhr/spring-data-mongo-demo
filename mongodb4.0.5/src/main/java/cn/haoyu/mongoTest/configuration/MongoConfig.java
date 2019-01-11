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
        return new MongoClient(new MongoClientURI("mongodb://192.168.116.147:27018,192.168.116.147:27019,192.168.116.147:27020/teamis?replicaSet=haoyu"));
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
