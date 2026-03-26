package com.example.hotelmanagementapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelmanagementapp.databinding.ActivityRoomDetailBinding;
import com.example.hotelmanagementapp.model.Room;
import com.google.android.material.snackbar.Snackbar;

public class RoomDetailActivity extends AppCompatActivity {
    public static final String EXTRA_ROOM = "extra_room";
    public static final String EXTRA_BOOKING_GUEST = "extra_booking_guest";
    public static final String EXTRA_BOOKING_TOTAL = "extra_booking_total";

    private ActivityRoomDetailBinding binding;
    private Room room;
    private final ActivityResultLauncher<Intent> bookingLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String guestName = result.getData().getStringExtra(EXTRA_BOOKING_GUEST);
                    double totalPrice = result.getData().getDoubleExtra(EXTRA_BOOKING_TOTAL, 0.0);
                    Snackbar.make(
                            binding.getRoot(),
                            getString(R.string.booking_saved_snackbar, guestName, totalPrice),
                            Snackbar.LENGTH_LONG
                    ).show();
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRoomDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        room = getIntent().getParcelableExtra(EXTRA_ROOM);
        if (room == null) {
            finish();
            return;
        }

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.room_details_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        bindRoom();
        binding.buttonMap.setOnClickListener(v -> openMap());
        binding.buttonBookNow.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.button_scale);
            binding.buttonBookNow.startAnimation(animation);
            launchBooking();
        });
    }

    private void bindRoom() {
        binding.textRoomName.setText(room.getName());
        binding.textRoomType.setText(getString(R.string.room_type_label, room.getType()));
        binding.textRoomPrice.setText(getString(R.string.price_per_night, room.getPricePerNight()));
        binding.textRoomDescription.setText(room.getDescription());

        int imageResId = getResources().getIdentifier(room.getImageResName(), "drawable", getPackageName());
        if (imageResId != 0) {
            binding.imageRoom.setImageResource(imageResId);
        }
    }

    private void launchBooking() {
        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra(EXTRA_ROOM, room);
        bookingLauncher.launch(intent);
    }

    private void openMap() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.map_query)));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException exception) {
            Toast.makeText(this, R.string.map_unavailable, Toast.LENGTH_SHORT).show();
        }
    }
}
