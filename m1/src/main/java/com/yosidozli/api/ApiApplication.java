package com.yosidozli.api;


import com.yosidozli.EventRecord;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.dropwizard.Application;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jdbi.v3.core.Jdbi;

import java.util.Properties;

public class ApiApplication extends Application<ApiConfiguration> {


    @Override
    public void initialize(Bootstrap<ApiConfiguration> bootstrap) {
        super.initialize(bootstrap);
    }

    @Override
    public void run(ApiConfiguration apiConfiguration, Environment environment) {
        final JdbiFactory jdbiFactory = new JdbiFactory();
        final Jdbi jdbi = jdbiFactory.build(environment, apiConfiguration.getDataSourceFactory(), "postgresql");
        KafkaProducer<String, EventRecord> producer = new KafkaProducer<>(producerProperties(apiConfiguration));
        environment.lifecycle().manage(new CloseableManaged(producer));
        environment.jersey().register(new EndPoint(jdbi.onDemand(DeviceDao.class), producer, apiConfiguration.getTopic(), apiConfiguration.getTable()));

    }

    public static void main(String[] args) throws Exception{
            new ApiApplication().run(args);
    }

    public static Properties producerProperties(ApiConfiguration apiConfiguration){
        Properties prop = new Properties();

        prop.put(ProducerConfig.ACKS_CONFIG,"1");
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,KafkaAvroSerializer.class.getName());
        System.out.println(apiConfiguration.getKafka().toString());

        prop.putAll(apiConfiguration.getKafka());
        return prop;
    }

}
