-- -----------------------------------------------------
-- Schema membership
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `membership`
  DEFAULT CHARACTER SET utf8;
USE `membership`;

-- -----------------------------------------------------
-- Table `membership`.`t_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `membership`.`t_point` (
  `id`          BIGINT(19) UNSIGNED NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `delete_time` DATETIME            NOT NULL DEFAULT '1970-01-01 00:00:00',
  `pts`         INT UNSIGNED        NOT NULL
  COMMENT '赠送积分数',
  `user_id`     BIGINT UNSIGNED     NOT NULL
  COMMENT '用户ID',
  `product_id`  BIGINT UNSIGNED     NOT NULL
  COMMENT '产品ID',
  PRIMARY KEY (`id`)
);
