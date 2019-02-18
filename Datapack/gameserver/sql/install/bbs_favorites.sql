DROP TABLE IF EXISTS `bbs_favorites`;
CREATE TABLE `bbs_favorites`
(
  `fav_id`     INT UNSIGNED     NOT NULL AUTO_INCREMENT,
  `object_id`  INT NOT NULL,
  `fav_bypass` VARCHAR(35)      NOT NULL,
  `fav_title`  VARCHAR(100)     NOT NULL,
  `add_date`   INT(15) UNSIGNED NOT NULL,
  PRIMARY KEY (`fav_id`),
  UNIQUE (object_id, fav_bypass),
  FOREIGN KEY FK_BBSFAVORITE_CHARACTER(object_id) REFERENCES characters (obj_Id) ON DELETE CASCADE
)
