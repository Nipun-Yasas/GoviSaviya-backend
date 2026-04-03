package com.megaminds.govisaviya.repository;

import com.megaminds.govisaviya.entity.DiseaseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiseaseRecordRepository extends JpaRepository<DiseaseRecord, Long> {

    /** Returns all disease records submitted by a given user. */
    List<DiseaseRecord> findByUserEmailOrderByIdentifiedAtDesc(String userEmail);
}
