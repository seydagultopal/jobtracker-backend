package com.seyda.jobtracker.config;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KafkaConfigTest {

    @Test
    void consumerTrustsOnlyApplicationAuthEventsPackage() throws Exception {
        KafkaConfig config = new KafkaConfig();
        Field bootstrapServers = KafkaConfig.class.getDeclaredField("bootstrapServers");
        bootstrapServers.setAccessible(true);
        bootstrapServers.set(config, "localhost:9092");

        DefaultKafkaConsumerFactory<?, ?> factory =
                (DefaultKafkaConsumerFactory<?, ?>) config.consumerFactory();
        Map<String, Object> properties = factory.getConfigurationProperties();

        assertEquals("com.seyda.jobtracker.auth",
                properties.get(JsonDeserializer.TRUSTED_PACKAGES));
    }
}
