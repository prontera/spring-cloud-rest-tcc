-- -----------------------------------------------------
-- Schema order
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `order`
  DEFAULT CHARACTER SET utf8;
USE `order`;

-- -----------------------------------------------------
-- Table `order`.`t_order`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `order`.`t_order` (
  `id`          BIGINT(19) UNSIGNED NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME            NOT NULL DEFAULT '1970-01-01 00:00:00',
  `delete_time` DATETIME            NOT NULL DEFAULT '1970-01-01 00:00:00',
  `user_id`     BIGINT UNSIGNED     NOT NULL
  COMMENT '下单用户ID',
  `product_id`  BIGINT UNSIGNED     NOT NULL
  COMMENT '产品ID',
  `price`       INT UNSIGNED        NOT NULL
  COMMENT '实际支付金额',
  `status`      TINYINT UNSIGNED    NOT NULL DEFAULT 0
  COMMENT '订单状态, 0为支付中, 1为交易完成, 2为全部资源已被撤销, 3为资源确认冲突',
  PRIMARY KEY (`id`),
  INDEX `idx_order_user_id_ct` (`user_id` ASC, `create_time` ASC)
);

-- -----------------------------------------------------
-- Table `order`.`t_order_conflict`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `order`.`t_order_conflict` (
  `id`           BIGINT(19) UNSIGNED NOT NULL AUTO_INCREMENT,
  `create_time`  DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `delete_time`  DATETIME            NOT NULL DEFAULT '1970-01-01 00:00:00',
  `error_detail` VARCHAR(4096)       NOT NULL
  COMMENT '资源冲突时的详细记录, 留作人工处理',
  `t_order_id`   BIGINT(19) UNSIGNED NOT NULL
  COMMENT '订单ID',
  PRIMARY KEY (`id`)
);

-- -----------------------------------------------------
-- Table `order`.`t_order_participant`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `order`.`t_order_participant` (
  `id`          BIGINT(19) UNSIGNED NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `delete_time` DATETIME            NOT NULL DEFAULT '1970-01-01 00:00:00',
  `expire_time` DATETIME            NOT NULL
  COMMENT '预留资源过期时间',
  `uri`         VARCHAR(255)        NOT NULL
  COMMENT '预留资源确认URI',
  `t_order_id`  BIGINT(19) UNSIGNED NOT NULL
  COMMENT '订单ID',
  PRIMARY KEY (`id`),
  INDEX `idx_order_participant_id` (`t_order_id` ASC)
);
