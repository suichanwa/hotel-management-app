package com.example.hotelmanagementapp.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hotelmanagementapp.data.HotelDbHelper;
import com.example.hotelmanagementapp.model.Booking;

import java.util.ArrayList;
import java.util.List;

public class BookingRepository {
    private final HotelDbHelper dbHelper;

    public BookingRepository(Context context) {
        dbHelper = new HotelDbHelper(context.getApplicationContext());
    }

    public long addBooking(Booking booking) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = toContentValues(booking);
        return db.insert(HotelDbHelper.TABLE_BOOKINGS, null, values);
    }

    public List<Booking> getAllBookings() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(getJoinedBookingQuery() + " ORDER BY b."
                + HotelDbHelper.COLUMN_BOOKING_CHECK_IN + " DESC", null);
        List<Booking> bookings = readBookings(cursor);
        cursor.close();
        return bookings;
    }

    public Booking getBookingById(long bookingId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                getJoinedBookingQuery() + " WHERE b." + HotelDbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(bookingId)}
        );
        Booking booking = null;
        if (cursor.moveToFirst()) {
            booking = mapBooking(cursor);
        }
        cursor.close();
        return booking;
    }

    public int updateBooking(Booking booking) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = toContentValues(booking);
        return db.update(
                HotelDbHelper.TABLE_BOOKINGS,
                values,
                HotelDbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(booking.getId())}
        );
    }

    public int deleteBooking(long bookingId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
                HotelDbHelper.TABLE_BOOKINGS,
                HotelDbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(bookingId)}
        );
    }

    public int getBookingCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + HotelDbHelper.TABLE_BOOKINGS,
                null
        );
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    private ContentValues toContentValues(Booking booking) {
        ContentValues values = new ContentValues();
        values.put(HotelDbHelper.COLUMN_BOOKING_ROOM_ID, booking.getRoomId());
        values.put(HotelDbHelper.COLUMN_BOOKING_GUEST_NAME, booking.getGuestName());
        values.put(HotelDbHelper.COLUMN_BOOKING_CHECK_IN, booking.getCheckInDate());
        values.put(HotelDbHelper.COLUMN_BOOKING_CHECK_OUT, booking.getCheckOutDate());
        values.put(HotelDbHelper.COLUMN_BOOKING_GUESTS, booking.getGuestsCount());
        values.put(HotelDbHelper.COLUMN_BOOKING_BREAKFAST, booking.isBreakfastIncluded() ? 1 : 0);
        values.put(HotelDbHelper.COLUMN_BOOKING_TOTAL_PRICE, booking.getTotalPrice());
        return values;
    }

    private List<Booking> readBookings(Cursor cursor) {
        List<Booking> bookings = new ArrayList<>();
        while (cursor.moveToNext()) {
            bookings.add(mapBooking(cursor));
        }
        return bookings;
    }

    private Booking mapBooking(Cursor cursor) {
        Booking booking = new Booking();
        booking.setId(cursor.getLong(cursor.getColumnIndexOrThrow("booking_id")));
        booking.setRoomId(cursor.getLong(cursor.getColumnIndexOrThrow(HotelDbHelper.COLUMN_BOOKING_ROOM_ID)));
        booking.setGuestName(cursor.getString(cursor.getColumnIndexOrThrow(HotelDbHelper.COLUMN_BOOKING_GUEST_NAME)));
        booking.setCheckInDate(cursor.getString(cursor.getColumnIndexOrThrow(HotelDbHelper.COLUMN_BOOKING_CHECK_IN)));
        booking.setCheckOutDate(cursor.getString(cursor.getColumnIndexOrThrow(HotelDbHelper.COLUMN_BOOKING_CHECK_OUT)));
        booking.setGuestsCount(cursor.getInt(cursor.getColumnIndexOrThrow(HotelDbHelper.COLUMN_BOOKING_GUESTS)));
        booking.setBreakfastIncluded(cursor.getInt(cursor.getColumnIndexOrThrow(HotelDbHelper.COLUMN_BOOKING_BREAKFAST)) == 1);
        booking.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(HotelDbHelper.COLUMN_BOOKING_TOTAL_PRICE)));
        booking.setRoomName(cursor.getString(cursor.getColumnIndexOrThrow("room_name")));
        booking.setRoomType(cursor.getString(cursor.getColumnIndexOrThrow("room_type")));
        return booking;
    }

    private String getJoinedBookingQuery() {
        return "SELECT b." + HotelDbHelper.COLUMN_ID + " AS booking_id, b."
                + HotelDbHelper.COLUMN_BOOKING_ROOM_ID + ", b."
                + HotelDbHelper.COLUMN_BOOKING_GUEST_NAME + ", b."
                + HotelDbHelper.COLUMN_BOOKING_CHECK_IN + ", b."
                + HotelDbHelper.COLUMN_BOOKING_CHECK_OUT + ", b."
                + HotelDbHelper.COLUMN_BOOKING_GUESTS + ", b."
                + HotelDbHelper.COLUMN_BOOKING_BREAKFAST + ", b."
                + HotelDbHelper.COLUMN_BOOKING_TOTAL_PRICE + ", r."
                + HotelDbHelper.COLUMN_ROOM_NAME + " AS room_name, r."
                + HotelDbHelper.COLUMN_ROOM_TYPE + " AS room_type"
                + " FROM " + HotelDbHelper.TABLE_BOOKINGS + " b"
                + " INNER JOIN " + HotelDbHelper.TABLE_ROOMS + " r"
                + " ON b." + HotelDbHelper.COLUMN_BOOKING_ROOM_ID + " = r." + HotelDbHelper.COLUMN_ID;
    }
}
