DROP TABLE IF EXISTS `clanhall`;
CREATE TABLE IF NOT EXISTS `clanhall` (
  `id` int(11) NOT NULL,
  owner_id int(11) NOT NULL DEFAULT '0',
  paid_until bigint(13) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY `id` (`id`),
  KEY `ownerId` (owner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8MB4;

