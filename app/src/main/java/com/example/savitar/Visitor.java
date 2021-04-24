package com.example.savitar;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Visitor implements Serializable {
    private String name;
    private String licensePlates;
    private String hostAddress;
    private String hostName;
    private String hostEmail;
    private String hostPhoneNo;
    private String condominium;
    private boolean allowEntrance;
    @Exclude
    private String id;

    public Visitor() {
    }

    public Visitor(String name, String licensePlates, String hostAddress, String hostName, String hostEmail, String hostPhoneNo, String condominium, boolean allowEntrance) {
        this.name = name;
        this.licensePlates = licensePlates;
        this.hostAddress = hostAddress;
        this.hostName = hostName;
        this.hostEmail = hostEmail;
        this.hostPhoneNo = hostPhoneNo;
        this.condominium = condominium;
        this.allowEntrance = allowEntrance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostEmail() {
        return hostEmail;
    }

    public void setHostEmail(String hostEmail) {
        this.hostEmail = hostEmail;
    }

    public String getHostPhoneNo() {
        return hostPhoneNo;
    }

    public void setHostPhoneNo(String hostPhoneNo) {
        this.hostPhoneNo = hostPhoneNo;
    }

    public boolean isAllowEntrance() {
        return allowEntrance;
    }

    public void setAllowEntrance(boolean allowEntrance) {
        this.allowEntrance = allowEntrance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCondominium() {
        return condominium;
    }

    public void setCondominium(String condominium) {
        this.condominium = condominium;
    }

    @Override
    public String toString() {
        return "Visitor{" +
                "name='" + name + '\'' +
                ", licensePlates='" + licensePlates + '\'' +
                ", hostAddress='" + hostAddress + '\'' +
                ", hostName='" + hostName + '\'' +
                ", hostEmail='" + hostEmail + '\'' +
                ", hostPhoneNo='" + hostPhoneNo + '\'' +
                ", condominium='" + condominium + '\'' +
                ", allowEntrance=" + allowEntrance +
                '}';
    }
}
