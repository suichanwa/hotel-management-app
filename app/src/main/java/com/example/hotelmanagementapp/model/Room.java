package com.example.hotelmanagementapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Room implements Parcelable {
    private long id;
    private String name;
    private String type;
    private double pricePerNight;
    private String description;
    private String imageResName;
    private boolean available;

    public Room() {
    }

    public Room(long id, String name, String type, double pricePerNight, String description,
                String imageResName, boolean available) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.description = description;
        this.imageResName = imageResName;
        this.available = available;
    }

    protected Room(Parcel in) {
        id = in.readLong();
        name = in.readString();
        type = in.readString();
        pricePerNight = in.readDouble();
        description = in.readString();
        imageResName = in.readString();
        available = in.readByte() != 0;
    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageResName() {
        return imageResName;
    }

    public void setImageResName(String imageResName) {
        this.imageResName = imageResName;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeDouble(pricePerNight);
        dest.writeString(description);
        dest.writeString(imageResName);
        dest.writeByte((byte) (available ? 1 : 0));
    }
}
