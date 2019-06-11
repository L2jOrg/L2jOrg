DROP TABLE IF EXISTS character_daily_missions;
CREATE TABLE IF NOT EXISTS character_daily_missions (
  `char_id`  INT UNSIGNED NOT NULL ,
  `mission_id`  INT UNSIGNED NOT NULL ,
  `status`  TINYINT UNSIGNED NOT NULL DEFAULT 1 ,
  `progress`  INT UNSIGNED NOT NULL DEFAULT 0 ,
  `last_completed`  BIGINT UNSIGNED NOT NULL ,
  PRIMARY KEY (`char_id`, `mission_id`),
  INDEX IDX_MISSION (`mission_id`),
  FOREIGN KEY FK_MISSION_CHARACTER (`char_id`) REFERENCES characters (`charId`) ON DELETE CASCADE
);
