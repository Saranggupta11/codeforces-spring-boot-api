package com.sarang.codeforcesapi.services;

import com.sarang.codeforcesapi.models.CfUserElastic;
import com.sarang.codeforcesapi.repositories.CodeforcesElasticRepository;
import com.sarang.codeforcesapi.utils.CodeforcesApiResponseElastic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CodeforcesElasticService {

    private static final String CODEFORCES_API_URL = "https://codeforces.com/api/user.info?handles={handle}";

    private CodeforcesElasticRepository codeforcesElasticRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public CodeforcesElasticService(CodeforcesElasticRepository codeforcesElasticRepository, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.codeforcesElasticRepository=codeforcesElasticRepository;
    }

    public CfUserElastic fetchAndSaveUser(String handle){
//        RestTemplate restTemplate= new RestTemplate();
        String apiUrl = CODEFORCES_API_URL.replace("{handle}", handle);
        CodeforcesApiResponseElastic res=restTemplate.getForObject(apiUrl, CodeforcesApiResponseElastic.class);

        CfUserElastic user = null;
        if (res != null) {
            user = res.getResult()[0];
        }

        if (user != null) {
            return codeforcesElasticRepository.save(user);
        }

        return null;


    }
    public Page<CfUserElastic> getAllUsers(){
        return codeforcesElasticRepository.findAll(PageRequest.of(0,10));
    }

    public Page<CfUserElastic> getUserByname(String name){
        return codeforcesElasticRepository.searchByName(name,PageRequest.of(0,10));
    }





}
