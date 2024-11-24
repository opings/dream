CREATE TABLE `id_segment`
(
    `id`                  bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `biz_tag`             varchar(50) NOT NULL COMMENT '业务标识',
    `max_id`              bigint(20) NOT NULL COMMENT '分配的id号段的最大值',
    `p_step`              bigint(20) NOT NULL COMMENT '步长',
    `last_update_time`    datetime    NOT NULL COMMENT '上一次最后更新时间',
    `current_update_time` datetime    NOT NULL COMMENT '当前更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_biz_tag` (`biz_tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='号段存储表';

insert into deep_core.id_segment (biz_tag, max_id, p_step, last_update_time, current_update_time)
values ('test', 0, 100, now(), now());


create table count.expression_rule
(
    id              bigint unsigned auto_increment comment 'primary key',
    rule_group      varchar(16)  default ''                   not null comment 'rule group',
    rule_code       varchar(32)  default ''                   not null comment 'rule code',
    rule_name       varchar(64)  default ''                   not null comment 'rule name',
    rule_desc       varchar(128) default ''                   not null comment 'rule desc',
    priority        bigint(20)   default 1                    not null comment 'priority',
    when_expression varchar(512) default ''                   not null comment ' when expression ',
    then_expression varchar(512) default ''                   not null comment ' then expression ',
    db_create_time  datetime(3)  default CURRENT_TIMESTAMP(3) not null comment ' data create time ',
    db_modify_time  datetime(3)  default CURRENT_TIMESTAMP(3) not null on update CURRENT_TIMESTAMP(3) comment ' data latest update time ',
    deleted         tinyint      default 0                    not null comment ' 1:deleted 0:normal ',
    PRIMARY KEY (`id`),
    UNIQUE KEY `rule_group_code` (`rule_group`, rule_code)
) comment ' expression rule ';


insert into expression_rule ( rule_group,
                              rule_code,
                              rule_name,
                              rule_desc,
                              priority,
                              when_expression,
                              then_expression)
values ('TEST',
        'TEST2',
        'TEST2',
        'TEST2',
        2,
        'fact.count<10',
        'result.match=true;result.rejectRuleCode=''TEST2'';result.errorCode=''TEST2'';result.errorMsg=''TEST2'';');




