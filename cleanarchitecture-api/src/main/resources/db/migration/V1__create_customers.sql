create table customers (
    id uuid primary key,
    name varchar(120) not null,
    email varchar(320) not null unique,
    status varchar(20) not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    deleted boolean not null default false,
    version bigint not null default 0
);

create index ix_customers_created_at on customers (created_at desc);
create index ix_customers_deleted on customers (deleted);
