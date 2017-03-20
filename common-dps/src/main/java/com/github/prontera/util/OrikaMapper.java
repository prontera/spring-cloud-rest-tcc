package com.github.prontera.util;

import com.github.prontera.util.converter.orika.InstantConverter;
import com.github.prontera.util.converter.orika.LocalDateConverter;
import com.github.prontera.util.converter.orika.LocalDateTimeConverter;
import com.github.prontera.util.converter.orika.LocalTimeConverter;
import com.github.prontera.util.converter.orika.OffsetDateTimeConverter;
import com.github.prontera.util.converter.orika.ZonedDateTimeConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import java.util.List;

/**
 * 简单封装orika, 实现深度转换Bean<->Bean的Mapper.
 * 仅适用于普通的bean, 如果遇到性能瓶颈请使用传统的setter
 */
public final class OrikaMapper {

    private static final MapperFacade FACADE;

    static {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        final ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter(new InstantConverter());
        converterFactory.registerConverter(new LocalDateConverter());
        converterFactory.registerConverter(new LocalDateTimeConverter());
        converterFactory.registerConverter(new LocalTimeConverter());
        converterFactory.registerConverter(new OffsetDateTimeConverter());
        converterFactory.registerConverter(new ZonedDateTimeConverter());
        FACADE = mapperFactory.getMapperFacade();
    }

    private OrikaMapper() {
    }

    /**
     * 基于Dozer转换对象的类型.
     */
    public static <S, D> D map(S source, Class<D> destinationClass) {
        return FACADE.map(source, destinationClass);
    }

    /**
     * 基于Dozer转换Collection中对象的类型.
     */
    public static <S, D> List<D> mapList(Iterable<S> sourceList, Class<D> destinationClass) {
        return FACADE.mapAsList(sourceList, destinationClass);
    }

}