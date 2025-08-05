CREATE TABLE IF NOT EXISTS bank_account (
    id_bank_account BIGINT AUTO_INCREMENT PRIMARY KEY,
    balance DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS user (
    id_user BINARY(16) PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    id_bank_account BIGINT NOT NULL,
    CONSTRAINT fk_user_bank_account FOREIGN KEY (id_bank_account) REFERENCES bank_account(id_bank_account)
);


CREATE TABLE IF NOT EXISTS friend (
    id_user_1 BINARY(16) NOT NULL,
    id_user_2 BINARY(16) NOT NULL,
    PRIMARY KEY (id_user_1, id_user_2),
    FOREIGN KEY (id_user_1) REFERENCES user(id_user),
    FOREIGN KEY (id_user_2) REFERENCES user(id_user)
);

CREATE TABLE IF NOT EXISTS transaction (
    id_transaction INT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(10, 2) NOT NULL,
    date DATETIME NOT NULL,
    description VARCHAR(255),
    id_user_sender BINARY(16) NOT NULL,
    id_user_receveir BINARY(16) NOT NULL,
    fee DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_user_sender) REFERENCES user(id_user),
    FOREIGN KEY (id_user_receveir) REFERENCES user(id_user)
);
