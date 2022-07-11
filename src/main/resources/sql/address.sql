-- auto-generated definition
create table address
(
    id                     integer default nextval('address_id_seq1'::regclass) not null
        constraint address_pk
            primary key,
    billing_account_number varchar(255)                                         not null
        constraint address_customer_billing_account_number_fk
            references customer,
    address_line1          varchar(255)                                         not null,
    address_line2          varchar(255),
    city                   varchar(255)                                         not null,
    zip                    varchar(255)                                         not null,
    state                  varchar(255)                                         not null
);

alter table address
    owner to postgres;

create unique index address_id_uindex
    on address (id);

