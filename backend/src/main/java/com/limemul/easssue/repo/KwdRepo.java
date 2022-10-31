package com.limemul.easssue.repo;

import com.limemul.easssue.entity.Kwd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KwdRepo extends JpaRepository<Kwd,Long> {

    /**
     * 랜덤 키워드 하나 반환
     */
    @Query(value = "select * from kwd order by rand() limit 1",nativeQuery = true)
    List<Kwd> findByRandom();
}
