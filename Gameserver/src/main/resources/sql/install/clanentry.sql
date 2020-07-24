DROP TABLE IF EXISTS `pledge_applicant`;
CREATE TABLE IF NOT EXISTS `pledge_applicant` (
  `charId` int(10) NOT NULL,
  `clanId` int(10) NOT NULL,
  `karma` tinyint(1) NOT NULL,
  `message` varchar(255) NOT NULL,
  PRIMARY KEY (`charId`,`clanId`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

DROP TABLE IF EXISTS `pledge_recruit`;
CREATE TABLE IF NOT EXISTS `pledge_recruit` (
  `clan_id` int(10) NOT NULL,
  `karma` tinyint(1) NOT NULL,
  `information` varchar(50) NOT NULL,
  `detailed_information` varchar(255) NOT NULL,
  `application_type` tinyint(1) NOT NULL,
  `recruit_type` tinyint(1) NOT NULL,
  PRIMARY KEY (`clan_id`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

DROP TABLE IF EXISTS `pledge_waiting_list`;
CREATE TABLE IF NOT EXISTS `pledge_waiting_list` (
  `char_id` int(10) NOT NULL,
  `karma` tinyint(1) NOT NULL,
  PRIMARY KEY (`char_id`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;