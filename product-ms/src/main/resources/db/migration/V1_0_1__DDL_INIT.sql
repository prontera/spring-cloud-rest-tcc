-- -----------------------------------------------------
-- Schema product
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `product`
  DEFAULT CHARACTER SET utf8;
USE `product`;

-- -----------------------------------------------------
-- Table `product`.`t_product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `product`.`t_product` (
  `id`          BIGINT(19) UNSIGNED NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME            NOT NULL DEFAULT '1970-01-01 00:00:00',
  `delete_time` DATETIME            NOT NULL DEFAULT '1970-01-01 00:00:00',
  `name`        VARCHAR(45)         NOT NULL
  COMMENT '商品名',
  `stock`       INT UNSIGNED        NOT NULL DEFAULT 10000000
  COMMENT '库存',
  `price`       INT UNSIGNED        NOT NULL DEFAULT 0
  COMMENT '售价',
  PRIMARY KEY (`id`)
);

-- -----------------------------------------------------
-- Table `product`.`t_product_stock_tcc`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `product`.`t_product_stock_tcc` (
  `id`           BIGINT(19) UNSIGNED NOT NULL AUTO_INCREMENT,
  `create_time`  DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `delete_time`  DATETIME            NOT NULL DEFAULT '1970-01-01 00:00:00',
  `expire_time`  DATETIME            NOT NULL DEFAULT '1970-01-01 00:00:00',
  `stock`        INT UNSIGNED        NOT NULL
  COMMENT '预留资源数量',
  `status`       TINYINT UNSIGNED    NOT NULL
  COMMENT '0为try, 1为confirm完成',
  `t_product_id` BIGINT(19) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_t_product_tcc_status_exptime` (`status` ASC, `expire_time` ASC)
);