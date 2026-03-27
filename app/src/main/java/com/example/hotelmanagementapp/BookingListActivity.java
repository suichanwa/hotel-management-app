package com.example.hotelmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hotelmanagementapp.adapter.BookingAdapter;
import com.example.hotelmanagementapp.databinding.ActivityBookingListBinding;
import com.example.hotelmanagementapp.model.Booking;
import com.example.hotelmanagementapp.repository.BookingRepository;
import com.example.hotelmanagementapp.ui.EditBookingDialogFragment;

import java.util.List;

public class BookingListActivity extends AppCompatActivity
        implements EditBookingDialogFragment.OnBookingUpdatedListener {

    private ActivityBookingListBinding binding;
    private BookingRepository bookingRepository;
    private BookingAdapter bookingAdapter;
    private Booking selectedBooking;
    private final ActivityResultLauncher<Intent> createBookingLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() != RESULT_OK) {
                            return;
                        }
                        Intent data = result.getData();
                        String guestName = data == null
                                ? null
                                : data.getStringExtra(RoomDetailActivity.EXTRA_BOOKING_GUEST);
                        loadBookings();
                        if (guestName != null && !guestName.isEmpty()) {
                            Toast.makeText(
                                    this,
                                    getString(R.string.booking_saved, guestName),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bookingRepository = new BookingRepository(this);
        bookingAdapter = new BookingAdapter(
                this,
                true,
                this::showBookingDetails,
                (booking, anchorView) -> selectedBooking = booking
        );

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.recyclerBookings.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerBookings.setAdapter(bookingAdapter);
        binding.fabCreateBooking.setOnClickListener(v -> launchCreateBooking());
        loadBookings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookings();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (selectedBooking == null) {
            return super.onContextItemSelected(item);
        }
        if (item.getItemId() == R.id.context_action_view) {
            showBookingDetails(selectedBooking);
            return true;
        }
        if (item.getItemId() == R.id.context_action_edit) {
            EditBookingDialogFragment.newInstance(selectedBooking)
                    .show(getSupportFragmentManager(), "edit_booking_dialog");
            return true;
        }
        if (item.getItemId() == R.id.context_action_delete) {
            showDeleteDialog(selectedBooking);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onBookingUpdated(Booking updatedBooking) {
        bookingRepository.updateBooking(updatedBooking);
        Toast.makeText(this, R.string.booking_updated, Toast.LENGTH_SHORT).show();
        loadBookings();
    }

    private void launchCreateBooking() {
        createBookingLauncher.launch(new Intent(this, BookingActivity.class));
    }

    private void loadBookings() {
        List<Booking> bookings = bookingRepository.getAllBookings();
        bookingAdapter.submitList(bookings);
        binding.textEmptyBookings.setVisibility(bookings.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showBookingDetails(Booking booking) {
        selectedBooking = booking;
        String details = getString(R.string.booking_guest_label, booking.getGuestName())
                + System.lineSeparator()
                + getString(
                R.string.booking_room_label,
                getString(R.string.room_card_description, booking.getRoomName(), booking.getRoomType())
        )
                + System.lineSeparator()
                + getString(R.string.booking_dates_label, booking.getCheckInDate(), booking.getCheckOutDate())
                + System.lineSeparator()
                + getString(R.string.booking_guests_label, booking.getGuestsCount())
                + System.lineSeparator()
                + getString(
                R.string.booking_breakfast_label,
                getString(booking.isBreakfastIncluded()
                        ? R.string.booking_breakfast
                        : R.string.booking_no_breakfast)
        )
                + System.lineSeparator()
                + getString(R.string.booking_total_label, booking.getTotalPrice());

        new AlertDialog.Builder(this)
                .setTitle(R.string.booking_details_title)
                .setMessage(details)
                .setNegativeButton(R.string.dialog_close, null)
                .setNeutralButton(R.string.dialog_delete, (dialog, which) -> showDeleteDialog(booking))
                .setPositiveButton(R.string.context_edit, (dialog, which) ->
                        EditBookingDialogFragment.newInstance(booking)
                                .show(getSupportFragmentManager(), "edit_booking_dialog"))
                .show();
    }

    private void showDeleteDialog(Booking booking) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_booking_title)
                .setMessage(getString(R.string.delete_booking_message, booking.getGuestName()))
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_delete, (dialog, which) -> {
                    bookingRepository.deleteBooking(booking.getId());
                    Toast.makeText(this, R.string.booking_deleted, Toast.LENGTH_SHORT).show();
                    loadBookings();
                })
                .show();
    }
}
