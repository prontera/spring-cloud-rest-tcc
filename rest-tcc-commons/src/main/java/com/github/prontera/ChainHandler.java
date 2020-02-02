package com.github.prontera;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Zhao Junjian
 * @date 2020/02/02
 */
public interface ChainHandler<I, O> {

    /**
     * @param in  从中读取数据的类型为{@link I}的对象
     * @param out 经转换后的结果
     */
    void invoke(@Nonnull I in, @Nonnull AtomicReference<O> out);

    /**
     * 经由{@link #invoke(Object, AtomicReference)}内部判断和处理后, 标记为当前数据已经被该handler处理
     */
    void complete();

    /**
     * 当类型为{@link Type#EXCLUSIVE}时, 实现者不应该继续迭代
     *
     * @return 当前处理器的类型
     */
    Type getType();

    enum Type {
        /**
         * exclusive类型{@link ChainHandler}, 当{@link #complete()}被调用后不应该继续迭代责任链
         */
        EXCLUSIVE,
        /**
         * shared类型{@link ChainHandler}, 也是默认类型
         */
        SHARED,
        /** LINE SEPARATOR */
        ;
    }

}
