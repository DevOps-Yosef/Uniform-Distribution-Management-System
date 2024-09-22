package com.zemen.udms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zemen.udms.model.LuStatus;

public interface LuStatusRepository extends JpaRepository<LuStatus, String> {

    @Query("SELECT ls FROM LuStatus ls WHERE ls.name = :name")
    LuStatus findByName(@Param("name") String name);

}
