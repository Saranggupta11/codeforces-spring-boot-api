package com.sarang.codeforcesapi.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sarang.codeforcesapi.models.CfUser;
import com.sarang.codeforcesapi.models.CfUserElastic;
import com.sarang.codeforcesapi.services.CodeforcesElasticService;
import com.sarang.codeforcesapi.services.CoderforcesService;
import com.sarang.codeforcesapi.utils.ApiResponse;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/codeforces")
public class CodeforcesController {

    private final CoderforcesService coderforcesService;

    private final CodeforcesElasticService codeforcesElasticService;

    @Autowired
    public CodeforcesController(CoderforcesService coderforcesService, CodeforcesElasticService codeforcesElasticService) {
        this.coderforcesService = coderforcesService;
        this.codeforcesElasticService = codeforcesElasticService;
    }

    @PostMapping("/users/{userHandle}")
    public ResponseEntity<CfUser> getUser(@PathVariable String userHandle) {
        CfUser user = coderforcesService.fetchAndSaveUser(userHandle);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();

    }

    @GetMapping("/users")
    public List<CfUser> getAllusers() {
        return coderforcesService.getAllUsers();
    }

    @GetMapping("/users/descByRating")
    public List<Document> getUsersByratingDesc() {
        return coderforcesService.sortUserByRatingDesc();
    }

    @GetMapping("/users/byCity")
    public List<Document> groupUsersByCity() {
        return coderforcesService.groupUsersByCity();
    }

    @GetMapping("/users/byCityDescRating")
    public List<Document> groupByCityandSortRating() {
        return coderforcesService.groupByCityandSortByrating();
    }

    @GetMapping("/users/highestRankedByCity")
    public List<Document> getHighestRankedInCity() {
        return coderforcesService.getHighestRankedByCity();
    }

    public JsonNode parseJson(String jsonString) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(jsonString);
    }

    @PostMapping("/elastic/users/{userHandle}")
    public CfUserElastic getUserElastic(@PathVariable String userHandle, @RequestBody String requestBody) throws Exception {
        JsonNode jsonBody = parseJson(requestBody);
        String date = jsonBody.get("date").asText();
        return codeforcesElasticService.fetchAndSaveUser(userHandle, date);
    }

    @GetMapping("/elastic/users")
    public Page<CfUserElastic> getElasticUsers(@RequestParam(name = "page", defaultValue = "0") int pageNumber, @RequestParam(name = "size", defaultValue = "10") int pageSize) {
        return codeforcesElasticService.getAllUsers(pageNumber, pageSize);

    }

    @DeleteMapping("/elastic/users")
    public void deleteAllUsers() {
        codeforcesElasticService.deleteAllUsers();
    }

    @GetMapping("/elastic/users/name/{name}")
    public ApiResponse getUserByName(@PathVariable String name,@RequestParam(name = "page", defaultValue = "0") int pageNumber, @RequestParam(name = "size", defaultValue = "10") int pageSize) {
        List<CfUserElastic>users=codeforcesElasticService.getUserByName(name,pageNumber,pageSize);
        if(!users.isEmpty()){
            return new ApiResponse(true,"users fetched successfully",users);
        }
        return new ApiResponse(false,"no users found",users);
    }

    @GetMapping("/elastic/users/byRatingAsc")
    public List<CfUserElastic> sortByRating() throws IOException {
        return codeforcesElasticService.sortByRatingAsc();
    }

    @GetMapping("/elastic/users/highestByCountry/{countryName}")
    public ApiResponse aggregateByCountry(@PathVariable String countryName) {
        CfUserElastic user=codeforcesElasticService.getHighestRatedCoderByCountry(countryName);
        if (user!=null) {
            List<CfUserElastic> list = new ArrayList<>();
            list.add(user);
            return new ApiResponse(true,"User fetched successfully",list);
        }
        return new ApiResponse(false,"No users found",null);
    }

    @GetMapping("/elastic/users/dateHistogram/{rating}")
    public ApiResponse dateHistogram(@PathVariable int rating,@RequestParam(name = "page", defaultValue = "0") int pageNumber, @RequestParam(name = "size", defaultValue = "10") int pageSize) {
        List<CfUserElastic> users= codeforcesElasticService.getUsersGreaterThanRatingOnaDate(rating,pageNumber,pageSize);

        if(users!=null){
            return new ApiResponse(true,"users fetched successfully for rating greater than "+ rating,users);
        }
        return new ApiResponse(false,"No users found for rating greater than "+rating,null);
    }

}
