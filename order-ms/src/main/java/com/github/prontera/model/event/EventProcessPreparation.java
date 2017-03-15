package com.github.prontera.model.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.prontera.common.model.response.Response;
import com.github.prontera.model.type.EventType;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @author Zhao Junjian
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"}, ignoreUnknown = true)
public class EventProcessPreparation implements Response {

    private static final long serialVersionUID = -2987715477494396585L;

    @NotNull
    @ApiModelProperty(value = "业务类型", example = "MAKE_DEAL", required = true)
    private String bizType;

    @NotNull
    @ApiModelProperty(value = "事件类型, 如NOTIFY, REQUEST等", example = "NOTIFY", required = true)
    private EventType eventType;

    @NotNull
    @NotBlank
    @ApiModelProperty(value = "全局事件ID", example = "1asdf9239349123", required = true)
    private String publishGuid;

    @NotNull
    @ApiModelProperty(value = "request payload", required = true)
    private Object payload;

}

