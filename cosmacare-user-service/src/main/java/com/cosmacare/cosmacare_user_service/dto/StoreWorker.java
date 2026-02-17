package com.cosmacare.cosmacare_user_service.dto;

import com.cosmacare.cosmacare_user_service.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StoreWorker {
    private String username;
    private String email;
    private String phoneNumber;
    private Status status;

}
