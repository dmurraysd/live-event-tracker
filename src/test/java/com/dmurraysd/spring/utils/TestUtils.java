package com.dmurraysd.spring.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.SerializationException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class TestUtils {

    public static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static <T> T readJsonResourceToObject(final String fileName, final Class<T> clazz) {
        try {
            final URI filepath = Objects.requireNonNull(TestUtils.class.getClassLoader().getResource(fileName)).toURI();
            String payload = Files.readString(Path.of(filepath));

            return deSerialize(payload, clazz);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> readJsonResourceToList(final String fileName, final Class<T> clazz) {
        try {
            final URI filepath = Objects.requireNonNull(TestUtils.class.getClassLoader().getResource(fileName)).toURI();
            String json = Files.readString(Path.of(filepath));

            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readJsonResourceToString(final String fileName) {
        try {
            final URI filepath = Objects.requireNonNull(TestUtils.class.getClassLoader().getResource(fileName)).toURI();
            return Files.readString(Path.of(filepath));
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T deSerialize(final String payload,
                                    final Class<T> clazz) {
        try {
            if (Objects.nonNull(payload)) {
                return objectMapper.readValue(payload, clazz);
            }
            return null;
        } catch (IOException e) {
            throw new SerializationException("Can't deserialize data [" + payload + "]" + e.getMessage());
        }
    }

    public static <T> String serialise(final T object) {
        try {
            if (Objects.nonNull(object)) {
                return objectMapper.writeValueAsString(object);
            }
            return null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
