package com.example.LT_Web2.repository;
import com.example.LT_Web2.entity.Tables;
import com.example.LT_Web2.entity.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableRepository extends JpaRepository<Tables, Long> {
    List<Tables> findByStatus(TableStatus status);

    List<Tables> findByStatusIn(List<TableStatus> statuses); // ← Thêm dòng này

    boolean existsByName(String name);
}

