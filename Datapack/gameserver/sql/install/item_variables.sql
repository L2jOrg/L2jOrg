DROP TABLE IF EXISTS `item_variables`;
CREATE TABLE IF NOT EXISTS `item_variables` (
  `id` int(10) UNSIGNED NOT NULL,
  `var` varchar(255) NOT NULL,
  `val` text NOT NULL,
  KEY `charId` (`id`)
);