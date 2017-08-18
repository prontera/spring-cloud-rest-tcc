package com.github.prontera.domain.type;

/**
 * @author Zhao Junjian
 */
public enum EventStatus {
    /**
     * 当Publisher中当basic.return返回NO_ROUTE以外的状态时，将事件置为未知异常
     */
    ERROR(-128),
    /**
     * 在Publisher中basic.ack中返回false，证明找不到exchange时事件状态变成NOT_FOUND
     * <p>
     * 在Subscriber中没有找到事件对应的Handler也会成为NOT_FOUND
     */
    NOT_FOUND(-3),
    /**
     * 在Publisher中basic.return找到exchange但是找不到queue时会成为NO_ROUTE
     */
    NO_ROUTE(-2),
    /**
     * 当Publisher没有注册business type而找到该发往哪个exchange和queue时的错误
     */
    FAILED(-1),
    /**
     * 新建事件时的状态
     */
    NEW(0),
    /**
     * 在Publisher中发送到Broker，但是还未收到publisher confirm时的状态
     */
    PENDING(1),
    /**
     * 当Publisher成功收到publisher confirm消息时将事件转换为DONE
     * <p>
     * 当Subscriber将事件处理完成后也会置为DONE
     */
    DONE(2);

    private final int status;

    EventStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
