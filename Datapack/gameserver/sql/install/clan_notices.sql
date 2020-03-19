DROP TABLE IF EXISTS `clan_notices`;
CREATE TABLE IF NOT EXISTS `clan_notices` (
  `clan_id` INT NOT NULL DEFAULT 0,
  `enabled` enum('true','false') DEFAULT 'false' NOT NULL,
  `notice` TEXT NOT NULL,
  PRIMARY KEY  (`clan_id`),
  FOREIGN KEY (`clan_id`) REFERENCES clan_data(clan_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;