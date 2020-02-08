package com.github.prontera.product.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.prontera.model.response.ResolvableResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author Zhao Junjian
 * @date 2020/01/25
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"}, ignoreUnknown = true)
public class QueryProductTxnResponse extends ResolvableResponse {

    private static final long serialVersionUID = -9159171999056024307L;

    private Long productId;

    private Long orderId;

    private Long amount;

    private Integer state;

    private LocalDateTime createAt;

    private LocalDateTime expireAt;

    private LocalDateTime doneAt;

}
