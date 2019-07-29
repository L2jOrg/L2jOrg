-- TODO change to characters relationships and put index
DROP TABLE IF EXISTS `character_friends`;
CREATE TABLE IF NOT EXISTS `character_friends` (
  `charId` INT UNSIGNED NOT NULL DEFAULT 0,
  `friendId` INT UNSIGNED NOT NULL DEFAULT 0,
  `relation` INT UNSIGNED NOT NULL DEFAULT 0,

  PRIMARY KEY (`charId`,`friendId`),
  FOREIGN KEY FK_FRIENDS_CHARACTER (`charId`) REFERENCES characters (`charId`) ON DELETE CASCADE,
  FOREIGN KEY FK_FRIENDS_FRIEND (`friendId`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;