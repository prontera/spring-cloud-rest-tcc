package com.github.prontera.util.converter.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Zhao Junjian
 */
public class OffsetDateTimeToIso8601Serializer extends JsonSerializer<OffsetDateTime> {
    public static final OffsetDateTimeToIso8601Serializer INSTANCE = new OffsetDateTimeToIso8601Serializer();

    @Override
    public void serialize(OffsetDateTime dateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }
}
