package com.github.prontera.account.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Zhao Junjian
 * @date 2020/01/22
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class BalanceReservingRequest {

    @ApiModelProperty(value = "订单ID", required = true, example = "1")
    private @NotNull Long orderId;

    @ApiModelProperty(value = "用户名", required = true, example = "chris")
    private @NotBlank String username;

    @ApiModelProperty(value = "扣减金额, 单位元", required = true, example = "47")
    private @NotNull @Min(1) Integer amount;

    @ApiModelProperty(value = "期望的预留资源时间", required = true, example = "7")
    private @NotNull @Min(1) @Max(900) Integer expectedReservingSeconds;

}
