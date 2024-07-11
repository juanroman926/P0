pragma foreign_keys;
pragma foreign_keys = ON;

drop table if exists "user";
drop table if exists "checkingAccount";

create table "user"(
    userId integer PRIMARY KEY autoincrement,
    username text,
    password text
);

insert into "user" (username, password) values ('admin', '1234');

create table "checkingAccount"(
    accountId integer primary key autoincrement,
    balance double default 0.0,
    userId integer not null references user(userId)
);

