



create table payment_method_type
(
    id          varchar(10) primary key ,
    name        varchar(255) not null,
    description text         null,
    created     datetime     not null default current_timestamp,
    modified    datetime     not null default current_timestamp on update current_timestamp
)
    auto_increment = 10001;-- set initial id value to 10001

insert into payment_method_type (id, name, description)
values
    ('CARD', 'Credit/Debit Card', 'Credit/Debit Card Payment Method'),
    ('PPAL', 'PayPal', 'PayPal Payment Method'),
    ('GPAY', 'Google Pay', 'Google Pay Payment Method'),
    ('APAY', 'Apple Pay', 'Apple Pay Payment Method'),
    ('AMZN', 'Amazon Pay', 'Amazon Pay Payment Method'),
    ('CASH', 'Cash Payment', 'Cash Payment Method'),
    ('BP', 'Bank Payment', 'Bank Payment Method'),
    ('OT', 'Other', 'Other Payment Method');

create table `user_payment_method`
(
    id              int primary key auto_increment,
    user_id         varchar(255) not null,
    payment_method_type_id varchar(10) not null,
    payment_method_token text not null,
    created         datetime     not null default current_timestamp,
    modified        datetime     not null default current_timestamp on update current_timestamp
)
    auto_increment = 10001;-- set initial id value to 10001

create table user_payment_method_CARD
(
    id              int primary key auto_increment,
    user_payment_method_id int not null,
    card_number     varchar(255) not null,
    card_holder_name varchar(255) not null,
    expiration_date varchar(10) not null,
    cvv             varchar(10) not null,
    created         datetime     not null default current_timestamp,
    modified        datetime     not null default current_timestamp on update current_timestamp
)
    auto_increment = 10001;-- set initial id value to 10001


create table user_payment_method_PP
(
    id              int primary key auto_increment,
    user_payment_method_id int not null,
    paypal_email    varchar(255) not null,
    created         datetime     not null default current_timestamp,
    modified        datetime     not null default current_timestamp on update current_timestamp
)
    auto_increment = 10001;-- set initial id value to 10001

    create table user_payment_method_GPAY
(

