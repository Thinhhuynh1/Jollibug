(function () {
    "use strict";

    const API_BASE  = "/api/manager/statistics/phantom-read";
    const MODE_API  = API_BASE + "/mode";
    const DEMO_API  = API_BASE + "/demo";
    const DELAY_MS  = 5000;

    /* ---- khởi tạo ---- */
    document.addEventListener("DOMContentLoaded", function () {
        // Chỉ kích hoạt khi đang ở trang statistics-orders
        const body = document.querySelector("body[data-admin-page='statistics-orders']");
        if (!body) return;

        // Hiện nút toggle trên topbar
        const toggleBtn  = document.getElementById("phantomReadToggle");
        const modeDesc   = document.getElementById("phantomReadModeDesc");
        if (toggleBtn)  toggleBtn.style.display  = "inline-flex";
        if (modeDesc)   modeDesc.style.display    = "inline";

        // Load chế độ hiện tại
        loadMode();

        // Sự kiện bấm toggle
        if (toggleBtn) {
            toggleBtn.addEventListener("click", async function () {
                const current = toggleBtn.dataset.mode === "UNSAFE" ? "UNSAFE" : "SAFE";
                const next    = current === "SAFE" ? "UNSAFE" : "SAFE";
                renderMode(next);   // optimistic
                await setMode(next);
            });
        }

        // Sự kiện bấm chạy demo (Tải lại & Thống kê 5s)
        const runBtn = document.getElementById("btn-phantom-read-run");
        if (runBtn) runBtn.addEventListener("click", runDemo);
    });

    /* ---- load / set mode ---- */
    async function loadMode() {
        try {
            const res  = await fetch(MODE_API);
            const data = await res.json();
            renderMode(data.mode || "SAFE");
        } catch (e) {
            renderMode("SAFE");
        }
    }

    async function setMode(mode) {
        try {
            const res  = await fetch(MODE_API + "?mode=" + encodeURIComponent(mode), { method: "POST" });
            const data = await res.json();
            renderMode(data.mode || "SAFE");
        } catch (e) {
            await loadMode();
        }
    }

    function renderMode(mode) {
        const btn   = document.getElementById("phantomReadToggle");
        const label = document.getElementById("phantomReadLabel");
        const desc  = document.getElementById("phantomReadModeDesc");

        if (!btn) return;
        const isUnsafe = mode === "UNSAFE";

        btn.dataset.mode = mode;
        btn.classList.toggle("is-unsafe", isUnsafe);

        if (label) label.textContent = mode;

        btn.title = isUnsafe
            ? "Chế độ UNSAFE – READ COMMITTED: có thể xảy ra Phantom Read"
            : "Chế độ SAFE – SERIALIZABLE: ngăn chặn Phantom Read";

        if (desc) {
            desc.textContent = isUnsafe
                ? "Isolation: READ COMMITTED"
                : "Isolation: SERIALIZABLE";
            desc.style.color = isUnsafe ? "#e74c3c" : "#27ae60";
        }
    }

    /* ---- chạy demo ---- */
    async function runDemo() {
        const toggleBtn = document.getElementById("phantomReadToggle");
        const mode      = toggleBtn ? toggleBtn.dataset.mode : "SAFE";
        const isolation = mode === "UNSAFE" ? "READ_COMMITTED" : "SERIALIZABLE";

        const runBtn    = document.getElementById("btn-phantom-read-run");
        if (runBtn) {
            runBtn.disabled = true;
            runBtn.innerHTML = "⏳ Đang thống kê (5s)...";
        }

        // Show circular loading card
        const statsLoading = document.getElementById("stats-loading-area");
        if (statsLoading) {
            statsLoading.style.display = "flex";
        }

        // Show countdown timer starting from 5s
        startCountdown(DELAY_MS / 1000);

        try {
            const url = DEMO_API
                + "?isolation=" + encodeURIComponent(isolation)
                + "&delayMs="   + encodeURIComponent(DELAY_MS);

            const res  = await fetch(url);
            const data = await res.json();

            stopCountdown();

            // Hide circular loading card when done
            if (statsLoading) {
                statsLoading.style.display = "none";
            }

            if (data.success) {
                renderResult(data);
            }

        } catch (e) {
            stopCountdown();
            if (statsLoading) {
                statsLoading.style.display = "none";
            }
            console.error("Lỗi kết nối tới server: ", e);
        } finally {
            if (runBtn) {
                runBtn.disabled = false;
                runBtn.innerHTML = "🔄 Tải lại & Thống kê (5s)";
            }
        }
    }

    function renderResult(data) {
        const s = data.secondRead || {};

        // Cập nhật trực tiếp số liệu lên các ô thống kê đầu trang
        updateMetricCard("card-total-orders", s.total);
        updateMetricCard("card-pending",      s.pending);
        updateMetricCard("card-confirmed",    s.confirmed);
        updateMetricCard("card-shipping",     s.shipping);
        updateMetricCard("card-delivered",    s.delivered);
        updateMetricCard("card-cancelled",    s.cancelled);
    }

    function updateMetricCard(id, val) {
        const el = document.getElementById(id);
        if (el && val != null) {
            // Hiệu ứng chớp đỏ Jollibug khi giá trị được cập nhật xong
            el.style.transition = "color 0.2s ease";
            el.style.color = "#e60000";
            el.textContent = val;
            setTimeout(() => {
                el.style.color = "";
            }, 1000);
        }
    }

    /* ---- countdown ---- */
    let _timer = null;
    function startCountdown(seconds) {
        const countNum = document.getElementById("loading-countdown-number");
        if (!countNum) return;
        
        let left = seconds;
        countNum.textContent = left;
        
        _timer = setInterval(function () {
            left--;
            if (left <= 0) {
                stopCountdown();
                return;
            }
            countNum.textContent = left;
        }, 1000);
    }
    
    function stopCountdown() {
        if (_timer) { clearInterval(_timer); _timer = null; }
        // Reset countdown display back to 5 for next run
        const countNum = document.getElementById("loading-countdown-number");
        if (countNum) countNum.textContent = "5";
    }
})();
