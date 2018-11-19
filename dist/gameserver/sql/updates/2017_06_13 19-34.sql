DROP TABLE IF EXISTS `fish`;
ALTER TABLE castle DROP COLUMN town_id;
REPLACE INTO installed_updates (`file_name`) VALUES ("2017_06_13 19-34");