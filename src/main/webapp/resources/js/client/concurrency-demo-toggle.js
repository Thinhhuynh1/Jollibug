(function () {
    const API_BASE = "/api/demo/concurrency-mode";

    document.addEventListener("DOMContentLoaded", function () {
        const toggle = document.querySelector("[data-demo-mode-toggle]");
        const label = document.querySelector("[data-demo-mode-label]");
        const resetButton = document.querySelector("[data-demo-reset-voucher]");

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

        if (resetButton) {
            resetButton.addEventListener("click", resetVoucher);
        }
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

    async function resetVoucher() {
        const resetButton = document.querySelector("[data-demo-reset-voucher]");

        if (resetButton) {
            resetButton.disabled = true;
            resetButton.textContent = "...";
        }

        try {
            await fetch(`${API_BASE}/reset-voucher`, {
                method: "POST"
            });
        } catch (error) {
            console.error("[DEMO MODE] Cannot reset voucher", error);
        } finally {
            if (resetButton) {
                resetButton.disabled = false;
                resetButton.textContent = "Reset";
            }
        }
    }

    function renderMode(mode) {
        const normalizedMode = mode === "UNSAFE" ? "UNSAFE" : "SAFE";
        const toggle = document.querySelector("[data-demo-mode-toggle]");
        const label = document.querySelector("[data-demo-mode-label]");

        if (!toggle || !label) {
            return;
        }

        toggle.dataset.mode = normalizedMode;
        toggle.classList.toggle("is-unsafe", normalizedMode === "UNSAFE");
        label.textContent = normalizedMode;
        toggle.title = normalizedMode === "UNSAFE"
            ? "Dang bat demo loi lost update"
            : "Dang bat che do khoa an toan";
    }
})();
