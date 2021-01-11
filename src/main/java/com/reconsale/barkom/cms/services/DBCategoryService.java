package com.reconsale.barkom.cms.services;

import com.reconsale.barkom.cms.models.DBCategory;
import com.reconsale.barkom.cms.repositories.DBCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DBCategoryService {

    private DBCategoryRepository dbCategoryRepository;

    @Autowired
    public DBCategoryService(DBCategoryRepository dbCategoryRepository) {
        this.dbCategoryRepository = dbCategoryRepository;
    }

    public DBCategory getCategoryByName(String name){
       return dbCategoryRepository.getDBCategoryByName(name);
    }

    public void save (DBCategory dbCategory){
        dbCategoryRepository.save(dbCategory);
    }

    public void deleteAll (){
        dbCategoryRepository.deleteAll();
    }
}
