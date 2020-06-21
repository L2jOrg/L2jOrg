DROP TABLE IF EXISTS `clan_notices`;
CREATE TABLE IF NOT EXISTS `clan_notices` (
  `clan_id` INT NOT NULL DEFAULT 0,
  `enabled` BOOLEAN  NOT NULL DEFAULT false,
  `notice` TEXT NOT NULL,
  PRIMARY KEY  (`clan_id`),
  FOREIGN KEY (`clan_id`) REFERENCES clan_data(clan_id) ON DELETE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;