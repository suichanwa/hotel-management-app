package com.example.hotelmanagementapp.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hotelmanagementapp.data.HotelDbHelper;
import com.example.hotelmanagementapp.model.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomRepository {
    private final HotelDbHelper dbHelper;

    public RoomRepository(Context context) {
        dbHelper = new HotelDbHelper(context.getApplicationContext());
    }

    public List<Room> getAllRooms() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                HotelDbHelper.TABLE_ROOMS,
                null,
                null,
                null,
                null,
                null,
                HotelDbHelper.COLUMN_ROOM_PRICE + " ASC"
        );
        List<Room> rooms = readRooms(cursor);
        cursor.close();
        return rooms;
    }

    public List<Room> searchRooms(String query) {
        String safeQuery = query == null ? "" : query.trim();
        if (safeQuery.isEmpty()) {
            return getAllRooms();
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = HotelDbHelper.COLUMN_ROOM_NAME + " LIKE ? OR "
                + HotelDbHelper.COLUMN_ROOM_TYPE + " LIKE ?";
        String wildcard = "%" + safeQuery + "%";
        Cursor cursor = db.query(
                HotelDbHelper.TABLE_ROOMS,
                null,
                selection,
                new String[]{wildcard, wildcard},
                null,
                null,
                HotelDbHelper.COLUMN_ROOM_PRICE + " ASC"
        );
        List<Room> rooms = readRooms(cursor);
        cursor.close();
        return rooms;
    }

    public Room getRoomById(long roomId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                HotelDbHelper.TABLE_ROOMS,
                null,
                HotelDbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(roomId)},
                null,
                null,
                null
        );
        Room room = null;
        if (cursor.moveToFirst()) {
            room = mapRoom(cursor);
        }
        cursor.close();
        return room;
    }

    public Room getFirstAvailableRoomByType(String type) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                HotelDbHelper.TABLE_ROOMS,
                null,
                HotelDbHelper.COLUMN_ROOM_TYPE + " = ? AND " + HotelDbHelper.COLUMN_ROOM_AVAILABLE + " = 1",
                new String[]{type},
                null,
                null,
                HotelDbHelper.COLUMN_ROOM_PRICE + " ASC",
                "1"
        );
        Room room = null;
        if (cursor.moveToFirst()) {
            room = mapRoom(cursor);
        }
        cursor.close();
        return room;
    }

    private List<Room> readRooms(Cursor cursor) {
        List<Room> rooms = new ArrayList<>();
        while (cursor.moveToNext()) {
            rooms.add(mapRoom(cursor));
        }
        return rooms;
    }

    private Room mapRoom(Cursor cursor) {
        Room room = new Room();
        room.setId(cursor.getLong(cursor.getColumnIndexOrThrow(HotelDbHelper.COLUMN_ID)));
        room.setName(cursor.getString(cursor.getColumnIndexOrThrow(HotelDbHelper.COLUMN_ROOM_NAME)));
        room.setType(cursor.getString(cursor.getColumnIndexOrThrow(HotelDbHelper.COLUMN_ROOM_TYPE)));
        room.setPricePerNight(cursor.getDouble(cursor.getColumnIndexOrThrow(HotelDbHelper.COLUMN_ROOM_PRICE)));
        room.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(HotelDbHelper.COLUMN_ROOM_DESCRIPTION)));
        room.setImageResName(cursor.getString(cursor.getColumnIndexOrThrow(HotelDbHelper.COLUMN_ROOM_IMAGE)));
        room.setAvailable(cursor.getInt(cursor.getColumnIndexOrThrow(HotelDbHelper.COLUMN_ROOM_AVAILABLE)) == 1);
        return room;
    }
}
