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

import com.example.hotelmanagementapp.R;
import com.example.hotelmanagementapp.RoomDetailActivity;
import com.example.hotelmanagementapp.adapter.RoomAdapter;
import com.example.hotelmanagementapp.databinding.FragmentRoomsBinding;
import com.example.hotelmanagementapp.model.Room;
import com.example.hotelmanagementapp.repository.RoomRepository;

import java.util.List;

public class RoomsFragment extends Fragment {
    private FragmentRoomsBinding binding;
    private RoomRepository roomRepository;
    private RoomAdapter roomAdapter;
    private String currentQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRoomsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        roomRepository = new RoomRepository(requireContext());
        roomAdapter = new RoomAdapter(this::openRoomDetails);
        binding.recyclerRooms.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerRooms.setAdapter(roomAdapter);
        loadRooms();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRooms();
    }

    public void filterRooms(String query) {
        currentQuery = query == null ? "" : query;
        loadRooms();
    }

    private void loadRooms() {
        if (binding == null || roomRepository == null) {
            return;
        }
        List<Room> rooms = roomRepository.searchRooms(currentQuery);
        roomAdapter.submitList(rooms);
        binding.textEmptyRooms.setVisibility(rooms.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void openRoomDetails(Room room) {
        Intent intent = new Intent(requireContext(), RoomDetailActivity.class);
        intent.putExtra(RoomDetailActivity.EXTRA_ROOM, room);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
