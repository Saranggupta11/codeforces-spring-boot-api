package com.sarang.codeforcesapi.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


@Document(indexName = "codeforces")
@Getter
@Setter
public class CfUserElastic {
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String lastName;

    @Field(type = FieldType.Keyword)
    private String country;

    @Field(type = FieldType.Long)
    private long lastOnlineTimeSeconds;

    @Field(type = FieldType.Keyword)
    private String city;

    @Field(type = FieldType.Integer)
    private int rating;

    @Field(type = FieldType.Integer)
    private int friendOfCount;

    @Field(type = FieldType.Keyword)
    private String titlePhoto;

    @Field(type = FieldType.Keyword)
    private String handle;

    @Field(type = FieldType.Keyword)
    private String avatar;

    @Field(type = FieldType.Text)
    private String firstName;

    @Field(type = FieldType.Integer)
    private int contribution;

    @Field(type = FieldType.Keyword)
    private String organization;

    @Field(type = FieldType.Keyword)
    private String rank;

    @Field(type = FieldType.Integer)
    private int maxRating;

    @Field(type = FieldType.Long)
    private long registrationTimeSeconds;

    @Field(type = FieldType.Keyword)
    private String maxRank;

    @Field(type = FieldType.Keyword)
    private String date;


}
