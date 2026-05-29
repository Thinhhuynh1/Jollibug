(function () {
    const API_BASE = "/api/staff/orders/concurrency-mode";

    document.addEventListener("DOMContentLoaded", function () {
        const toggle = document.getElementById("orderConcurrencyToggle");
        const label = document.getElementById("orderConcurrencyLabel");

        if (!toggle || !label) {
            return;
        }

        loadMode();

        toggle.addEventListener("click", async function () {
            const currentMode = toggle.dataset.mode === "UNSAFE" ? "UNSAFE" : "SAFE";
            const nextMode = currentMode === "SAFE" ? "UNSAFE" : "SAFE";
            renderMode(nextMode);
            await setMode(nextMode);
        });
    });

    async function loadMode() {
        try {
            const response = await fetch(API_BASE);
            const data = await response.json();

            if (response.ok) {
                renderMode(data.mode || "SAFE");
            }
        } catch (error) {
            renderMode("SAFE");
        }
    }

    async function setMode(mode) {
        try {
            const response = await fetch(`${API_BASE}?mode=${encodeURIComponent(mode)}`, {
                method: "POST"
            });
            const data = await response.json();

            if (response.ok && data.success) {
                renderMode(data.mode || mode);
                return;
            }

            await loadMode();
        } catch (error) {
            console.error("[DEMO MODE] Cannot update mode", error);
            await loadMode();
        }
    }

    function renderMode(mode) {
        const normalizedMode = mode === "UNSAFE" ? "UNSAFE" : "SAFE";
        const toggle = document.getElementById("orderConcurrencyToggle");
        const label = document.getElementById("orderConcurrencyLabel");

        if (!toggle || !label) {
            return;
        }

        toggle.dataset.mode = normalizedMode;
        toggle.classList.toggle("is-unsafe", normalizedMode === "UNSAFE");
        label.textContent = normalizedMode;
        toggle.title = normalizedMode === "UNSAFE"
            ? "Đang bật demo Non-repeatable Read (Isolation: READ COMMITTED)"
            : "Đang bật chế độ khóa an toàn (Isolation: SERIALIZABLE)";
    }
})();
