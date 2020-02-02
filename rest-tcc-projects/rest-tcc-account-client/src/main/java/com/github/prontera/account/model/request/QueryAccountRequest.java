package com.github.prontera.account.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @author Zhao Junjian
 * @date 2020/01/28
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class QueryAccountRequest {

    @ApiModelProperty(value = "用户名", required = true, example = "trump")
    private @NotBlank String name;

}
