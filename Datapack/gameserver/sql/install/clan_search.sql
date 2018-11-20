CREATE TABLE `clan_search_clan_applicants` (
  `char_id` int(11) NOT NULL,
  `preffered_clan_id` int(11) NOT NULL,
  `char_name` varchar(255) NOT NULL,
  `char_level` int(11) NOT NULL,
  `char_class_id` int(11) NOT NULL,
  `search_type` enum('SLT_FRIEND_LIST','SLT_PLEDGE_MEMBER_LIST','SLT_ADDITIONAL_FRIEND_LIST','SLT_ADDITIONAL_LIST','SLT_ANY') NOT NULL DEFAULT 'SLT_ANY',
  `desc` varchar(255) NOT NULL DEFAULT '',
  `timestamp` int(11) DEFAULT NULL,
  PRIMARY KEY (`char_id`)
) DEFAULT CHARSET=utf8;

CREATE TABLE `clan_search_registered_clans` (
  `clan_id` int(11) NOT NULL,
  `search_type` enum('SLT_FRIEND_LIST','SLT_PLEDGE_MEMBER_LIST','SLT_ADDITIONAL_FRIEND_LIST','SLT_ADDITIONAL_LIST','SLT_ANY') NOT NULL DEFAULT 'SLT_ANY',
  `desc` varchar(255) NOT NULL,
  `timestamp` int(11) DEFAULT NULL,
  `application` tinyint(1) NOT NULL DEFAULT '0',
  `sub_unit` smallint NOT NULL DEFAULT '0',
  PRIMARY KEY (`clan_id`)
) DEFAULT CHARSET=utf8;

CREATE TABLE `clan_search_waiting_players` (
  `char_id` int(11) NOT NULL,
  `char_name` varchar(255) NOT NULL,
  `char_level` int(11) NOT NULL,
  `char_class_id` int(11) NOT NULL,
  `search_type` enum('SLT_FRIEND_LIST','SLT_PLEDGE_MEMBER_LIST','SLT_ADDITIONAL_FRIEND_LIST','SLT_ADDITIONAL_LIST','SLT_ANY') DEFAULT NULL,
  `timestamp` int(11) DEFAULT NULL,
  PRIMARY KEY (`char_id`)
) DEFAULT CHARSET=utf8;