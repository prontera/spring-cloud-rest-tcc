package com.github.prontera.util.converter.orika;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.time.Instant;

/**
 * @author Zhao Junjian
 */
public class InstantConverter extends BidirectionalConverter<Instant, Instant> {
    @Override
    public Instant convertTo(Instant instant, Type<Instant> type, MappingContext mappingContext) {
        return Instant.from(instant);
    }

    @Override
    public Instant convertFrom(Instant instant, Type<Instant> type, MappingContext mappingContext) {
        return Instant.from(instant);
    }
}
