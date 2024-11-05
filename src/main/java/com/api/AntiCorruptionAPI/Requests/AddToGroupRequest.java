package com.api.AntiCorruptionAPI.Requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToGroupRequest {
    private Long userId;
    private Long groupId;
}
