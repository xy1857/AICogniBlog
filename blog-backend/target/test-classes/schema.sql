-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`      VARCHAR(50)     NOT NULL COMMENT '用户名（登录账号）',
    `password_hash` VARCHAR(255)    NOT NULL COMMENT '密码哈希（BCrypt）',
    `email`         VARCHAR(100)    NOT NULL COMMENT '邮箱',
    `nickname`      VARCHAR(50)     DEFAULT NULL COMMENT '昵称',
    `avatar_base64` MEDIUMTEXT      DEFAULT NULL COMMENT '头像 Base64',
    `bio`           VARCHAR(200)    DEFAULT NULL COMMENT '个人简介',
    `role`          TINYINT         NOT NULL DEFAULT 0 COMMENT '角色: 0=普通用户 1=管理员',
    `status`        TINYINT         NOT NULL DEFAULT 1 COMMENT '状态: 0=禁用 1=启用',
    `last_login_at` DATETIME        DEFAULT NULL COMMENT '最后登录时间',
    `deleted`       TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0=正常 1=已删除',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 分类表
CREATE TABLE IF NOT EXISTS `category` (
    `id`         INT          NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name`       VARCHAR(50)  NOT NULL COMMENT '分类名称',
    `slug`       VARCHAR(50)  NOT NULL COMMENT '分类别名（URL友好）',
    `deleted`    TINYINT      NOT NULL DEFAULT 0,
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章分类表';

-- 标签表
CREATE TABLE IF NOT EXISTS `tag` (
    `id`         INT         NOT NULL AUTO_INCREMENT COMMENT '标签ID',
    `name`       VARCHAR(30) NOT NULL COMMENT '标签名称',
    `deleted`    TINYINT     NOT NULL DEFAULT 0,
    `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 文章表
CREATE TABLE IF NOT EXISTS `article` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '文章ID',
    `author_id`    BIGINT       NOT NULL COMMENT '作者ID',
    `category_id`  INT          DEFAULT NULL COMMENT '分类ID',
    `title`        VARCHAR(200) NOT NULL COMMENT '文章标题',
    `summary`      VARCHAR(500) DEFAULT NULL COMMENT '文章摘要',
    `content_md`   LONGTEXT     NOT NULL COMMENT 'Markdown 原文',
    `content_html` LONGTEXT     NOT NULL COMMENT '渲染后的 HTML',
    `cover_url`    VARCHAR(500) DEFAULT NULL COMMENT '封面图URL',
    `status`       TINYINT      NOT NULL DEFAULT 0 COMMENT '状态: 0=草稿 1=已发布',
    `view_count`   INT          NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `deleted`      TINYINT      NOT NULL DEFAULT 0,
    `published_at` DATETIME     DEFAULT NULL COMMENT '发布时间',
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FULLTEXT KEY `ft_title_content` (`title`, `content_md`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- 文章-标签关联表
CREATE TABLE IF NOT EXISTS `article_tag` (
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `tag_id`     INT    NOT NULL COMMENT '标签ID',
    PRIMARY KEY (`article_id`, `tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签关联表';

-- 评论表
CREATE TABLE IF NOT EXISTS `comment` (
    `id`                  BIGINT   NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `article_id`          BIGINT   NOT NULL COMMENT '文章ID',
    `user_id`             BIGINT   NOT NULL COMMENT '评论者ID',
    `parent_id`           BIGINT   DEFAULT NULL COMMENT '父评论ID，NULL表示顶级评论',
    `content`             TEXT     NOT NULL COMMENT '评论内容',
    `status`              TINYINT  NOT NULL DEFAULT 0 COMMENT '状态: 0=待审核 1=已发布 2=已拒绝',
    `ai_audit_result`     TINYINT  DEFAULT NULL COMMENT 'AI审核结果: 0=不安全 1=安全',
    `ai_reply_suggestion` TEXT     DEFAULT NULL COMMENT 'AI回复建议',
    `deleted`             TINYINT  NOT NULL DEFAULT 0,
    `created_at`          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_article_id` (`article_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 文章点赞表
CREATE TABLE IF NOT EXISTS `article_like` (
    `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
    `article_id`  BIGINT   NOT NULL COMMENT '文章ID',
    `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    PRIMARY KEY (`user_id`, `article_id`),
    KEY `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章点赞表';

-- 浏览足迹表（每用户每文章一条，按最近浏览时间更新）
CREATE TABLE IF NOT EXISTS `browse_history` (
    `user_id`    BIGINT   NOT NULL COMMENT '用户ID',
    `article_id` BIGINT   NOT NULL COMMENT '文章ID',
    `viewed_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '浏览时间',
    PRIMARY KEY (`user_id`, `article_id`),
    KEY `idx_user_viewed` (`user_id`, `viewed_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户浏览足迹';

-- 订阅表（分类/标签）
CREATE TABLE IF NOT EXISTS `subscription` (
    `id`         BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`    BIGINT   NOT NULL COMMENT '用户ID',
    `target_type` VARCHAR(20) NOT NULL COMMENT '类型: category / tag',
    `target_id`  BIGINT   NOT NULL COMMENT '目标ID（分类ID或标签ID）',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户订阅';

-- 关注表
CREATE TABLE IF NOT EXISTS `follow` (
    `follower_id`  BIGINT   NOT NULL COMMENT '关注者ID',
    `following_id` BIGINT   NOT NULL COMMENT '被关注者ID',
    `created_at`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`follower_id`, `following_id`),
    KEY `idx_follower` (`follower_id`),
    KEY `idx_following` (`following_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注';

