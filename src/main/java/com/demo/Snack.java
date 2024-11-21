package com.demo;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Cacheable
public class Snack extends PanacheEntityBase {

    @Id
    @SequenceGenerator(
            name = "snackSequence",
            sequenceName = "snack_id_seq",
            allocationSize = 1,
            initialValue = 4)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "snackSequence")
    public Long id;

    @Column(length = 40, unique = true)
    public String name;

    public Snack() {
    }

    public Snack(String name) {
        this.name = name;
    }
}
