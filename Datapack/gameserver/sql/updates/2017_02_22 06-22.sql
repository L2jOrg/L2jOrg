ALTER TABLE characters ADD COLUMN `clan_attendance` TINYINT NOT NULL DEFAULT '0' AFTER `clanid`;
ALTER TABLE clan_data ADD COLUMN `hunting_progress` INT NOT NULL DEFAULT '0' AFTER `disband_penalty`;
ALTER TABLE clan_data ADD COLUMN `yesterday_hunting_reward` INT NOT NULL DEFAULT '0' AFTER `hunting_progress`;
ALTER TABLE clan_data ADD COLUMN `yesterday_attendance_reward` INT NOT NULL DEFAULT '0' AFTER `yesterday_hunting_reward`;
REPLACE INTO installed_updates (`file_name`) VALUES ("2017_02_22 06-22");