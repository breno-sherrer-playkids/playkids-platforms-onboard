/*
drop table if exists "user" cascade;
drop table if exists lottery cascade;
drop table if exists ticket cascade;
drop table if exists eventlog cascade;
*/

CREATE TABLE IF NOT EXISTS "user" (id SERIAL PRIMARY KEY, email VARCHAR(80) NOT NULL UNIQUE, password TEXT NOT NULL, credits DECIMAL(10, 4) NOT NULL, congratulate BOOLEAN DEFAULT false NOT NULL);

CREATE TABLE IF NOT EXISTS ticket (id SERIAL PRIMARY KEY, iduser INT NOT NULL, idlottery INT NOT NULL, buydatetime TIMESTAMP NOT NULL,  FOREIGN KEY (iduser) REFERENCES "user"(id) ON DELETE RESTRICT);

CREATE TABLE IF NOT EXISTS lottery (id SERIAL PRIMARY KEY, status INT DEFAULT 0 NOT NULL, title VARCHAR(30) NOT NULL, prize DECIMAL(10, 4) NOT NULL, ticketprice DECIMAL(10, 4) NOT NULL, idwinnerticket INT NULL, lotterydatetime TIMESTAMP NOT NULL,  FOREIGN KEY (idwinnerticket) REFERENCES ticket(id) ON DELETE RESTRICT);

alter table ticket
    add constraint fk_ticket_lottery
    foreign key (IDLottery)
    REFERENCES lottery (ID) on delete restrict;

CREATE TABLE eventlog (
	id SERIAL PRIMARY KEY,
	type varchar(30) NOT NULL,
	author varchar(80) NOT NULL,
	eventDateTime timestamp NOT NULL
);