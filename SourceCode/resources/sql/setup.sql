PRAGMA foreign_keys = OFF;

CREATE TABLE semester (
  semester_id INTEGER PRIMARY KEY AUTOINCREMENT,
  description VARCHAR NOT NULL
);

CREATE TABLE `group` (
  group_id INTEGER PRIMARY KEY AUTOINCREMENT,
  name VARCHAR NOT NULL,
  groupage_id INTEGER NOT NULL,
  semester_id INTEGER,
  FOREIGN KEY (groupage_id) REFERENCES groupage(groupage_id),
  FOREIGN KEY (semester_id) REFERENCES semester(semester_id)
);

CREATE TABLE student (
  student_id INTEGER PRIMARY KEY AUTOINCREMENT,
  matr_no VARCHAR NOT NULL,
  firstname VARCHAR NOT NULL,
  lastname VARCHAR NOT NULL,
  email VARCHAR NOT NULL,
  group_id INTEGER NOT NULL,
  semester_id INTEGER NOT NULL,
  FOREIGN KEY (group_id) REFERENCES `group`(group_id),
  FOREIGN KEY (semester_id) REFERENCES semester(semester_id)
);

CREATE TABLE groupage (
  groupage_id INTEGER PRIMARY KEY AUTOINCREMENT,
  description VARCHAR NOT NULL,
  semester_id INTEGER,
  FOREIGN KEY (semester_id) REFERENCES semester(semester_id)
);

PRAGMA foreign_keys = ON;