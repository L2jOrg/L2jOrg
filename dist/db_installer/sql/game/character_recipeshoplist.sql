DROP TABLE IF EXISTS `character_recipeshoplist`;
CREATE TABLE IF NOT EXISTS `character_recipeshoplist` (
  `charId` int(10) unsigned NOT NULL DEFAULT 0,
  `recipeId` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `price` bigint(20) UNSIGNED NOT NULL DEFAULT 0,
  `index` tinyint(3) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`charId`,`recipeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;