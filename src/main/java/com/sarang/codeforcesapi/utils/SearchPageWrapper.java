package com.sarang.codeforcesapi.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;

@Getter
@Setter
public class SearchPageWrapper <T>{
    private SearchPage<T> searchPage;

    @JsonIgnoreProperties(value = "aggregations")
    public SearchPage<T> getSearchPage() {
        return searchPage;
    }
}
