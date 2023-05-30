package com.sarang.codeforcesapi.services;


import com.sarang.codeforcesapi.models.CfUser;
import com.sarang.codeforcesapi.repositories.CodeforcesRepository;
import com.sarang.codeforcesapi.utils.CodeforcesApiResponse;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CoderforcesService {

    private static final String CODEFORCES_API_URL = "https://codeforces.com/api/user.info?handles={handle}";

    @Autowired
    private CodeforcesRepository codeforcesRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public CfUser fetchAndSaveUser(String handle){

        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = CODEFORCES_API_URL.replace("{handle}", handle);
        CodeforcesApiResponse res=restTemplate.getForObject(apiUrl,CodeforcesApiResponse.class);

        CfUser user = null;
        if (res != null) {
            user = res.getResult()[0];
        }

        if (user != null) {
            return codeforcesRepository.save(user);
        }

        return null;


    }
    public List<CfUser> getAllUsers(){
        return codeforcesRepository.findAll();
    }

    public List<Document> sortUserByRatingDesc(){
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC,"rating");
        Aggregation aggregation = Aggregation.newAggregation(sortOperation);
        List<Document> result=mongoTemplate.aggregate(aggregation,CfUser.class,Document.class).getMappedResults();
        return result ;
    }

    public List<Document> groupUsersByCity(){
        GroupOperation groupOperation= Aggregation.group("city").count().as("count").push("$$ROOT").as("users");
        Aggregation aggregation = Aggregation.newAggregation(groupOperation);
        List<Document> result=mongoTemplate.aggregate(aggregation,CfUser.class,Document.class).getMappedResults();
        return result ;
    }
    public List<Document> groupByCityandSortByrating(){
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC,"rating");
        GroupOperation groupByCity = Aggregation.group("city").push("$$ROOT").as("users");

        Aggregation aggregation = Aggregation.newAggregation(sortOperation,groupByCity);

        List<Document> result=mongoTemplate.aggregate(aggregation, CfUser.class,Document.class).getMappedResults();
        return result;
    }
    public List<Document> getHighestRankedByCity(){
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC,"rating");
        GroupOperation groupByCity = Aggregation.group("city").first(Aggregation.ROOT).as("highest_ranked");

        Aggregation aggregation = Aggregation.newAggregation(sortOperation,groupByCity);

        List<Document> result=mongoTemplate.aggregate(aggregation, CfUser.class,Document.class).getMappedResults();
        return result;
    }

}
