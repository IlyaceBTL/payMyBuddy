USE `payMyBuddy`;

-- Insertion de comptes bancaires
INSERT INTO `bank_account` (`balance`) VALUES
(1000.00), -- ID 1
(500.00),  -- ID 2
(1500.00); -- ID 3

-- Insertion d'utilisateurs
INSERT INTO `user` (`idUser`, `userName`, `lastName`, `firstName`, `email`, `password`, `idBankAccount`) VALUES
(UNHEX(REPLACE('11111111-1111-1111-1111-111111111111', '-', '')), 'jdoe', 'Doe', 'John', 'jdoe@example.com', 'hashed_password_1', 1),
(UNHEX(REPLACE('22222222-2222-2222-2222-222222222222', '-', '')), 'asmith', 'Smith', 'Alice', 'asmith@example.com', 'hashed_password_2', 2),
(UNHEX(REPLACE('33333333-3333-3333-3333-333333333333', '-', '')), 'bwayne', 'Wayne', 'Bruce', 'bwayne@example.com', 'hashed_password_3', 3);

-- Insertion de relations d’amitié
INSERT INTO `friend` (`idUser_1`, `idUser_2`) VALUES
(UNHEX(REPLACE('11111111-1111-1111-1111-111111111111', '-', '')), UNHEX(REPLACE('22222222-2222-2222-2222-222222222222', '-', ''))),
(UNHEX(REPLACE('22222222-2222-2222-2222-222222222222', '-', '')), UNHEX(REPLACE('33333333-3333-3333-3333-333333333333', '-', '')));

-- Insertion de transactions
INSERT INTO `transaction` (`amount`, `date`, `description`, `idUserSender`, `idUserReceveir`, `fee`) VALUES
(100.00, NOW(), 'Dinner payment', UNHEX(REPLACE('11111111-1111-1111-1111-111111111111', '-', '')), UNHEX(REPLACE('22222222-2222-2222-2222-222222222222', '-', '')), 2.00),
(50.00, NOW(), 'Taxi split', UNHEX(REPLACE('22222222-2222-2222-2222-222222222222', '-', '')), UNHEX(REPLACE('33333333-3333-3333-3333-333333333333', '-', '')), 1.00);
