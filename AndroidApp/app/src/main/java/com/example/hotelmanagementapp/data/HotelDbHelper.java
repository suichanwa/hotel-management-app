package com.example.hotelmanagementapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HotelDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "hotel_management.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_ROOMS = "rooms";
    public static final String TABLE_BOOKINGS = "bookings";

    public static final String COLUMN_ID = "id";

    public static final String COLUMN_ROOM_NAME = "name";
    public static final String COLUMN_ROOM_TYPE = "type";
    public static final String COLUMN_ROOM_PRICE = "price_per_night";
    public static final String COLUMN_ROOM_DESCRIPTION = "description";
    public static final String COLUMN_ROOM_IMAGE = "image_res_name";
    public static final String COLUMN_ROOM_AVAILABLE = "is_available";

    public static final String COLUMN_BOOKING_ROOM_ID = "room_id";
    public static final String COLUMN_BOOKING_GUEST_NAME = "guest_name";
    public static final String COLUMN_BOOKING_CHECK_IN = "check_in_date";
    public static final String COLUMN_BOOKING_CHECK_OUT = "check_out_date";
    public static final String COLUMN_BOOKING_GUESTS = "guests_count";
    public static final String COLUMN_BOOKING_BREAKFAST = "breakfast_included";
    public static final String COLUMN_BOOKING_TOTAL_PRICE = "total_price";

    public HotelDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createRoomsTable = "CREATE TABLE " + TABLE_ROOMS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ROOM_NAME + " TEXT NOT NULL, "
                + COLUMN_ROOM_TYPE + " TEXT NOT NULL, "
                + COLUMN_ROOM_PRICE + " REAL NOT NULL, "
                + COLUMN_ROOM_DESCRIPTION + " TEXT NOT NULL, "
                + COLUMN_ROOM_IMAGE + " TEXT NOT NULL, "
                + COLUMN_ROOM_AVAILABLE + " INTEGER NOT NULL DEFAULT 1)";

        String createBookingsTable = "CREATE TABLE " + TABLE_BOOKINGS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_BOOKING_ROOM_ID + " INTEGER NOT NULL, "
                + COLUMN_BOOKING_GUEST_NAME + " TEXT NOT NULL, "
                + COLUMN_BOOKING_CHECK_IN + " TEXT NOT NULL, "
                + COLUMN_BOOKING_CHECK_OUT + " TEXT NOT NULL, "
                + COLUMN_BOOKING_GUESTS + " INTEGER NOT NULL, "
                + COLUMN_BOOKING_BREAKFAST + " INTEGER NOT NULL, "
                + COLUMN_BOOKING_TOTAL_PRICE + " REAL NOT NULL, "
                + "FOREIGN KEY(" + COLUMN_BOOKING_ROOM_ID + ") REFERENCES "
                + TABLE_ROOMS + "(" + COLUMN_ID + "))";

        db.execSQL(createRoomsTable);
        db.execSQL(createBookingsTable);
        seedRooms(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
        onCreate(db);
    }

    private void seedRooms(SQLiteDatabase db) {
        insertRoom(db, "Ocean Breeze Single", "Single", 95.0,
                "A bright single room with a city skyline view, smart TV, and workspace.",
                "hotel_room_single", true);
        insertRoom(db, "Royal Comfort Double", "Double", 145.0,
                "An elegant double room with a king-size bed, minibar, and reading corner.",
                "hotel_room_double", true);
        insertRoom(db, "Azure Executive Suite", "Suite", 245.0,
                "A spacious suite with a lounge area, premium toiletries, and panoramic windows.",
                "hotel_room_suite", true);
        insertRoom(db, "Golden Deluxe Retreat", "Deluxe", 310.0,
                "A deluxe room with a private sitting area, luxury linens, and premium breakfast.",
                "hotel_room_deluxe", true);
    }

    private void insertRoom(SQLiteDatabase db, String name, String type, double price, String description,
                            String imageResName, boolean isAvailable) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_NAME, name);
        values.put(COLUMN_ROOM_TYPE, type);
        values.put(COLUMN_ROOM_PRICE, price);
        values.put(COLUMN_ROOM_DESCRIPTION, description);
        values.put(COLUMN_ROOM_IMAGE, imageResName);
        values.put(COLUMN_ROOM_AVAILABLE, isAvailable ? 1 : 0);
        db.insert(TABLE_ROOMS, null, values);
    }
}
