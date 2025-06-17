package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;

public abstract class Worker {
    protected City city;
    protected String name;
    protected String role;

    public Worker(City city, String name, String role) {
        this.city = city;
        this.name = name;
        this.role = role;
    }

    public City getCity() {
        return city;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
}
