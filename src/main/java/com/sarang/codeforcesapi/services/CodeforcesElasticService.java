package com.sarang.codeforcesapi.services;

import com.sarang.codeforcesapi.models.CfUserElastic;
import com.sarang.codeforcesapi.repositories.CodeforcesElasticRepository;
import com.sarang.codeforcesapi.utils.CodeforcesApiResponseElastic;
import com.sarang.codeforcesapi.utils.SearchHitsWrapper;
import com.sarang.codeforcesapi.utils.SearchPageWrapper;
import org.elasticsearch.client.RestClient;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    public SearchHitsWrapper<CfUserElastic> sortByRatingAsc() {
        TermsAggregationBuilder aggregationBuilder = terms("rating_stats").field("rating")
                .order(BucketOrder.count(true));
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withSort(SortBuilders.fieldSort("rating").order(SortOrder.ASC))
                .addAggregation(aggregationBuilder)
                .withPageable(PageRequest.of(0, 10))
                .build();
        SearchHits<CfUserElastic> searchHits = operations.search(searchQuery, CfUserElastic.class);
        SearchHitsWrapper<CfUserElastic> wrapper = new SearchHitsWrapper<>();
        wrapper.setSearchHits(searchHits);
        return wrapper;
    }
    public SearchHitsWrapper<CfUserElastic> aggregateCountriesAndCities() {
        TermsAggregationBuilder countryAggregation = AggregationBuilders.terms("countries")
                .field("country.keyword")
                .size(10)
                .subAggregation(
                        AggregationBuilders.terms("cities")
                                .field("city.keyword")
                                .size(10)
                );

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withAggregations(countryAggregation)
                .withPageable(PageRequest.of(0, 10))
                .build();

        System.out.println(nativeSearchQuery.getQuery());

        SearchHits<CfUserElastic> searchHits = operations.search(nativeSearchQuery, CfUserElastic.class);
        SearchHitsWrapper<CfUserElastic> wrapper = new SearchHitsWrapper<>();
        wrapper.setSearchHits(searchHits);
        return wrapper;
    }









}
