package com.github.prontera.enums;

import com.sankuai.supply.domain.Poi;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Zhao Junjian
 * @date 2019/06/25
 */
public enum OrderEvent {
    /**
     * 完成poi独立同步
     */
    POI_PUSHED(PoiState.IDLE, PoiState.RUNNING, poi -> true, poi -> {
        poi.setState(PoiState.RUNNING);
    }),
    /**
     * 完成room独立同步
     */
    ACTIVATE_ROOM(PoiState.RUNNING, PoiState.ROOM_CALIBRATED, poi -> true, poi -> {
        poi.setState(PoiState.ROOM_CALIBRATED);
    }),
    /**
     * 完成image独立同步
     */
    ACTIVATE_IMAGE(PoiState.ROOM_CALIBRATED, PoiState.IMAGE_CALIBRATED, poi -> true, poi -> {
        poi.setGuid("");
        poi.setState(PoiState.IDLE);
    }),
    /** LINE SEPARATOR */
    ;

    private final PoiState source;

    private final PoiState target;

    private final Predicate<? super Poi> guard;

    private final Consumer<? super Poi> action;

    OrderEvent(@Nonnull PoiState source, @Nonnull PoiState target,
               @Nonnull Predicate<? super Poi> guard, @Nonnull Consumer<? super Poi> action) {
        this.source = Objects.requireNonNull(source);
        this.target = Objects.requireNonNull(target);
        this.guard = Objects.requireNonNull(guard);
        this.action = Objects.requireNonNull(action);
    }

    public PoiState target() {
        return target;
    }

    public boolean tryTransiting(@Nonnull Poi domain) {
        Objects.requireNonNull(domain);
        final boolean inSource = source == domain.getState();
        boolean isTransit = inSource && guard.test(domain);
        if (isTransit) {
            action.accept(domain);
        }
        return isTransit;
    }

}
