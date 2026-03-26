package com.example.hotelmanagementapp.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.hotelmanagementapp.R;
import com.example.hotelmanagementapp.databinding.DialogEditBookingBinding;
import com.example.hotelmanagementapp.model.Booking;
import com.example.hotelmanagementapp.model.Room;
import com.example.hotelmanagementapp.repository.RoomRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class EditBookingDialogFragment extends DialogFragment {
    public interface OnBookingUpdatedListener {
        void onBookingUpdated(Booking updatedBooking);
    }

    private static final String ARG_BOOKING_ID = "booking_id";
    private static final String ARG_ROOM_ID = "room_id";
    private static final String ARG_ROOM_TYPE = "room_type";
    private static final String ARG_GUEST_NAME = "guest_name";
    private static final String ARG_CHECK_IN = "check_in";
    private static final String ARG_CHECK_OUT = "check_out";
    private static final String ARG_GUESTS = "guests";
    private static final String ARG_BREAKFAST = "breakfast";
    private static final double BREAKFAST_PRICE_PER_GUEST = 15.0;
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private DialogEditBookingBinding binding;
    private OnBookingUpdatedListener listener;
    private RoomRepository roomRepository;
    private Room selectedRoom;
    private Calendar checkInCalendar;
    private Calendar checkOutCalendar;

    public static EditBookingDialogFragment newInstance(Booking booking) {
        EditBookingDialogFragment fragment = new EditBookingDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_BOOKING_ID, booking.getId());
        args.putLong(ARG_ROOM_ID, booking.getRoomId());
        args.putString(ARG_ROOM_TYPE, booking.getRoomType());
        args.putString(ARG_GUEST_NAME, booking.getGuestName());
        args.putString(ARG_CHECK_IN, booking.getCheckInDate());
        args.putString(ARG_CHECK_OUT, booking.getCheckOutDate());
        args.putInt(ARG_GUESTS, booking.getGuestsCount());
        args.putBoolean(ARG_BREAKFAST, booking.isBreakfastIncluded());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnBookingUpdatedListener) {
            listener = (OnBookingUpdatedListener) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogEditBookingBinding.inflate(requireActivity().getLayoutInflater());
        roomRepository = new RoomRepository(requireContext());

        setupForm();

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.edit_booking_title)
                .setView(binding.getRoot())
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_save, null)
                .create();

        dialog.setOnShowListener(dialogInterface ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> saveBooking()));
        return dialog;
    }

    private void setupForm() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.room_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerRoomType.setAdapter(adapter);

        checkInCalendar = Calendar.getInstance();
        checkOutCalendar = Calendar.getInstance();
        resetTime(checkInCalendar);
        resetTime(checkOutCalendar);

        Bundle args = requireArguments();
        binding.editGuestName.setText(args.getString(ARG_GUEST_NAME, ""));
        binding.seekGuests.setProgress(Math.max(0, args.getInt(ARG_GUESTS, 1) - 1));
        binding.checkBreakfast.setChecked(args.getBoolean(ARG_BREAKFAST));
        binding.textGuests.setText(getString(R.string.guests_count, getGuestsCount()));
        selectedRoom = roomRepository.getRoomById(args.getLong(ARG_ROOM_ID));

        String originalType = args.getString(ARG_ROOM_TYPE, "");
        String[] types = getResources().getStringArray(R.array.room_types);
        for (int index = 0; index < types.length; index++) {
            if (types[index].equalsIgnoreCase(originalType)) {
                binding.spinnerRoomType.setSelection(index);
                break;
            }
        }

        try {
            checkInCalendar.setTime(DATE_FORMAT.parse(args.getString(ARG_CHECK_IN)));
            checkOutCalendar.setTime(DATE_FORMAT.parse(args.getString(ARG_CHECK_OUT)));
            resetTime(checkInCalendar);
            resetTime(checkOutCalendar);
        } catch (ParseException exception) {
            checkOutCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        binding.buttonCheckIn.setOnClickListener(v -> showDatePicker(true));
        binding.buttonCheckOut.setOnClickListener(v -> showDatePicker(false));
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

        updateDateViews();
        updateSelectedRoom();
        updatePriceSummary();
    }

    private void showDatePicker(boolean isCheckIn) {
        Calendar target = isCheckIn ? checkInCalendar : checkOutCalendar;
        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    target.set(year, month, dayOfMonth);
                    resetTime(target);
                    updateDateViews();
                    updatePriceSummary();
                },
                target.get(Calendar.YEAR),
                target.get(Calendar.MONTH),
                target.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void updateSelectedRoom() {
        String selectedType = String.valueOf(binding.spinnerRoomType.getSelectedItem());
        if (selectedRoom == null || !selectedType.equalsIgnoreCase(selectedRoom.getType())) {
            selectedRoom = roomRepository.getFirstAvailableRoomByType(selectedType);
        }
    }

    private void updateDateViews() {
        binding.textCheckIn.setText(getString(R.string.check_in_value, DATE_FORMAT.format(checkInCalendar.getTime())));
        binding.textCheckOut.setText(getString(R.string.check_out_value, DATE_FORMAT.format(checkOutCalendar.getTime())));
    }

    private void updatePriceSummary() {
        binding.textTotalPrice.setText(getString(R.string.price_summary, calculateTotalPrice()));
    }

    private void saveBooking() {
        String guestName = binding.editGuestName.getText().toString().trim();
        if (guestName.isEmpty() || selectedRoom == null) {
            Toast.makeText(requireContext(), R.string.field_required, Toast.LENGTH_SHORT).show();
            return;
        }
        if (getNights() <= 0) {
            Toast.makeText(requireContext(), R.string.invalid_dates, Toast.LENGTH_SHORT).show();
            return;
        }

        Booking updatedBooking = new Booking();
        updatedBooking.setId(requireArguments().getLong(ARG_BOOKING_ID));
        updatedBooking.setRoomId(selectedRoom.getId());
        updatedBooking.setGuestName(guestName);
        updatedBooking.setCheckInDate(DATE_FORMAT.format(checkInCalendar.getTime()));
        updatedBooking.setCheckOutDate(DATE_FORMAT.format(checkOutCalendar.getTime()));
        updatedBooking.setGuestsCount(getGuestsCount());
        updatedBooking.setBreakfastIncluded(binding.checkBreakfast.isChecked());
        updatedBooking.setTotalPrice(calculateTotalPrice());

        if (listener != null) {
            listener.onBookingUpdated(updatedBooking);
        }
        dismiss();
    }

    private double calculateTotalPrice() {
        if (selectedRoom == null) {
            return 0.0;
        }
        long nights = getNights();
        if (nights <= 0) {
            return 0.0;
        }
        double roomTotal = selectedRoom.getPricePerNight() * nights;
        double breakfastTotal = binding.checkBreakfast.isChecked()
                ? BREAKFAST_PRICE_PER_GUEST * getGuestsCount() * nights
                : 0.0;
        return roomTotal + breakfastTotal;
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
