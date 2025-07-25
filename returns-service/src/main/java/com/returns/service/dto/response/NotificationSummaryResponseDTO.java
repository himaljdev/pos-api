package com.returns.service.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class NotificationSummaryResponseDTO {
    private long unreadCount;
    private List<NotificationHistoryResponseDTO> latestNotifications;
}
