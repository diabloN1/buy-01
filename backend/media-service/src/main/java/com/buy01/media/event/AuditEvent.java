package com.buy01.media.event;

import java.time.Instant;

public record AuditEvent(
        String entityId,
        EntityType entityType,
        AuditAction action,
        String executorId,
        boolean isAdmin,
        Instant timestamp
) {}
