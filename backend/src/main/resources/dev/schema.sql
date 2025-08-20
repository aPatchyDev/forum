-- Delete existing tables
drop table if exists member;
drop table if exists post;
drop table if exists comment;

-- Create tables

create table member(
       id bigint unsigned auto_increment primary key not null,
       username varchar(20) unique not null,
       password varchar(255) not null
);

create table post(
       id bigint unsigned auto_increment primary key not null,
       author_id bigint unsigned,
       title varchar(255) not null,
       body text not null,
       created_at timestamp not null,
       updated_at timestamp not null,

       foreign key (author_id) references member(id) on delete set null
);

create table comment(
       id bigint unsigned auto_increment primary key not null,
       post_id bigint unsigned not null,
       author_id bigint unsigned,
       body text not null,

       foreign key (author_id) references member(id) on delete set null,
       foreign key (post_id) references post(id)
);
