CREATE USER 'chris'
    IDENTIFIED BY '123123';

-- -----------------------------------------------------
-- Schema order
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `order`
    DEFAULT CHARACTER SET utf8mb4;
GRANT ALL ON `order`.* TO 'chris';
USE `order`;

-- -----------------------------------------------------
-- Table `order`.`t_order`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `order`.`t_order`
(
    `id`         BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT,
    `user_id`    BIGINT UNSIGNED  NOT NULL COMMENT '下单用户ID',
    `product_id` BIGINT UNSIGNED  NOT NULL COMMENT '产品ID',
    `price`      INT UNSIGNED     NOT NULL COMMENT '实际支付金额',
    `quantity`   INT UNSIGNED     NOT NULL COMMENT '下单数量',
    `state`      TINYINT UNSIGNED NOT NULL COMMENT '订单状态',
    `guid`       BIGINT UNSIGNED  NOT NULL COMMENT '幂等GUID',
    `create_at`  DATETIME         NOT NULL COMMENT '创建时间',
    `update_at`  DATETIME         NOT NULL COMMENT '修改时间',
    `expire_at`  DATETIME         NOT NULL COMMENT '失效时间',
    `done_at`    DATETIME         NOT NULL DEFAULT '2000-01-01 00:00:00' COMMENT '完成时间',
    PRIMARY KEY (`id`),
    INDEX `idx_order_user_id` (`user_id` ASC),
    INDEX `idx_order_guid` (`guid` ASC)
);

-- -----------------------------------------------------
-- Schema account
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `account`
    DEFAULT CHARACTER SET utf8mb4;
GRANT ALL ON `account`.* TO 'chris';
USE `account`;

-- -----------------------------------------------------
-- Table `account`.`t_account`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `account`.`t_account`
(
    `id`        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`      VARCHAR(20)     NOT NULL COMMENT '用户名',
    `balance`   BIGINT UNSIGNED NOT NULL DEFAULT 100000000 COMMENT '余额, 单位元',
    `create_at` DATETIME        NOT NULL COMMENT '创建时间',
    `update_at` DATETIME        NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uni_account_name` (`name` ASC)
);

-- -----------------------------------------------------
-- Table `account`.`t_account_transaction`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `account`.`t_account_transaction`
(
    `id`        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id`   BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `order_id`  BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
    `amount`    BIGINT          NOT NULL COMMENT '预留资源金额',
    `state`     TINYINT         NOT NULL COMMENT '预留资源状态',
    `create_at` DATETIME        NOT NULL COMMENT '创建时间',
    `update_at` DATETIME        NOT NULL COMMENT '修改时间',
    `expire_at` DATETIME        NOT NULL COMMENT '失效时间',
    `done_at`   DATETIME        NOT NULL DEFAULT '2000-01-01 00:00:00' COMMENT '事务完成时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uni_account_txn_order` (`order_id` ASC)
);

INSERT INTO `account`.`t_account` (`name`, `create_at`, `update_at`)
VALUES ('chris', now(), now());

INSERT INTO `account`.`t_account` (`name`, `create_at`, `update_at`)
VALUES ('scott', now(), now());

INSERT INTO `account`.`t_account` (`name`, `create_at`, `update_at`)
VALUES ('ryan', now(), now());

-- -----------------------------------------------------
-- Schema product
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `product`
    DEFAULT CHARACTER SET utf8mb4;
GRANT ALL ON `product`.* TO 'chris';
USE `product`;

-- -----------------------------------------------------
-- Table `product`.`t_product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `product`.`t_product`
(
    `id`        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`      VARCHAR(20)     NOT NULL COMMENT '产品名',
    `inventory` BIGINT UNSIGNED NOT NULL DEFAULT 9999 COMMENT '库存余量, 单位个',
    `create_at` DATETIME        NOT NULL COMMENT '创建时间',
    `update_at` DATETIME        NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uni_product_name` (`name` ASC)
);

-- -----------------------------------------------------
-- Table `product`.`t_product_transaction`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `product`.`t_product_transaction`
(
    `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `product_id` BIGINT UNSIGNED NOT NULL COMMENT '产品ID',
    `order_id`   BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
    `amount`     BIGINT          NOT NULL COMMENT '预留资源数量',
    `state`      TINYINT         NOT NULL COMMENT '预留资源状态',
    `create_at`  DATETIME        NOT NULL COMMENT '创建时间',
    `update_at`  DATETIME        NOT NULL COMMENT '修改时间',
    `expire_at`  DATETIME        NOT NULL COMMENT '失效时间',
    `done_at`    DATETIME        NOT NULL DEFAULT '2000-01-01 00:00:00' COMMENT '事务完成时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uni_product_txn_order` (`order_id` ASC)
);

INSERT INTO `t_product` (`name`, `create_at`, `update_at`)
VALUES ('gba', now(), now());

INSERT INTO `t_product` (`name`, `create_at`, `update_at`)
VALUES ('ps4', now(), now());

INSERT INTO `t_product` (`name`, `create_at`, `update_at`)
VALUES ('fc', now(), now());
