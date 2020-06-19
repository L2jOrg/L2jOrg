DROP TABLE IF EXISTS `item_special_abilities`;
CREATE TABLE IF NOT EXISTS `item_special_abilities` (
  `objectId` int(10) unsigned NOT NULL,
  `type` tinyint(1) unsigned NOT NULL DEFAULT 1,
  `optionId` int(10) unsigned NOT NULL,
  `position` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`objectId`,`optionId`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;