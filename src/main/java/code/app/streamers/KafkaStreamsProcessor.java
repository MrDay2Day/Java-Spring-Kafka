package code.app.streamers;

import com.trades.Trade;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsProcessor {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.properties.schema.registry.url}")
    private String schemaRegistryUrl;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class.getName());
        props.put("schema.registry.url", schemaRegistryUrl);
        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public KStream<String, Trade> kStream(StreamsBuilder streamsBuilder) {
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", schemaRegistryUrl);

        final SpecificAvroSerde<Trade> tradeSerde = new SpecificAvroSerde<>();
        tradeSerde.configure(serdeConfig, false);

        KStream<String, Trade> stream = streamsBuilder.stream("trades", Consumed.with(Serdes.String(), tradeSerde));

        stream
                .filter((key, trade) -> {
                    if(trade.getPrice() > 15000.00){
                        return true;
                    } else {
                        return false;
                    }
                })
                .mapValues((key, trade) -> {
                    System.out.println("Trade received here: " + trade.getTimestamp());
                    double amount = trade.getAmount();
                    trade.setAmount(amount + 2.00);
                    return trade;
                })
                .peek((key, trade) -> {
                    System.out.println("Processing high-value trade: " + trade.getTimestamp());
                })
                .to("high-value-trades", Produced.with(Serdes.String(), tradeSerde));

        return stream;
    }
}