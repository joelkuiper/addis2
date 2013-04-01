CREATE TABLE users (
    id serial PRIMARY KEY,  
    email varchar(255) not null unique
);
