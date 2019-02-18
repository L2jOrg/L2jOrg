CREATE TABLE IF NOT EXISTS `clan_data`
(
  `clan_id`                     INT              NOT NULL DEFAULT '0',
  `clan_level`                  TINYINT UNSIGNED NOT NULL DEFAULT '0',
  `ally_id`                     INT              NULL,
  `crest`                       VARBINARY(256)   NULL     DEFAULT NULL,
  `reputation_score`            INT              NOT NULL DEFAULT '0',
  `warehouse`                   INT              NOT NULL DEFAULT '0',
  `expelled_member`             INT UNSIGNED     NOT NULL DEFAULT '0',
  `leaved_ally`                 INT UNSIGNED     NOT NULL DEFAULT '0',
  `dissolved_ally`              INT UNSIGNED     NOT NULL DEFAULT '0',
  `auction_bid_at`              INT              NOT NULL DEFAULT '0',
  `academy_graduates`           INT              NOT NULL DEFAULT '0',
  `castle_defend_count`         INT              NOT NULL DEFAULT '0',
  `disband_end`                 INT              NOT NULL DEFAULT '0',
  `disband_penalty`             INT              NOT NULL DEFAULT '0',
  `hunting_progress`            INT              NOT NULL DEFAULT '0',
  `yesterday_hunting_reward`    INT              NOT NULL DEFAULT '0',
  `yesterday_attendance_reward` INT              NOT NULL DEFAULT '0',
  PRIMARY KEY (`clan_id`)
);

CREATE TABLE IF NOT EXISTS `ally_data`
(
  `ally_id`         INT            NOT NULL        DEFAULT '0',
  `ally_name`       VARCHAR(45) CHARACTER SET UTF8 DEFAULT NULL,
  `leader_id`       INT            NOT NULL        DEFAULT '0',
  `expelled_member` INT UNSIGNED   NOT NULL        DEFAULT '0',
  `crest`           VARBINARY(192) NULL            DEFAULT NULL,
  PRIMARY KEY (`ally_id`),
  FOREIGN KEY FK_ALLY_CLAN (leader_id) REFERENCES clan_data (clan_id) ON DELETE CASCADE
);

ALTER TABLE clan_data ADD FOREIGN KEY FK_CLAN_ALLY (ally_id) REFERENCES ally_data (ally_id) ON DELETE SET NULL ;