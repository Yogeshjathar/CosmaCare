package com.cosmacare.cosmacare_user_service.entity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;

public class RoleDeserializer extends JsonDeserializer<Role> {
    @Override
    public Role deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText().toUpperCase();
        try {
            return Role.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw InvalidFormatException.from(
                    p,
                    "Invalid value for Role enum",
                    value,
                    Role.class
            );
        }
    }
}
