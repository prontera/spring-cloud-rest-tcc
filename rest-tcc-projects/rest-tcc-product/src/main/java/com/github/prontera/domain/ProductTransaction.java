package com.github.prontera.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.prontera.product.enums.ReservingState;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author Zhao Junjian
 * @date 2020/01/24
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"}, ignoreUnknown = true)
public class ProductTransaction extends IdenticalDomain {
    private static final long serialVersionUID = -1997296571733378076L;

    private Long id;

    private Long productId;

    private Long orderId;

    private Long amount;

    private ReservingState state;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private LocalDateTime expireAt;

    private LocalDateTime doneAt;

}
