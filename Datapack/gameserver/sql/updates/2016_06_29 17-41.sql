ALTER TABLE castle ADD COLUMN `owner_id` INT NOT NULL DEFAULT '0' AFTER `last_siege_date`;
ALTER TABLE clanhall ADD COLUMN `owner_id` INT NOT NULL DEFAULT '0' AFTER `last_siege_date`;
UPDATE castle LEFT JOIN clan_data ON clan_data.hasCastle = castle.id SET castle.owner_id = clan_data.clan_id WHERE clan_data.hasCastle IS NOT NULL;
UPDATE clanhall LEFT JOIN clan_data ON clan_data.hasHideout = clanhall.id SET clanhall.owner_id = clan_data.clan_id WHERE clan_data.hasHideout IS NOT NULL;
ALTER TABLE clan_data DROP COLUMN hasCastle;
ALTER TABLE clan_data DROP COLUMN hasHideout;