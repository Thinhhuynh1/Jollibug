INSERT INTO VAITRO (MaVT, TenVT) 
VALUES 
('1', 'ROLE_ADMIN'), 
('2', 'ROLE_CLIENT'),
('3', 'ROLE_STAFF'),
('4', 'ROLE_MANAGER');

-- Tài khoản mẫu với pass là 123456 | pass ở dưới là 123456 đổi thành BCrypt lên Page BCrypt đổi thử đi
INSERT INTO USER (Password, Email, HoTen, SDT, TrangThai, MaVT) 
VALUES 
('$2a$12$3iN4vL5Rclp3TgCOvlYi9Ow5huS.YavR922zHw1WhfchxgPwlMq.e', 'user1@fastfood.vn', 'User A', '0900000001', 'ACTIVE', 2),
('$2a$12$3iN4vL5Rclp3TgCOvlYi9Ow5huS.YavR922zHw1WhfchxgPwlMq.e', 'user2@fastfood.vn', 'User B', '0900000002', 'ACTIVE', 3),
('$2a$12$3iN4vL5Rclp3TgCOvlYi9Ow5huS.YavR922zHw1WhfchxgPwlMq.e', 'user3@fastfood.vn', 'User C', '0900000003', 'ACTIVE', 4),
('$2a$12$3iN4vL5Rclp3TgCOvlYi9Ow5huS.YavR922zHw1WhfchxgPwlMq.e', 'user4@fastfood.vn', 'User D', '0900000004', 'ACTIVE', 1),
('$2a$12$3iN4vL5Rclp3TgCOvlYi9Ow5huS.YavR922zHw1WhfchxgPwlMq.e', 'user5@fastfood.vn', 'User E', '0900000005', 'BANNED', 2), 
('$2a$12$3iN4vL5Rclp3TgCOvlYi9Ow5huS.YavR922zHw1WhfchxgPwlMq.e', 'user6@fastfood.vn', 'User F', '0900000006', 'ACTIVE', 2),
('$2a$12$3iN4vL5Rclp3TgCOvlYi9Ow5huS.YavR922zHw1WhfchxgPwlMq.e', 'user7@fastfood.vn', 'User G', '0900000007', 'ACTIVE', 2),
('$2a$12$3iN4vL5Rclp3TgCOvlYi9Ow5huS.YavR922zHw1WhfchxgPwlMq.e', 'user8@fastfood.vn', 'User H', '0900000008', 'ACTIVE', 2),
('$2a$12$3iN4vL5Rclp3TgCOvlYi9Ow5huS.YavR922zHw1WhfchxgPwlMq.e', 'user9@fastfood.vn', 'User I', '0900000009', 'ACTIVE', 2),
('$2a$12$3iN4vL5Rclp3TgCOvlYi9Ow5huS.YavR922zHw1WhfchxgPwlMq.e', 'user10@fastfood.vn', 'User J', '0900000010', 'ACTIVE', 2),
('$2a$12$3iN4vL5Rclp3TgCOvlYi9Ow5huS.YavR922zHw1WhfchxgPwlMq.e', 'user11@fastfood.vn', 'User K', '0900000011', 'ACTIVE', 2),
('$2a$12$3iN4vL5Rclp3TgCOvlYi9Ow5huS.YavR922zHw1WhfchxgPwlMq.e', 'user12@fastfood.vn', 'User L', '0900000012', 'ACTIVE', 2),
('$2a$12$3iN4vL5Rclp3TgCOvlYi9Ow5huS.YavR922zHw1WhfchxgPwlMq.e', 'user13@fastfood.vn', 'User M', '0900000013', 'ACTIVE', 2),
('$2a$12$3iN4vL5Rclp3TgCOvlYi9Ow5huS.YavR922zHw1WhfchxgPwlMq.e', 'user14@fastfood.vn', 'User N', '0900000014', 'ACTIVE', 2),
('$2a$12$3iN4vL5Rclp3TgCOvlYi9Ow5huS.YavR922zHw1WhfchxgPwlMq.e', 'user15@fastfood.vn', 'User O', '0900000015', 'BANNED', 2),
('$2a$12$3iN4vL5Rclp3TgCOvlYi9Ow5huS.YavR922zHw1WhfchxgPwlMq.e', 'user16@fastfood.vn', 'User P', '0900000016', 'ACTIVE', 2),
('$2a$12$3iN4vL5Rclp3TgCOvlYi9Ow5huS.YavR922zHw1WhfchxgPwlMq.e', 'user17@fastfood.vn', 'User Q', '0900000017', 'ACTIVE', 2),
('$2a$12$3iN4vL5Rclp3TgCOvlYi9Ow5huS.YavR922zHw1WhfchxgPwlMq.e', 'user18@fastfood.vn', 'User R', '0900000018', 'ACTIVE', 2),
('$2a$12$3iN4vL5Rclp3TgCOvlYi9Ow5huS.YavR922zHw1WhfchxgPwlMq.e', 'user19@fastfood.vn', 'User S', '0900000019', 'ACTIVE', 2),
('$2a$12$3iN4vL5Rclp3TgCOvlYi9Ow5huS.YavR922zHw1WhfchxgPwlMq.e', 'user20@fastfood.vn', 'User T', '0900000020', 'ACTIVE', 2);