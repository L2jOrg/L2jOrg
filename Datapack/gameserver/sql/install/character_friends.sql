DROP TABLE IF EXISTS character_relationship;
CREATE TABLE IF NOT EXISTS character_relationship (
  `char_id` INT UNSIGNED NOT NULL DEFAULT 0,
  `friend_id` INT UNSIGNED NOT NULL DEFAULT 0,
  `relation` ENUM('FRIEND', 'BLOCK') NOT NULL DEFAULT 'FRIEND',

  PRIMARY KEY (`char_id`,`friend_id`),
  KEY `relation`(`relation`),
  FOREIGN KEY FK_FRIENDS_CHARACTER (`char_id`) REFERENCES characters (`charId`) ON DELETE CASCADE,
  FOREIGN KEY FK_FRIENDS_FRIEND (`friend_id`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;