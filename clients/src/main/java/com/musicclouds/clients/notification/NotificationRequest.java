package com.musicclouds.clients.notification;

public record NotificationRequest(
        Integer toUserId,
        String toUserEmail,
        String message
) {
}
