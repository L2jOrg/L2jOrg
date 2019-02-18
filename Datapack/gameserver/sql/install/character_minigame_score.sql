DROP TABLE IF EXISTS `character_minigame_score`;
CREATE TABLE `character_minigame_score` (
  `object_id` int(11) NOT NULL,
  `score` int(11) NOT NULL,
  PRIMARY KEY (`object_id`,`score`),
  FOREIGN KEY FK_MINIGAMES_CHARACTER(object_id) REFERENCES characters(obj_Id) ON DELETE CASCADE
);