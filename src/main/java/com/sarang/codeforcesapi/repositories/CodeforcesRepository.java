package com.sarang.codeforcesapi.repositories;

import com.sarang.codeforcesapi.models.CfUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeforcesRepository extends MongoRepository<CfUser,String> {

}
