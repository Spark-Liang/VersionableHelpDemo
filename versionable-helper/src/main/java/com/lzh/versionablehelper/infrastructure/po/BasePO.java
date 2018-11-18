package com.lzh.versionablehelper.infrastructure.po;

import javax.persistence.*;
import java.time.LocalDate;

@MappedSuperclass
public class BasePO {

    private Long id;

    private Long version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Version
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

}
