package com.sarang.codeforcesapi.services;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.json.JsonData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sarang.codeforcesapi.models.CfUserElastic;
import com.sarang.codeforcesapi.repositories.CodeforcesElasticRepository;
import com.sarang.codeforcesapi.utils.CodeforcesApiResponseElastic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Service
public class CodeforcesElasticService {

    private static final String CODEFORCES_API_URL = "https://codeforces.com/api/user.info?handles={handle}";
    private final CodeforcesElasticRepository codeforcesElasticRepository;
    private final RestTemplate restTemplate;
    private final ElasticsearchOperations operations;


    @Autowired
    public CodeforcesElasticService(CodeforcesElasticRepository codeforcesElasticRepository, RestTemplate restTemplate, ElasticsearchOperations elasticsearchOperations) {
        this.operations = elasticsearchOperations;
        this.restTemplate = restTemplate;
        this.codeforcesElasticRepository = codeforcesElasticRepository;
    }

    public CfUserElastic fetchAndSaveUser(String handle, String date) throws Exception {
        try {
            String apiUrl = CODEFORCES_API_URL.replace("{handle}", handle);
            CodeforcesApiResponseElastic res = restTemplate.getForObject(apiUrl, CodeforcesApiResponseElastic.class);

            if (res != null && res.getResult().length > 0) {
                CfUserElastic user = res.getResult()[0];

                Long millis;
                try {
                    millis = new SimpleDateFormat("dd/MM/yyyy").parse(date).getTime();
                } catch (ParseException e) {
                    throw new ParseException("Invalid date format. Please provide date in the format: dd/MM/yyyy", 0);
                }

                String epochSeconds = millis.toString();
                user.setDate(epochSeconds);
                return codeforcesElasticRepository.save(user);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return null;
    }


    public Page<CfUserElastic> getAllUsers(int pageNumber, int pageSize) {
        return codeforcesElasticRepository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    public void deleteAllUsers() {
        codeforcesElasticRepository.deleteAll();
    }

    public List<CfUserElastic> getUserByName(String name, int pageNumber, int pageSize) {
        Page<CfUserElastic> page = codeforcesElasticRepository.searchByName(name, PageRequest.of(pageNumber, pageSize));
        List<CfUserElastic> users = page.getContent();
        return users;
    }

    public List<CfUserElastic> sortByRatingAsc() {
        Query query = NativeQuery.builder().withSort(Sort.by(Sort.Direction.ASC, "rating")).build();
        SearchHits<CfUserElastic> searchHits = operations.search(query, CfUserElastic.class);

        List<CfUserElastic> cfUserElastics = searchHits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());

        return cfUserElastics;
    }

    public CfUserElastic getHighestRatedCoderByCountry(String country) {
        Query query = NativeQuery.builder().withQuery(q -> q.match(m -> m.field("country").query(country)))
                .withSort(Sort.by(Sort.Direction.DESC, "rating"))
                .withPageable(PageRequest.of(0, 1)).build();

        SearchHits<CfUserElastic> searchHits = operations.search(query, CfUserElastic.class);
        if (searchHits.hasSearchHits()) {
            return searchHits.getSearchHit(0).getContent();
        } else {
            return null;
        }
    }

    public List<CfUserElastic> getUsersGreaterThanRatingOnaDate(int rating, int pageNumber, int pageSize) {
        Query query = NativeQuery.builder().withAggregation("dates", Aggregation.of(a -> a.terms(ta -> ta.field("date").size(10))))
                .withQuery(q -> q.range(r -> r.field("rating").gt(JsonData.of(rating))))
                .withSort(Sort.by(Sort.Direction.DESC, "rating"))
                .withPageable(PageRequest.of(pageNumber, pageSize)).build();

        SearchHits<CfUserElastic> searchHits = operations.search(query, CfUserElastic.class);
        List<CfUserElastic> res = new ArrayList<>();

        if (searchHits.hasSearchHits()) {
            for (SearchHit<CfUserElastic> searchHit : searchHits) {
                res.add(searchHit.getContent());
            }
            return res;
        } else {
            return null;
        }

    }
    public String dateAggregation(){
        String url = "http://localhost:9200/codeforces/_search";
        String requestBody = "{\n" +
                "  \"query\": {\n" +
                "    \"range\": {\n" +
                "      \"rating\": {\n" +
                "        \"gte\": 1900\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"aggs\": {\n" +
                "    \"dates\": {\n" +
                "      \"terms\": {\n" +
                "        \"field\": \"date\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        CompletableFuture<HttpResponse<String>> responseFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = responseFuture.thenApply(HttpResponse::body).join();
        System.out.println(responseBody);
        return responseBody;
    }






}
