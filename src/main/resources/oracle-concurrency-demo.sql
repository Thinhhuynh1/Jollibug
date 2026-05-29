
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
-- Trigger nay SELECT ... FOR UPDATE tren MONAN roi UPDATE MONAN
-- Neu 2 checkout cung dat 2 mon theo thu tu nguoc nhau, deadlock co the xay ra

-- Chuan bi 2 don hang moi
INSERT INTO DONHANG (MATK_KH, NGAYDAT, TONGTIENMON, TIENGIAMGIA, THANHTIEN, TRANGTHAIDON, UPDATED_AT)
VALUES (1, CURRENT_TIMESTAMP, 0, 0, 0, 'PENDING', CURRENT_TIMESTAMP);

INSERT INTO DONHANG (MATK_KH, NGAYDAT, TONGTIENMON, TIENGIAMGIA, THANHTIEN, TRANGTHAIDON, UPDATED_AT)
VALUES (1, CURRENT_TIMESTAMP, 0, 0, 0, 'PENDING', CURRENT_TIMESTAMP);

COMMIT;

-- Thay :ORDER_A va :ORDER_B bang 2 MaDH vua tao

-- Session 1: insert mon 1 truoc, trigger se khoa MONAN(1)
INSERT INTO CHITIETDH (MADH, MAMON, TENMON, SOLUONG, DONGIA, THANHTIEN)
SELECT :ORDER_A, MAMON, TENMON, 1, GIA, GIA
FROM MONAN
WHERE MAMON = 1;

-- Session 2: insert mon 2 truoc, trigger se khoa MONAN(2)
INSERT INTO CHITIETDH (MADH, MAMON, TENMON, SOLUONG, DONGIA, THANHTIEN)
SELECT :ORDER_B, MAMON, TENMON, 1, GIA, GIA
FROM MONAN
WHERE MAMON = 2;

-- Session 1: tiep tuc insert mon 2, nay se doi MONAN(2)
INSERT INTO CHITIETDH (MADH, MAMON, TENMON, SOLUONG, DONGIA, THANHTIEN)
SELECT :ORDER_A, MAMON, TENMON, 1, GIA, GIA
FROM MONAN
WHERE MAMON = 2;

-- Session 2: tiep tuc insert mon 1, nay se doi MONAN(1)
INSERT INTO CHITIETDH (MADH, MAMON, TENMON, SOLUONG, DONGIA, THANHTIEN)
SELECT :ORDER_B, MAMON, TENMON, 1, GIA, GIA
FROM MONAN
WHERE MAMON = 1;

-- Oracle co the bao ORA-00060 deadlock detected

-- CACH GIAI QUYET:
-- Thong nhat thu tu xu ly item trong checkout
-- Vi du luon sap xep danh sach mon theo MAMON tang dan truoc khi insert CHITIETDH

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

-- Session 2 se phai cho Session 1, nhung khong bi deadlock
-- vi ca hai session deu khoa theo thu tu: MONAN(1) -> MONAN(2)

-- ---------------------------------------------------------
-- E. Lost update / locking voi ton kho theo luong checkout cu
-- ---------------------------------------------------------
-- HIEN TUONG
-- Session 1
SELECT SOLUONGTON
FROM MONAN
WHERE MAMON = 1;
-- Gia su Session 1 tinh toan ton moi = 45

-- Session 2
SELECT SOLUONGTON
FROM MONAN
WHERE MAMON = 1;
-- Gia su Session 2 tinh toan ton moi = 60
UPDATE MONAN
SET SOLUONGTON = 60
WHERE MAMON = 1;
COMMIT;

-- Session 1
UPDATE MONAN
SET SOLUONGTON = 45
WHERE MAMON = 1;
COMMIT;
-- Update cua Session 2 bi mat

-- CACH GIAI QUYET
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
-- Session 2 phai cho Session 1

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
-- F. Voucher race condition va cach xu ly
-- ---------------------------------------------------------
-- HIEN TUONG NEU CHI DOC ROI TINH TOAN NGOAI APP
-- Session 1
SELECT SOLUONG, SOLANSUDUNG
FROM MAGIAMGIA
WHERE MAGG = 1;

-- Session 2
SELECT SOLUONG, SOLANSUDUNG
FROM MAGIAMGIA
WHERE MAGG = 1;

-- Ca 2 session cung thay con luot va deu tiep tuc su dung

-- CACH GIAI QUYET THEO LUONG CHECKOUT DA SUA
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
-- Session 2 se cho hoac bao het luot tuy trang thai du lieu sau khi Session 1 commit
