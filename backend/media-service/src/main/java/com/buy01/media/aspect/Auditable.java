package com.buy01.media.aspect;

import com.buy01.media.event.AuditAction;
import com.buy01.media.event.EntityType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    AuditAction action();

    EntityType entityType() default EntityType.MEDIA;

    String entityId() default "";
}