CREATE TABLE IF NOT EXISTS `gameservers` (
  `server_id` INT NOT NULL,
  `host` VARCHAR(255) NOT NULL,
  `server_type` INT,
  PRIMARY KEY (`server_id`)
);