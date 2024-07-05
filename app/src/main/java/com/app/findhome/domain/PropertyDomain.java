package com.app.findhome.domain;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Map;

public class PropertyDomain implements Serializable {
    private int id;
    private String title;
    private String type;
    private String address;
    private String description;
    private String pickPath;
    private int price;
    private int member;
    private boolean wifi;
    private int size;
    private boolean garage;
    private double score;

    private String userId;

    private Map<String, Boolean> facilities;

    public PropertyDomain() {
    }

    public PropertyDomain(int id, String type, String title, String address, String pickPath, int price, int member, int size, double score, String description, String userId, Map<String, Boolean> facilities) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.address = address;
        this.description = description;
        this.pickPath = pickPath;
        this.price = price;
        this.member = member;
        this.wifi = wifi;
        this.garage = garage;
        this.size = size;
        this.score = score;
        this.userId = userId;
        this.facilities = facilities;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPickPath() {
        return pickPath;
    }

    public void setPickPath(String pickPath) {
        this.pickPath = pickPath;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getMember() {
        return member;
    }

    public void setMember(int member) {
        this.member = member;
    }

//    public boolean getWifi() {
//        return wifi;
//    }
//
//    public void setWifi(boolean wifi) {
//        this.wifi = wifi;
//    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

//    public boolean getGarage() {
//        return garage;
//    }
//
//    public void setGarage(boolean garage) {
//        this.garage = garage;
//    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String, Boolean> getFacilities() {
        return facilities;
    }

    public void setFacilities(Map<String, Boolean> facilities) {
        this.facilities = facilities;
    }

    public boolean getWifi() {
        return facilities != null && Boolean.TRUE.equals(facilities.getOrDefault("wifi", false));
    }

    public void setWifi(boolean wifi) {
        if (facilities != null) {
            facilities.put("wifi", wifi);
        }
    }

    public boolean getGarage() {
        return facilities != null && Boolean.TRUE.equals(facilities.getOrDefault("garage", false));
    }

    public void setGarage(boolean garage) {
        if (facilities != null) {
            facilities.put("garage", garage);
        }
    }
//    @NonNull
//    @Override
//    public String toString() {
//        StringBuilder builder = new StringBuilder();
//        builder.append("PropertyDomain(");
//        builder.append("\"id\":").append(id).append(",");
//        builder.append("\"type\":\"").append(type).append("\",");
//        builder.append("\"title\":\"").append(title).append("\",");
//        builder.append("\"address\":\"").append(address).append("\",");
//        builder.append("\"pickPath\":\"").append(pickPath).append("\",");
//        builder.append("\"price\":").append(price).append(",");
//        builder.append("\"member\":").append(member).append(",");
//        builder.append("\"size\":").append(size).append(",");
//        builder.append("\"score\":").append(score).append(",");
//        builder.append("\"description\":\"").append(description).append("\",");
//        builder.append("\"userId\":\"").append(userId).append("\",");
//        builder.append("\"facilities\":").append(mapToJsonString(facilities));
//        builder.append(")");
//        return builder.toString();
//    }
//
    private String mapToJsonString(Map<String, Boolean> map) {
        StringBuilder builder = new StringBuilder();
        builder.append("mapOf(");
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            builder.append("\"").append(entry.getKey()).append("\" to ").append(entry.getValue()).append(",");
        }
        if (builder.length() > 1) {
            builder.setLength(builder.length() - 1);
        }
        builder.append(")");
        return builder.toString();
    }

    @NonNull
    @Override
    public String toString() {
        return "PropertyDomain(" +
                id + ",\"" +
                type + "\",\"" +
                title + "\",\"" +
                address + "\",\"" +
                pickPath + "\"," +
                price + "," +
                member + "," +
                size + "," +
                score + ",\"" +
                description + "\",\"" +
                userId + "\"," +
                mapToJsonString(facilities) +
                ")";
    }
}
