DROP TABLE IF EXISTS `announcements`;
CREATE TABLE IF NOT EXISTS `announcements` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `type` ENUM('NORMAL', 'CRITICAL', 'AUTO_NORMAL', 'AUTO_CRITICAL') NOT NULL DEFAULT 'NORMAL',
  `initial` bigint(20) NOT NULL DEFAULT 0,
  `delay` bigint(20) NOT NULL DEFAULT 0,
  `repeat` int(11) NOT NULL DEFAULT 0,
  `author` text NOT NULL,
  `content` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

INSERT INTO announcements (`author`, `content`) VALUES
('System', 'Welcome to L2j Org!'),
('System', 'Report any bug at https://github.com/JoeAlisson/L2jOrg/issues');
