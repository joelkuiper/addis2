CREATE TABLE users (
    id serial PRIMARY KEY,  
    openid text not null unique
);
