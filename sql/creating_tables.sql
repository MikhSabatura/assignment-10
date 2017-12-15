CREATE TABLE USERS (
  id_user       NUMBER NOT NULL PRIMARY KEY,
  user_login    VARCHAR2(20),
  user_password VARCHAR2(25)
);

CREATE TABLE GROUPS (
  id_group          NUMBER NOT NULL PRIMARY KEY,
  group_name        VARCHAR2(20),
  group_description VARCHAR2(50)
);

CREATE TABLE GROUPS_USERS (
  id_user  NUMBER NOT NULL,
  id_group NUMBER NOT NULL,
  PRIMARY KEY (id_user, id_group),
  FOREIGN KEY (id_user) REFERENCES USERS (id_user),
  FOREIGN KEY (id_group) REFERENCES GROUPS (id_group)
);
