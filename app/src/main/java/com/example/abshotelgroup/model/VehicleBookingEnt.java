package com.example.abshotelgroup.model;

public class VehicleBookingEnt {
    private Integer id;
    private String nic;
    private String vehType;
    private int numOfDays;
    private User user;

    public VehicleBookingEnt() {
    }

    public VehicleBookingEnt(Integer id, String nic, String vehType, int numOfDays, User user) {
        this.id = id;
        this.nic = nic;
        this.vehType = vehType;
        this.numOfDays = numOfDays;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getVehType() {
        return vehType;
    }

    public void setVehType(String vehType) {
        this.vehType = vehType;
    }

    public int getNumOfDays() {
        return numOfDays;
    }

    public void setNumOfDays(int numOfDays) {
        this.numOfDays = numOfDays;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}