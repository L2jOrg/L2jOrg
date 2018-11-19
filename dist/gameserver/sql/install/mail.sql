CREATE TABLE IF NOT EXISTS `mail` (
  `message_id` int(11) NOT NULL AUTO_INCREMENT,
  `sender_id` int(11) NOT NULL,
  `sender_name` varchar(32) CHARACTER SET utf8 NOT NULL,
  `receiver_id` int(10) NOT NULL,
  `receiver_name` varchar(32) CHARACTER SET utf8 NOT NULL,
  `expire_time` int(11) NOT NULL,
  `topic` tinytext CHARACTER SET utf8 NOT NULL,
  `body` text CHARACTER SET utf8 NOT NULL,
  `price` bigint(20) NOT NULL,
  `type` int NOT NULL,
  `unread` tinyint(4) NOT NULL DEFAULT '1',
  `returned` tinyint(4) NOT NULL DEFAULT '0',
  `system_topic` int(10) NOT NULL DEFAULT '0',
  `system_body` int(10) NOT NULL DEFAULT '0',
  `system_params` text CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`message_id`),
  KEY `sender_id` (`sender_id`),
  KEY `receiver_id` (`receiver_id`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `mail_attachments` (
  `message_id` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  UNIQUE KEY `item_id` (`item_id`),
  KEY `messageId` (`message_id`),
  FOREIGN KEY (`message_id`) REFERENCES `mail` (`message_id`) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `character_mail` (
  `char_id` int(11) NOT NULL,
  `message_id` int(11) NOT NULL,
  `is_sender` tinyint(1) NOT NULL,
  PRIMARY KEY (`char_id`,`message_id`),
  KEY `message_id` (`message_id`),
  FOREIGN KEY (`message_id`) REFERENCES `mail` (`message_id`) ON DELETE CASCADE
) ENGINE=InnoDB;