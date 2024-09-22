package com.zemen.udms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zemen.udms.model.PositionCategory;

public interface PositionCategoryRepository extends JpaRepository<PositionCategory, String> {

    @Query("SELECT pc FROM PositionCategory pc WHERE pc.name = :name")
    PositionCategory findByName(@Param("name") String name);

    @Query("SELECT pc.name FROM PositionCategory pc ORDER BY pc.name ASC")
    List<String> findAllPositionCategoryNames();

    @Query("SELECT pc FROM PositionCategory pc ORDER BY pc.name ASC")
    List<PositionCategory> findAll();

}
