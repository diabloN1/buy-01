package com.__buy.user_service.event;

import java.time.Instant;

public record AuditEvent(
        String entityId,
        EntityType entityType,
        AuditAction action,
        String executorId,
        boolean isAdmin,
        Instant timestamp
) {}
