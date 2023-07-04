BEGIN TRANSACTION;

DROP TABLE IF EXISTS tenmo_user, account, transfer;

DROP SEQUENCE IF EXISTS seq_user_id, seq_account_id, seq_transfer_id;

-- Sequence to start user_id values at 1001 instead of 1
CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  START WITH 1001
  NO MAXVALUE;

CREATE TABLE tenmo_user (
	user_id int NOT NULL DEFAULT nextval('seq_user_id'),
	username varchar(50) NOT NULL,
	password_hash varchar(200) NOT NULL,
	CONSTRAINT PK_tenmo_user PRIMARY KEY (user_id),
	CONSTRAINT UQ_username UNIQUE (username)
);

-- Sequence to start account_id values at 2001 instead of 1
-- Note: Use similar sequences with unique starting values for additional tables
CREATE SEQUENCE seq_account_id
  INCREMENT BY 1
  START WITH 2001
  NO MAXVALUE;

CREATE TABLE account (
	account_id int NOT NULL DEFAULT nextval('seq_account_id'),
	user_id int NOT NULL,
	balance decimal(13, 2) NOT NULL,
	CONSTRAINT PK_account PRIMARY KEY (account_id),
	CONSTRAINT FK_account_tenmo_user FOREIGN KEY (user_id) REFERENCES tenmo_user (user_id)
);

-- Sequence to start transfer_id values at 3001 instead of 1
-- Note: Use similar sequences with unique starting values for additional tables
CREATE SEQUENCE seq_transfer_id
  INCREMENT BY 1
  START WITH 3001
  NO MAXVALUE;

CREATE TABLE transfer (
	transfer_id int NOT NULL DEFAULT nextval('seq_transfer_id'),
	account_id_from int NOT NULL,
	account_id_to int NOT NULL,
	balance_transfer numeric(13, 2) NOT NULL,
	status varchar(20) NOT NULL,
	transfer_type varchar(8) NOT NULL,
	CONSTRAINT PK_transfer_id PRIMARY KEY (transfer_id),
	CONSTRAINT FK_sending_account FOREIGN KEY (account_id_to) REFERENCES account (account_id),
	CONSTRAINT FK_receiving_account FOREIGN KEY (account_id_from) REFERENCES account (account_id)
);


INSERT INTO tenmo_user (username, password_hash)
VALUES ('bob', '$2a$10$G/MIQ7pUYupiVi72DxqHquxl73zfd7ZLNBoB2G6zUb.W16imI2.W2'),
       ('user', '$2a$10$Ud8gSvRS4G1MijNgxXWzcexeXlVs4kWDOkjE7JFIkNLKEuE57JAEy');

INSERT INTO account (user_id, balance)
VALUES (1001, 1000),
       (1002, 1000);

INSERT INTO transfer(account_id_from, account_id_to, balance_transfer, status, transfer_type)
VALUES (2001, 2002, 100, 'Pending', 'Request'),
       (2001, 2002, 50, 'Pending', 'Request'),
       (2002, 2001, 100, 'Pending', 'Request'),
       (2002, 2001, 50, 'Pending', 'Payment'),
       (2001, 2002, 100, 'Approved', 'Payment'),
       (2001, 2002, 2000, 'Pending', 'Payment'),
       (2001, 2002, 100, 'Pending', 'Request');



COMMIT;