package com.sarang.codeforcesapi.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;


@Document(indexName = "codeforces")
@Getter
@Setter
public class CfUserElastic {
    @Id
    private String id;
    private String lastName;
    private String country;
    private long lastOnlineTimeSeconds;
    private String city;
    private int rating;
    private int friendOfCount;
    private String titlePhoto;
    private String handle;
    private String avatar;
    private String firstName;
    private int contribution;
    private String organization;
    private String rank;
    private int maxRating;
    private long registrationTimeSeconds;
    private String maxRank;
}
