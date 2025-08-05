-- Insertion de comptes bancaires
INSERT INTO `bank_account` (`balance`) VALUES
(1000.00),
(500.00),
(1500.00);

-- Insertion d'utilisateurs avec les colonnes dans le bon ordre
INSERT INTO `user` (`id_user`, `email`, `first_name`, `last_name`, `password`, `user_name`, `id_bank_account`) VALUES
(UNHEX(REPLACE('11111111-1111-1111-1111-111111111111', '-', '')), 'jdoe@example.com', 'John', 'Doe', 'hashed_password_1', 'jdoe', 1),
(UNHEX(REPLACE('22222222-2222-2222-2222-222222222222', '-', '')), 'asmith@example.com', 'Alice', 'Smith', 'hashed_password_2', 'asmith', 2),
(UNHEX(REPLACE('33333333-3333-3333-3333-333333333333', '-', '')), 'bwayne@example.com', 'Bruce', 'Wayne', 'hashed_password_3', 'bwayne', 3);

-- Insertion de relations d’amitié
INSERT INTO `friend` (`id_user_1`, `id_user_2`) VALUES
(UNHEX(REPLACE('11111111-1111-1111-1111-111111111111', '-', '')), UNHEX(REPLACE('22222222-2222-2222-2222-222222222222', '-', ''))),
(UNHEX(REPLACE('22222222-2222-2222-2222-222222222222', '-', '')), UNHEX(REPLACE('33333333-3333-3333-3333-333333333333', '-', '')));

-- Insertion de transactions
INSERT INTO `transaction` (`amount`, `date`, `description`, `id_user_sender`, `id_user_receveir`, `fee`) VALUES
(100.00, NOW(), 'Dinner payment', UNHEX(REPLACE('11111111-1111-1111-1111-111111111111', '-', '')), UNHEX(REPLACE('22222222-2222-2222-2222-222222222222', '-', '')), 2.00),
(50.00, NOW(), 'Taxi split', UNHEX(REPLACE('22222222-2222-2222-2222-222222222222', '-', '')), UNHEX(REPLACE('33333333-3333-3333-3333-333333333333', '-', '')), 1.00);
