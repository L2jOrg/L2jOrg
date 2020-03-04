DROP TABLE IF EXISTS `castle`;
CREATE TABLE IF NOT EXISTS `castle` (
  `id` INT NOT NULL,
  `name` VARCHAR(25) NOT NULL,
  `side` ENUM('NEUTRAL','LIGHT','DARK') NOT NULL DEFAULT 'NEUTRAL',
  `treasury` BIGINT NOT NULL DEFAULT 0,
  `siege_date` DATETIME ,
  `siege_time_registration_end` DATETIME,
  `show_npc_crest` BOOLEAN NOT NULL DEFAULT FALSE,
  `ticket_buy_count` SMALLINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8MB4;

INSERT IGNORE INTO `castle`(`id`, `name`) VALUES
(1,'Gludio'),
(2,'Dion'),
(3,'Giran'),
(4,'Oren'),
(5,'Aden');