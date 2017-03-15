package com.github.prontera.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.prontera.common.model.BasicDomain;
import com.github.prontera.model.type.EventPublishStatus;
import com.github.prontera.model.type.EventType;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"}, ignoreUnknown = true)
public class EventPublish extends BasicDomain {

    private static final long serialVersionUID = -6135552303498764455L;

    @NotNull
    @ApiModelProperty(value = "事件状态", required = true)
    private EventPublishStatus eventStatus;

    @NotNull
    @ApiModelProperty(value = "0为NOTIFY, 1为REQUEST, 2为RESPONSE", required = true)
    private EventType eventType;

    @NotNull
    @Size(max = 30)
    @ApiModelProperty(value = "业务类型", required = true)
    private String bizType;

    @NotNull
    @NotBlank
    @Size(max = 1024)
    @JsonRawValue
    @ApiModelProperty(value = "payload", required = true)
    private String payload;

    @NotNull
    @Size(max = 36)
    @ApiModelProperty(value = "guid", required = true)
    private String publishGuid;

    @Size(max = 36)
    @ApiModelProperty(value = "process guid")
    private String processGuid;
}