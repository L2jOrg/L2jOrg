DROP TABLE IF EXISTS `recipes`;
CREATE TABLE IF NOT EXISTS `recipes` (
  `player_id` INT UNSIGNED NOT NULL,
  `id` INT NOT NULL,
  PRIMARY KEY (`player_id`, `id`),
  FOREIGN KEY  (`player_id`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;