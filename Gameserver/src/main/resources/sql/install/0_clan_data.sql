DROP TABLE IF EXISTS `clan_member_reputation`;
DROP TABLE IF EXISTS `clan_subpledges`;
DROP TABLE IF EXISTS `clan_privs`;
DROP TABLE IF EXISTS `clan_skills`;
DROP TABLE IF EXISTS `clan_wars`;
DROP TABLE IF EXISTS `clan_notices`;
DROP TABLE IF EXISTS `clan_data`;

CREATE TABLE IF NOT EXISTS `clan_data`
(
    `clan_id`                  INT               NOT NULL DEFAULT 0,
    `clan_name`                VARCHAR(45),
    `clan_level`               INT,
    `reputation_score`         INT               NOT NULL DEFAULT 0,
    `hasCastle`                INT,
    `blood_alliance_count`     SMALLINT UNSIGNED NOT NULL DEFAULT 0,
    `ally_id`                  INT,
    `ally_name`                VARCHAR(45),
    `leader_id`                INT,
    `crest_id`                 INT,
    `crest_large_id`           INT,
    `ally_crest_id`            INT,
    `auction_bid_at`           INT               NOT NULL DEFAULT 0,
    `ally_penalty_expiry_time` BIGINT UNSIGNED   NOT NULL DEFAULT 0,
    `ally_penalty_type`        TINYINT(1)        NOT NULL DEFAULT 0,
    `char_penalty_expiry_time` BIGINT UNSIGNED   NOT NULL DEFAULT 0,
    `dissolving_expiry_time`   BIGINT UNSIGNED   NOT NULL DEFAULT 0,
    `new_leader_id`            INT UNSIGNED      NOT NULL DEFAULT 0,
    `max_online_member`        INT               NOT NULL DEFAULT 0,
    `prev_max_online_member`   INT               NOT NULL DEFAULT 0,
    `hunting_points`           INT               NOT NULL DEFAULT 0,
    `prev_hunting_points`      INT               NOT NULL DEFAULT 0,
    `arena_progress`           INT UNSIGNED      NOT NULL DEFAULT 0,

    PRIMARY KEY (`clan_id`),
    KEY `ally_id` (`ally_id`),
    KEY `leader_id` (`leader_id`),
    KEY `auction_bid_at` (`auction_bid_at`)
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;


CREATE TABLE IF NOT EXISTS `clan_privs`
(
    `clan_id` INT NOT NULL DEFAULT 0,
    `rank`    INT NOT NULL DEFAULT 0,
    `privs`   INT NOT NULL DEFAULT 0,

    PRIMARY KEY (`clan_id`, `rank`),
    FOREIGN KEY (`clan_id`) REFERENCES clan_data (clan_id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8MB4;

CREATE TABLE IF NOT EXISTS `clan_skills`
(
    `clan_id`       INT NOT NULL DEFAULT 0,
    `skill_id`      INT NOT NULL DEFAULT 0,
    `skill_level`   INT NOT NULL DEFAULT 0,
    `sub_pledge_id` INT NOT NULL DEFAULT -2,

    PRIMARY KEY (`clan_id`, `skill_id`, `sub_pledge_id`),
    FOREIGN KEY (clan_id) REFERENCES clan_data (clan_id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8MB4;

CREATE TABLE IF NOT EXISTS `clan_subpledges`
(
    `clan_id`       INT NOT NULL DEFAULT 0,
    `sub_pledge_id` INT NOT NULL DEFAULT 0,
    `name`          VARCHAR(45),
    `leader_id`     INT NOT NULL DEFAULT 0,

    PRIMARY KEY (`clan_id`, `sub_pledge_id`),
    KEY `leader_id` (`leader_id`),
    FOREIGN KEY (clan_id) REFERENCES clan_data (clan_id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8MB4;

CREATE TABLE IF NOT EXISTS `clan_wars`
(
    `clan1`      INT                                                                       NOT NULL,
    `clan2`      INT                                                                       NOT NULL,
    `clan1Kill`  INT                                                                       NOT NULL DEFAULT 0,
    `clan2Kill`  INT                                                                       NOT NULL DEFAULT 0,
    `winnerClan` VARCHAR(35)                                                               NOT NULL DEFAULT 0,
    `startTime`  BIGINT                                                                    NOT NULL DEFAULT 0,
    `endTime`    BIGINT                                                                    NOT NULL DEFAULT 0,
    `state`      ENUM ('DECLARATION', 'BLOOD_DECLARATION', 'MUTUAL', 'WIN', 'LOSS', 'TIE') NOT NULL,

    FOREIGN KEY (`clan1`) REFERENCES clan_data (clan_id) ON DELETE CASCADE,
    FOREIGN KEY (`clan2`) REFERENCES clan_data (clan_id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8MB4;

CREATE TABLE IF NOT EXISTS `clan_notices`
(
    `clan_id` INT     NOT NULL,
    `enabled` BOOLEAN NOT NULL DEFAULT false,
    `notice`  TEXT    NOT NULL,

    PRIMARY KEY (`clan_id`),
    FOREIGN KEY (`clan_id`) REFERENCES clan_data (clan_id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `clan_member_reputation`
(
    `clan_id`               INT          NOT NULL,
    `player_id`             INT UNSIGNED NOT NULL,
    `last_reputation_level` SMALLINT     NOT NULL DEFAULT 0,

    PRIMARY KEY (`clan_id`, `player_id`),
    FOREIGN KEY (`clan_id`) REFERENCES clan_data (clan_id) ON DELETE CASCADE,
    FOREIGN KEY (`player_id`) REFERENCES characters (charId) ON DELETE CASCADE
)