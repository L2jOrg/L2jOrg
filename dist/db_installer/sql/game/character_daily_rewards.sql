DROP TABLE IF EXISTS `character_daily_rewards`;
CREATE TABLE IF NOT EXISTS `character_daily_rewards` (
  `charId`  int(10) UNSIGNED NOT NULL ,
  `rewardId`  int(3) UNSIGNED NOT NULL ,
  `status`  tinyint(1) UNSIGNED NOT NULL DEFAULT 1 ,
  `progress`  int UNSIGNED NOT NULL DEFAULT 0 ,
  `lastCompleted`  bigint UNSIGNED NOT NULL ,
  PRIMARY KEY (`charId`, `rewardId`)
);
