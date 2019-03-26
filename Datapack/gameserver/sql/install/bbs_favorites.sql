DROP TABLE IF EXISTS `bbs_favorites`;
CREATE TABLE IF NOT EXISTS `bbs_favorites` (
	`favId` INT UNSIGNED NOT NULL AUTO_INCREMENT,
	`playerId` INT UNSIGNED NOT NULL,
	`favTitle` VARCHAR(50) NOT NULL,
	`favBypass` VARCHAR(127) NOT NULL,
	`favAddDate` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (`favId`),
	UNIQUE INDEX `favId_playerId` (`favId`, `playerId`)
)
COMMENT='This table saves the Favorite links for the CB.'
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB;
