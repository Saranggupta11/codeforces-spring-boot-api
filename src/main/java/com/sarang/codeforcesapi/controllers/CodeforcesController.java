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
    public ApiResponse<CfUser> getUser(@PathVariable String userHandle) {
        CfUser user = null;
        try {
            user = coderforcesService.fetchAndSaveUser(userHandle);
        } catch (Exception e) {
            return new ApiResponse<CfUser>(false, e.getMessage(), null);
        }
        if (user != null) {
            List<CfUser> list = new ArrayList<>();
            list.add(user);
            return new ApiResponse<CfUser>(true, "User fetched and saved successfully successfully", list);
        }
        return new ApiResponse<CfUser>(false, "error occurred not able to fech and save user", null);

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
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(jsonString);
        } catch (Exception e) {
            throw new Exception("Failed to parse JSON: " + e.getMessage());
        }
    }

    @PostMapping("/elastic/users/{userHandle}")
    public ApiResponse<CfUserElastic> fetchAndSaveUserElastic(@PathVariable String userHandle, @RequestBody String requestBody) {
        if (requestBody == null) {
            return new ApiResponse<>(false, "Request body is null", null);
        }
        JsonNode jsonBody = null;
        try {
            jsonBody = parseJson(requestBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonBody == null || !jsonBody.has("date") || jsonBody.get("date").isNull()) {
            return new ApiResponse<>(false, "missing 'date' field in the request body", null);
        }
        String date = jsonBody.get("date").asText();
        if (date.isEmpty()) {
            return new ApiResponse<>(false, "Date field is empty", null);
        }

        CfUserElastic user = null;
        try {
            user = codeforcesElasticService.fetchAndSaveUser(userHandle, date);
        } catch (Exception e) {
            return new ApiResponse<>(false, e.getMessage(), null);
        }

        if (user != null) {
            List<CfUserElastic> list = new ArrayList<>();
            list.add(user);
            return new ApiResponse<>(true, "User fetched and saved successfully", list);
        }

        return new ApiResponse<>(false, "Failed to fetch and save user", null);
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
    public ApiResponse<CfUserElastic> getUserByName(@PathVariable String name, @RequestParam(name = "page", defaultValue = "0") int pageNumber, @RequestParam(name = "size", defaultValue = "10") int pageSize) {
        List<CfUserElastic> users = codeforcesElasticService.getUserByName(name, pageNumber, pageSize);
        if (!users.isEmpty()) {
            return new ApiResponse<CfUserElastic>(true, "users fetched successfully", users);
        }
        return new ApiResponse<CfUserElastic>(true, "no users found", users);
    }

    @GetMapping("/elastic/users/byRatingAsc")
    public List<CfUserElastic> sortByRating() throws IOException {
        return codeforcesElasticService.sortByRatingAsc();
    }

    @GetMapping("/elastic/users/highestByCountry/{countryName}")
    public ApiResponse<CfUserElastic> aggregateByCountry(@PathVariable String countryName) {
        CfUserElastic user = codeforcesElasticService.getHighestRatedCoderByCountry(countryName);
        if (user != null) {
            List<CfUserElastic> list = new ArrayList<>();
            list.add(user);
            return new ApiResponse<CfUserElastic>(true, "User fetched successfully", list);
        }
        return new ApiResponse<CfUserElastic>(false ,"No users found or please enter a valid country", null);
    }

    @GetMapping("/elastic/users/dateHistogram/{rating}")
    public ApiResponse<CfUserElastic> dateHistogram(@PathVariable int rating, @RequestParam(name = "page", defaultValue = "0") int pageNumber, @RequestParam(name = "size", defaultValue = "10") int pageSize) {
        List<CfUserElastic> users = codeforcesElasticService.getUsersGreaterThanRatingOnaDate(rating, pageNumber, pageSize);

        if (users != null) {
            return new ApiResponse<CfUserElastic>(true, "users fetched successfully for rating greater than " + rating, users);
        }
        return new ApiResponse<CfUserElastic>(true, "No users found for rating greater than " + rating, null);
    }

    @GetMapping("/elastic/test")
    public JsonNode test() throws Exception {
        String response=codeforcesElasticService.dateAggregation();
        JsonNode jsonNode=parseJson(response);
        return jsonNode.get("aggregations");

    }

}
