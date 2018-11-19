DROP TABLE IF EXISTS `bbs_favorites`;
CREATE TABLE `bbs_favorites` (
`fav_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
`object_id` INT(15) UNSIGNED NOT NULL,
`fav_bypass` VARCHAR(35) NOT NULL,
`fav_title` VARCHAR(100) NOT NULL,
`add_date` INT(15) UNSIGNED NOT NULL,
PRIMARY KEY(`fav_id`),
INDEX(`object_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
ALTER TABLE bbs_favorites ADD UNIQUE INDEX ix_obj_id_bypass (object_id, fav_bypass);
