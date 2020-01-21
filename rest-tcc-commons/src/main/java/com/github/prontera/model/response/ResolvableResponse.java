package com.github.prontera.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Zhao Junjian
 * @date 2020/01/20
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ResolvableResponse implements Serializable {
    private static final long serialVersionUID = 2589811349478861719L;

    @ApiModelProperty(value = "响应标志位", required = true, example = "true")
    private boolean successful;

    @ApiModelProperty(value = "响应码", required = true, example = "20000")
    private int code;

    @ApiModelProperty(value = "响应信息", required = true, example = "true")
    private @NotNull String message;

}
