CREATE TABLE users (
    id bigserial PRIMARY KEY,  
    openid text not null unique
);
