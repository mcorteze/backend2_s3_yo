create database letrasypapeles_db;
create user 'myuser'@'%' identified by 'password';
grant all on letrasypapeles_db.* to 'myuser'@'%';