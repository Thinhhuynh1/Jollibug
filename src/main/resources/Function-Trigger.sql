
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
        RAISE_APPLICATION_ERROR(-20002, 'So luong ton kho khong du de dat hang');
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
            SoLuongTon = SoLuongTon + item.SoLuong,
            SoLuongDaBan = SoLuongDaBan - item.SoLuong
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

CREATE OR REPLACE FUNCTION FUNC_CALC_SUBTOTAL(
    p_dongia IN NUMBER,
    p_soluong IN NUMBER
)
RETURN NUMBER
AS
BEGIN
    RETURN NVL(p_dongia, 0) * NVL(p_soluong, 0);
END;
/

CREATE OR REPLACE FUNCTION FUNC_FIND_DISCOUNT_ID(
    p_macode IN VARCHAR2
)
RETURN NUMBER
AS
    v_magg NUMBER;
BEGIN
    SELECT MAGG
    INTO v_magg
    FROM MAGIAMGIA
    WHERE UPPER(MACODE) = UPPER(TRIM(p_macode))
      AND CURRENT_TIMESTAMP BETWEEN NGAYBATDAU AND NGAYKETTHUC;

    RETURN v_magg;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
/

CREATE OR REPLACE FUNCTION FUNC_CALC_DISCOUNT(
    p_macode IN VARCHAR2,
    p_subtotal IN NUMBER
)
RETURN NUMBER
AS
    v_loaigiam MAGIAMGIA.LOAIGIAM%TYPE;
    v_mucgiam MAGIAMGIA.MUCGIAM%TYPE;
    v_dieukien MAGIAMGIA.DIEUKIEN%TYPE;
BEGIN
    SELECT LOAIGIAM, MUCGIAM, DIEUKIEN
    INTO v_loaigiam, v_mucgiam, v_dieukien
    FROM MAGIAMGIA
    WHERE UPPER(MACODE) = UPPER(TRIM(p_macode))
      AND CURRENT_TIMESTAMP BETWEEN NGAYBATDAU AND NGAYKETTHUC;

    IF v_dieukien IS NOT NULL AND NVL(p_subtotal, 0) < v_dieukien THEN
        RETURN 0;
    END IF;

    IF UPPER(v_loaigiam) IN ('PERCENT', 'PERCENTAGE') THEN
        RETURN NVL(p_subtotal, 0) * NVL(v_mucgiam, 0) / 100;
    END IF;

    IF UPPER(v_loaigiam) = 'AMOUNT' THEN
        RETURN LEAST(NVL(v_mucgiam, 0), NVL(p_subtotal, 0));
    END IF;

    RETURN 0;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
END;
/

CREATE OR REPLACE FUNCTION FUNC_IS_VALID_ADDRESS(
    p_matk IN NUMBER,
    p_madc IN NUMBER
)
RETURN NUMBER
AS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM DIACHI
    WHERE MaDC = p_madc
      AND MaTK = p_matk;

    RETURN CASE WHEN v_count > 0 THEN 1 ELSE 0 END;
END;
/

CREATE OR REPLACE FUNCTION FUNC_IS_VALID_PTTT(
    p_mapt IN VARCHAR2
)
RETURN NUMBER
AS
    v_count NUMBER;
    v_mapt VARCHAR2(100);
BEGIN
    v_mapt := UPPER(TRIM(p_mapt));

    IF v_mapt IN ('COD', 'BANK', 'EWALLET', 'CREDIT_CARD') THEN
        RETURN 1;
    END IF;

    SELECT COUNT(*)
    INTO v_count
    FROM PHUONGTHUCTT
    WHERE MAPT = v_mapt;

    RETURN CASE WHEN v_count > 0 THEN 1 ELSE 0 END;
END;
/

--PROCEDURE
CREATE OR REPLACE PROCEDURE PROC_REGISTER_USER(
    p_password  IN VARCHAR2,
    p_hoten     IN VARCHAR2,
    p_sdt       IN VARCHAR2,
    p_email     IN VARCHAR2
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
        'ACTIVE',
        1,
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

CREATE OR REPLACE PROCEDURE PROC_SET_DEFAULT_ADDRESS(
    p_matk IN NUMBER,
    p_madc IN NUMBER
)
AS
BEGIN
    UPDATE DIACHI
    SET isDefault = 0
    WHERE MaTK = p_matk;

    UPDATE DIACHI
    SET isDefault = 1
    WHERE MaDC = p_madc
      AND MaTK = p_matk;
END;
/

CREATE OR REPLACE PROCEDURE PROC_CREATE_ADDRESS(
    p_matk IN NUMBER,
    p_tendiachi IN VARCHAR2,
    p_tennguoinhan IN VARCHAR2,
    p_sdtnguoinhan IN VARCHAR2,
    p_diachicuthe IN VARCHAR2,
    p_tinhthanh IN VARCHAR2,
    p_quanhuyen IN VARCHAR2,
    p_phuongxa IN VARCHAR2,
    p_isdefault IN NUMBER,
    p_madc OUT NUMBER
)
AS
BEGIN
    IF p_isdefault = 1 THEN
        UPDATE DIACHI
        SET isDefault = 0
        WHERE MaTK = p_matk;
    END IF;

    INSERT INTO DIACHI (
        MaTK,
        TenDiaChi,
        TenNguoiNhan,
        SDTNguoiNhan,
        DiaChiCuThe,
        TinhThanh,
        QuanHuyen,
        PhuongXa,
        isDefault,
        created_at,
        updated_at
    )
    VALUES (
        p_matk,
        p_tendiachi,
        p_tennguoinhan,
        p_sdtnguoinhan,
        p_diachicuthe,
        p_tinhthanh,
        p_quanhuyen,
        p_phuongxa,
        p_isdefault,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    )
    RETURNING MaDC INTO p_madc;
END;
/

CREATE OR REPLACE PROCEDURE PROC_UPDATE_ADDRESS(
    p_madc IN NUMBER,
    p_tendiachi IN VARCHAR2,
    p_tennguoinhan IN VARCHAR2,
    p_sdtnguoinhan IN VARCHAR2,
    p_diachicuthe IN VARCHAR2,
    p_tinhthanh IN VARCHAR2,
    p_quanhuyen IN VARCHAR2,
    p_phuongxa IN VARCHAR2
)
AS
BEGIN
    UPDATE DIACHI
    SET TenDiaChi = p_tendiachi,
        TenNguoiNhan = p_tennguoinhan,
        SDTNguoiNhan = p_sdtnguoinhan,
        DiaChiCuThe = p_diachicuthe,
        TinhThanh = p_tinhthanh,
        QuanHuyen = p_quanhuyen,
        PhuongXa = p_phuongxa,
        updated_at = CURRENT_TIMESTAMP
    WHERE MaDC = p_madc;
END;
/

CREATE OR REPLACE PROCEDURE PROC_DELETE_ADDRESS(
    p_madc IN NUMBER
)
AS
BEGIN
    DELETE FROM DIACHI
    WHERE MaDC = p_madc;
END;
/

CREATE OR REPLACE PROCEDURE PROC_CREATE_SUPPORT_REQUEST(
    p_matk_kh IN NUMBER,
    p_tieude IN VARCHAR2,
    p_noidung IN VARCHAR2,
    p_mayc OUT NUMBER
)
AS
BEGIN
    INSERT INTO YEUCAUHOTRO (
        MaTK_KH,
        TieuDe,
        NoiDung,
        TrangThai,
        created_at
    )
    VALUES (
        p_matk_kh,
        p_tieude,
        p_noidung,
        'PENDING',
        CURRENT_TIMESTAMP
    )
    RETURNING MaYC INTO p_mayc;
END;
/

CREATE OR REPLACE PROCEDURE PROC_UPDATE_SUPPORT_STATUS(
    p_mayc IN NUMBER,
    p_trangthai IN VARCHAR2
)
AS
BEGIN
    UPDATE YEUCAUHOTRO
    SET TrangThai = p_trangthai
    WHERE MaYC = p_mayc;
END;
/

CREATE OR REPLACE PROCEDURE PROC_ASSIGN_SUPPORT_REQUEST(
    p_mayc IN NUMBER,
    p_matk_nv IN NUMBER
)
AS
BEGIN
    UPDATE YEUCAUHOTRO
    SET MaTK_NV = p_matk_nv,
        TrangThai = 'PROCESSING'
    WHERE MaYC = p_mayc;
END;
/

CREATE OR REPLACE PROCEDURE PROC_DELETE_SUPPORT_REQUEST(
    p_mayc IN NUMBER
)
AS
BEGIN
    DELETE FROM YEUCAUHOTRO
    WHERE MaYC = p_mayc;
END;
/

CREATE OR REPLACE PROCEDURE PROC_CREATE_ORDER(
    p_matk_kh IN NUMBER,
    p_madc IN NUMBER,
    p_tongtienmon IN NUMBER,
    p_tiengiamgia IN NUMBER,
    p_thanhtien IN NUMBER,
    p_magg IN NUMBER,
    p_ghichu IN CLOB,
    p_madh OUT NUMBER
)
AS
BEGIN
    INSERT INTO DONHANG (
        MaTK_KH,
        MaTK_NV,
        NgayDat,
        MaDC,
        TongTienMon,
        TienGiamGia,
        ThanhTien,
        TrangThaiDon,
        MaGG,
        GhiChu,
        updated_at
    )
    VALUES (
        p_matk_kh,
        NULL,
        CURRENT_TIMESTAMP,
        p_madc,
        p_tongtienmon,
        p_tiengiamgia,
        p_thanhtien,
        'PENDING',
        p_magg,
        p_ghichu,
        CURRENT_TIMESTAMP
    )
    RETURNING MaDH INTO p_madh;
END;
/

CREATE OR REPLACE PROCEDURE PROC_CREATE_ORDER_ITEM(
    p_madh IN NUMBER,
    p_mamon IN NUMBER,
    p_tenmon IN VARCHAR2,
    p_soluong IN NUMBER,
    p_dongia IN NUMBER,
    p_thanhtien IN NUMBER
)
AS
BEGIN
    INSERT INTO CHITIETDH (
        MaDH,
        MaMon,
        TenMon,
        SoLuong,
        DonGia,
        ThanhTien
    )
    VALUES (
        p_madh,
        p_mamon,
        p_tenmon,
        p_soluong,
        p_dongia,
        p_thanhtien
    );
END;
/

CREATE OR REPLACE PROCEDURE PROC_CREATE_PAYMENT(
    p_madh IN NUMBER,
    p_mapt IN VARCHAR2,
    p_sotien IN NUMBER,
    p_trangthai IN VARCHAR2
)
AS
BEGIN
    INSERT INTO THANHTOAN (
        MaDH,
        MaPT,
        NgayTT,
        SoTien,
        TrangThaiTT
    )
    VALUES (
        p_madh,
        p_mapt,
        CURRENT_TIMESTAMP,
        p_sotien,
        p_trangthai
    );
END;
/

CREATE OR REPLACE PROCEDURE PROC_RESERVE_VOUCHER(
    p_magg IN NUMBER
)
AS
    v_soluong MAGIAMGIA.SoLuong%TYPE;
    v_solansudung MAGIAMGIA.SoLanSuDung%TYPE;
BEGIN
    SELECT SoLuong, SoLanSuDung
    INTO v_soluong, v_solansudung
    FROM MAGIAMGIA
    WHERE MaGG = p_magg
      AND CURRENT_TIMESTAMP BETWEEN NgayBatDau AND NgayKetThuc
    FOR UPDATE;

    IF v_solansudung >= v_soluong THEN
        RAISE_APPLICATION_ERROR(-20021, 'Hết lượng sử dụng');
    END IF;

    UPDATE MAGIAMGIA
    SET SoLanSuDung = SoLanSuDung + 1
    WHERE MaGG = p_magg;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20022, 'Voucher không có hiệu lực');
END;
/

CREATE OR REPLACE PROCEDURE PROC_ORDER(
    p_matk_kh IN NUMBER,
    p_madc IN NUMBER,
    p_tongtienmon IN NUMBER,
    p_tiengiamgia IN NUMBER,
    p_thanhtien IN NUMBER,
    p_magg IN NUMBER,
    p_ghichu IN CLOB,
    p_madh OUT NUMBER
)
AS
BEGIN
    PROC_CREATE_ORDER(
        p_matk_kh,
        p_madc,
        p_tongtienmon,
        p_tiengiamgia,
        p_thanhtien,
        p_magg,
        p_ghichu,
        p_madh
    );
END;
/
