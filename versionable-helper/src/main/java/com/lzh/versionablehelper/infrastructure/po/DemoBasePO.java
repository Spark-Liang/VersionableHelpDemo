package com.lzh.versionablehelper.infrastructure.po;

import com.lzh.versionablehelper.infrastructure.constants.Gender;

import javax.persistence.*;

@MappedSuperclass
public class DemoBasePO extends BasePO {

    private String name;

    private Gender gender;

    private Integer age;

    @Column
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(length = 8)
    @Enumerated(value = EnumType.STRING)
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Column
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
