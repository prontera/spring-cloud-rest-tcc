package com.github.prontera.model.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.prontera.common.model.request.Request;
import com.github.prontera.model.type.EventType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author Zhao Junjian
 */
@Getter
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"}, ignoreUnknown = true)
public class EventPublishPreparation implements Request {

    private static final long serialVersionUID = 5820937169683749781L;

    @NotNull
    @ApiModelProperty(value = "业务类型", example = "MAKE_DEAL", required = true)
    private String bizType;

    @NotNull
    @ApiModelProperty(value = "事件类型, 如NOTIFY, REQUEST等", example = "NOTIFY", required = true)
    private EventType eventType;

    @NotNull
    @ApiModelProperty(value = "实际payload", required = true)
    private Object payload;

}

