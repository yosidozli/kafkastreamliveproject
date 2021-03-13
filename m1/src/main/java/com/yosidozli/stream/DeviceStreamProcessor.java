package com.yosidozli.stream;

import com.yosidozli.api.DeviceDao;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import com.yosidozli.EventRecord;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.util.Collections;
import java.util.Properties;

public class DeviceStreamProcessor extends ConfiguredCommand<StreamConfiguration> {
    private DeviceDao database;
    private KafkaStreams kafkaStreams;

    protected DeviceStreamProcessor(String name, String description) {
        super(name, description);
    }

    @Override
    protected void run(Bootstrap<StreamConfiguration> bootstrap, Namespace namespace, StreamConfiguration configuration)  {

        //create db
        database = initDataBase(configuration);
        kafkaStreams = initKafkaStream(configuration);


        kafkaStreams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));

    }

    private KafkaStreams initKafkaStream(StreamConfiguration configuration) {
        Properties prop = new Properties();
        prop.put(StreamsConfig.APPLICATION_ID_CONFIG, configuration.getApplicationId());
        prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getBootstrapServers());
        prop.put("schema.registry.url", configuration.getSchemeRegistry());
        Serde<String> keySerde = Serdes.String();
        Serde<EventRecord> valueSerde = new SpecificAvroSerde<>();
        valueSerde.configure(Collections.singletonMap("schema.registry.url", configuration.getSchemeRegistry()), false);

        BytesToJson bytesToJson = new BytesToJson();

        StreamsBuilder builder = new  StreamsBuilder();

        KStream<String, EventRecord> stream = builder.stream(configuration.getSourceTopic(), Consumed.with(keySerde, valueSerde));
        stream.mapValues(value -> bytesToJson.apply(value.getBytes()))
                .flatMapValues(v -> v)
                .foreach((uuid, value) -> {
                    boolean isCharging = Long.parseLong( value.get("charging").toString()) > 1;
                    database.insertDeviceState(configuration.getTable(),uuid,isCharging);
                });

        return new KafkaStreams(builder.build(),prop);

    }

    private DeviceDao initDataBase(StreamConfiguration configuration) {
        MetricRegistry registry = new MetricRegistry();
        JmxReporter jmxReporter = JmxReporter.forRegistry(registry).build();
        jmxReporter.start();
//        JdbiFactory jdbiFactory = new  JdbiFactory();
//        Jdbi jdbi = jdbiFactory.build(,configuration.getDataSourceFactory(),configuration.getDataSourceFactory().build(registry,"device-db"),"device-db");
        Jdbi jdbi = Jdbi.create(configuration.getDataSourceFactory().build(registry, "deviceDb"));
        jdbi.installPlugin(new SqlObjectPlugin());
        return jdbi.onDemand(DeviceDao.class);
    }
}
