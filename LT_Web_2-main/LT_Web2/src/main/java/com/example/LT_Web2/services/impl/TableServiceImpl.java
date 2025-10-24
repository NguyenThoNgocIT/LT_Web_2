package com.example.LT_Web2.services.impl;
import com.example.LT_Web2.entity.Tables;
import com.example.LT_Web2.entity.TableStatus;
import com.example.LT_Web2.exception.BusinessException;
import com.example.LT_Web2.exception.ResourceNotFoundException;
import com.example.LT_Web2.repository.TableRepository;
import com.example.LT_Web2.services.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class TableServiceImpl implements TableService {

    private final TableRepository tableRepository;

    // Các trạng thái hợp lệ có thể chuyển từ AVAILABLE
    private static final Set<TableStatus> FROM_AVAILABLE = Set.of(TableStatus.RESERVED, TableStatus.OCCUPIED);
    // Từ RESERVED
    private static final Set<TableStatus> FROM_RESERVED = Set.of(TableStatus.OCCUPIED, TableStatus.AVAILABLE);
    // Từ OCCUPIED
    private static final Set<TableStatus> FROM_OCCUPIED = Set.of(TableStatus.COMPLETED);
    // Từ COMPLETED
    private static final Set<TableStatus> FROM_COMPLETED = Set.of(TableStatus.AVAILABLE);

    @Override
    public Tables save(Tables table) {
        if (table.getName() == null || table.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên bàn không được để trống");
        }
        if (tableRepository.existsByName(table.getName())) {
            throw new BusinessException("Đã tồn tại bàn có tên: " + table.getName());
        }
        // Mặc định trạng thái mới là AVAILABLE
        if (table.getStatus() == null) {
            table.setStatus(TableStatus.AVAILABLE);
        }
        return tableRepository.save(table);
    }
    @Override
    public List<Tables> saveAll(List<Tables> tables) {
        for (Tables table : tables) {
            if (table.getName() == null || table.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Tên bàn không được để trống");
            }
            if (tableRepository.existsByName(table.getName())) {
                throw new BusinessException("Đã tồn tại bàn có tên: " + table.getName());
            }
        }
        return tableRepository.saveAll(tables);
    }

    @Override
    public List<Tables> findByStatus(TableStatus status) {
        return tableRepository.findByStatus(status);
    }
    @Override
    public Tables findById(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bàn với ID: " + id));
    }

    @Override
    public List<Tables> findAll() {
        return tableRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        Tables table = findById(id);
        if (table.getStatus() != TableStatus.AVAILABLE) {
            throw new BusinessException("Chỉ có thể xóa bàn đang ở trạng thái 'Trống'");
        }
        tableRepository.deleteById(id);
    }

    @Override
    public Tables updateStatus(Long id, TableStatus newStatus) {
        Tables table = findById(id);
        TableStatus currentStatus = table.getStatus();

        // 🔒 Kiểm tra chuyển trạng thái hợp lệ
        if (!isValidTransition(currentStatus, newStatus)) {
            throw new BusinessException(
                    String.format("Không thể chuyển bàn từ trạng thái '%s' sang '%s'",
                            currentStatus, newStatus)
            );
        }

        table.setStatus(newStatus);
        return tableRepository.save(table);
    }

    // ✅ Hàm kiểm tra luồng trạng thái hợp lệ
    private boolean isValidTransition(TableStatus from, TableStatus to) {
        if (from == TableStatus.AVAILABLE) {
            return FROM_AVAILABLE.contains(to);
        } else if (from == TableStatus.RESERVED) {
            return FROM_RESERVED.contains(to);
        } else if (from == TableStatus.OCCUPIED) {
            return FROM_OCCUPIED.contains(to);
        } else if (from == TableStatus.COMPLETED) {
            return FROM_COMPLETED.contains(to);
        }
        return false;
    }

    // 💡 (Mở rộng) Phương thức tiện ích: lấy tất cả bàn trống hoặc có thể dùng ngay
    public List<Tables> getAvailableOrReservedTables() {
        return tableRepository.findByStatusIn(Arrays.asList(TableStatus.AVAILABLE, TableStatus.RESERVED));
    }
}