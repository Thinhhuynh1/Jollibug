
-- ---------------------------------------------------------
-- A. Non-repeatable read
-- ---------------------------------------------------------
-- Session 1
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
SELECT THANHTIEN
FROM DONHANG
WHERE MADH = 1;

-- Session 2
UPDATE DONHANG
SET THANHTIEN = THANHTIEN + 10000
WHERE MADH = 1;
COMMIT;

-- Session 1
SELECT THANHTIEN
FROM DONHANG
WHERE MADH = 1;
COMMIT;

-- CACH GIAI QUYET 1: dung SERIALIZABLE de giu snapshot on dinh
-- Session 1
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
SELECT THANHTIEN
FROM DONHANG
WHERE MADH = 1;

-- Session 2
UPDATE DONHANG
SET THANHTIEN = THANHTIEN + 10000
WHERE MADH = 1;
COMMIT;

-- Session 1
SELECT THANHTIEN
FROM DONHANG
WHERE MADH = 1;
COMMIT;

-- CACH GIAI QUYET 2: khoa dong can doc neu muon tranh bi sua chen ngang
-- Session 1
SELECT THANHTIEN
FROM DONHANG
WHERE MADH = 1
FOR UPDATE;

-- Session 2
-- UPDATE DONHANG
-- SET THANHTIEN = THANHTIEN + 10000
-- WHERE MADH = 1;
-- Session 2 phai cho Session 1 COMMIT/ROLLBACK

-- Session 1
COMMIT;

-- ---------------------------------------------------------
-- B. Phantom read
-- ---------------------------------------------------------
-- Session 1
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
SELECT COUNT(*) AS SO_DON, NVL(SUM(THANHTIEN), 0) AS DOANH_THU
FROM DONHANG
WHERE TRANGTHAIDON = 'CONFIRMED';

-- Session 2
INSERT INTO DONHANG (MATK_KH, NGAYDAT, TONGTIENMON, TIENGIAMGIA, THANHTIEN, TRANGTHAIDON, UPDATED_AT)
VALUES (1, CURRENT_TIMESTAMP, 120000, 0, 120000, 'CONFIRMED', CURRENT_TIMESTAMP);
COMMIT;

-- Session 1
SELECT COUNT(*) AS SO_DON, NVL(SUM(THANHTIEN), 0) AS DOANH_THU
FROM DONHANG
WHERE TRANGTHAIDON = 'CONFIRMED';
COMMIT;

-- CACH GIAI QUYET: dung SERIALIZABLE de giu tap ban ghi nhin thay khong doi
-- Session 1
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
SELECT COUNT(*) AS SO_DON, NVL(SUM(THANHTIEN), 0) AS DOANH_THU
FROM DONHANG
WHERE TRANGTHAIDON = 'CONFIRMED';

-- Session 2
INSERT INTO DONHANG (MATK_KH, NGAYDAT, TONGTIENMON, TIENGIAMGIA, THANHTIEN, TRANGTHAIDON, UPDATED_AT)
VALUES (1, CURRENT_TIMESTAMP, 130000, 0, 130000, 'CONFIRMED', CURRENT_TIMESTAMP);
COMMIT;

-- Session 1
SELECT COUNT(*) AS SO_DON, NVL(SUM(THANHTIEN), 0) AS DOANH_THU
FROM DONHANG
WHERE TRANGTHAIDON = 'CONFIRMED';
COMMIT;

-- ---------------------------------------------------------
-- C. Deadlock theo flow checkout that su
-- ---------------------------------------------------------
-- Trong project hien tai:
-- INSERT vao CHITIETDH se kich hoat trigger trg_ctdh_upd_soluongton
-- Trigger này SELECT ... FOR UPDATE trên MONAN rồi UPDATE MONAN
-- Nếu 2 checkout cùng đặt 2 món theo thứ tự ngược nhau, deadlock có thể xảy ra

-- Chuẩn bị 2 đơn hàng mới
INSERT INTO DONHANG (MATK_KH, NGAYDAT, TONGTIENMON, TIENGIAMGIA, THANHTIEN, TRANGTHAIDON, UPDATED_AT)
VALUES (1, CURRENT_TIMESTAMP, 0, 0, 0, 'PENDING', CURRENT_TIMESTAMP);

INSERT INTO DONHANG (MATK_KH, NGAYDAT, TONGTIENMON, TIENGIAMGIA, THANHTIEN, TRANGTHAIDON, UPDATED_AT)
VALUES (1, CURRENT_TIMESTAMP, 0, 0, 0, 'PENDING', CURRENT_TIMESTAMP);

COMMIT;

-- Thay :ORDER_A và :ORDER_B bằng 2 MaDH vừa tạo

-- Session 1: insert món 1 trước, trigger sẽ khóa MONAN(1)
INSERT INTO CHITIETDH (MADH, MAMON, TENMON, SOLUONG, DONGIA, THANHTIEN)
SELECT :ORDER_A, MAMON, TENMON, 1, GIA, GIA
FROM MONAN
WHERE MAMON = 1;

-- Session 2: insert món 2 trước, trigger sẽ khóa MONAN(2)
INSERT INTO CHITIETDH (MADH, MAMON, TENMON, SOLUONG, DONGIA, THANHTIEN)
SELECT :ORDER_B, MAMON, TENMON, 1, GIA, GIA
FROM MONAN
WHERE MAMON = 2;

-- Session 1: tiếp tục insert món 2, nay sẽ đợi MONAN(2)
INSERT INTO CHITIETDH (MADH, MAMON, TENMON, SOLUONG, DONGIA, THANHTIEN)
SELECT :ORDER_A, MAMON, TENMON, 1, GIA, GIA
FROM MONAN
WHERE MAMON = 2;

-- Session 2: tiếp tục insert món 1, nay sẽ đợi MONAN(1)
INSERT INTO CHITIETDH (MADH, MAMON, TENMON, SOLUONG, DONGIA, THANHTIEN)
SELECT :ORDER_B, MAMON, TENMON, 1, GIA, GIA
FROM MONAN
WHERE MAMON = 1;

-- Oracle có thể báo ORA-00060 deadlock detected

-- CÁCH GIẢI QUYẾT:
-- Thống nhất thứ tự xử lý item trong checkout
-- Ví dụ luôn sắp xếp danh sách món theo MAMON tăng dần trước khi insert CHITIETDH

-- ---------------------------------------------------------
-- D. Lock ordering that avoids deadlock on MONAN
-- ---------------------------------------------------------
-- Session 1
SELECT SOLUONGTON
FROM MONAN
WHERE MAMON = 1
FOR UPDATE;

SELECT SOLUONGTON
FROM MONAN
WHERE MAMON = 2
FOR UPDATE;

-- Session 2
SELECT SOLUONGTON
FROM MONAN
WHERE MAMON = 1
FOR UPDATE;

-- Session 2 sẽ phải chờ Session 1, nhưng không bị deadlock
-- vì cả hai session đều khóa theo thứ tự: MONAN(1) -> MONAN(2)

-- ---------------------------------------------------------
-- E. Lost update / locking với tồn kho theo luồng checkout cũ
-- ---------------------------------------------------------
-- HIỆN TƯỢNG
-- Session 1
SELECT SOLUONGTON
FROM MONAN
WHERE MAMON = 1;
-- Giả sử Session 1 tính toán tồn mới = 45

-- Session 2
SELECT SOLUONGTON
FROM MONAN
WHERE MAMON = 1;
-- Giả sử Session 2 tính toán tồn mới = 60
UPDATE MONAN
SET SOLUONGTON = 60
WHERE MAMON = 1;
COMMIT;

-- Session 1
UPDATE MONAN
SET SOLUONGTON = 45
WHERE MAMON = 1;
COMMIT;
-- Update của Session 2 bị mất

-- CÁCH GIẢI QUYẾT
-- Session 1
SELECT SOLUONGTON
FROM MONAN
WHERE MAMON = 1
FOR UPDATE;

-- Session 2
SELECT SOLUONGTON
FROM MONAN
WHERE MAMON = 1
FOR UPDATE;
-- Session 2 phải chờ Session 1

-- Session 1
UPDATE MONAN
SET SOLUONGTON = SOLUONGTON - 5
WHERE MAMON = 1;
COMMIT;

-- Session 2
UPDATE MONAN
SET SOLUONGTON = SOLUONGTON + 10
WHERE MAMON = 1;
COMMIT;

-- ---------------------------------------------------------
-- F. Voucher race condition và cách xử lý
-- ---------------------------------------------------------
-- HIỆN TƯỢNG NẾU CHỈ ĐỌC RỒI TÍNH TOÁN NGOÀI APP
-- Session 1
SELECT SOLUONG, SOLANSUDUNG
FROM MAGIAMGIA
WHERE MAGG = 1;

-- Session 2
SELECT SOLUONG, SOLANSUDUNG
FROM MAGIAMGIA
WHERE MAGG = 1;

-- Cả 2 session cùng thấy còn lượt và đều tiếp tục sử dụng

-- CÁCH GIẢI QUYẾT THEO LUỒNG CHECKOUT ĐÃ SỬA
-- Session 1
BEGIN
    PROC_RESERVE_VOUCHER(1);
END;
/

-- Session 2
BEGIN
    PROC_RESERVE_VOUCHER(1);
END;
/
-- Session 2 sẽ chờ hoặc báo hết lượt tùy trạng thái dữ liệu sau khi Session 1 commit
