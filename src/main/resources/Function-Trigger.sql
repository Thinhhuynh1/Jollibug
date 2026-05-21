
-- Trigger
--Luu lich su khi thao tac tren don hang
CREATE OR REPLACE TRIGGER trg_donhang_ins_lishsu
AFTER INSERT ON DONHANG
FOR EACH ROW
BEGIN
    INSERT INTO LICHSUTRANGTHAIDH (
        MaDH,
        TrangThaiCu,
        TrangThaiMoi,
        MaNguoiThucHien,
        LyDo,
        ThoiGian
    )
    VALUES (
        :NEW.MaDH,
        NULL,
        :NEW.TrangThaiDon,
        :NEW.MaTK_NV,
        'Tao don hang moi',
        CURRENT_TIMESTAMP
    );
END;
/

CREATE OR REPLACE TRIGGER trg_donhang_upd_trangthai
AFTER UPDATE OF TrangThaiDon ON DONHANG
FOR EACH ROW
WHEN (OLD.TrangThaiDon <> NEW.TrangThaiDon)
BEGIN
    INSERT INTO LICHSUTRANGTHAIDH (
        MaDH,
        TrangThaiCu,
        TrangThaiMoi,
        MaNguoiThucHien,
        LyDo,
        ThoiGian
    )
    VALUES (
        :NEW.MaDH,
        :OLD.TrangThaiDon,
        :NEW.TrangThaiDon,
        :NEW.MaTK_NV,
        'Cap nhat trang thai don hang',
        CURRENT_TIMESTAMP
    );
END;
/

--Mua hang udp SoLuongTon
CREATE OR REPLACE TRIGGER trg_ctdh_upd_soluongton
BEFORE INSERT ON CHITIETDH
FOR EACH ROW
DECLARE
    v_so_luong_ton MONAN.SoLuongTon%TYPE;
BEGIN
    SELECT SoLuongTon
    INTO v_so_luong_ton
    FROM MONAN
    WHERE MaMon = :NEW.MaMon
    FOR UPDATE; --Khoa dong cho update, Tranh bi concurence

    IF v_so_luong_ton < :NEW.SoLuong THEN
        RAISE_APPLICATION_ERROR(
            -20002, 'So luong ton kho khong du de dat hang'
        );
    END IF;

    UPDATE MONAN
    SET SoLuongTon = SoLuongTon - :NEW.SoLuong,
    SoLuongDaBan = SoLuongDaBan + :NEW.SoLuong
    WHERE MaMon = :NEW.MaMon;
END;
/

--Huy don hang hoan lai SoLuongTon
CREATE OR REPLACE TRIGGER trg_donhang_cancel_hoanlaisoluong
AFTER UPDATE OF TrangThaiDon ON DONHANG
FOR EACH ROW
WHEN (OLD.TrangThaiDon != 'CANCELLED' AND NEW.TrangThaiDon = 'CANCELLED')
BEGIN
    FOR item IN (
        SELECT MaMon, SoLuong
        FROM CHITIETDH
        WHERE MaDH = :NEW.MaDH
    )
    LOOP
        UPDATE MONAN
        SET 
        BEGIN
            SoLuongTon = SoLuongTon + item.SoLuong,
            SoLuongDaBan = SoLuongDaBan - item.SoLuong
        END
        WHERE MaMon = item.MaMon;
    END LOOP;
END;
/

--Trigger cho updated_at
CREATE OR REPLACE TRIGGER trg_nguoidung_upd_time
BEFORE UPDATE ON NGUOIDUNG
FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER trg_diachi_upd_time
BEFORE UPDATE ON DIACHI
FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER trg_danhmuc_upd_time
BEFORE UPDATE ON DANHMUC
FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER trg_monan_upd_time
BEFORE UPDATE ON MONAN
FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER trg_donhang_upd_time
BEFORE UPDATE ON DONHANG
FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

--FUNCTION
CREATE OR REPLACE FUNCTION FUNC_EMAIL_EXISTS(p_email IN VARCHAR2)
RETURN NUMBER
AS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM NGUOIDUNG
    WHERE LOWER(TRIM(Email)) = LOWER(TRIM(p_email));

    RETURN v_count;
END;
/

CREATE OR REPLACE FUNCTION FUNC_SDT_EXISTS(p_sdt IN VARCHAR2)
RETURN NUMBER
AS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM NGUOIDUNG
    WHERE SDT = p_sdt;

    RETURN v_count;
END;
/

--PROCEDURE
CREATE OR REPLACE PROCEDURE PROC_CREATE_USER(
    p_password  IN VARCHAR2,
    p_hoten     IN VARCHAR2,
    p_sdt       IN VARCHAR2,
    p_email     IN VARCHAR2,
    p_trangthai IN VARCHAR2,
    p_mavt      IN NUMBER
)
AS
BEGIN
    INSERT INTO NGUOIDUNG (
        Password,
        HoTen,
        SDT,
        Email,
        TrangThai,
        MaVT,
        created_at,
        updated_at
    )
    VALUES (
        p_password,
        p_hoten,
        p_sdt,
        p_email,
        p_trangthai,
        p_mavt,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );
END;
/

CREATE OR REPLACE PROCEDURE PROC_UPDATE_USER_PASSWORD(
    p_matk     IN NUMBER,
    p_password IN VARCHAR2
)
AS
BEGIN
    UPDATE NGUOIDUNG
    SET Password = p_password,
        updated_at = CURRENT_TIMESTAMP
    WHERE MaTK = p_matk;
END;
/
