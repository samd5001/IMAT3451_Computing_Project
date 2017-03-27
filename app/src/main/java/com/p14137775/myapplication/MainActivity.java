package com.p14137775.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import classes.User;
import wrappers.SQLExercisesWrapper;

public class MainActivity extends AppCompatActivity implements ExerciseAreasFragment.OnAreaSelected, ExerciseSearchFragment.OnExerciseSelected, ExerciseDetailsFragment.OnBegin {

    ExerciseFragment exerciseFragment;
    ExerciseFragment workoutFragment;
    ExerciseFragment historyFragment;
    User user;
    SQLExercisesWrapper db;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = getSharedPreferences("preferences", MODE_PRIVATE);

        if (prefs.getBoolean("firstRun", true)) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            prefs.edit().putBoolean("firstRun", false).apply();
            prefs.edit().putBoolean("loggedIn", false).apply();
        }
        exerciseFragment = new ExerciseFragment();
        workoutFragment = new ExerciseFragment();
        historyFragment = new ExerciseFragment();
        db = new SQLExercisesWrapper(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_dumbbell);
        TabLayout tabs = (TabLayout) findViewById(R.id.tabLayout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

        setSupportActionBar(toolbar);
        setupViewPager(viewPager);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        int position = viewPager.getCurrentItem();
        int count = adapter.getItem(position).getChildFragmentManager().getBackStackEntryCount();

        if (count <= 1) {
            super.onBackPressed();
        } else {
            adapter.getItem(position).getChildFragmentManager().popBackStack();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.add("Track", exerciseFragment);
        adapter.add("Plans", workoutFragment);
        adapter.add("History", historyFragment);
        viewPager.setAdapter(adapter);
    }

    public SQLExercisesWrapper getDb() {
        return db;
    }

    @Override
    public void onAreaSelected(String area) {
        ArrayList<String> exercises = db.getExerciseList(area);
        if (!exercises.isEmpty()) {
            ExerciseSearchFragment searchFragment = new ExerciseSearchFragment();
            Bundle args = new Bundle();
            args.putStringArrayList("exercises", exercises);
            searchFragment.setArguments(args);
            exerciseFragment.getChildFragmentManager().beginTransaction()
                    .replace(R.id.placeholder, searchFragment, "exerciseSearch").addToBackStack(null).commit();
        } else Toast.makeText(getApplicationContext(),
                "Loading database. Please wait and try again", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onExerciseSelected(String name) {
        ExerciseDetailsFragment detailsFragment = new ExerciseDetailsFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        detailsFragment.setArguments(args);
        exerciseFragment.getChildFragmentManager().beginTransaction()
                .replace(R.id.placeholder, detailsFragment, "exerciseDetails").addToBackStack(null).commit();
    }

    @Override
    public void onBegin() {

    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList = new ArrayList<>();
        private List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void add(String title, Fragment fragment) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
