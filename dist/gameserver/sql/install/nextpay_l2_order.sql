CREATE TABLE `nextpay_l2_order` (
  `order_id` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `product_id` int(11) NOT NULL DEFAULT '0',
  `volute` int(11) NOT NULL DEFAULT '0',
  `product_count` int(11) NOT NULL DEFAULT '0',
  `server` int(11) NOT NULL DEFAULT '0',
  `char_name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `profit` float NOT NULL DEFAULT '0',
  `comment` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`order_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
