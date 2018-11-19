DROP TABLE IF EXISTS `instant_clanhall`;
CREATE TABLE IF NOT EXISTS `instant_clanhall_info` (
  `id` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `siege_date` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`)
);
CREATE TABLE IF NOT EXISTS `instant_clanhall_owners` (
  `owner_id` INT NOT NULL DEFAULT '0',
  `id` tinyint(3) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`owner_id`)
);
