DROP TABLE IF EXISTS `character_shortcuts`;
CREATE TABLE `character_shortcuts` (
  `player_id` INT UNSIGNED NOT NULL DEFAULT 0,
  `client_id` INT NOT NULL DEFAULT 0,
  `class_index` int(1) NOT NULL DEFAULT '0',
  `type` ENUM('ITEM', 'SKILL', 'ACTION', 'MACRO', 'RECIPE', 'BOOKMARK') NOT NULL ,
  `shortcut_id` decimal(16) ,
  `level` SMALLINT,
  `sub_level` INT(4) NOT NULL DEFAULT '0',

  PRIMARY KEY (`player_id`, `client_id` ,`class_index`),
  KEY `shortcut_id` (`shortcut_id`),
  FOREIGN KEY FK_SHORTCUTS_CHARACTERS (`player_id`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;