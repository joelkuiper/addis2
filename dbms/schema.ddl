
    create table projects (
        id  bigserial not null,
        short_name varchar(255),
        description varchar(255),
        objective varchar(255),
        created_at date,
        updated_at date,
        owner int8,
        primary key (id)
    );

    create table users (
        id  bigserial not null,
        openid varchar(255) not null unique,
        primary key (id)
    );

    alter table projects 
        add constraint FKC479187A810DFE30 
        foreign key (owner) 
        references users;
