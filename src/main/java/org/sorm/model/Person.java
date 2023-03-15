package org.sorm.model;

import org.sorm.Column;
import org.sorm.PrimaryKey;

public class Person {
    @PrimaryKey
    private long id;

    @Column
    private int age;

    @Column
    private String name;

    public Person() {
    }

    public Person(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
