SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `sec_app`;
CREATE TABLE `sec_app`(
`id`          bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '主键',
`app_name`    varchar(64) NOT NULL COMMENT '应用名称',
`deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0 没有删除, 1 已经删除',
`create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`update_user` varchar(11) DEFAULT 'admin' COMMENT '更新用户id',
`create_user` varchar(11) DEFAULT 'admin' COMMENT '创建用户id',
PRIMARY KEY (`id`) USING BTREE,
UNIQUE KEY `app_name` (`app_name`) USING BTREE
) ENGINE = InnoDB
DEFAULT CHARSET = utf8 COMMENT ='应用表';


DROP TABLE IF EXISTS `sec_user_social`;
CREATE TABLE `sec_user_social`(
  `user_id`   bigint(20)  NOT NULL COMMENT '用户id',
  `app_id`  bigint(20) NOT NULL COMMENT '应用id',
  `provider_type` varchar(11) NOT NULL COMMENT '认证类型',
  `open_id`    varchar(64) NOT NULL COMMENT '针对于某类应用某类认证方式，id唯一',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0 没有删除, 1 已经删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(11) DEFAULT 'admin' COMMENT '更新用户id',
  `create_user` varchar(11) DEFAULT 'admin' COMMENT '创建用户id',
  PRIMARY KEY (`user_id`,`app_id`,`provider_type`, `open_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='用户Social关系表';

DROP TABLE IF EXISTS `sec_user`;
CREATE TABLE `sec_user`
(
  `id`          bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`    varchar(64) NOT NULL COMMENT '账号名',
  `password`    varchar(64) NOT NULL COMMENT '密码',
  `enable`      tinyint(1)      NOT NULL DEFAULT 1 COMMENT '1 启用, 0 禁用',
  `no_expired`      tinyint(1)      NOT NULL DEFAULT 1 COMMENT '1 账号没有过期，0 账号已经过期',
  `credential_no_expired`      tinyint(1)      NOT NULL DEFAULT 1 COMMENT '1 密码没有过期，0 密码已经过期',
  `no_lock`      tinyint(1)      NOT NULL DEFAULT 1 COMMENT '1 账号没有被锁定，0 账号被锁定',
  `en_name`    varchar(255)         DEFAULT NULL COMMENT '英文名称',
  `cn_name`    varchar(255)         DEFAULT NULL COMMENT '中文名称',
  `tel_phone`       varchar(11)          DEFAULT NULL COMMENT '手机',
  `email_address`       varchar(50)          DEFAULT NULL COMMENT '邮箱',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0 没有删除, 1 已经删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(11) DEFAULT 'admin' COMMENT '更新用户id',
  `create_user` varchar(11) DEFAULT 'admin' COMMENT '创建用户id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`) USING BTREE,
  UNIQUE KEY `tel_phone` (`tel_phone`) USING BTREE,
  UNIQUE KEY `email_address` (`email_address`) USING BTREE

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='用户表';

DROP TABLE IF EXISTS `sec_role`;
CREATE TABLE `sec_role`
(
  `id`          bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_name`    varchar(64) NOT NULL COMMENT '角色名称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(11) DEFAULT 'admin' COMMENT '更新用户id',
  `create_user` varchar(11) DEFAULT 'admin' COMMENT '创建用户id',
  `app_id` int(11) NOT NULL COMMENT '应用id, 1 云平台, 2 数字资产',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0 没有删除, 1 已经删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_roleName_appId` (`role_name`, `app_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='角色表';

DROP TABLE IF EXISTS `sec_user_role`;
CREATE TABLE `sec_user_role`
(
  `user_id`          bigint(20)  NOT NULL  COMMENT '用户id',
  `role_id`          bigint(64)  NOT NULL COMMENT '用户id',
  `app_id` int(11) NOT NULL COMMENT '应用id, 1 云平台, 2 数字资产',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(11) DEFAULT 'admin' COMMENT '更新用户id',
  `create_user` varchar(11) DEFAULT 'admin' COMMENT '创建用户id',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0 没有删除, 1 已经删除',
  PRIMARY KEY (`user_id`, `role_id`)
) ENGINE = InnoDB
DEFAULT CHARSET = utf8 COMMENT ='用户角色关系表';

DROP TABLE IF EXISTS `sec_resource`;

CREATE TABLE `sec_resource`
(
  `id`          bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '主键',
  `resource_name`    varchar(64) NOT NULL COMMENT '资源名称',
  `url`        varchar(1000) DEFAULT NULL COMMENT 'url路径',
  `type`       int(2)      NOT NULL COMMENT '权限类型，前端路由-1，前端按钮-2， 后端API-3',
  `permission` varchar(64)   DEFAULT NULL COMMENT '权限表达式',
  `method`     varchar(8)   DEFAULT NULL COMMENT 'http方法',
  `app_id` int(11) NOT NULL COMMENT '应用id, 1 云平台, 2 数字资产',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0 没有删除, 1 已经删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(11) DEFAULT 'admin' COMMENT '更新用户id',
  `create_user` varchar(11) DEFAULT 'admin' COMMENT '创建用户id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `ux_resourceName_appId` (`resource_name`, `app_id`) USING BTREE,
  UNIQUE KEY `ux_permission_appId` (`permission`, `app_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='资源表';

DROP TABLE IF EXISTS `sec_role_resource`;
CREATE TABLE `sec_role_resource`
(
  `role_id`          bigint(20)  NOT NULL  COMMENT '角色id',
  `resource_id`          bigint(20)  NOT NULL COMMENT '资源id',
  `app_id` int(11) NOT NULL COMMENT '应用id, 1 云平台, 2 数字资产',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(11) DEFAULT 'admin' COMMENT '更新用户id',
  `create_user` varchar(11) DEFAULT 'admin' COMMENT '创建用户id',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0 没有删除, 1 已经删除',
  PRIMARY KEY (`role_id`, `resource_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='角色资源关系表';