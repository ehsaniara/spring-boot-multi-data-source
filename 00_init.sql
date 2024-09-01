create user replicator with replication encrypted password 'postgres_user_for_db_read';
select pg_create_physical_replication_slot('replication_slot');
