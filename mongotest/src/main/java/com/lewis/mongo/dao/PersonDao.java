package com.lewis.mongo.dao;

import com.lewis.mongo.entity.Person;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Created by liu on 2017/6/7.
 */
public interface PersonDao extends MongoRepository<Person, ObjectId> {

    @Query(value = "{'age' : {'$gte' : ?0, '$lte' : ?1}, 'name' : ?2}", fields = "{'name' : 1, 'age' : 1}")
    List<Person> findByAge(int age1, int age2, String name);
}
