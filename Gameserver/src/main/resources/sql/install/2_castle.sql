DROP TABLE IF EXISTS `castle_functions`;
DROP TABLE IF EXISTS `castle`;

CREATE TABLE IF NOT EXISTS `castle`
(
    `id`                          INT                             NOT NULL,
    `name`                        VARCHAR(25)                     NOT NULL,
    `side`                        ENUM ('NEUTRAL','LIGHT','DARK') NOT NULL DEFAULT 'NEUTRAL',
    `treasury`                    BIGINT                          NOT NULL DEFAULT 0,
    `siege_date`                  DATETIME,
    `siege_time_registration_end` DATETIME,
    `show_npc_crest`              BOOLEAN                         NOT NULL DEFAULT FALSE,
    `ticket_buy_count`            SMALLINT                        NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8MB4;

INSERT IGNORE INTO `castle`(`id`, `name`)
VALUES (3, 'Giran');

CREATE TABLE IF NOT EXISTS `castle_functions`
(
    `castle_id` INT                 NOT NULL,
    `type`      int(1)              NOT NULL DEFAULT '0',
    `level`     int(3)              NOT NULL DEFAULT '0',
    `lease`     int(10)             NOT NULL DEFAULT '0',
    `rate`      decimal(20, 0)      NOT NULL DEFAULT '0',
    `endTime`   bigint(13) unsigned NOT NULL DEFAULT '0',
    PRIMARY KEY (`castle_id`, `type`),
    FOREIGN KEY FK_CASTLE (`castle_id`) REFERENCES castle (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8MB4;