package com.megaminds.govisaviya.repository;

import com.megaminds.govisaviya.entity.PolygonRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolygonRecordRepository extends JpaRepository<PolygonRecord, Long> {

    /** Returns all polygon records created by a given user. */
    List<PolygonRecord> findByUserEmailOrderByCreatedAtDesc(String userEmail);
}
