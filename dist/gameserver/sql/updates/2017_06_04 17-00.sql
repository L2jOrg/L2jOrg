ALTER TABLE raidboss_status ADD COLUMN `death_time` INT NOT NULL DEFAULT '0';
REPLACE INTO installed_updates (`file_name`) VALUES ("2017_06_04 17-00");