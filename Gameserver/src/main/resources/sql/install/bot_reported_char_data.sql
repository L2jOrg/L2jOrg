DROP TABLE IF EXISTS `bot_reported_char_data`;
CREATE TABLE IF NOT EXISTS `bot_reported_char_data` (
	`bot_id` INT UNSIGNED NOT NULL DEFAULT 0,
	`reporter_id` INT UNSIGNED NOT NULL DEFAULT 0,
	`type` ENUM('BOT', 'ADENA_ADS') NOT NULL DEFAULT 'BOT',
    `report_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

	PRIMARY KEY (`bot_id`, `reporter_id`, `type`),
    FOREIGN KEY FK_BOT_CHARACTER (`bot_id`) REFERENCES characters (`charId`) ON DELETE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;