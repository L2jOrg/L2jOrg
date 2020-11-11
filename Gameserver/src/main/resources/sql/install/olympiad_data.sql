DROP TABLE IF EXISTS `olympiad_data`;
CREATE TABLE IF NOT EXISTS `olympiad_data`
(
    `id`                 TINYINT UNSIGNED   NOT NULL DEFAULT 0,
    `current_cycle`      INT UNSIGNED       NOT NULL DEFAULT 1,
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


DROP TABLE IF EXISTS olympiad_history;
CREATE TABLE olympiad_history
(
    `player_id`           INT UNSIGNED NOT NULL,
    `server`              INT          NOT NULL,
    `cycle`               INT UNSIGNED NOT NULL,
    `class_id`            INT          NOT NULL,
    `points`              INT          NOT NULL,
    `battles`             INT          NOT NULL,
    `battles_won`         INT          NOT NULL,
    `battles_lost`        INT          NOT NULL,
    `overall_rank`        INT          NOT NULL,
    `overall_count`       INT          NOT NULL,
    `overall_class_rank`  INT          NOT NULL,
    `overall_class_count` INT          NOT NULL,
    `server_class_rank`   INT          NOT NULL,
    `server_class_count`  INT          NOT NULL,
    PRIMARY KEY (`player_id`, `server`, `cycle`),
    FOREIGN KEY (`player_id`) REFERENCES characters(`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;


DROP TABLE IF EXISTS olympiad_rankers_snapshot;
CREATE TABLE olympiad_rankers_snapshot(
    `player_id` INT UNSIGNED NOT NULL,
    `server` INT NOT NULL,
    `player_name` VARCHAR(35) NOT NULL,
    `clan_name` VARCHAR(45) NOT NULL,
    `rank` BIGINT NOT NULL,
    `previous_rank` BIGINT NOT NULL,
    `level` TINYINT NOT NULL,
    `class_id` TINYINT NOT NULL,
    `clan_level` INT NOT NULL,
    `battles_won` MEDIUMINT NOT NULL,
    `battles_lost` MEDIUMINT NOT NULL,
    `points` INT NOT NULL,
    `hero_count` INT NOT NULL,
    `legend_count` INT NOT NULL,
    PRIMARY KEY (`player_id`, `server`)
) ENGINE = InnoDB
 DEFAULT CHARSET = UTF8MB4;

DROP TABLE IF EXISTS olympiad_rankers_class_snapshot;
CREATE TABLE olympiad_rankers_class_snapshot(
  `player_id` INT UNSIGNED NOT NULL,
  `server` INT NOT NULL,
  `player_name` VARCHAR(35) NOT NULL,
  `clan_name` VARCHAR(45) NOT NULL,
  `rank` BIGINT NOT NULL,
  `previous_rank` BIGINT NOT NULL,
  `level` TINYINT NOT NULL,
  `class_id` TINYINT NOT NULL,
  `clan_level` INT NOT NULL,
  `battles_won` MEDIUMINT NOT NULL,
  `battles_lost` MEDIUMINT NOT NULL,
  `points` INT NOT NULL,
  `hero_count` INT NOT NULL,
  `legend_count` INT NOT NULL,
  PRIMARY KEY (`player_id`, `server`)
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;


CREATE OR REPLACE VIEW olympiad_rankers AS
SELECT
       participant.player_id,
       participant.server,
       player.char_name AS player_name,
       IFNULL(clan.clan_name, '') AS clan_name,
       RANK() over (ORDER BY participant.points DESC) AS `rank`,
       IFNULL(snapshot.`rank`, 0) AS previous_rank,
       player.level,
       player.classid AS class_id,
       IFNULL(clan.clan_level, 0) AS clan_level,
       participant.battles_won,
       participant.battles_lost,
       participant.points,
       IFNULL(hero.count, 0) AS hero_count,
       0 AS legend_count
FROM olympiad_participants participant
JOIN characters player ON player.charId = participant.player_id
LEFT JOIN olympiad_rankers_snapshot snapshot on participant.player_id = snapshot.player_id and participant.server = snapshot.server
LEFT JOIN clan_data clan ON clan.clan_id = player.clanid
LEFT JOIN heroes hero on player.charId = hero.charId;

CREATE OR REPLACE VIEW olympiad_rankers_class AS
SELECT
    participant.player_id,
    participant.server,
    player.char_name AS player_name,
    IFNULL(clan.clan_name, '') AS clan_name,
    RANK() over (PARTITION BY player.classid ORDER BY participant.points DESC) AS `rank`,
    IFNULL(snapshot.`rank`, 0) AS previous_rank,
    player.level,
    player.classid AS class_id,
    IFNULL(clan.clan_level, 0) AS clan_level,
    participant.battles_won,
    participant.battles_lost,
    participant.points,
    IFNULL(hero.count, 0) AS hero_count,
    0 AS legend_count
FROM olympiad_participants participant
JOIN characters player on participant.player_id = player.charId
LEFT JOIN olympiad_rankers_class_snapshot snapshot on participant.player_id = snapshot.player_id and participant.server = snapshot.server
LEFT JOIN clan_data clan on clan.clan_id  = player.clanid
LEFT JOIN heroes hero on player.charId = hero.charId;