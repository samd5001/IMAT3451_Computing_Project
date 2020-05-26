/*SQL commands used to create user and databases on the server*/
CREATE DATABASE data;
USE data
CREATE TABLE exercises (
    name varchar(30) NOT NULL PRIMARY KEY,
    description varchar(400) NOT NULL,
    type tinyint NOT NULL,
    imageURL varchar(50), 
    minThreshold int,
    maxThreshold int,
    areasWorked varchar(50),
    userMade tinyint NOT NULL
);

CREATE TABLE plans(
    name varchar(20) NOT NULL UNIQUE PRIMARY KEY,
    description varchar(300) NOT NULL,
    days varchar (14) NOT NULL,
    userMade tinyint NOT NULL
);

CREATE TABLE days(
    planName varchar(40) NOT NULL,
    dayNumber int NOT NULL,
    exercises varchar(300) NOT NULL,
    sets varchar(100) NOT NULL,
    sets varchar(200) NOT NULL,
    PRIMARY KEY (planName, dayNumber),
    FOREIGN KEY (planName) REFERENCES plans(name) ON DELETE CASCADE)
);

CREATE DATABASE users;
CREATE TABLE users (
    email varchar(50) NOT NULL PRIMARY KEY,
    passwordHash varchar(50) NOT NULL,
    passwordSalt varchar(10) NOT NULL,
    name varchar(40) NOT NULL,
    dob date,
    gender tinyint,
    height float,
    weight float,
    goal tinyint,
    units tinyint
);

CREATE USER 'web'@'localhost' IDENTIFIED BY 'Memes123';
GRANT SELECT on users.user TO 'web'@'localhost';
GRANT INSERT on users.user TO 'web'@'localhost';
GRANT DELETE on users.user TO 'web'@'localhost';
GRANT UPDATE on users.user TO 'web'@'localhost';
GRANT ALL PRIVILEGES on data.* TO 'web'@'localhost';
FLUSH PRIVILEGES;