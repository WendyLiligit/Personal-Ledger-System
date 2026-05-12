package com.personal_ledger.config;

import com.personal_ledger.entity.Record;
import com.personal_ledger.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RecordService recordService;

    @Override
    public void run(String... args) throws Exception {
        // 仅在数据库无记录时插入示例数据
        if (recordService.count() == 0) {
            // 支出记录（15条）
            saveRecord("expense", 128.00, "餐饮", LocalDate.of(2026, 5, 26), "火锅聚餐");
            saveRecord("expense", 32.00, "交通", LocalDate.of(2026, 5, 25), "滴滴打车");
            saveRecord("expense", 299.00, "购物", LocalDate.of(2026, 5, 24), "京东购物-耳机");
            saveRecord("expense", 45.00, "娱乐", LocalDate.of(2026, 5, 23), "电影票");
            saveRecord("expense", 18.50, "餐饮", LocalDate.of(2026, 5, 22), "午餐-沙县小吃");
            saveRecord("expense", 70.00, "交通", LocalDate.of(2026, 5, 20), "地铁月票");
            saveRecord("expense", 236.50, "购物", LocalDate.of(2026, 5, 18), "超市采购");
            saveRecord("expense", 56.00, "餐饮", LocalDate.of(2026, 5, 17), "奶茶咖啡");
            saveRecord("expense", 199.00, "购物", LocalDate.of(2026, 5, 15), "衣服");
            saveRecord("expense", 35.00, "交通", LocalDate.of(2026, 5, 14), "共享单车月卡");
            saveRecord("expense", 88.00, "娱乐", LocalDate.of(2026, 5, 12), "游戏充值");
            saveRecord("expense", 320.00, "住房", LocalDate.of(2026, 5, 10), "水电燃气费");
            saveRecord("expense", 450.00, "餐饮", LocalDate.of(2026, 5, 8), "朋友聚餐");
            saveRecord("expense", 28.00, "医疗", LocalDate.of(2026, 5, 5), "感冒药");
            saveRecord("expense", 1200.00, "住房", LocalDate.of(2026, 5, 1), "房租");

            // 收入记录（5条）
            saveRecord("income", 12500.00, "工资", LocalDate.of(2026, 5, 15), "5月工资");
            saveRecord("income", 500.00, "兼职", LocalDate.of(2026, 5, 20), "设计外包");
            saveRecord("income", 200.00, "理财", LocalDate.of(2026, 5, 22), "基金收益");
            saveRecord("income", 100.00, "红包", LocalDate.of(2026, 5, 25), "生日红包");
            saveRecord("income", 300.00, "其他", LocalDate.of(2026, 5, 28), "二手转卖");

            System.out.println("已插入20条示例收支记录");
        }
    }

    private void saveRecord(String type, double amount, String category, LocalDate date, String note) {
        Record record = new Record();
        record.setType(type);
        record.setAmount(amount);
        record.setCategory(category);
        record.setDate(date);
        record.setNote(note);
        record.setUserId(1L);  // 默认用户ID
        recordService.saveRecord(record);
    }
}