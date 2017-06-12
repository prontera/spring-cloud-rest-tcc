package com.github.prontera.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.prontera.domain.type.EventStatus;
import com.github.prontera.model.BasicDomain;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"}, ignoreUnknown = true)
public class EventPublisher extends BasicDomain {
    private static final long serialVersionUID = 2840081172778887899L;

    @NotNull
    @NotBlank
    private String businessType;

    @NotNull
    private EventStatus eventStatus;

    @NotNull
    @JsonRawValue
    private String payload;

    @NotNull
    private Integer lockVersion;

    @NotNull
    @NotBlank
    private String guid;

}