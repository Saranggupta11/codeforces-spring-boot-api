package com.sarang.codeforcesapi.services;


import com.sarang.codeforcesapi.models.CfUser;
import com.sarang.codeforcesapi.repositories.CodeforcesRepository;
import com.sarang.codeforcesapi.utils.CodeforcesApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CoderforcesService {

    private static final String CODEFORCES_API_URL = "https://codeforces.com/api/user.info?handles={handle}";

    @Autowired
    private CodeforcesRepository codeforcesRepository;

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

}
