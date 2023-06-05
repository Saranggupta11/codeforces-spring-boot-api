package com.sarang.codeforcesapi.utils;


import com.sarang.codeforcesapi.models.CfUserElastic;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeforcesApiResponseElastic {
    private String status;
    private CfUserElastic[]result;
}
