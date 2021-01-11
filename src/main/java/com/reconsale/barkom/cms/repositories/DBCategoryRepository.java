package com.reconsale.barkom.cms.repositories;

import com.reconsale.barkom.cms.models.DBCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DBCategoryRepository extends JpaRepository<DBCategory, Long> {

    @Query(value = "select c from DBCategory c where c.name = :string")
    DBCategory getDBCategoryByName(String string);
}
