package com.github.prontera.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Zhao Junjian
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"}, ignoreUnknown = true)
public class LoginResponse extends RestfulResponse {
    private static final long serialVersionUID = 7883775815440213351L;

    @JsonProperty("mobile")
    @ApiModelProperty(value = "手机号", example = "18888888888", required = true)
    private String mobile;

    @JsonProperty("balance")
    @ApiModelProperty(value = "用户的初始化余额", example = "100000000", required = true)
    private Long balance;

}
