package com.example.LT_Web2.services;

import com.example.LT_Web2.entity.Tables;
import com.example.LT_Web2.entity.TableStatus;
import java.util.List;

public interface TableService {
    Tables save(Tables table);
    List<Tables> saveAll(List<Tables> tables);
    Tables findById(Long id);
    List<Tables> findAll();
    List<Tables> findByStatus(TableStatus status);
    void deleteById(Long id);
    Tables updateStatus(Long id, TableStatus status);
}