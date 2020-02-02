package com.github.prontera;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Zhao Junjian
 * @date 2020/02/02
 */
public final class Pipeline<I, O> {

    private final Object lock = new Object();

    private final List<GenericChainHandler<I, O>> handlers = new LinkedList<>();

    public void addLast(@Nonnull GenericChainHandler<I, O> handler) {
        Objects.requireNonNull(handler);
        synchronized (lock) {
            handlers.add(handler);
        }
    }

    public O fire(@Nonnull I in) {
        Objects.requireNonNull(in);
        final AtomicReference<O> out = new AtomicReference<>();
        for (GenericChainHandler<I, O> handler : handlers) {
            handler.invoke(in, out);
            if (handler.isRequestCompletion() && handler.getType() == ChainHandler.Type.EXCLUSIVE) {
                break;
            }
        }
        return out.get();
    }

}
