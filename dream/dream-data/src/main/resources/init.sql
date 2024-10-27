CREATE TABLE `id_segment` (
                              `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
                              `biz_tag` varchar(50) NOT NULL COMMENT '业务标识',
                              `max_id` bigint(20) NOT NULL COMMENT '分配的id号段的最大值',
                              `p_step` bigint(20) NOT NULL COMMENT '步长',
                              `last_update_time` datetime NOT NULL COMMENT '上一次最后更新时间',
                              `current_update_time` datetime NOT NULL COMMENT '当前更新时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_biz_tag` (`biz_tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='号段存储表';

insert into deep_core.id_segment (biz_tag, max_id, p_step, last_update_time, current_update_time) values ('test', 0, 100, now(), now());

