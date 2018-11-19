DROP TABLE IF EXISTS `instant_residence`;
CREATE TABLE IF NOT EXISTS `instant_clanhall` (
  `owner_id` INT NOT NULL DEFAULT '0',
  `id` tinyint(3) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`owner_id`)
);
