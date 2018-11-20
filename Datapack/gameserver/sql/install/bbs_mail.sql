DROP TABLE IF EXISTS `bbs_mail`;
CREATE TABLE `bbs_mail` (
`message_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
`to_name` VARCHAR(35) NOT NULL,
`to_object_id` INT UNSIGNED NOT NULL,
`from_name` VARCHAR(35) NOT NULL,
`from_object_id` INT UNSIGNED NOT NULL,
`title` VARCHAR(128) NOT NULL,
`message` TEXT NOT NULL,
`post_date` INT(15) UNSIGNED NOT NULL,
`read` SMALLINT NOT NULL DEFAULT '0',
`box_type` SMALLINT NOT NULL DEFAULT '0',
PRIMARY KEY(`message_id`),
INDEX(`to_object_id`),
INDEX(`from_object_id`),
INDEX(`read`),
INDEX(`box_type`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
