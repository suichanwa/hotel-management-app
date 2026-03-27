package com.example.hotelmanagementapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagementapp.R;
import com.example.hotelmanagementapp.databinding.ItemBookingBinding;
import com.example.hotelmanagementapp.model.Booking;

import java.util.ArrayList;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    public interface OnBookingClickListener {
        void onBookingClick(Booking booking);
    }

    public interface OnBookingLongClickListener {
        void onBookingLongClick(Booking booking, View anchorView);
    }

    private final List<Booking> bookings = new ArrayList<>();
    private final Activity hostActivity;
    private final boolean enableContextMenu;
    private final OnBookingClickListener clickListener;
    private final OnBookingLongClickListener longClickListener;

    public BookingAdapter(Activity hostActivity, boolean enableContextMenu,
                          OnBookingClickListener clickListener,
                          OnBookingLongClickListener longClickListener) {
        this.hostActivity = hostActivity;
        this.enableContextMenu = enableContextMenu;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    public void submitList(List<Booking> bookingList) {
        bookings.clear();
        bookings.addAll(bookingList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemBookingBinding binding = ItemBookingBinding.inflate(inflater, parent, false);
        BookingViewHolder holder = new BookingViewHolder(binding);
        if (enableContextMenu) {
            hostActivity.registerForContextMenu(holder.itemView);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        Context context = holder.itemView.getContext();
        holder.binding.textGuestName.setText(booking.getGuestName());
        holder.binding.textRoomName.setText(context.getString(
                R.string.room_card_description,
                booking.getRoomName(),
                booking.getRoomType()
        ));
        holder.binding.textBookingDates.setText(context.getString(
                R.string.booking_dates,
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        ));
        holder.binding.textBookingGuests.setText(context.getString(
                R.string.booking_guests,
                booking.getGuestsCount()
        ));
        holder.binding.textBreakfast.setText(booking.isBreakfastIncluded()
                ? R.string.booking_breakfast
                : R.string.booking_no_breakfast);
        holder.binding.textTotalPrice.setText(context.getString(
                R.string.booking_total,
                booking.getTotalPrice()
        ));
        holder.itemView.setOnClickListener(clickListener == null
                ? null
                : v -> clickListener.onBookingClick(booking));

        if (enableContextMenu) {
            holder.itemView.setOnLongClickListener(v -> {
                longClickListener.onBookingLongClick(booking, v);
                v.showContextMenu();
                return true;
            });
        } else {
            holder.itemView.setOnLongClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private final ItemBookingBinding binding;

        BookingViewHolder(ItemBookingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, R.id.context_action_view, 0, R.string.context_view);
            menu.add(0, R.id.context_action_edit, 1, R.string.context_edit);
            menu.add(0, R.id.context_action_delete, 2, R.string.context_delete);
        }
    }
}
