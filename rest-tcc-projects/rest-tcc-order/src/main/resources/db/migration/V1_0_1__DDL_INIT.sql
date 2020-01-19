-- -----------------------------------------------------
-- Schema order
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `order`
    DEFAULT CHARACTER SET utf8mb4;
USE `order`;

-- -----------------------------------------------------
-- Table `order`.`t_machine_coordinator`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `order`.`t_machine_coordinator`
(
    `lock_key`     VARCHAR(64)         NOT NULL COMMENT '锁key',
    `guid`         VARCHAR(64)         NOT NULL DEFAULT '' COMMENT '锁',
    `machine_id`   BIGINT(19) UNSIGNED NOT NULL COMMENT '业务无关的机器ID',
    `mtt_sec`      INT                 NOT NULL DEFAULT 7 COMMENT 'Maximum Tolerance Time, 用于指定该lock_key的最大可容忍的失联时间',
    `mpt_sec`      INT                 NOT NULL DEFAULT 5 COMMENT 'Maximum Polling Time, 用于指定follower刷新in-charge分片时间',
    `heartbeat_at` DATETIME            NOT NULL DEFAULT '2000-01-01 00:00:00' COMMENT '当前leader的最后心跳时间',
    `epoch`        BIGINT(19) UNSIGNED NOT NULL COMMENT '纪元',
    `time_zone`    INT                 NOT NULL COMMENT '记录时区',
    PRIMARY KEY (`lock_key`)
);

-- -----------------------------------------------------
-- Table `order`.`t_machine`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `order`.`t_machine`
(
    `id`            BIGINT(19) UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`          VARCHAR(64)         NOT NULL DEFAULT '' COMMENT '机器名称',
    `mtt_sec`       INT                 NOT NULL DEFAULT 7 COMMENT 'Maximum Tolerance Time, 记录当前机器的最大可容忍的失联时间',
    `mpt_sec`       INT                 NOT NULL DEFAULT 5 COMMENT 'Maximum Polling Time, 记录当前机器的刷新in-charge分片时间',
    `partition_set` VARCHAR(128)        NOT NULL DEFAULT '' COMMENT '负责处理的分区',
    `heartbeat_at`  DATETIME            NOT NULL DEFAULT '2000-01-01 00:00:00' COMMENT '心跳时间',
    `time_zone`     INT                 NOT NULL COMMENT '记录时区',
    PRIMARY KEY (`id`),
    INDEX `idx_machine_heartbeat` (`heartbeat_at` ASC),
    UNIQUE INDEX `idx_machine_name` (`name` ASC)
);

-- -----------------------------------------------------
-- Table `order`.`t_order`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `order`.`t_order`
(
    `id`         BIGINT(19) UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id`    BIGINT UNSIGNED     NOT NULL COMMENT '下单用户ID',
    `product_id` BIGINT UNSIGNED     NOT NULL COMMENT '产品ID',
    `price`      INT UNSIGNED        NOT NULL COMMENT '实际支付金额',
    `quantity`   INT UNSIGNED        NOT NULL COMMENT '下单数量',
    `state`      TINYINT UNSIGNED    NOT NULL COMMENT '订单状态',
    `version`    INT UNSIGNED        NOT NULL COMMENT '版本号',
    `partition`  INT                 NOT NULL COMMENT '分区归属',
    `create_at`  DATETIME            NOT NULL COMMENT '创建时间',
    `update_at`  DATETIME            NOT NULL COMMENT '修改时间',
    `expire_at`  DATETIME            NOT NULL COMMENT '失效时间',
    `done_at`    DATETIME            NOT NULL COMMENT '完成时间',
    `time_zone`  INT                 NOT NULL COMMENT '记录时区',
    PRIMARY KEY (`id`),
    INDEX `idx_order_user_id` (`user_id` ASC),
    INDEX `idx_order_state_mid` (`state` ASC, `machine_id` ASC)
);
