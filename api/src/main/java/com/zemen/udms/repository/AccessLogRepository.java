package com.zemen.udms.repository;


import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.zemen.udms.model.AccessLog;


@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, String> {

    @Query("SELECT al FROM AccessLog al ORDER BY al.date DESC")
    Page<AccessLog> findAllOrderByDate(Pageable pageable);

    @Query("SELECT al FROM AccessLog al WHERE al.date between :dateFrom and :dateTo ORDER BY al.date DESC")
    Page<AccessLog> findByDateBetween(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, Pageable pageable);

    @Query("SELECT count(al) FROM AccessLog al WHERE al.date between :dateFrom and :dateTo")
    long countByDateBetween(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT count(al) FROM AccessLog al")
    long countAll();
}
