package com.github.prontera.account.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author Zhao Junjian
 * @date 2020/01/23
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class QueryAccountTxnRequest {

    @ApiModelProperty(value = "订单ID", required = true, example = "1")
    private @NotNull Long orderId;

}
