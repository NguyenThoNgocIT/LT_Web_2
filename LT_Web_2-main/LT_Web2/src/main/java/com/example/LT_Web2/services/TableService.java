package com.example.LT_Web2.services;

import com.example.LT_Web2.entity.Table;
import com.example.LT_Web2.entity.TableStatus;
import java.util.List;

public interface TableService {
    Table save(Table table);
    Table findById(Long id);
    List<Table> findAll();
    List<Table> findByStatus(TableStatus status);
    void deleteById(Long id);
    Table updateStatus(Long id, TableStatus status);
}