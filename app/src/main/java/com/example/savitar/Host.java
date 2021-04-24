package com.example.savitar;

import java.io.Serializable;

public class Host implements Serializable {
    private String fName;
    private String address;
    private String email;
    private String phone;

    public Host() {
    }

    public Host(String fName, String address, String email, String phone) {
        this.fName = fName;
        this.address = address;
        this.email = email;
        this.phone = phone;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
