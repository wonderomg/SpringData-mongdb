package com.lewis.mongo.mongoDao;

import com.lewis.mongo.entity.Person;

import java.util.List;

/**
 * Created by liu on 2017/6/7.
 */
public interface PersonMongoDao {
    List<Person> findAll();
    void insertPerson(Person user);
    void removePerson(String userName);
    void updatePerson();
    List<Person> findForRequery(String userName);
}
