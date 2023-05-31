package com.sarang.codeforcesapi.controllers;

import com.sarang.codeforcesapi.models.CfUser;
import com.sarang.codeforcesapi.models.CfUserElastic;
import com.sarang.codeforcesapi.services.CodeforcesElasticService;
import com.sarang.codeforcesapi.services.CoderforcesService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/codeforces")
public class CodeforcesController {

    @Autowired
    private CoderforcesService coderforcesService;

    @Autowired
    private CodeforcesElasticService codeforcesElasticService;

    @PostMapping("/users/{userHandle}")
    public ResponseEntity<CfUser> getUser(@PathVariable String userHandle){
        CfUser user=coderforcesService.fetchAndSaveUser(userHandle);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();

    }
    @GetMapping("/users")
    public List<CfUser> getAllusers(){
        return coderforcesService.getAllUsers();
    }

    @GetMapping("/users/descByRating")
    public List<Document> getUsersByratingDesc(){
        return coderforcesService.sortUserByRatingDesc();
    }
    @GetMapping("/users/byCity")
    public List<Document> groupUsersByCity(){
        return coderforcesService.groupUsersByCity();
    }
    @GetMapping("/users/byCityDescRating")
public List<Document> groupByCityandSortRating(){
        return coderforcesService.groupByCityandSortByrating();
    }

    @GetMapping("/users/highestRankedByCity")
    public List<Document> getHighestRankedInCity(){
        return coderforcesService.getHighestRankedByCity();
    }

    @PostMapping("/elastic/users/{userHandle}")
    public CfUserElastic getUserElastic(@PathVariable String userHandle){
        return codeforcesElasticService.fetchAndSaveUser(userHandle);
    }

    @GetMapping("/elastic/users")
    public Page<CfUserElastic> getElasticUsers(){
        return codeforcesElasticService.getAllUsers();
    }

    @GetMapping("/elastic/users/name/{name}")
    public Page<CfUserElastic> getUserByName(@PathVariable String name){
        return codeforcesElasticService.getUserByname(name);
    }
}
