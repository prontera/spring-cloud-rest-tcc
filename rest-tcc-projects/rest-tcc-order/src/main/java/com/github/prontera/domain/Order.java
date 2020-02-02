package com.github.prontera.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.prontera.annotation.marker.NonBehavior;
import com.github.prontera.enums.OrderState;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author Zhao Junjian
 * @date 2020/01/20
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"}, ignoreUnknown = true)
public class Order extends IdenticalDomain {

    private static final long serialVersionUID = -7328815311359399684L;

    private Long id;

    private Long userId;

    private Long productId;

    private Integer price;

    private Integer quantity;

    private OrderState state;

    private Long guid;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    @NonBehavior
    private LocalDateTime expireAt;

    @NonBehavior
    private LocalDateTime doneAt;

}
