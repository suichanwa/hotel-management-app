package com.example.hotelmanagementapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hotelmanagementapp.BookingListActivity;
import com.example.hotelmanagementapp.R;
import com.example.hotelmanagementapp.adapter.BookingAdapter;
import com.example.hotelmanagementapp.databinding.FragmentBookingsBinding;
import com.example.hotelmanagementapp.model.Booking;
import com.example.hotelmanagementapp.repository.BookingRepository;

import java.util.List;

public class BookingsFragment extends Fragment {
    private FragmentBookingsBinding binding;
    private BookingRepository bookingRepository;
    private BookingAdapter bookingAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBookingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookingRepository = new BookingRepository(requireContext());
        bookingAdapter = new BookingAdapter(requireActivity(), false, null, null);
        binding.recyclerBookingsPreview.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerBookingsPreview.setAdapter(bookingAdapter);
        binding.buttonManageBookings.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), BookingListActivity.class)));
        refreshBookings();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshBookings();
    }

    public void refreshBookings() {
        if (binding == null || bookingRepository == null) {
            return;
        }
        List<Booking> bookings = bookingRepository.getAllBookings();
        bookingAdapter.submitList(bookings);
        binding.textBookingCount.setText(getString(R.string.booking_count, bookings.size()));
        binding.textEmptyBookings.setVisibility(bookings.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
