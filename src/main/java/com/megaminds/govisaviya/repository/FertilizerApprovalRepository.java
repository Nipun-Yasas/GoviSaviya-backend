package com.megaminds.govisaviya.repository;

import com.megaminds.govisaviya.entity.FertilizerApproval;
import com.megaminds.govisaviya.entity.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FertilizerApprovalRepository extends JpaRepository<FertilizerApproval, Long> {

    /** All requests submitted by a specific farmer (newest first). */
    List<FertilizerApproval> findByFarmerEmailOrderBySubmittedAtDesc(String farmerEmail);

    /** All requests with a given status – used by admins to filter the queue. */
    List<FertilizerApproval> findByStatusOrderBySubmittedAtDesc(ApprovalStatus status);

    /** All requests ordered by submission date (admin full list). */
    List<FertilizerApproval> findAllByOrderBySubmittedAtDesc();
}
