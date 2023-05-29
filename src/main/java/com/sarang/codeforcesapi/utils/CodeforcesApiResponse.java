package com.sarang.codeforcesapi.utils;

import com.sarang.codeforcesapi.models.CfUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeforcesApiResponse {
    private String status;
    private CfUser []result;
}
