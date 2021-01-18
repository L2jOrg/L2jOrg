DROP TABLE IF EXISTS `olympiad_data`;
CREATE TABLE IF NOT EXISTS `olympiad_data`
(
    `id`                 TINYINT UNSIGNED   NOT NULL DEFAULT 0,
    `current_cycle`      INT UNSIGNED       NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;


DROP TABLE IF EXISTS `olympiad_participants`;
CREATE TABLE `olympiad_participants`
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

DROP TABLE IF EXISTS `olympiad_matches`;
CREATE TABLE `olympiad_matches`(
   `num` INT NOT NULL AUTO_INCREMENT,
   `player_id`     INT UNSIGNED NOT NULL,
   `server`        INT          NOT NULL,
   `class_id`   INT NOT NULL,
   `opponent`      INT UNSIGNED NOT NULL,
   `date`    DATETIME NOT NULL DEFAULT (CURRENT_TIMESTAMP),
   `duration` INT NOT NULL,
   `result`  ENUM('VICTORY', 'DRAW', 'LOSS'),
   `win`  INT NOT NULL DEFAULT 0,
   `loss` INT NOT NULL DEFAULT 0,
   `tie`  INT NOT NULL DEFAULT 0,
   PRIMARY KEY (`num`),
   FOREIGN KEY (`player_id`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

DROP TABLE IF EXISTS `olympiad_heroes_matches`;
CREATE TABLE `olympiad_heroes_matches`(
   `num` INT NOT NULL,
   `player_id`     INT UNSIGNED NOT NULL,
   `server`        INT          NOT NULL,
   `class_id`   INT NOT NULL,
   `opponent`      INT UNSIGNED NOT NULL,
   `date`    DATETIME NOT NULL DEFAULT (CURRENT_TIMESTAMP),
   `duration` INT NOT NULL,
   `result`  ENUM('VICTORY', 'DRAW', 'LOSS'),
   `win`  INT NOT NULL DEFAULT 0,
   `loss` INT NOT NULL DEFAULT 0,
   `tie`  INT NOT NULL DEFAULT 0,
   PRIMARY KEY (`num`),
   FOREIGN KEY (`player_id`) REFERENCES characters (`charId`) ON DELETE CASCADE,
   KEY(`class_id`)
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
    `rank` BIGINT NOT NULL,
    `previous_rank` BIGINT NOT NULL,
    `class_id` TINYINT NOT NULL,
    `battles` MEDIUMINT NOT NULL,
    `battles_won` MEDIUMINT NOT NULL,
    `battles_lost` MEDIUMINT NOT NULL,
    `points` INT NOT NULL,
    `update_date` DATE NOT NULL,
    `points_claimed` BOOL NOT NULL DEFAULT FALSE,
    PRIMARY KEY (`player_id`, `server`),
    KEY (`rank`)
) ENGINE = InnoDB
 DEFAULT CHARSET = UTF8MB4;

DROP TABLE IF EXISTS olympiad_rankers_class_snapshot;
CREATE TABLE olympiad_rankers_class_snapshot(
  `player_id` INT UNSIGNED NOT NULL,
  `server` INT NOT NULL,
  `rank` BIGINT NOT NULL,
  `previous_rank` BIGINT NOT NULL,
  `class_id` TINYINT NOT NULL,
  `battles` MEDIUMINT NOT NULL,
  `battles_won` MEDIUMINT NOT NULL,
  `battles_lost` MEDIUMINT NOT NULL,
  `points` INT NOT NULL,
  `update_date` DATE NOT NULL,
  PRIMARY KEY (`player_id`, `server`),
  KEY (`class_id`, `rank`)
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

DROP TABLE IF EXISTS olympiad_heroes_history;
CREATE TABLE olympiad_heroes_history (
 `player_id` INT UNSIGNED NOT NULL,
 `server` INT NOT NULL,
 `class_id` TINYINT NOT NULL,
 `hero_count` INT NOT NULL DEFAULT 1,
 `legend_count` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`player_id`, `server`),
  FOREIGN KEY (`player_id`) REFERENCES characters(`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
 DEFAULT CHARSET = UTF8MB4;

DROP TABLE IF EXISTS olympiad_heroes;
CREATE TABLE olympiad_heroes (
 `player_id` INT UNSIGNED NOT NULL,
 `server` INT NOT NULL,
 `class_id` TINYINT NOT NULL,
 `legend` BOOL NOT NULL DEFAULT FALSE,
 `claimed` BOOL NOT NULL DEFAULT FALSE,
 PRIMARY KEY (`player_id`, `server`),
 FOREIGN KEY (`player_id`) REFERENCES characters(`charId`) ON DELETE CASCADE,
 UNIQUE KEY (`class_id`)
)ENGINE = InnoDB
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
       participant.battles,
       participant.battles_won,
       participant.battles_lost,
       participant.points,
       IFNULL(hero.hero_count, 0) AS hero_count,
       IFNULL(hero.legend_count, 0) AS legend_count
FROM olympiad_participants participant
JOIN characters player ON player.charId = participant.player_id -- AND player.accesslevel = 0
LEFT JOIN olympiad_rankers_snapshot snapshot on participant.player_id = snapshot.player_id and participant.server = snapshot.server
LEFT JOIN clan_data clan ON clan.clan_id = player.clanid
LEFT JOIN olympiad_heroes_history hero on participant.player_id = hero.player_id AND participant.server = hero.server
WHERE participant.battles > 0;

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
    participant.battles,
    participant.battles_won,
    participant.battles_lost,
    participant.points,
    IFNULL(hero.hero_count, 0) AS hero_count,
    IFNULL(hero.legend_count, 0) AS legend_count
FROM olympiad_participants participant
JOIN characters player on participant.player_id = player.charId -- AND player.accesslevel = 0
LEFT JOIN olympiad_rankers_class_snapshot snapshot on participant.player_id = snapshot.player_id and participant.server = snapshot.server
LEFT JOIN clan_data clan on clan.clan_id  = player.clanid
LEFT JOIN olympiad_heroes_history hero on participant.player_id = hero.player_id AND participant.server = hero.server
WHERE participant.battles > 0;