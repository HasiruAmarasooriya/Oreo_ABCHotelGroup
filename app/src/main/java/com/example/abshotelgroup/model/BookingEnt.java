package com.example.abshotelgroup.model;

import java.util.Date;

public class BookingEnt {
    private Integer id;
    private String nic;
    private Date bookDate;
    private int numOfRooms;
    private String bookType;
    private User user;

    public BookingEnt() {
    }

    public BookingEnt(Integer id, String nic, Date bookDate, int numOfRooms, String bookType, User user) {
        this.id = id;
        this.nic = nic;
        this.bookDate = bookDate;
        this.numOfRooms = numOfRooms;
        this.bookType = bookType;
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

    public Date getBookDate() {
        return bookDate;
    }

    public void setBookDate(Date bookDate) {
        this.bookDate = bookDate;
    }

    public int getNumOfRooms() {
        return numOfRooms;
    }

    public void setNumOfRooms(int numOfRooms) {
        this.numOfRooms = numOfRooms;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}