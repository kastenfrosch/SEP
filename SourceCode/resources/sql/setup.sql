PRAGMA foreign_keys = OFF;

DROP TABLE IF EXISTS semester;
CREATE TABLE semester (
  semester_id INTEGER PRIMARY KEY AUTOINCREMENT,
  description VARCHAR NOT NULL
);

DROP TABLE IF EXISTS `group`;
CREATE TABLE `group` (
  group_id INTEGER PRIMARY KEY AUTOINCREMENT,
  name VARCHAR NOT NULL,
  groupage_id INTEGER NOT NULL,
  semester_id INTEGER,
  FOREIGN KEY (groupage_id) REFERENCES groupage(groupage_id),
  FOREIGN KEY (semester_id) REFERENCES semester(semester_id)
);

DROP TABLE IF EXISTS person;
CREATE TABLE person (
  person_id INTEGER PRIMARY KEY AUTOINCREMENT,
  firstname VARCHAR NOT NULL,
  lastname VARCHAR NOT NULL,
  email VARCHAR NOT NULL
);

DROP TABLE IF EXISTS student;
CREATE TABLE student (
  student_id INTEGER PRIMARY KEY AUTOINCREMENT,
  person_id INTEGER NOT NULL,
  matr_no VARCHAR NOT NULL,
  group_id INTEGER NOT NULL,
  semester_id INTEGER NOT NULL,
  FOREIGN KEY (group_id) REFERENCES `group`(group_id),
  FOREIGN KEY (semester_id) REFERENCES semester(semester_id),
  FOREIGN KEY (person_id) REFERENCES person(person_id)
);


DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  user_name VARCHAR PRIMARY KEY,
  person_id INTEGER NOT NULL,
  password VARCHAR NOT NULL
);

DROP TABLE IF EXISTS groupage;
CREATE TABLE groupage (
  groupage_id INTEGER PRIMARY KEY AUTOINCREMENT,
  description VARCHAR NOT NULL,
  semester_id INTEGER,
  FOREIGN KEY (semester_id) REFERENCES semester(semester_id)
);

PRAGMA foreign_keys = ON;