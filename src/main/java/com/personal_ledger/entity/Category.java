package com.personal_ledger.entity;

import javax.persistence.*;

@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;   // 分类名称
    private String type;   // "income" 或 "expense"
    private boolean preset = true; // 是否预设分类

    // 构造器、getter/setter
    public Category() {}
    public Category(String name, String type) { this.name = name; this.type = type; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isPreset() { return preset; }
    public void setPreset(boolean preset) { this.preset = preset; }
}