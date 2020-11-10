DROP TABLE IF EXISTS `olympiad_data`;
CREATE TABLE IF NOT EXISTS `olympiad_data`
(
    `id`                 TINYINT UNSIGNED   NOT NULL DEFAULT 0,
    `current_cycle`      MEDIUMINT UNSIGNED NOT NULL DEFAULT 1,
    `period`             MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
    `olympiad_end`       BIGINT UNSIGNED    NOT NULL DEFAULT '0',
    `validation_end`     BIGINT UNSIGNED    NOT NULL DEFAULT '0',
    `next_weekly_change` BIGINT UNSIGNED    NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;


DROP TABLE IF EXISTS `olympiad_participants`;
CREATE TABLE IF NOT EXISTS `olympiad_participants`
(
    `player_id`     INT UNSIGNED NOT NULL,
    `server`        INT          NOT NULL,
    `points`        MEDIUMINT    NOT NULL DEFAULT 0,
    `battles`       MEDIUMINT    NOT NULL DEFAULT 0,
    `battles_won`   MEDIUMINT    NOT NULL DEFAULT 0,
    `battles_lost`  MEDIUMINT    NOT NULL DEFAULT 0,
    `battles_today` MEDIUMINT    NOT NULL DEFAULT 0,
    PRIMARY KEY (`player_id`, `server`),
    FOREIGN KEY (`player_id`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;