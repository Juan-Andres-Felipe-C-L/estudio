create database study;
use study;

create table roles(
id int auto_increment,
name varchar(20) not null,
primary key(id)
);

create table users(
id int auto_increment,
username varchar(30) not null,
email varchar(30) not null,
password varchar(50) not null,
rol_id int not null,
foreign key (rol_id) references roles(id),
primary key(id)
);

insert into roles(name) values('ADMIN'),
('EMPLEADO'),
('CLIENTE');

insert into users(username, email, password, rol_id) values('Maik', 'michael@sena.com', 'admin', 1),
('Sara', 'sara@sena.com', 'empleada', 2),
('David', 'david@sena.com', 123, 3); 
