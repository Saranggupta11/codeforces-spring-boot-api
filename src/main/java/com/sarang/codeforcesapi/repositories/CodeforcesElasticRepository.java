package com.sarang.codeforcesapi.repositories;

import com.sarang.codeforcesapi.models.CfUserElastic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeforcesElasticRepository extends ElasticsearchRepository<CfUserElastic,String> {

    @Query("{\"bool\": {\"must\": [{\"wildcard\": {\"firstName\": \"*?0*\"}}]}}")
    Page<CfUserElastic> searchByName(String name, Pageable pageable);

}
