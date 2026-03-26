package com.example.hotelmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.hotelmanagementapp.databinding.ActivityMainBinding;
import com.example.hotelmanagementapp.ui.BookingsFragment;
import com.example.hotelmanagementapp.ui.RoomsFragment;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG_ROOMS = "rooms_fragment";
    private static final String TAG_BOOKINGS = "bookings_fragment";

    private ActivityMainBinding binding;
    private RoomsFragment roomsFragment;
    private BookingsFragment bookingsFragment;
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        roomsFragment = (RoomsFragment) getSupportFragmentManager().findFragmentByTag(TAG_ROOMS);
        bookingsFragment = (BookingsFragment) getSupportFragmentManager().findFragmentByTag(TAG_BOOKINGS);
        if (roomsFragment == null) {
            roomsFragment = new RoomsFragment();
        }
        if (bookingsFragment == null) {
            bookingsFragment = new BookingsFragment();
        }

        setupSearch();
        binding.bottomNavigation.setOnItemSelectedListener(onBottomNavigationSelected());

        if (savedInstanceState == null) {
            binding.bottomNavigation.setSelectedItemId(R.id.navigation_rooms);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (currentFragment instanceof RoomsFragment) {
            ((RoomsFragment) currentFragment).filterRooms(currentQuery);
        } else if (currentFragment instanceof BookingsFragment) {
            ((BookingsFragment) currentFragment).refreshBookings();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            binding.searchView.requestFocus();
            binding.searchView.setIconified(false);
            return true;
        }
        if (item.getItemId() == R.id.action_view_bookings) {
            startActivity(new Intent(this, BookingListActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.action_about) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.about_title)
                    .setMessage(R.string.about_message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSearch() {
        binding.searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query == null ? "" : query;
                applyQueryToRooms();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText == null ? "" : newText;
                applyQueryToRooms();
                return true;
            }
        });
    }

    private void applyQueryToRooms() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (currentFragment instanceof RoomsFragment) {
            ((RoomsFragment) currentFragment).filterRooms(currentQuery);
        }
    }

    private NavigationBarView.OnItemSelectedListener onBottomNavigationSelected() {
        return item -> {
            if (item.getItemId() == R.id.navigation_rooms) {
                binding.searchView.setVisibility(View.VISIBLE);
                showFragment(roomsFragment, TAG_ROOMS);
                roomsFragment.filterRooms(currentQuery);
                return true;
            }
            if (item.getItemId() == R.id.navigation_bookings) {
                binding.searchView.setVisibility(View.GONE);
                showFragment(bookingsFragment, TAG_BOOKINGS);
                bookingsFragment.refreshBookings();
                return true;
            }
            return false;
        };
    }

    private void showFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment, tag)
                .commit();
    }
}
