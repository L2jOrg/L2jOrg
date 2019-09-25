CREATE TABLE IF NOT EXISTS `characters` (
  `charId` INT UNSIGNED NOT NULL DEFAULT 0,
  `account_name` VARCHAR(45) DEFAULT NULL,
  `char_name` VARCHAR(35) NOT NULL,
  `level` TINYINT UNSIGNED DEFAULT NULL,
  `maxHp` MEDIUMINT UNSIGNED DEFAULT NULL,
  `curHp` MEDIUMINT UNSIGNED DEFAULT NULL,
  `maxCp` MEDIUMINT UNSIGNED DEFAULT NULL,
  `curCp` MEDIUMINT UNSIGNED DEFAULT NULL,
  `maxMp` MEDIUMINT UNSIGNED DEFAULT NULL,
  `curMp` MEDIUMINT UNSIGNED DEFAULT NULL,
  `face` TINYINT UNSIGNED DEFAULT NULL,
  `hairStyle` TINYINT UNSIGNED DEFAULT NULL,
  `hairColor` TINYINT UNSIGNED DEFAULT NULL,
  `sex` TINYINT UNSIGNED DEFAULT NULL,
  `heading` MEDIUMINT DEFAULT NULL,
  `x` MEDIUMINT DEFAULT NULL,
  `y` MEDIUMINT DEFAULT NULL,
  `z` MEDIUMINT DEFAULT NULL,
  `exp` BIGINT UNSIGNED DEFAULT 0,
  `expBeforeDeath` BIGINT UNSIGNED DEFAULT 0,
  `sp` BIGINT UNSIGNED NOT NULL DEFAULT 0,
  `reputation` INT DEFAULT NULL,
  `fame` MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
  `raidbossPoints` MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
  `pvpkills` SMALLINT UNSIGNED DEFAULT NULL,
  `pkkills` SMALLINT UNSIGNED DEFAULT NULL,
  `clanid` INT UNSIGNED DEFAULT NULL,
  `race` TINYINT UNSIGNED DEFAULT NULL,
  `classid` TINYINT UNSIGNED DEFAULT NULL,
  `base_class` TINYINT UNSIGNED NOT NULL DEFAULT 0,
  `transform_id` SMALLINT UNSIGNED NOT NULL DEFAULT 0,
  `deletetime` BIGINT UNSIGNED NOT NULL DEFAULT '0',
  `cancraft` TINYINT UNSIGNED DEFAULT NULL,
  `title` VARCHAR(21) DEFAULT NULL,
  `title_color` MEDIUMINT UNSIGNED NOT NULL DEFAULT 0xECF9A2,
  `accesslevel` MEDIUMINT DEFAULT 0,
  `online` TINYINT UNSIGNED DEFAULT NULL,
  `onlinetime` INT DEFAULT NULL,
  `char_slot` TINYINT UNSIGNED DEFAULT NULL,
  `lastAccess` BIGINT UNSIGNED NOT NULL DEFAULT '0',
  `clan_privs` INT UNSIGNED DEFAULT 0,
  `wantspeace` TINYINT UNSIGNED DEFAULT 0,
  `power_grade` TINYINT UNSIGNED DEFAULT NULL,
  `nobless` TINYINT UNSIGNED NOT NULL DEFAULT 0,
  `subpledge` SMALLINT NOT NULL DEFAULT 0,
  `lvl_joined_academy` TINYINT UNSIGNED NOT NULL DEFAULT 0,
  `apprentice` INT UNSIGNED NOT NULL DEFAULT 0,
  `sponsor` INT UNSIGNED NOT NULL DEFAULT 0,
  `clan_join_expiry_time` BIGINT UNSIGNED NOT NULL DEFAULT '0',
  `clan_create_expiry_time` BIGINT UNSIGNED NOT NULL DEFAULT '0',
  `bookmarkslot` SMALLINT UNSIGNED NOT NULL DEFAULT 0,
  `vitality_points` MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
  `createDate` DATE NOT NULL DEFAULT  (CURRENT_DATE),
  `language` VARCHAR(2) DEFAULT NULL,
  `pccafe_points` INT NOT NULL DEFAULT '0',
  PRIMARY KEY (`charId`),
  KEY `account_name` (`account_name`),
  KEY `char_name` (`char_name`),
  KEY `clanid` (`clanid`),
  KEY `online` (`online`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE VIEW rankers AS
SELECT charId,
       char_name,
       exp,
       base_class as class,
       race,
       clanid,
       rank() over (w) as 'rank'
from characters
where level >= 76
    AND ( base_class BETWEEN 88 AND 118 OR base_class IN (131, 134, 195) )
WINDOW w as (ORDER BY exp desc )
LIMIT 150;


CREATE VIEW rankers_race AS
SELECT charId,
       char_name,
       exp,
       base_class as class,
       race,
       clanid,
       rank() over (
           PARTITION BY  race
           ORDER BY exp desc
           ) as 'rank'
from characters
where level >= 76
AND ( base_class BETWEEN 88 AND 118 OR base_class IN (131, 134, 195) )
LIMIT 150;