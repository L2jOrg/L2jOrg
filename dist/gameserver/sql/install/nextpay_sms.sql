CREATE TABLE `nextpay_sms` (
  `id` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `amount_usd` float NOT NULL DEFAULT '0',
  `number` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `text` text COLLATE utf8_bin,
  `prefix` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `hash` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `country` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `op` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `sms_date` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `amount` float NOT NULL DEFAULT '0',
  `phone` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `last_modified` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `status` int(11) NOT NULL DEFAULT '0',
  `eup` float DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
