package org.nanospark.versionablehelper.core.infrastructure.po;

import org.nanospark.versionablehelper.core.annotation.CrossDayChangeDetect;
import org.nanospark.versionablehelper.core.infrastructure.constants.Gender;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
public class DemoBasePO extends VersionableBasePO {

    private String name;

    private Gender gender;

    private Integer age;

    @Override
    public String toString() {
        return "DemoBasePO{" +
                "name='" + name + '\'' +
                ", gender=" + gender +
                ", age=" + age +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DemoBasePO)) return false;
        if (!super.equals(o)) return false;
        DemoBasePO that = (DemoBasePO) o;
        return Objects.equals(name, that.name) &&
                gender == that.gender &&
                Objects.equals(age, that.age);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, gender, age);
    }

    @Column
    @CrossDayChangeDetect
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
