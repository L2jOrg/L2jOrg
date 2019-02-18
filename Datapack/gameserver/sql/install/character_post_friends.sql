CREATE TABLE IF NOT EXISTS `character_post_friends`(
  `object_id` INT(11) NOT NULL,
  `post_friend` INT(11) NOT NULL,
  PRIMARY KEY (`object_id`,`post_friend`),
  FOREIGN KEY FK_POST_CHARACTER(object_id) REFERENCES characters(obj_Id) ON DELETE CASCADE,
  FOREIGN KEY FK_POST_CHARACTER_F(post_friend) REFERENCES characters(obj_Id) ON DELETE CASCADE

);