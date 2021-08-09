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
  `clan_id` INT NOT NULL,
  `karma` TINYINT NOT NULL,
  `information` VARCHAR(50) NOT NULL,
  `detailed_information` VARCHAR(255) NOT NULL,
  `application_type` TINYINT NOT NULL,
  `recruit_type` TINYINT NOT NULL,
  PRIMARY KEY (`clan_id`),
  FOREIGN KEY (`clan_id`) REFERENCES clan_data(clan_id) ON DELETE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

DROP TABLE IF EXISTS `pledge_waiting_list`;
CREATE TABLE IF NOT EXISTS `pledge_waiting_list` (
  `char_id` INT UNSIGNED NOT NULL,
  `karma` tinyint(1) NOT NULL,
  PRIMARY KEY (`char_id`),
  FOREIGN KEY (`char_id`) REFERENCES characters(charId) ON DELETE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;