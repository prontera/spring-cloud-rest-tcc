-- -----------------------------------------------------
-- Schema account
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `account`
  DEFAULT CHARACTER SET utf8;
USE `account`;

-- -----------------------------------------------------
-- Table `account`.`t_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `account`.`t_user` (
  `id`          BIGINT(19) UNSIGNED NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `delete_time` DATETIME            NOT NULL DEFAULT '1970-01-01 00:00:00',
  `mobile`      VARCHAR(20)         NOT NULL
  COMMENT '手机号',
  `login_pwd`   VARCHAR(128)        NOT NULL
  COMMENT '登录密码',
  `pwd_salt`    VARCHAR(128)        NOT NULL
  COMMENT '密码盐',
  `balance`     BIGINT(19)          NOT NULL DEFAULT 100000000
  COMMENT '余额, 单位分',
  UNIQUE INDEX `uni_user_mobile` (`mobile` ASC),
  PRIMARY KEY (`id`)
);

-- -----------------------------------------------------
-- Table `account`.`t_user_balance_tcc`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `account`.`t_user_balance_tcc` (
  `id`          BIGINT(19) UNSIGNED NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `delete_time` DATETIME            NOT NULL DEFAULT '1970-01-01 00:00:00',
  `expire_time` DATETIME            NOT NULL DEFAULT '1970-01-01 00:00:00',
  `amount`      BIGINT              NOT NULL
  COMMENT '预留资源金额',
  `status`      TINYINT             NOT NULL DEFAULT 0
  COMMENT '0为try, 1为confirm完成',
  `t_user_id`   BIGINT(19) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_t_user_tcc_status_exptime` (`status` ASC, `expire_time` ASC)
);