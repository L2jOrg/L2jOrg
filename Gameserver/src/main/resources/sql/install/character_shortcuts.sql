DROP TABLE IF EXISTS `character_shortcuts`;
CREATE TABLE `character_shortcuts` (
  `player_id` INT UNSIGNED NOT NULL DEFAULT 0,
  `client_id` INT NOT NULL DEFAULT 0,
  `type` ENUM('ITEM', 'SKILL', 'ACTION', 'MACRO', 'RECIPE', 'BOOKMARK') NOT NULL ,
  `shortcut_id` INT NOT NULL,
  `level` SMALLINT,
  `sub_level` INT NOT NULL DEFAULT 0,
  `character_type` SMALLINT NOT NULL DEFAULT 1,
  `active` BOOLEAN NOT NULL DEFAULT false,

  PRIMARY KEY (`player_id`, `client_id`),
  FOREIGN KEY FK_SHORTCUTS_CHARACTERS (`player_id`) REFERENCES characters (`charId`) ON DELETE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;