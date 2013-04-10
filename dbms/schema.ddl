
    create table populations (
        id  bigserial not null,
        indication_concept_url varchar(255),
        primary key (id)
    );

    create table projects (
        id  bigserial not null,
        created_at date,
        description varchar(255),
        objective varchar(255),
        short_name varchar(255),
        updated_at date,
        owner int8,
        population int8,
        primary key (id)
    );

    create table users (
        id  bigserial not null,
        openid varchar(255) not null,
        primary key (id)
    );

    alter table projects 
        add constraint FKC479187A810DFE30 
        foreign key (owner) 
        references users;

    alter table projects 
        add constraint FKC479187A44AF8CC 
        foreign key (population) 
        references populations;

    alter table users 
        add constraint openid_ unique (openid);
