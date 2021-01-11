package com.reconsale.barkom.cms.repositories;

import com.reconsale.barkom.cms.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query(value = "select m from Message m where m.category = :c")
    List<Message> findAllByCategory(String c);

    @Query(value = "select m from Message m where lower(m.title) like lower(concat('%', :search, '%')) and m.category =:c")
    List<Message> findAllByCategoryAndTitle(String c, @Param("search") String title);
}
