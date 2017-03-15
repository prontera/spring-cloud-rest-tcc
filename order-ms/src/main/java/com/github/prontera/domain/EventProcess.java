package com.github.prontera.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.prontera.common.model.BasicDomain;
import com.github.prontera.model.type.EventProcessStatus;
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
public class EventProcess extends BasicDomain {

    private static final long serialVersionUID = 645740808932416922L;

    @NotNull
    @ApiModelProperty(value = "处理事件的当前状态", required = true, example = "NEW")
    private EventProcessStatus eventStatus;

    @NotNull
    @ApiModelProperty(value = "处理事件的类型", required = true, example = "REQUEST")
    private EventType eventType;

    @NotNull
    @NotBlank
    @ApiModelProperty(value = "业务类型", required = true)
    private String bizType;

    @NotNull
    @NotBlank
    @Size(max = 1024)
    @JsonRawValue
    @ApiModelProperty(value = "request payload", required = true)
    private String reqPayload;

    @Size(max = 1024)
    @JsonRawValue
    @ApiModelProperty(value = "response payload", required = true)
    private String respPayload;

    @NotNull
    @Size(max = 36)
    @ApiModelProperty(value = "publish guid", required = true)
    private String publishGuid;

    @NotNull
    @Size(max = 36)
    @ApiModelProperty(value = "guid", required = true)
    private String processGuid;
}