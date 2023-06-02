package com.sarang.codeforcesapi.services;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.json.JsonData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sarang.codeforcesapi.models.CfUserElastic;
import com.sarang.codeforcesapi.repositories.CodeforcesElasticRepository;
import com.sarang.codeforcesapi.utils.CodeforcesApiResponseElastic;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.QueryBuilders;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;


@Service
public class CodeforcesElasticService {

    private static final String CODEFORCES_API_URL = "https://codeforces.com/api/user.info?handles={handle}";

    private CodeforcesElasticRepository codeforcesElasticRepository;
    private final RestTemplate restTemplate;

    @Autowired
    ElasticsearchOperations operations;


    @Autowired
    RestClient restClient;

    @Autowired
    public CodeforcesElasticService(CodeforcesElasticRepository codeforcesElasticRepository, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.codeforcesElasticRepository = codeforcesElasticRepository;
    }

    public CfUserElastic fetchAndSaveUser(String handle) throws ParseException {
//        RestTemplate restTemplate= new RestTemplate();
        String apiUrl = CODEFORCES_API_URL.replace("{handle}", handle);
        CodeforcesApiResponseElastic res = restTemplate.getForObject(apiUrl, CodeforcesApiResponseElastic.class);

        CfUserElastic user = null;
        if (res != null) {
            user = res.getResult()[0];
        }

        if (user != null) {
            String strDate = "22/05/2023";
            Long millis = new SimpleDateFormat("dd/MM/yyyy").parse(strDate).getTime();
            String epochSeconds=millis.toString();
            user.setDate(epochSeconds);
            return codeforcesElasticRepository.save(user);
        }

        return null;


    }

    public Page<CfUserElastic> getAllUsers() {
        return codeforcesElasticRepository.findAll(PageRequest.of(0, 10));
    }

    public void deleteAllusers(){
        codeforcesElasticRepository.deleteAll();
    }

    public Page<CfUserElastic> getUserByname(String name) {
        return codeforcesElasticRepository.searchByName(name, PageRequest.of(0, 10));
    }

    public List<CfUserElastic> sortByRatingAsc() {
        Query query = NativeQuery.builder()
                .withSort(Sort.by(Sort.Direction.ASC, "rating"))
                .build();
        SearchHits<CfUserElastic> searchHits = operations.search(query, CfUserElastic.class);

        List<CfUserElastic> cfUserElastics = searchHits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return cfUserElastics;
    }

    public CfUserElastic getHighestRatedCoderByCountry(String country) {
        Query query = NativeQuery.builder()
                .withQuery(q -> q
                        .match(m -> m
                                .field("country")
                                .query(country)
                        )
                ).withSort(Sort.by(Sort.Direction.DESC, "rating"))
                .withPageable(PageRequest.of(0,1))
                .build();

        SearchHits<CfUserElastic> searchHits = operations.search(query, CfUserElastic.class);
        if (searchHits.hasSearchHits()) {
            return searchHits.getSearchHit(0).getContent();
        } else {
            return null; // or handle the case when no results are found
        }
    }

    public List<CfUserElastic> dateHistogram(){
        Query query = NativeQuery.builder()
                .withAggregation("dates", Aggregation.of(a -> a
                        .terms(ta -> ta.field("date").size(10))))
                .withQuery(q -> q.range(r -> r.field("rating").gt(JsonData.of(1900))))
                .withSort(Sort.by(Sort.Direction.DESC, "rating"))
                .withPageable(PageRequest.of(0, 10))
                .build();

        SearchHits<CfUserElastic> searchHits = operations.search(query, CfUserElastic.class);
        List<CfUserElastic> res = new ArrayList<>();
        for (SearchHit<CfUserElastic> searchHit : searchHits) {
            res.add(searchHit.getContent());
        }
        return res;
    }






}
