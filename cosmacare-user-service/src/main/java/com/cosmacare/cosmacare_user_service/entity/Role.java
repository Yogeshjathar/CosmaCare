package com.cosmacare.cosmacare_user_service.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
@JsonDeserialize(using = RoleDeserializer.class)
public enum Role {
    STORE_WORKER,
    STORE_MANAGER,
    TECHNICIAN
}
