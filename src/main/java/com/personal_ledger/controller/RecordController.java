package com.personal_ledger.controller;

import com.personal_ledger.entity.Record;
import com.personal_ledger.entity.Category;
import com.personal_ledger.service.RecordService;
import com.personal_ledger.service.CsvImportService;
import com.personal_ledger.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class RecordController {

    @Autowired
    private RecordService recordService;
    @Autowired
    private CsvImportService csvImportService;
    @Autowired
    private CategoryService categoryService;

    // ========== 页面跳转（Thymeleaf） ==========

    /**
     * 收支记录列表页（支持筛选、分页）
     */
    @GetMapping("/records")
    public String listRecordsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        Page<Record> recordPage = recordService.searchWithConditions(startDate, endDate, type, category, minAmount, maxAmount, pageable);
        model.addAttribute("records", recordPage.getContent());
        model.addAttribute("page", recordPage);

        // 本月汇总数据
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        double income = Optional.ofNullable(recordService.sumAmountByTypeAndDateRange("income", startOfMonth, endOfMonth)).orElse(0.0);
        double expense = Optional.ofNullable(recordService.sumAmountByTypeAndDateRange("expense", startOfMonth, endOfMonth)).orElse(0.0);
        Map<String, Double> summary = new HashMap<>();
        summary.put("income", income);
        summary.put("expense", expense);
        summary.put("balance", income - expense);
        model.addAttribute("summary", summary);

        // 分类列表（供筛选下拉框使用）
        model.addAttribute("categories", categoryService.findAll());
        return "records";
    }

    /**
     * 记账录入页面（整合手动记账和批量导入，支持新增和编辑）
     */
    @GetMapping("/entry")
    public String entryPage(@RequestParam(required = false) Long id, Model model) {
        if (id != null) {
            Record record = recordService.getRecordById(id);
            model.addAttribute("record", record);
        } else {
            model.addAttribute("record", new Record());
        }
        model.addAttribute("categories", categoryService.findAll());
        return "record-entry";
    }

    /**
     * 保存记录（新增或更新）
     */
    @PostMapping("/records/save")
    public String saveRecord(Record record) {
        recordService.saveRecord(record);
        return "redirect:/records";
    }

    /**
     * 删除记录（页面跳转版）
     */
    @GetMapping("/records/delete/{id}")
    public String deleteRecordPage(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return "redirect:/records";
    }

    /**
     * 批量导入页面（旧版独立页面，可保留，但建议使用 /entry 中的批量导入Tab）
     */
    @GetMapping("/records/import")
    public String showImportPage() {
        return "import";
    }

    /**
     * 处理 CSV 导入请求
     */
    @PostMapping("/records/import/upload")
    public String uploadCsv(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            List<Record> records = csvImportService.parseCsv(file);
            int count = csvImportService.saveAll(records);
            redirectAttributes.addFlashAttribute("message", "成功导入 " + count + " 条记录");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "导入失败：" + e.getMessage());
        }
        return "redirect:/records";
    }

    // ========== REST API（供前端 AJAX 调用，用于动态刷新列表和图表） ==========

    @GetMapping("/api/records")
    @ResponseBody
    public Page<Record> apiRecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "date"));
        return recordService.searchWithConditions(startDate, endDate, type, category, minAmount, maxAmount, pageable);
    }

    @GetMapping("/api/records/summary")
    @ResponseBody
    public Map<String, Double> apiSummary() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        Double totalIncome = recordService.sumAmountByTypeAndDateRange("income", startOfMonth, endOfMonth);
        Double totalExpense = recordService.sumAmountByTypeAndDateRange("expense", startOfMonth, endOfMonth);
        Map<String, Double> summary = new HashMap<>();
        summary.put("income", totalIncome != null ? totalIncome : 0.0);
        summary.put("expense", totalExpense != null ? totalExpense : 0.0);
        return summary;
    }

    @DeleteMapping("/api/records/{id}")
    @ResponseBody
    public void deleteRecordApi(@PathVariable Long id) {
        recordService.deleteRecord(id);
    }

    @GetMapping("/api/categories")
    @ResponseBody
    public List<Map<String, String>> getCategories() {
        List<Category> categories = categoryService.findAll();
        return categories.stream()
                .map(c -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", c.getName());
                    map.put("type", c.getType());
                    return map;
                })
                .collect(Collectors.toList());
    }
}