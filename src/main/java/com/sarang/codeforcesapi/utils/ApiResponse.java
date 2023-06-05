package com.sarang.codeforcesapi.utils;

import com.sarang.codeforcesapi.models.CfUserElastic;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse {
    private boolean success;
    private String message;
    private List<CfUserElastic> users;
}
