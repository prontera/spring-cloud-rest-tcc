package com.github.prontera;

/**
 * @author Zhao Junjian
 * @date 2020/02/02
 */
public abstract class GenericChainHandler<I, O> implements ChainHandler<I, O> {

    private volatile boolean isRequestCompletion;

    @Override
    public void complete() {
        isRequestCompletion = true;
    }

    public boolean isRequestCompletion() {
        return isRequestCompletion;
    }

}
