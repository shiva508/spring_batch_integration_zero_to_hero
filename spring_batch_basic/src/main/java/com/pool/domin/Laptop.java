package com.pool.domin;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TBL_LAPTOP")
public class Laptop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long laptopId;
    private String laptopName;
    private Integer price;

    public Laptop() {
    }

    public Laptop(String laptopName, Integer price) {
        this.laptopName = laptopName;
        this.price = price;
    }

    public Long getLaptopId() {
        return laptopId;
    }

    public String getLaptopName() {
        return laptopName;
    }

    public Integer getPrice() {
        return price;
    }

    public void setLaptopId(Long laptopId) {
        this.laptopId = laptopId;
    }

    public void setLaptopName(String laptopName) {
        this.laptopName = laptopName;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

}
