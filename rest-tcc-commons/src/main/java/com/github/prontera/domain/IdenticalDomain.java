package com.github.prontera.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Zhao Junjian
 * @date 2020/01/21
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode
public class IdenticalDomain implements Serializable {

    private static final long serialVersionUID = -3893569795599411412L;

    private Long id;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

}
