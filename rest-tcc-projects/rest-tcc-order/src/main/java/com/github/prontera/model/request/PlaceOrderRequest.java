package com.github.prontera.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"}, ignoreUnknown = true)
public class PlaceOrderRequest extends RestfulRequest {

    private static final long serialVersionUID = -7089168357959601473L;

    @ApiModelProperty(value = "产品ID", required = true, example = "1")
    private @NotNull Long productId;

    @ApiModelProperty(value = "用户ID", required = true, example = "1")
    private @NotNull Long userId;

    @ApiModelProperty(value = "扣减金额, 单位元", required = true, example = "1")
    private @NotNull @Min(1) Integer price;

    @ApiModelProperty(value = "扣减库存数", required = true, example = "1")
    private @NotNull @Min(1) Integer quantity;

}
