DROP TABLE IF EXISTS `donations`;
CREATE TABLE donations
(
    payerid VARCHAR(255) NOT NULL,
    paymentid VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    amount INT,
    claimed BOOLEAN DEFAULT 0
)