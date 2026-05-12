package com.personal_ledger.service;

import com.personal_ledger.entity.Record;
import com.personal_ledger.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecordService {

    @Autowired
    private RecordRepository recordRepository;

    // 原有方法：分页查询（无金额区间）
    public Page<Record> getRecords(int page, int size, LocalDate startDate, LocalDate endDate, String type, String category) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return recordRepository.search(startDate, endDate, type, category, pageable);
    }

    // 新增方法：支持金额区间筛选（由 Controller 调用）
    public Page<Record> searchWithConditions(LocalDate startDate, LocalDate endDate,
                                             String type, String category,
                                             Double minAmount, Double maxAmount,
                                             Pageable pageable) {
        // 先调用 repository 的基础筛选（日期、类型、分类）
        Page<Record> page = recordRepository.search(startDate, endDate, type, category, pageable);
        // 如果不需要金额过滤，直接返回
        if (minAmount == null && maxAmount == null) {
            return page;
        }
        // 否则在内存中过滤金额（适合数据量不大的场景）
        List<Record> filtered = page.getContent().stream()
                .filter(r -> (minAmount == null || r.getAmount() >= minAmount) &&
                        (maxAmount == null || r.getAmount() <= maxAmount))
                .collect(Collectors.toList());
        return new PageImpl<>(filtered, pageable, filtered.size());
    }

    // 新增方法：按类型和日期范围统计总金额
    public Double sumAmountByTypeAndDateRange(String type, LocalDate start, LocalDate end) {
        return recordRepository.sumAmountByTypeAndDateRange(type, start, end);
    }

    // 保存或更新记录
    public Record saveRecord(Record record) {
        return recordRepository.save(record);
    }

    // 根据 ID 获取记录
    public Record getRecordById(Long id) {
        return recordRepository.findById(id).orElse(null);
    }

    // 删除记录
    public void deleteRecord(Long id) {
        recordRepository.deleteById(id);
    }
//示例数据
    public long count() {
        return recordRepository.count();
    }
}