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

    private final CodeforcesRepository codeforcesRepository;

    private final MongoTemplate mongoTemplate;

    private final RestTemplate restTemplate;

    @Autowired
    public CoderforcesService(CodeforcesRepository codeforcesRepository, RestTemplate restTemplate, MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.restTemplate = restTemplate;
        this.codeforcesRepository = codeforcesRepository;
    }


    public CfUser fetchAndSaveUser(String handle) throws Exception {
        try {
            String apiUrl = CODEFORCES_API_URL.replace("{handle}", handle);
            CodeforcesApiResponse res = restTemplate.getForObject(apiUrl, CodeforcesApiResponse.class);

            if (res != null && res.getResult().length > 0) {
                CfUser user = res.getResult()[0];
                return codeforcesRepository.save(user);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return null;
    }

    public List<CfUser> getAllUsers() {
        return codeforcesRepository.findAll();
    }

    public List<Document> sortUserByRatingDesc() {
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "rating");
        Aggregation aggregation = Aggregation.newAggregation(sortOperation);
        List<Document> result = mongoTemplate.aggregate(aggregation, CfUser.class, Document.class).getMappedResults();
        return result;
    }

    public List<Document> groupUsersByCity() {
        GroupOperation groupOperation = Aggregation.group("city").count().as("count").push("$$ROOT").as("users");
        Aggregation aggregation = Aggregation.newAggregation(groupOperation);
        List<Document> result = mongoTemplate.aggregate(aggregation, CfUser.class, Document.class).getMappedResults();
        return result;
    }

    public List<Document> groupByCityandSortByrating() {
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "rating");
        GroupOperation groupByCity = Aggregation.group("city").push("$$ROOT").as("users");

        Aggregation aggregation = Aggregation.newAggregation(sortOperation, groupByCity);

        List<Document> result = mongoTemplate.aggregate(aggregation, CfUser.class, Document.class).getMappedResults();
        return result;
    }

    public List<Document> getHighestRankedByCity() {
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "rating");
        GroupOperation groupByCity = Aggregation.group("city").first(Aggregation.ROOT).as("highest_ranked");

        Aggregation aggregation = Aggregation.newAggregation(sortOperation, groupByCity);

        List<Document> result = mongoTemplate.aggregate(aggregation, CfUser.class, Document.class).getMappedResults();

        return result;
    }

}
