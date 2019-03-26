DROP TABLE IF EXISTS `airships`;
CREATE TABLE IF NOT EXISTS `airships` (
  `owner_id` INT, -- object id of the player or clan, owner of this airship
  `fuel` decimal(5,0) NOT NULL DEFAULT 600,
  PRIMARY KEY (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;