package vn.fastfood.dto;

import java.sql.Timestamp;

public class OrderStatusHistoryResponse {
    private String oldStatus;
    private String newStatus;
    private Long actorId;
    private String reason;
    private Timestamp createdAt;
    private String displayLabel;

    public OrderStatusHistoryResponse() {
    }

    public OrderStatusHistoryResponse(
            String oldStatus,
            String newStatus,
            Long actorId,
            String reason,
            Timestamp createdAt,
            String displayLabel
    ) {
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.actorId = actorId;
        this.reason = reason;
        this.createdAt = createdAt;
        this.displayLabel = displayLabel;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public Long getActorId() {
        return actorId;
    }

    public void setActorId(Long actorId) {
        this.actorId = actorId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public void setDisplayLabel(String displayLabel) {
        this.displayLabel = displayLabel;
    }
}
