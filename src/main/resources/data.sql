

INSERT INTO hosts(name,password,created_at,created_by,update_at,update_by,version_no,delete_flag) 
VALUES('library01','library01','2024-6-27 00:00:00.000000',1,NULL,NULL,1,0);

INSERT INTO users(name, email,password,created_at,created_by,update_at,update_by,version_no,delete_flag) 
VALUES('田中太郎', 'tanaka@canon.co.jp', 'himitu','2024-6-27 00:00:00.000000',1,NULL,NULL,1,0);
INSERT INTO users(name, email,password,created_at,created_by,update_at,update_by,version_no,delete_flag) 
VALUES('鈴木一郎', 'suzuki@canon.co.jp', 'himitu','2024-6-27 00:00:00.000000',1,NULL,NULL,1,0);
INSERT INTO users(name, email,password,created_at,created_by,update_at,update_by,version_no,delete_flag) 
VALUES('山田花子', 'yamada@canon.co.jp', 'himitu','2024-6-27 00:00:00.000000',1,NULL,NULL,1,0);



INSERT INTO books(title, author,created_at,created_by,update_at,update_by,version_no,delete_flag) 
VALUES('ワンピース', '尾田栄一郎', '2024-6-27 00:00:00.000000',1,NULL,NULL,1,0);
INSERT INTO books(title, author,created_at,created_by,update_at,update_by,version_no,delete_flag) 
VALUES('進撃の巨人', '諫山創', '2024-6-27 00:00:00.000000',1,NULL,NULL,1,0);
INSERT INTO books(title, author,created_at,created_by,update_at,update_by,version_no,delete_flag) 
VALUES('東京喰種', '石田スイ', '2024-6-27 00:00:00.000000',1,NULL,NULL,1,0);
INSERT INTO books(title, author,created_at,created_by,update_at,update_by,version_no,delete_flag) 
VALUES('ドラえもん', '藤子f不二雄', '2024-6-27 00:00:00.000000',1,NULL,NULL,1,0);
INSERT INTO books(title, author,created_at,created_by,update_at,update_by,version_no,delete_flag) 
VALUES('ちびまる子ちゃん', 'さくらももこ', '2024-6-27 00:00:00.000000',1,NULL,NULL,1,0);


INSERT INTO rentals(user_id, book_id, rental_date, limit_date, return_date,created_at,created_by,update_at,update_by,version_no,delete_flag) 
VALUES(2,1,'2023-12-28','2024-1-2','2024-1-2', '2023-6-27 00:00:00.000000',1,NULL,NULL,2,0);
INSERT INTO rentals(user_id, book_id, rental_date, limit_date, return_date,created_at,created_by,update_at,update_by,version_no,delete_flag) 
VALUES(3,3,'2024-5-30','2024-6-4','2024-6-4', '2024-6-27 00:00:00.000000',1,NULL,NULL,2,0);
INSERT INTO rentals(user_id, book_id, rental_date, limit_date, return_date,created_at,created_by,update_at,update_by,version_no,delete_flag) 
VALUES(3,3,'2024-6-8','2024-6-13','2024-06-10', '2024-6-27 00:00:00.000000',1,NULL,NULL,2,0);
INSERT INTO rentals(user_id, book_id, rental_date, limit_date, return_date,created_at,created_by,update_at,update_by,version_no,delete_flag) 
VALUES(1,2,'2024-6-6','2024-6-14','2024-06-11', '2024-6-27 00:00:00.000000',1,NULL,NULL,2,0);
INSERT INTO rentals(user_id, book_id, rental_date, limit_date, return_date,created_at,created_by,update_at,update_by,version_no,delete_flag) 
VALUES(1,1,'2024-6-11','2024-6-16','2024-06-16', '2024-6-27 00:00:00.000000',1,NULL,NULL,2,0);
INSERT INTO rentals(user_id, book_id, rental_date, limit_date, return_date,created_at,created_by,update_at,update_by,version_no,delete_flag) 
VALUES(2,4,'2024-6-12','2024-6-17',NULL, '2024-6-27 00:00:00.000000',1,NULL,NULL,1,0);
INSERT INTO rentals(user_id, book_id, rental_date, limit_date, return_date,created_at,created_by,update_at,update_by,version_no,delete_flag) 
VALUES(1,5,'2024-6-16','2024-6-21',NULL, '2024-6-27 00:00:00.000000',1,NULL,NULL,1,0);
INSERT INTO rentals(user_id, book_id, rental_date, limit_date, return_date,created_at,created_by,update_at,update_by,version_no,delete_flag) 
VALUES(2,1,'2024-6-17','2024-6-22',NULL, '2024-6-27 00:00:00.000000',1,NULL,NULL,1,0);

