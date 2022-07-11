-- auto-generated definition
create table customer
(
    billing_account_number varchar(255) not null
        primary key,
    email_id               varchar(255) not null,
    first_name             varchar(255) not null,
    last_name              varchar(255) not null,
    phone_number           varchar(255) not null
);

alter table customer
    owner to postgres;

