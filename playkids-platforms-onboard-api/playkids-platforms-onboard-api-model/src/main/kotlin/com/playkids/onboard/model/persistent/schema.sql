/*
drop table if exists "user" cascade;
drop table if exists lottery cascade;
drop table if exists ticket cascade;
drop table if exists eventlog cascade;
*/

CREATE TABLE "user" (id SERIAL PRIMARY KEY, email VARCHAR(80) NOT NULL UNIQUE, password TEXT NOT NULL, credits DECIMAL(10, 4) NOT NULL, congratulate BOOLEAN DEFAULT false NOT NULL);

CREATE TABLE ticket (id SERIAL PRIMARY KEY, iduser INT NOT NULL, idlottery INT NOT NULL, buydatetime TIMESTAMP NOT NULL,  FOREIGN KEY (iduser) REFERENCES "user"(id) ON DELETE RESTRICT);

CREATE TABLE lottery (id SERIAL PRIMARY KEY, status INT DEFAULT 0 NOT NULL, title VARCHAR(30) NOT NULL, prize DECIMAL(10, 4) NOT NULL, ticketprice DECIMAL(10, 4) NOT NULL, idwinnerticket INT NULL, lotterydatetime TIMESTAMP NOT NULL,  FOREIGN KEY (idwinnerticket) REFERENCES ticket(id) ON DELETE RESTRICT);

alter table ticket
    add constraint fk_ticket_lottery
    foreign key (IDLottery)
    REFERENCES lottery (ID) on delete restrict;

CREATE TABLE eventlog (	id SERIAL PRIMARY KEY,	type varchar(30) NOT NULL,	author varchar(80) NOT NULL,	eventDateTime timestamp NOT NULL);

-- Template Lotteries
insert into lottery (status, title, prize, ticketprice, lotterydatetime)
values (1, 'Some Random Lottery', 200000, 15, current_timestamp + interval '5 minutes');

insert into lottery (status, title, prize, ticketprice, lotterydatetime)
values (1, 'Movile Build. guessing raffle', 50, 15, current_timestamp + interval '10 minutes');

insert into lottery (status, title, prize, ticketprice, lotterydatetime)
values (1, 'MEGASENA', 300000, 5, current_timestamp + interval '15 minutes');