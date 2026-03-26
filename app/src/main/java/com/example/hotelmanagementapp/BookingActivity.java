package com.example.hotelmanagementapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelmanagementapp.databinding.ActivityBookingBinding;
import com.example.hotelmanagementapp.model.Booking;
import com.example.hotelmanagementapp.model.Room;
import com.example.hotelmanagementapp.repository.BookingRepository;
import com.example.hotelmanagementapp.repository.RoomRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BookingActivity extends AppCompatActivity {
    private static final double BREAKFAST_PRICE_PER_GUEST = 15.0;
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private ActivityBookingBinding binding;
    private RoomRepository roomRepository;
    private BookingRepository bookingRepository;
    private Room selectedRoom;
    private Calendar checkInCalendar;
    private Calendar checkOutCalendar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        roomRepository = new RoomRepository(this);
        bookingRepository = new BookingRepository(this);
        selectedRoom = getIntent().getParcelableExtra(RoomDetailActivity.EXTRA_ROOM);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        setupDates();
        setupSpinner();
        setupGuests();
        setupListeners();
        updateSelectedRoom();
        updateDateViews();
        updatePriceSummary();
    }

    private void setupDates() {
        checkInCalendar = Calendar.getInstance();
        resetTime(checkInCalendar);
        checkOutCalendar = (Calendar) checkInCalendar.clone();
        checkOutCalendar.add(Calendar.DAY_OF_MONTH, 1);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.room_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerRoomType.setAdapter(adapter);

        if (selectedRoom != null) {
            String[] types = getResources().getStringArray(R.array.room_types);
            for (int index = 0; index < types.length; index++) {
                if (types[index].equalsIgnoreCase(selectedRoom.getType())) {
                    binding.spinnerRoomType.setSelection(index);
                    break;
                }
            }
        }
    }

    private void setupGuests() {
        binding.seekGuests.setProgress(0);
        binding.textGuests.setText(getString(R.string.guests_count, getGuestsCount()));
    }

    private void setupListeners() {
        binding.buttonCheckIn.setOnClickListener(v -> showDatePicker(checkInCalendar, true));
        binding.buttonCheckOut.setOnClickListener(v -> showDatePicker(checkOutCalendar, false));
        binding.spinnerRoomType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateSelectedRoom();
                updatePriceSummary();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        binding.seekGuests.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.textGuests.setText(getString(R.string.guests_count, getGuestsCount()));
                updatePriceSummary();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        binding.checkBreakfast.setOnCheckedChangeListener((buttonView, isChecked) -> updatePriceSummary());
        binding.buttonSaveBooking.setOnClickListener(v -> saveBooking());
    }

    private void showDatePicker(Calendar calendar, boolean isCheckIn) {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar target = isCheckIn ? checkInCalendar : checkOutCalendar;
                    target.set(year, month, dayOfMonth);
                    resetTime(target);
                    updateDateViews();
                    updatePriceSummary();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void updateDateViews() {
        binding.textCheckIn.setText(getString(R.string.check_in_value, DATE_FORMAT.format(checkInCalendar.getTime())));
        binding.textCheckOut.setText(getString(R.string.check_out_value, DATE_FORMAT.format(checkOutCalendar.getTime())));
    }

    private void updateSelectedRoom() {
        String selectedType = String.valueOf(binding.spinnerRoomType.getSelectedItem());
        if (selectedRoom == null || !selectedType.equalsIgnoreCase(selectedRoom.getType())) {
            selectedRoom = roomRepository.getFirstAvailableRoomByType(selectedType);
        }
        if (selectedRoom != null) {
            binding.textSelectedRoom.setText(getString(R.string.selected_room, selectedRoom.getName()));
        } else {
            binding.textSelectedRoom.setText(R.string.room_not_available);
        }
    }

    private void updatePriceSummary() {
        binding.textTotalPrice.setText(getString(R.string.price_summary, calculateTotalPrice()));
    }

    private double calculateTotalPrice() {
        if (selectedRoom == null) {
            return 0.0;
        }
        long nights = getNights();
        if (nights <= 0) {
            return 0.0;
        }
        double roomTotal = nights * selectedRoom.getPricePerNight();
        double breakfastTotal = binding.checkBreakfast.isChecked()
                ? nights * getGuestsCount() * BREAKFAST_PRICE_PER_GUEST
                : 0.0;
        return roomTotal + breakfastTotal;
    }

    private void saveBooking() {
        String guestName = binding.editGuestName.getText().toString().trim();
        if (guestName.isEmpty() || selectedRoom == null) {
            Toast.makeText(this, R.string.field_required, Toast.LENGTH_SHORT).show();
            return;
        }
        if (getNights() <= 0) {
            Toast.makeText(this, R.string.invalid_dates, Toast.LENGTH_SHORT).show();
            return;
        }

        Booking booking = new Booking();
        booking.setRoomId(selectedRoom.getId());
        booking.setGuestName(guestName);
        booking.setCheckInDate(DATE_FORMAT.format(checkInCalendar.getTime()));
        booking.setCheckOutDate(DATE_FORMAT.format(checkOutCalendar.getTime()));
        booking.setGuestsCount(getGuestsCount());
        booking.setBreakfastIncluded(binding.checkBreakfast.isChecked());
        booking.setTotalPrice(calculateTotalPrice());

        long bookingId = bookingRepository.addBooking(booking);
        if (bookingId > 0) {
            Toast.makeText(this, getString(R.string.booking_saved, guestName), Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra(RoomDetailActivity.EXTRA_BOOKING_GUEST, guestName);
            resultIntent.putExtra(RoomDetailActivity.EXTRA_BOOKING_TOTAL, booking.getTotalPrice());
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    private int getGuestsCount() {
        return binding.seekGuests.getProgress() + 1;
    }

    private long getNights() {
        long difference = checkOutCalendar.getTimeInMillis() - checkInCalendar.getTimeInMillis();
        return TimeUnit.MILLISECONDS.toDays(difference);
    }

    private void resetTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}
