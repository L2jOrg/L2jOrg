DROP TABLE IF EXISTS `bbs_memo`;
CREATE TABLE `bbs_memo` (
`memo_id` int(11) NOT NULL auto_increment,
`account_name` varchar(45) NOT NULL,
`char_name` varchar(35) NOT NULL,
`ip` varchar(16) NOT NULL,
`title` varchar(128) NOT NULL,
`memo` text NOT NULL,
`post_date` INT(15) UNSIGNED NOT NULL,
PRIMARY KEY(`memo_id`),
INDEX(account_name)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
