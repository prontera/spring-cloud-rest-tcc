-- -----------------------------------------------------
-- Table `t_event_pub`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `t_event_pub` (
  `id`           BIGINT(19)    NOT NULL AUTO_INCREMENT,
  `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME      NOT NULL DEFAULT '1970-01-01 00:00:00',
  `delete_time`  DATETIME      NOT NULL DEFAULT '1970-01-01 00:00:00',
  `biz_type`     VARCHAR(64)   NOT NULL
  COMMENT '业务类型',
  `event_status` TINYINT       NOT NULL
  COMMENT '事件状态, -128为未知错误, -3为NOT_FOUND(找不到exchange), -2为NO_ROUTE(找到exchange但是找不到queue), -1为FAILED(如类型尚未注册等的业务失败), 0为NEW(消息落地), 1为PENDING, 2为DONE',
  `payload`      VARCHAR(1024) NOT NULL
  COMMENT '请求时的描述负载',
  `lock_version` INT UNSIGNED  NOT NULL DEFAULT 0
  COMMENT '锁版本号',
  `pub_guid`     VARCHAR(64)   NOT NULL
  COMMENT '消息发布时的GUID，用于消费者作去重',
  PRIMARY KEY (`id`),
  INDEX `idx_event_pub_es_ut` (`event_status` ASC, `update_time` DESC),
  UNIQUE INDEX `uni_event_pub_pg` (`pub_guid` ASC)
);

-- -----------------------------------------------------
-- Table `t_event_sub`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `t_event_sub` (
  `id`           BIGINT(19)    NOT NULL AUTO_INCREMENT,
  `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME      NOT NULL DEFAULT '1970-01-01 00:00:00',
  `delete_time`  DATETIME      NOT NULL DEFAULT '1970-01-01 00:00:00',
  `biz_type`     VARCHAR(64)   NOT NULL
  COMMENT '业务类型',
  `event_status` TINYINT       NOT NULL
  COMMENT '事件状态, -128为未知错误, -3为NOT_FOUND(找不到handler), -1为FAILED(业务失败), 0为NEW, 2为DONE',
  `payload`      VARCHAR(1024) NOT NULL
  COMMENT '请求方的描述负载',
  `lock_version` INT UNSIGNED  NOT NULL DEFAULT 0
  COMMENT '锁版本号',
  `pub_guid`     VARCHAR(64)   NOT NULL
  COMMENT '请求方GUID',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uni_event_sub_pg` (`pub_guid` ASC),
  INDEX `idx_event_sub_es` (`event_status` ASC)
);

