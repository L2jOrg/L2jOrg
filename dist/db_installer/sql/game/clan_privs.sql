DROP TABLE IF EXISTS `clan_privs`;
CREATE TABLE IF NOT EXISTS `clan_privs` (
  `clan_id` INT NOT NULL DEFAULT 0,
  `rank` INT NOT NULL DEFAULT 0,
  `party` INT NOT NULL DEFAULT 0,
  `privs` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`clan_id`,`rank`,`party`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;