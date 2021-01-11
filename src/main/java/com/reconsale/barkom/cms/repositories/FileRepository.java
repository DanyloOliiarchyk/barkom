package com.reconsale.barkom.cms.repositories;

import com.reconsale.barkom.cms.models.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    @Query(value = "select f from File f where f.title = :string")
    File findByTitle(String string);
}
