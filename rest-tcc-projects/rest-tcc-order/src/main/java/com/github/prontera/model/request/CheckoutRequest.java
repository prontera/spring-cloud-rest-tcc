package com.github.prontera.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author Zhao Junjian
 * @date 2020/01/20
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"}, ignoreUnknown = true)
public class CheckoutRequest {

    @ApiModelProperty(value = "产品ID", required = true, example = "823")
    private @NotNull Long productId;

    @ApiModelProperty(value = "用户ID", required = true, example = "132")
    private @NotNull Long userId;

    @ApiModelProperty(value = "扣减金额, 单位元", required = true, example = "47")
    private @NotNull @Min(1) Integer price;

    @ApiModelProperty(value = "扣减库存数", required = true, example = "1")
    private @NotNull @Min(1) Integer quantity;

}
