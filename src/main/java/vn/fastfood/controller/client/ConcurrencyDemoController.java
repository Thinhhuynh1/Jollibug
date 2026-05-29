package vn.fastfood.controller.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.fastfood.config.CouponUsageDemoSettings;
import vn.fastfood.config.DBConnection;

@RestController
@RequestMapping("/api/demo/concurrency-mode")
public class ConcurrencyDemoController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMode() {
        return ResponseEntity.ok(
                Map.of(
                        "mode", CouponUsageDemoSettings.getMode(),
                        "unsafeDelayMs", CouponUsageDemoSettings.getUnsafeDelayMs(),
                        "safeDelayMs", CouponUsageDemoSettings.getSafeDelayMs()
                )
        );
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> setMode(@RequestParam("mode") String mode) {
        String normalizedMode = CouponUsageDemoSettings.setMode(mode);
        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "mode", normalizedMode
                )
        );
    }

    @PostMapping("/reset-voucher")
    public ResponseEntity<Map<String, Object>> resetVoucher(
            @RequestParam(value = "maGG", defaultValue = "1") long maGG,
            @RequestParam(value = "soLuong", defaultValue = "10") int soLuong,
            @RequestParam(value = "soLanSuDung", defaultValue = "5") int soLanSuDung
    ) {
        String sql = """
            UPDATE MAGIAMGIA
            SET SoLuong = ?,
                SoLanSuDung = ?
            WHERE MaGG = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, soLuong);
            ps.setInt(2, soLanSuDung);
            ps.setLong(3, maGG);

            int updatedRows = ps.executeUpdate();

            return ResponseEntity.ok(
                    Map.of(
                            "success", updatedRows > 0,
                            "maGG", maGG,
                            "soLuong", soLuong,
                            "soLanSuDung", soLanSuDung
                    )
            );

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    Map.of(
                            "success", false,
                            "message", "Khong the reset voucher demo."
                    )
            );
        }
    }
}
