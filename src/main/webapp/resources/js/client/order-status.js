const ORDER_STATUS_LABELS = {
    PENDING: "Chờ xác nhận",
    CONFIRMED: "Đã xác nhận",
    SHIPPING: "Đang giao",
    DELIVERED: "Đã giao",
    CANCEL_REQUESTED: "Đang yêu cầu hủy",
    CANCELLED: "Đã hủy"
};

const ORDER_TIMELINE_STEPS = [
    { key: "PENDING", label: "Chờ xác nhận" },
    { key: "CONFIRMED", label: "Đã xác nhận" },
    { key: "SHIPPING", label: "Đang giao" },
    { key: "DELIVERED", label: "Đã giao" }
];

function normalizeStatus(status) {
    return (status || "").trim().toUpperCase();
}

function displayStatus(status) {
    return ORDER_STATUS_LABELS[normalizeStatus(status)] || status || "-";
}

function getStatusClass(status) {
    return normalizeStatus(status).toLowerCase().replace("_", "-");
}

function canReviewOrder(status) {
    return normalizeStatus(status) === "DELIVERED";
}

function isTimelineCompleteStatus(status) {
    const normalized = normalizeStatus(status);
    return normalized === "DELIVERED" || normalized === "CANCELLED";
}

function getTimelineSteps(currentStatus) {
    const normalized = normalizeStatus(currentStatus);

    if (normalized === "CANCEL_REQUESTED") {
        return [
            { key: "PENDING", label: "Chờ xác nhận" },
            { key: "CONFIRMED", label: "Đã xác nhận" },
            { key: "CANCEL_REQUESTED", label: "Yêu cầu hủy" }
        ];
    }

    if (normalized === "CANCELLED") {
        return [
            { key: "PENDING", label: "Chờ xác nhận" },
            { key: "CANCELLED", label: "Đã hủy" }
        ];
    }

    return ORDER_TIMELINE_STEPS;
}

function buildOrderTimelineHtml(status) {
    const currentStatus = normalizeStatus(status);
    const steps = getTimelineSteps(currentStatus);
    const currentIndex = steps.findIndex(step => step.key === currentStatus);

    return steps.map((step, index) => {
        let stateClass = "";
        const isCompleteStep = index < currentIndex
            || (index === currentIndex && isTimelineCompleteStatus(currentStatus));

        if (isCompleteStep) {
            stateClass = "is-complete";
        } else if (index === currentIndex) {
            stateClass = "is-active";
        }

        if (currentStatus === "CANCELLED" && step.key === "CANCELLED") {
            stateClass = "is-cancelled";
        }

        const showCheck = isCompleteStep && currentStatus !== "CANCELLED";

        return `
            <div class="timeline-step ${stateClass}">
                <div class="timeline-step__dot">
                    ${showCheck ? "✓" : ""}
                </div>
                <div class="timeline-step__content">
                    <strong>${step.label}</strong>
                </div>
            </div>
        `;
    }).join("");
}

function renderOrderTimeline(status, containerId = "orderTimeline") {
    const timeline = document.getElementById(containerId);
    if (!timeline) return;

    timeline.innerHTML = buildOrderTimelineHtml(status);
}

function buildReviewOrderUrl(orderId, maMon) {
    let url = `/orders/reviews/create?orderId=${orderId}`;
    if (maMon) {
        url += `&maMon=${maMon}`;
    }
    return url;
}
