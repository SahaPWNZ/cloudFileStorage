create table users
(
    id       bigint generated by default as identity
        primary key,
    login    varchar(255) UNIQUE ,
    password varchar(255)
);