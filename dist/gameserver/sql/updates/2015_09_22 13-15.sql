CREATE TABLE IF NOT EXISTS `clan_leader_request` (
  `clan_id` int(11) NOT NULL,
  `new_leader_id` int(11) NOT NULL,
  `time` bigint(20) NOT NULL,
  PRIMARY KEY (`clan_id`)
);

ALTER TABLE clan_data ADD COLUMN castle_defend_count INT NOT NULL DEFAULT '0';
UPDATE clan_data SET castle_defend_count=(SELECT reward_count FROM castle WHERE id=hasCastle) WHERE hasCastle > 0;
ALTER TABLE castle DROP COLUMN reward_count;
ALTER TABLE clan_subpledges ADD COLUMN `upgraded` INT NOT NULL DEFAULT '0';
ALTER TABLE clan_data ADD COLUMN `disband_end` INT NOT NULL DEFAULT '0';
ALTER TABLE clan_data ADD COLUMN `disband_penalty` INT NOT NULL DEFAULT '0';
UPDATE clan_subpledges SET `upgraded` = 1 WHERE type <> 0 AND type <> -1;
