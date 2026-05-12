package com.personal_ledger.repository;

import com.personal_ledger.entity.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;

public interface RecordRepository extends JpaRepository<Record, Long> {
    Page<Record> findAllByOrderByDateDesc(Pageable pageable);

    @Query("SELECT r FROM Record r WHERE " +
            "(:startDate IS NULL OR r.date >= :startDate) AND " +
            "(:endDate IS NULL OR r.date <= :endDate) AND " +
            "(:type IS NULL OR r.type = :type) AND " +
            "(:category IS NULL OR r.category = :category) " +
            "ORDER BY r.date DESC")
    Page<Record> search(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("type") String type,
                        @Param("category") String category,
                        Pageable pageable);
    @Query("SELECT SUM(r.amount) FROM Record r WHERE r.type = :type AND r.date BETWEEN :start AND :end")
    Double sumAmountByTypeAndDateRange(@Param("type") String type, @Param("start") LocalDate start, @Param("end") LocalDate end);


}