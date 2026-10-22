package com.linh.lms.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RefreshResponse {
    private String accessToken;

    public RefreshResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
