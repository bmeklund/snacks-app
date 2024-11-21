DROP TABLE IF EXISTS `Snack`;
CREATE TABLE `Snack` (
    id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` varchar(255) DEFAULT NULL);

INSERT INTO Snack(id, name) VALUES (1, 'Pizza');
INSERT INTO Snack(id, name) VALUES (2, 'Beer');
INSERT INTO Snack(id, name) VALUES (3, 'Soft drink');

