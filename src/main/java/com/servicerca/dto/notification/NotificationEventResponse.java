package com.servicerca.dto.notification; import java.time.Instant;import java.util.*; public record NotificationEventResponse(String type,UUID requestId,String message,Instant occurredAt){}
