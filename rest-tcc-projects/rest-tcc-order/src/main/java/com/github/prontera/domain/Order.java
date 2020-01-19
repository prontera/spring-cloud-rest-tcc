package com.github.prontera.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.prontera.enums.OrderState;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * @author Zhao Junjian
 * @date 2020/01/18
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"}, ignoreUnknown = true)
public class Order implements Serializable {

    private static final long serialVersionUID = -7880174624621304089L;

    private Long id;

    private Long userId;

    private Long productId;

    private Integer price;

    private OrderState state;

    private OffsetDateTime createAt;

    private OffsetDateTime updateAt;

    private Integer timeZone;

}
