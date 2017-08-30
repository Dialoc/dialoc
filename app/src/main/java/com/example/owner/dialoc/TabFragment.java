package com.example.owner.dialoc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by Rahul on 11/26/2016.
 */

public class TabFragment extends Fragment {
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 3;

//    private int[] tabIcons = {
//            R.drawable.home_icon_unselected,
//            R.drawable.schedule_unselected,
//            R.drawable.locker_unselected,
//            R.drawable.notification_unselected
//    };

//    private int[] tabIconsSelected = {
//            R.drawable.home_icon_selected,
//            R.drawable.schedule_selected,
//            R.drawable.locker_selected,
//            R.drawable.notification_selected
//    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate tab_layout and setup Views

        View x = inflater.inflate(R.layout.tab_layout, null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        // Set an Adapter for the View Pager
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                System.out.println("Position: " + position);
                switch(position) {
                    case 0:
                        System.out.println("Case 0");
                        getActivity().setTitle("Home Clinic");
                        break;
                    case 1:
                        System.out.println("Case 1");
                        getActivity().setTitle("Backup Clinic");
                        break;
                    case 2:
                        System.out.println("Case 2");
                        getActivity().setTitle("Nearby Clinics");
                        break;
                    }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        // Apparentely a work around, setupWithViewPager doesn't work without the runnable

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
                setupTabIcons();
                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
//                        tab.setIcon(tabIconsSelected[tab.getPosition()]);
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
//                        tab.setIcon(tabIcons[tab.getPosition()]);
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

            }
        });

        return x;
    }

    public void setupTabIcons() {
//        tabLayout.getTabAt(0).setIcon(tabIconsSelected[0]);
        tabLayout.getTabAt(0).setText("Home Clinic");
//        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(1).setText("Backup Clinic");
//        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(2).setText("Nearby Clinics");
    }

    class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        // Return fragment with respect to Position

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return new HomeClinicFragment();
                case 1:
                    return new BackupClinicFragment();
                case 2:
                    return new NearbyClinicsFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return int_items;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0:
                    return "Home Clinic";
                case 1:
                    return "Backup Clinic";
                case 2:
                    return "Nearby Clinics";
            }
            return null;

//            Drawable image = ContextCompat.getDrawable(getContext(), tabIcons[position]);
//            image.setBounds(0, 0, 100, 100);
//            SpannableString sb = new SpannableString("Hello");
//            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
//            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            return sb;
        }
    }
}
