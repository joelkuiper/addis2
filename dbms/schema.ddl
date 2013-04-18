
    create table interventions (
        id  bigserial not null,
        concept_url varchar(255),
        primary key (id)
    );

    create table outcomes (
        id  bigserial not null,
        concept_url varchar(255),
        primary key (id)
    );

    create table populations (
        id  bigserial not null,
        concept_url varchar(255),
        primary key (id)
    );

    create table project_interventions (
        project int8 not null,
        interventions int8 not null
    );

    create table project_outcomes (
        project int8 not null,
        outcomes int8 not null
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

    alter table project_interventions 
        add constraint UK557C39D6840FFC7C unique (interventions);

    alter table project_interventions 
        add constraint FK557C39D6D6052905 
        foreign key (interventions) 
        references interventions;

    alter table project_interventions 
        add constraint FK557C39D6BC260140 
        foreign key (project) 
        references projects;

    alter table project_outcomes 
        add constraint UKBC9A35C7374E361 unique (outcomes);

    alter table project_outcomes 
        add constraint FKBC9A35C7A28655C1 
        foreign key (outcomes) 
        references outcomes;

    alter table project_outcomes 
        add constraint FKBC9A35C7BC260140 
        foreign key (project) 
        references projects;

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
