package com.app.thechatrooms.ui.myTrips;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.thechatrooms.R;
import com.app.thechatrooms.adapters.TabsAdapter;
import com.app.thechatrooms.models.User;
import com.app.thechatrooms.utilities.Parameters;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyTripsFragment extends Fragment {

    TabLayout tabLayout;
    TabsAdapter tabsAdapter;
    ViewPager viewPager;
    User user;
    public MyTripsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_trips, container, false);
        user = (User) getArguments().getSerializable(Parameters.USER_ID);
        tabLayout = view.findViewById(R.id.myTrips_tab_layout);
        viewPager = view.findViewById(R.id.myTrips_view_pager);
        tabsAdapter = new TabsAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Parameters.USER_ID, user);
        DriverTripsFragment driverTripsFragment = new DriverTripsFragment();
        RiderTripsFragment riderTripsFragment = new RiderTripsFragment();
        driverTripsFragment.setArguments(bundle);
        riderTripsFragment.setArguments(bundle);
        tabsAdapter.addFragment(driverTripsFragment,"Driver");
        tabsAdapter.addFragment(riderTripsFragment,"Rider");
        viewPager.setAdapter(tabsAdapter);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

}

