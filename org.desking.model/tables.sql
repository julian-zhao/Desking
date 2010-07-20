


set schema app;

SELECT PRODUCT.* FROM PRODUCT INNER JOIN ORDER_PRODUCT r ON r.PRODUCTID = PRODUCT.ID WHERE r.ORDERID = 'O0001';


create table "ORDER" (
id varchar(40) not null,
name varchar(100) not null,
customerid varchar(40),
primary key(id)
);

create table Order_Product (
OrderId varchar(40) not null,
ProductId varchar(40) not null
);

create table "Customer" (
id varchar(40) not null,
name varchar(100) not null,
primary key(id)
);

create table ProductCategory (
id varchar(40) not null,
name varchar(50) not null,
primary key(id)
);

create table Product (
id varchar(40) not null,
name varchar(50) not null,
description varchar(400),
categoryId varchar(40) not null,
primary key(id)
);


