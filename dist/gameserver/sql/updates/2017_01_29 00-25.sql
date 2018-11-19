ALTER TABLE clan_search_registered_clans ADD COLUMN `application` tinyint(1) NOT NULL DEFAULT '0' AFTER `timestamp`;
ALTER TABLE clan_search_registered_clans ADD COLUMN `sub_unit` smallint NOT NULL DEFAULT '0' AFTER `application`;
ALTER TABLE clan_search_registered_clans DROP COLUMN title;