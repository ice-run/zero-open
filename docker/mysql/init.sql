SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS `zero_open` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

CREATE USER IF NOT EXISTS 'zero'@'%' IDENTIFIED BY 'zero';

GRANT ALL PRIVILEGES ON `zero_open`.* TO 'zero'@'%';

FLUSH PRIVILEGES;

USE `zero_open`;

-- \org\springframework\security\oauth2\server\authorization\client\oauth2-registered-client-schema.sql
CREATE TABLE oauth2_registered_client
(
    id                            varchar(100)                            NOT NULL,
    client_id                     varchar(100)                            NOT NULL,
    client_id_issued_at           timestamp     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret                 varchar(200)  DEFAULT NULL,
    client_secret_expires_at      timestamp     DEFAULT NULL,
    client_name                   varchar(200)                            NOT NULL,
    client_authentication_methods varchar(1000)                           NOT NULL,
    authorization_grant_types     varchar(1000)                           NOT NULL,
    redirect_uris                 varchar(1000) DEFAULT NULL,
    post_logout_redirect_uris     varchar(1000) DEFAULT NULL,
    scopes                        varchar(1000)                           NOT NULL,
    client_settings               varchar(2000)                           NOT NULL,
    token_settings                varchar(2000)                           NOT NULL,
    PRIMARY KEY (id)
);

-- \org\springframework\security\oauth2\server\authorization\oauth2-authorization-schema.sql
/*
IMPORTANT:
    If using PostgreSQL, update ALL columns defined with 'blob' to 'text',
    as PostgreSQL does not support the 'blob' data type.
*/
CREATE TABLE oauth2_authorization
(
    id                            varchar(100) NOT NULL,
    registered_client_id          varchar(100) NOT NULL,
    principal_name                varchar(200) NOT NULL,
    authorization_grant_type      varchar(100) NOT NULL,
    authorized_scopes             varchar(1000) DEFAULT NULL,
    attributes                    blob          DEFAULT NULL,
    state                         varchar(500)  DEFAULT NULL,
    authorization_code_value      blob          DEFAULT NULL,
    authorization_code_issued_at  timestamp     DEFAULT NULL,
    authorization_code_expires_at timestamp     DEFAULT NULL,
    authorization_code_metadata   blob          DEFAULT NULL,
    access_token_value            blob          DEFAULT NULL,
    access_token_issued_at        timestamp     DEFAULT NULL,
    access_token_expires_at       timestamp     DEFAULT NULL,
    access_token_metadata         blob          DEFAULT NULL,
    access_token_type             varchar(100)  DEFAULT NULL,
    access_token_scopes           varchar(1000) DEFAULT NULL,
    oidc_id_token_value           blob          DEFAULT NULL,
    oidc_id_token_issued_at       timestamp     DEFAULT NULL,
    oidc_id_token_expires_at      timestamp     DEFAULT NULL,
    oidc_id_token_metadata        blob          DEFAULT NULL,
    refresh_token_value           blob          DEFAULT NULL,
    refresh_token_issued_at       timestamp     DEFAULT NULL,
    refresh_token_expires_at      timestamp     DEFAULT NULL,
    refresh_token_metadata        blob          DEFAULT NULL,
    user_code_value               blob          DEFAULT NULL,
    user_code_issued_at           timestamp     DEFAULT NULL,
    user_code_expires_at          timestamp     DEFAULT NULL,
    user_code_metadata            blob          DEFAULT NULL,
    device_code_value             blob          DEFAULT NULL,
    device_code_issued_at         timestamp     DEFAULT NULL,
    device_code_expires_at        timestamp     DEFAULT NULL,
    device_code_metadata          blob          DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE `rbac_user`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `username`    varchar(32)     NOT NULL COMMENT '用户名',
    `password`    varchar(128)    NOT NULL COMMENT '密码',
    `create_time` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `valid`       tinyint(1)      NOT NULL DEFAULT '1' COMMENT '是否有效：0 无效，1 有效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='用户';


CREATE TABLE `rbac_role`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `code`        varchar(64)     NOT NULL COMMENT 'code',
    `name`        varchar(64)     NOT NULL COMMENT '名称',
    `create_time` datetime                 DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                 DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `valid`       tinyint(1)      NOT NULL DEFAULT '1',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_code` (`code`),
    UNIQUE KEY `idx_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='角色';


CREATE TABLE `rbac_permission`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `code`        varchar(64)     NOT NULL COMMENT 'code',
    `name`        varchar(64)     NOT NULL COMMENT '名称',
    `create_time` datetime                 DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                 DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `valid`       tinyint(1)      NOT NULL DEFAULT '1' COMMENT '是否有效：1有效，0无效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_code` (`code`),
    UNIQUE KEY `idx_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='权限';


CREATE TABLE `rbac_user_role`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_id`     bigint          NOT NULL COMMENT '用户 id',
    `role_id`     bigint          NOT NULL COMMENT '角色 id',
    `create_time` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `valid`       tinyint(1)      NOT NULL DEFAULT '1' COMMENT '是否有效：0 无效，1 有效',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`),
    UNIQUE KEY `idx_user_id_role_id` (`user_id`, `role_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='用户角色';


CREATE TABLE `rbac_role_permission`
(
    `id`            bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `role_id`       bigint          NOT NULL COMMENT '角色 id',
    `permission_id` bigint          NOT NULL COMMENT '权限 id',
    `create_time`   datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `valid`         tinyint(1)      NOT NULL DEFAULT '1' COMMENT '是否有效：0 无效，1 有效',
    PRIMARY KEY (`id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_permission_id` (`permission_id`),
    UNIQUE KEY `idx_role_id_permission_id` (`role_id`, `permission_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='角色权限';

CREATE TABLE `rbac_group`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `parent_id`   bigint unsigned NOT NULL DEFAULT '0' COMMENT '父级 id，0 表示顶级',
    `name`        varchar(64)     NOT NULL COMMENT '名称',
    `admin_id`    bigint unsigned NOT NULL COMMENT '管理员 id',
    `create_time` datetime                 DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                 DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `valid`       tinyint(1)      NOT NULL DEFAULT '1' COMMENT '是否有效：1有效，0无效',
    PRIMARY KEY (`id`),
    KEY `parent_id` (`parent_id`),
    UNIQUE KEY `name` (`name`),
    KEY `admin_id` (`admin_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='组织架构';

INSERT INTO rbac_user (username, password, create_time, update_time, valid)
VALUES ('admin', '{bcrypt}$2a$10$mATi3BkTD59cTEeTvBCiduGzoB2PooohWUjerUfi2jIfRpQj/7E1a', DEFAULT, DEFAULT, DEFAULT);
INSERT INTO rbac_role (code, name, create_time, update_time, valid)
VALUES ('admin', '超级管理', DEFAULT, DEFAULT, DEFAULT);
INSERT INTO rbac_permission (code, name, create_time, update_time, valid)
VALUES ('admin', '超级管理', DEFAULT, DEFAULT, DEFAULT);
INSERT INTO rbac_user_role (user_id, role_id, create_time, update_time, valid)
VALUES (1, 1, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO rbac_role_permission (role_id, permission_id, create_time, update_time, valid)
VALUES (1, 1, DEFAULT, DEFAULT, DEFAULT);

CREATE TABLE IF NOT EXISTS `zero_open`.`file_info`
(
    `id`          varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL COMMENT 'id',
    `code`        varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL COMMENT 'code',
    `name`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL COMMENT '文件名',
    `origin`      varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '源文件名',
    `type`        varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文件类型',
    `size`        bigint unsigned                                        NOT NULL COMMENT '文件大小',
    `path`        varchar(16) COLLATE utf8mb4_bin                        NOT NULL COMMENT '文件路径',
    `create_time` datetime                                               NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                                               NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `valid`       tinyint(1)                                             NOT NULL DEFAULT '1' COMMENT '是否有效：1有效，0无效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `code` (`code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin
    COMMENT ='文件信息';


CREATE TABLE IF NOT EXISTS `zero_open`.`dict_code`
(
    `id`          bigint unsigned                 NOT NULL AUTO_INCREMENT COMMENT 'id',
    `code`        varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '编码',
    `name`        varchar(32) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '名称',
    `key`         varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '键',
    `value`       varchar(64) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '值',
    `sort`        int                             NOT NULL DEFAULT '1' COMMENT '排序，默认值 1，数值越小，排序越前',
    `note`        varchar(64) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '注释',
    `create_time` datetime                        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `valid`       tinyint(1)                      NOT NULL DEFAULT '1' COMMENT '是否有效：0 无效，1 有效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_code_key` (`code`, `key`),
    KEY `idx_code` (`code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='数据字典';

INSERT INTO zero_open.dict_code (id, code, name, `key`, value, sort, note, create_time, update_time, valid)
VALUES (1, 'demo', '示例', 'cat', '猫', 1, '喵喵', '2025-01-01 00:00:00', '2025-01-01 00:00:00', 1);

INSERT INTO zero_open.dict_code (id, code, name, `key`, value, sort, note, create_time, update_time, valid)
VALUES (2, 'demo', '示例', 'dog', '狗', 2, '汪汪', '2025-01-01 00:00:00', '2025-01-01 00:00:00', 1);

