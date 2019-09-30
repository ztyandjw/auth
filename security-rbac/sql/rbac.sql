SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


CREATE TABLE `sec_user`
(
  `id`          bigint(64)  NOT NULL COMMENT '主键',
  `username`    varchar(64) NOT NULL COMMENT '账号名',
  `password`    varchar(64) NOT NULL COMMENT '密码',
  `enable`      tinyint(1)      NOT NULL DEFAULT 1 COMMENT '1 启用, 0 禁用',
  `no_expired`      tinyint(1)      NOT NULL DEFAULT 1 COMMENT '1 账号没有过期，0 账号已经过期',
  `credential_no_expired`      tinyint(1)      NOT NULL DEFAULT 1 COMMENT '1 密码没有过期，0 密码已经过期',
  `no_lock`      tinyint(1)      NOT NULL DEFAULT 1 COMMENT '1 账号没有被锁定，0 账号被锁定',
  `app_id` int(11) NOT NULL COMMENT '应用id, 0 云平台, 1 ivt, 2 数字资产',
  `profile_id` bigint(64)  NOT NULL COMMENT '概要id',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0 没有删除, 1 已经删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(11) NOT NULL DEFAULT '0' COMMENT '更新用户id',
  `create_user` varchar(11) NOT NULL DEFAULT '0' COMMENT '创建用户id',
  PRIMARY KEY (`id`)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='用户表';




CREATE TABLE `sec_user_profile`
(
  `id`          bigint(64)  NOT NULL COMMENT '主键',
  `username`    varchar(50) NOT NULL COMMENT '账号名',
  `en_name`    varchar(255)         DEFAULT NULL COMMENT '英文名称',
  `cn_name`    varchar(255)         DEFAULT NULL COMMENT '中文名称',
  `phone`       varchar(11)          DEFAULT NULL COMMENT '手机',
  `email`       varchar(50)          DEFAULT NULL COMMENT '邮箱',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0 没有删除, 1 已经删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(11) NOT NULL DEFAULT '0' COMMENT '更新用户id',
  `create_user` varchar(11) NOT NULL DEFAULT '0' COMMENT '创建用户id',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='用户概要表';


CREATE TABLE `sec_role`
(
  `id`          bigint(64)  NOT NULL COMMENT '主键',
  `role_name`    varchar(50) NOT NULL COMMENT '角色名称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(11) NOT NULL DEFAULT '0' COMMENT '更新用户id',
  `create_user` varchar(11) NOT NULL DEFAULT '0' COMMENT '创建用户id',
  `app_id` int(11) NOT NULL COMMENT '应用id, 0 云平台, 1 ivt, 2 数字资产',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0 没有删除, 1 已经删除',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='角色表';


  CREATE TABLE `sec_user_role`
(
  `user_id`          bigint(64)  NOT NULL COMMENT '用户id',
  `role_id`          bigint(64)  NOT NULL COMMENT '用户id',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(11) NOT NULL DEFAULT '0' COMMENT '更新用户id',
  `create_user` varchar(11) NOT NULL DEFAULT '0' COMMENT '创建用户id',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0 没有删除, 1 已经删除',
  PRIMARY KEY (`user_id`, `role_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='用户角色关系表';


CREATE TABLE `sec_resource`
(
  `id`          bigint(64)  NOT NULL COMMENT '主键',
  `name`    varchar(64) NOT NULL COMMENT '资源名称',
  `url`        varchar(1000) DEFAULT NULL COMMENT 'url路径',
  `type`       int(2)      NOT NULL COMMENT '权限类型，前端路由-1，前端按钮-2， 后端API-3',
  `permission` varchar(64)   DEFAULT NULL COMMENT '权限表达式',
  `method`     varchar(8)   DEFAULT NULL COMMENT 'http方法',
  `app_id` int(11) NOT NULL COMMENT '应用id, 0 云平台, 1 ivt, 2 数字资产',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0 没有删除, 1 已经删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(11) NOT NULL DEFAULT '0' COMMENT '更新用户id',
  `create_user` varchar(11) NOT NULL DEFAULT '0' COMMENT '创建用户id',
  PRIMARY KEY (`id`)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='资源表';


CREATE TABLE `sec_role_resource`
(
  `role_id`          bigint(64)  NOT NULL COMMENT '角色id',
  `resource_id`          bigint(64)  NOT NULL COMMENT '资源id',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(11) NOT NULL DEFAULT '0' COMMENT '更新用户id',
  `create_user` varchar(11) NOT NULL DEFAULT '0' COMMENT '创建用户id',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0 没有删除, 1 已经删除',
    PRIMARY KEY (`role_id`, `resource_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='角色资源关系表';