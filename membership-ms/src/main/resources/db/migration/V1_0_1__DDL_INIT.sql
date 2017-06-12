-- -----------------------------------------------------
-- Schema membership
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `membership`
  DEFAULT CHARACTER SET utf8;
USE `membership`;

-- -----------------------------------------------------
-- Table `membership`.`t_point_flow`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `membership`.`t_point_flow` (
  `id`          BIGINT(19) UNSIGNED NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `delete_time` DATETIME            NOT NULL DEFAULT '1970-01-01 00:00:00',
  `point`       INT UNSIGNED        NOT NULL
  COMMENT '赠送积分数',
  `order_id`    BIGINT UNSIGNED     NOT NULL DEFAULT 0
  COMMENT '订单ID',
  `user_id`     BIGINT UNSIGNED     NOT NULL
  COMMENT '用户ID',
  `product_id`  BIGINT UNSIGNED     NOT NULL
  COMMENT '产品ID',
  PRIMARY KEY (`id`),
  INDEX `idx_point_flow_user_id` (`user_id` ASC),
  INDEX `idx_point_flow_order_id` (`order_id` ASC)
);

-- -----------------------------------------------------
-- Table `membership`.`t_point_sum`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `membership`.`t_point_sum` (
  `id`          BIGINT(19) UNSIGNED NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `delete_time` DATETIME            NOT NULL DEFAULT '1970-01-01 00:00:00',
  `user_id`     BIGINT UNSIGNED     NOT NULL
  COMMENT '用户ID',
  `point_sum`   INT UNSIGNED        NOT NULL DEFAULT 0
  COMMENT '总积分数',
  PRIMARY KEY (`id`),
  INDEX `idx_point_sum_user_id` (`user_id` ASC)
);
