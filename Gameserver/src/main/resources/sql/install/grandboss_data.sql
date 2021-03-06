DROP TABLE IF EXISTS `grandboss_data`;
CREATE TABLE IF NOT EXISTS `grandboss_data` (
  `boss_id` SMALLINT unsigned NOT NULL,
  `x` MEDIUMINT NOT NULL,
  `y` MEDIUMINT NOT NULL,
  `z` MEDIUMINT NOT NULL,
  `heading` MEDIUMINT NOT NULL DEFAULT 0,
  `respawn_time` BIGINT(13) unsigned NOT NULL DEFAULT 0,
  `hp` DOUBLE NOT NULL,
  `mp` DOUBLE NOT NULL,
  `status` ENUM('DEAD', 'ALIVE', 'FIGHTING') NOT NULL DEFAULT 'ALIVE',
  PRIMARY KEY (`boss_id`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

INSERT IGNORE INTO `grandboss_data` (`boss_id`,`x`,`y`,`z`,`heading`,`hp`,`mp`) VALUES
(29001, -21610, 181594, -5734, 0, 229898.48, 667.776), -- Queen Ant
(29006, 17726, 108915, -6480, 0, 622493.58388, 3793.536), -- Core
(29014, 55024, 17368, -5412, 10126, 1176982, 3793.536), -- Orfen
(29020, 116033, 17447, 10107, -25348, 4068372, 39960), -- Baium
(29022, 52207, 217230, -3341, 0, 28531442, 12240), -- Zaken
-- (29028, -105200, -253104, -15264, 0, 62041918, 2248572), -- Valakas
(29068, 125798, 125390, -3952,32768, 799999999 , 3887395 ); -- Antharas
-- (25286, 185080, -12613, -5499, 16550, 556345880, 86847), -- Anakim
-- (25283, 185062, -9605, -5499, 15640, 486021997, 79600), -- Lilith