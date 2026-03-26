package com.example.hotelmanagementapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagementapp.R;
import com.example.hotelmanagementapp.databinding.ItemRoomBinding;
import com.example.hotelmanagementapp.model.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    public interface OnRoomClickListener {
        void onRoomClick(Room room);
    }

    private final List<Room> rooms = new ArrayList<>();
    private final OnRoomClickListener onRoomClickListener;

    public RoomAdapter(OnRoomClickListener onRoomClickListener) {
        this.onRoomClickListener = onRoomClickListener;
    }

    public void submitList(List<Room> roomList) {
        rooms.clear();
        rooms.addAll(roomList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRoomBinding binding = ItemRoomBinding.inflate(inflater, parent, false);
        return new RoomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = rooms.get(position);
        Context context = holder.itemView.getContext();
        holder.binding.textRoomName.setText(room.getName());
        holder.binding.textRoomType.setText(context.getString(R.string.room_type_label, room.getType()));
        holder.binding.textRoomPrice.setText(context.getString(R.string.price_per_night, room.getPricePerNight()));
        holder.binding.textRoomAvailability.setText(room.isAvailable()
                ? R.string.room_availability_yes
                : R.string.room_availability_no);
        int imageResId = context.getResources().getIdentifier(
                room.getImageResName(),
                "drawable",
                context.getPackageName()
        );
        if (imageResId != 0) {
            holder.binding.imageRoom.setImageResource(imageResId);
        }
        holder.itemView.setOnClickListener(v -> onRoomClickListener.onRoomClick(room));

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in_item);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        private final ItemRoomBinding binding;

        RoomViewHolder(ItemRoomBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
