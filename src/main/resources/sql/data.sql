INSERT INTO user (username,email, password, activated) VALUES ('admin', 'admin@mail.me', 'b8f57d6d6ec0a60dfe2e20182d4615b12e321cad9e2979e0b9f81e0d6eda78ad9b6dcfe53e4e22d1', true);
INSERT INTO user (username,email, password, activated) VALUES ('jdoe', 'user@mail.me', 'd6dfa9ff45e03b161e7f680f35d90d5ef51d243c2a8285aa7e11247bc2c92acde0c2bb626b1fac74', true);
INSERT INTO user (username,email, password, activated) VALUES ('tmunim', 'user@mail.me', 'd6dfa9ff45e03b161e7f680f35d90d5ef51d243c2a8285aa7e11247bc2c92acde0c2bb626b1fac74', true);
INSERT INTO user (username,email, password, activated) VALUES ('jjones', 'user@mail.me', 'd6dfa9ff45e03b161e7f680f35d90d5ef51d243c2a8285aa7e11247bc2c92acde0c2bb626b1fac74', true);

INSERT INTO authority (name) VALUES ('ROLE_USER');
INSERT INTO authority (name) VALUES ('ROLE_ADMIN');

INSERT INTO user_authority (username,authority) VALUES ('jdoe', 'ROLE_USER');
INSERT INTO user_authority (username,authority) VALUES ('tmunim', 'ROLE_USER');
INSERT INTO user_authority (username,authority) VALUES ('jjones', 'ROLE_USER');
INSERT INTO user_authority (username,authority) VALUES ('admin', 'ROLE_USER');
INSERT INTO user_authority (username,authority) VALUES ('admin', 'ROLE_ADMIN');

INSERT INTO employee(first_name,last_name,user_name) values ('john','doe','jdoe');
INSERT INTO employee(first_name,last_name,user_name) values ('tina','munim','tmunim');
INSERT INTO employee(first_name,last_name,user_name) values ('jack','jones','jjones');

INSERT INTO tweet(entry,hashtag,employee_id) values ('Ni Hao','greeting',1);
INSERT INTO tweet(entry,hashtag,employee_id) values ('Hola','greeting',1);
INSERT INTO tweet(entry,hashtag,employee_id) values ('Nice day','greeting',1);
INSERT INTO tweet(entry,hashtag,employee_id) values ('Good weather','greeting',2);
INSERT INTO tweet(entry,hashtag,employee_id) values ('Thanks','greeting',2);
INSERT INTO tweet(entry,hashtag,employee_id) values ('Hello','greeting',2);
INSERT INTO tweet(entry,hashtag,employee_id) values ('Good Morning','greeting',3);
INSERT INTO tweet(entry,hashtag,employee_id) values ('How are you?','greeting',3);
INSERT INTO tweet(entry,hashtag,employee_id) values ('Good Night','greeting',3);
INSERT INTO tweet(entry,hashtag,employee_id) values ('Chao','greeting',3);

INSERT INTO follower(employee_id,follower_id) values(1,3);
INSERT INTO follower(employee_id,follower_id) values(1,2);
INSERT INTO follower(employee_id,follower_id) values(2,3);