DROP TABLE IF EXISTS `grandboss_data`;
CREATE TABLE IF NOT EXISTS `grandboss_data` (
  `boss_id` smallint(5) unsigned NOT NULL,
  `loc_x` mediumint(6) NOT NULL,
  `loc_y` mediumint(6) NOT NULL,
  `loc_z` mediumint(6) NOT NULL,
  `heading` mediumint(6) NOT NULL DEFAULT '0',
  `respawn_time` bigint(13) unsigned NOT NULL DEFAULT '0',
  `currentHP` decimal(30,15) NOT NULL,
  `currentMP` decimal(30,15) NOT NULL,
  `status` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`boss_id`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

INSERT IGNORE INTO `grandboss_data` (`boss_id`,`loc_x`,`loc_y`,`loc_z`,`heading`,`currentHP`,`currentMP`) VALUES
(29001, -21610, 181594, -5734, 0, 229898.48, 667.776), -- Queen Ant
(29006, 17726, 108915, -6480, 0, 622493.58388, 3793.536), -- Core
(29014, 55024, 17368, -5412, 10126, 1176982, 3793.536), -- Orfen
(29020, 116033, 17447, 10107, -25348, 4068372, 39960), -- Baium
(29022, 52207, 217230, -3341, 0, 28531442, 12240), -- Zaken
-- (29028, -105200, -253104, -15264, 0, 62041918, 2248572), -- Valakas
(29068, 185708, 114298, -8221,32768, 62802301, 1998000); -- Antharas
-- (25286, 185080, -12613, -5499, 16550, 556345880, 86847), -- Anakim
-- (25283, 185062, -9605, -5499, 15640, 486021997, 79600), -- Lilith
