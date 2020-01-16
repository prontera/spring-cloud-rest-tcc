package com.github.prontera.util.converter.orika;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.time.OffsetDateTime;

/**
 * @author Zhao Junjian
 */
public class OffsetDateTimeConverter extends BidirectionalConverter<OffsetDateTime, OffsetDateTime> {
    @Override
    public OffsetDateTime convertTo(OffsetDateTime zdt, Type<OffsetDateTime> type, MappingContext mappingContext) {
        return OffsetDateTime.from(zdt);
    }

    @Override
    public OffsetDateTime convertFrom(OffsetDateTime zdt, Type<OffsetDateTime> type, MappingContext mappingContext) {
        return OffsetDateTime.from(zdt);
    }
}
