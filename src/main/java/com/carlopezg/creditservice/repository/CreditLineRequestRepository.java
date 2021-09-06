package com.carlopezg.creditservice.repository;

import com.carlopezg.creditservice.domain.CreditLineRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditLineRequestRepository extends JpaRepository<CreditLineRequest, Long> {

    Optional<CreditLineRequest> findFirstByApplicantOrderByRequestedDateDesc(String applicant);
}
