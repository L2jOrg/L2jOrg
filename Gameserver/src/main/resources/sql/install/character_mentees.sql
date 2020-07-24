DROP TABLE IF EXISTS `character_mentees`;
CREATE TABLE IF NOT EXISTS `character_mentees` (
  `charId` int(10) unsigned NOT NULL DEFAULT '0',
  `mentorId` int(10) unsigned NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;