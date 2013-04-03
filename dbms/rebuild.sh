#!/bin/bash
PRIV_USER=postgres
PRIV_PWD=develop

APP_USER=addis
APP_PWD=develop

PORT=5432
HOST=localhost
USER=addis
PASSWORD=develop
DATABASE=addis

PGPASSWORD=$PRIV_PWD psql -h $HOST -p $PORT -U $PRIV_USER -c  "SELECT pg_terminate_backend(pg_stat_activity.procpid) FROM pg_stat_activity WHERE pg_stat_activity.datname = '$DATABASE'" || exit
PGPASSWORD=$PRIV_PWD psql -h $HOST -p $PORT -U $PRIV_USER -c "DROP DATABASE IF EXISTS $DATABASE" || exit
PGPASSWORD=$PRIV_PWD psql -h $HOST -p $PORT -U $PRIV_USER -d postgres -c "CREATE DATABASE $DATABASE ENCODING 'utf-8' OWNER $USER" || exit
PGPASSWORD=$PRIV_PWD psql -h $HOST -p $PORT -U $PRIV_USER -d $DATABASE -c 'CREATE EXTENSION IF NOT EXISTS "uuid-ossp"' || exit

PGPASSWORD=$APP_PWD psql -h $HOST -p $PORT -U $APP_USER --quiet --set ON_ERROR_STOP=1 -f schema.ddl || exit
