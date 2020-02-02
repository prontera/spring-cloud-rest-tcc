package com.github.prontera.product.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @author Zhao Junjian
 * @date 2020/01/29
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class QueryProductRequest {

    @ApiModelProperty(value = "产品名", required = true, example = "ps4")
    private @NotBlank String productName;

}
