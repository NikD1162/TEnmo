BEGIN TRANSACTION;

DROP TABLE IF EXISTS account CASCADE;

CREATE TABLE account (
	account_id serial,
	user_id int,
	balance numeric(13, 2),
	CONSTRAINT PK_account PRIMARY KEY (account_id),
	CONSTRAINT FK_user FOREIGN KEY (user_id)
);


INSERT INTO account (account_id, user_id, balance)
VALUES (1, 1, 100),


COMMIT;