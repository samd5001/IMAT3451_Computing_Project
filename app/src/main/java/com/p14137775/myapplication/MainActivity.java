package com.p14137775.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import classes.Day;
import classes.Exercise;
import classes.Plan;
import classes.User;
import wrappers.SQLWrapper;

public class MainActivity extends AppCompatActivity implements ExerciseAreasFragment.OnAreaSelected,
        ExerciseSearchFragment.OnExerciseSelected, ExerciseDetailsFragment.OnDetail,
        ExerciseTrackFragment.OnComplete, PlanSearchFragment.OnPlanSelected, WelcomeFragment.OnWelcomeComplete,
        ExerciseCreateFragment.OnCreateExercise, PlanDetailsFragment.OnPlanBegin, PlanCurrentFragment.OnCurrentSelect,
        HistorySearchFragment.OnRecordsSelected, HistoryCategoryFragment.OnCategorySelected {

    private ExerciseFragment exerciseFragment;
    private PlanFragment planFragment;
    private HistoryFragment historyFragment;
    private User user;
    private Exercise exercise;
    private Plan plan;
    private Day day;
    private SQLWrapper db;
    private TabLayout tabs;
    private ViewPagerAdapter adapter;
    private SharedPreferences prefs;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("preferences", MODE_PRIVATE);
        db = new SQLWrapper(getApplicationContext(), prefs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_dumbbell);
        tabs = (TabLayout) findViewById(R.id.tabLayout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);
        exerciseFragment = new ExerciseFragment();
        planFragment = new PlanFragment();
        historyFragment = new HistoryFragment();
        setSupportActionBar(toolbar);
        setupViewPager(viewPager);
        tabs.setupWithViewPager(viewPager);
        if (prefs.getBoolean("firstRun", true)) {
            prefs.edit().putBoolean("loggedIn", false).apply();
            prefs.edit().putBoolean("syncing", false).apply();
        }

        final Handler handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (!prefs.getBoolean("syncing", false)) {
                    db.syncData();
                }
                Log.d("Handlers", "Called on main thread");
                handler.postDelayed(runnable, 300000);
            }

        };
        handler.post(runnable);
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
            case R.id.sync:
                if (!prefs.getBoolean("syncing", false)) {
                    db.syncData();
                }
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
        adapter.add("Plans", planFragment);
        adapter.add("History", historyFragment);
        viewPager.setAdapter(adapter);
    }

    public void incrementTab() {
        if (tabs.getSelectedTabPosition() < tabs.getTabCount() - 1) {
            tabs.getTabAt(tabs.getSelectedTabPosition() + 1).select();
        }
    }

    public SQLWrapper getDb() {
        return db;
    }

    public User getUser() {
        return user;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public Plan getPlan() {
        return plan;
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }

    @Override
    public void onAreaSelected(String area) {
        user = new SQLWrapper(getApplicationContext(), prefs).getUser();
        ArrayList<String> exercises = db.getExerciseList(area);
        if (!exercises.isEmpty()) {
            ExerciseSearchFragment searchFragment = new ExerciseSearchFragment();
            Bundle args = new Bundle();
            args.putStringArrayList("exercises", exercises);
            searchFragment.setArguments(args);
            exerciseFragment.getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_top, R.anim.slide_out_bottom)
                    .replace(R.id.placeholder, searchFragment, "exerciseSearch").addToBackStack(null).commit();
        } else Toast.makeText(getApplicationContext(),
                "Loading database. Please wait and try again (An internet connection is required for initial setup", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onExerciseSelected(String name) {
        exercise = db.getExercise(name);
        ExerciseDetailsFragment detailsFragment = new ExerciseDetailsFragment();
        exerciseFragment.getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_top, R.anim.slide_out_bottom)
                .replace(R.id.placeholder, detailsFragment, "exerciseDetails").addToBackStack(null).commit();
    }

    @Override
    public void onExerciseAdd() {
        exerciseFragment.getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_top, R.anim.slide_out_bottom)
                .replace(R.id.placeholder, new ExerciseCreateFragment(), "exerciseCreate").addToBackStack(null).commit();
    }

    @Override
    public void onBegin() {
        exerciseFragment.getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_top, R.anim.slide_out_bottom)
                .replace((R.id.placeholder), new ExerciseTrackFragment(), "exerciseTracking").addToBackStack(null).commit();
    }

    @Override
    public void onDelete(Exercise exercise) {
        exerciseFragment.getChildFragmentManager().popBackStack();
        exerciseFragment.getChildFragmentManager().popBackStack();
        Toast.makeText(getApplicationContext(),
                "Exercise deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onComplete() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        int position = viewPager.getCurrentItem();
        int count = adapter.getItem(position).getChildFragmentManager().getBackStackEntryCount() - 1;
        for (int i = 0; i < count; i++) {
            adapter.getItem(position).getChildFragmentManager().popBackStack();
        }
    }

    @Override
    public void onWelcomeComplete() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        prefs.edit().putBoolean("firstRun", false).apply();
        exerciseFragment.getChildFragmentManager().beginTransaction()
                .replace(R.id.placeholder, new ExerciseAreasFragment(), "exerciseAreas").addToBackStack(null).commit();
        planFragment.getChildFragmentManager().beginTransaction()
                .replace(R.id.placeholder, new PlanCurrentFragment(), "planCurrent").addToBackStack(null).commit();
        historyFragment.getChildFragmentManager().beginTransaction()
                .replace(R.id.placeholder, new HistoryCategoryFragment(), "planCurrent").addToBackStack(null).commit();
    }

    @Override
    public void onCreateExercise() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        int position = viewPager.getCurrentItem();
        FragmentManager fm = adapter.getItem(position).getChildFragmentManager();
        fm.popBackStack();
        if (fm.getBackStackEntryCount() > 2) {
            fm.popBackStack();
        }
    }

    @Override
    public void onPlanSelected(Plan plan) {
        this.plan = plan;
        planFragment.getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_top, R.anim.slide_out_bottom)
                .replace(R.id.placeholder, new PlanDetailsFragment(), "planDetails").addToBackStack(null).commit();
    }

    @Override
    public void onPlanBegin(Day day) {
        this.day = day;
        planFragment.getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_top, R.anim.slide_out_bottom)
                .replace(R.id.placeholder, new PlanTrackMainFragment(), "planDetails").addToBackStack(null).commit();
    }

    @Override
    public void onPlanContinue(Plan plan) {
        onPlanSelected(plan);
    }

    @Override
    public void onBrowse() {
        planFragment.getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_top, R.anim.slide_out_bottom)
                .replace(R.id.placeholder, new PlanSearchFragment(), "planSearch").addToBackStack(null).commit();
    }

    public Day getDay() {
        return day;
    }

    @Override
    public void onRecordsSelected(String name) {
        Bundle args = new Bundle();
        args.putString("exerciseName", name);
        HistoryDisplayFragment showFragment = new HistoryDisplayFragment();
        showFragment.setArguments(args);
        historyFragment.getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_top, R.anim.slide_out_bottom)
                .replace(R.id.placeholder, showFragment, "planSearch").addToBackStack(null).commit();
    }

    @Override
    public void onCategorySelected(String name) {
        if (name.equals("Exercises")) {
            historyFragment.getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_top, R.anim.slide_out_bottom)
                    .replace(R.id.placeholder, new HistorySearchFragment(), "planSearch").addToBackStack(null).commit();
        }
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
