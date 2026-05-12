package com.personal_ledger.service;

import com.personal_ledger.entity.Record;
import com.personal_ledger.repository.RecordRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvImportService {

    @Autowired
    private RecordRepository recordRepository;

    public List<Record> parseCsv(MultipartFile file) throws Exception {
        List<Record> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            for (CSVRecord csvRecord : parser) {
                Record record = new Record();
                String dateStr = csvRecord.get("交易时间");
                String typeStr = csvRecord.get("交易类型");
                String amountStr = csvRecord.get("金额");
                String category = csvRecord.get("收支分类");
                String note = csvRecord.get("备注");

                LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                double amount = Double.parseDouble(amountStr);
                String type = "支出".equals(typeStr) ? "expense" : "income";

                record.setDate(date);
                record.setType(type);
                record.setAmount(amount);
                record.setCategory(category);
                record.setNote(note);
                record.setUserId(1L);
                records.add(record);
            }
        }
        return records;
    }

    public int saveAll(List<Record> records) {
        return recordRepository.saveAll(records).size();
    }
}