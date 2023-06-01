package com.sarang.codeforcesapi.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.core.SearchHits;

@Getter
@Setter
public class SearchHitsWrapper<T> {
    private SearchHits<T> searchHits;

    // Getters and setters

    @JsonIgnoreProperties(value = "aggregations")
    public SearchHits<T> getSearchHits() {
        return searchHits;
    }
}

