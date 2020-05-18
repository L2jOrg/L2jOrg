package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.commons.database.annotation.Table;

import java.util.Objects;

@Table("global_tasks")
public class TaskData {

    @NonUpdatable
    private int id;
    private String name;
    private String type;
    @Column("last_activation")
    private long lastActivation;
    private String param1;
    private String param2;
    private String param3;

    public String getName() {
        return name;
    }

    public String geType() {
        return type;
    }

    public void setLastActivation(long lastActivation) {
        this.lastActivation = lastActivation;
    }

    public int getId() {
        return id;
    }

    public String getParam1() {
        return param1;
    }

    public String getParam2() {
        return param2;
    }

    public String getparam3() {
        return param3;
    }

    public long getLastActivation() {
        return lastActivation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskData taskData = (TaskData) o;
        return id == taskData.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public void setParam3(String param3) {
        this.param3 = param3;
    }
}

