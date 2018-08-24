package ml.medyas.popularmoviesapp;


import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MoviesFragment.OnFragmentInteractionListener{
    @BindView(R.id.toolbar)
    Toolbar tb;
    @BindView(R.id.category_tabs)
    TabLayout tabs;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.menu_main_search)
    SearchView mSearch;
    @BindView(R.id.menu_main_fav)
    ImageView mFav;
    @BindView(R.id.menu_main_settings) ImageView mSettings;

    private PagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(tb);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();

        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false);

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(mPagerAdapter);
        viewpager.setCurrentItem(0);
        tabs.setupWithViewPager(viewpager);

        if(savedInstanceState != null) {
            viewpager.setCurrentItem(savedInstanceState.getInt("selected", 0));
            tabs.getTabAt(savedInstanceState.getInt("selected", 0)).select();
        }

        mFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FavouriteActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent,
                            ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                } else {
                    startActivity(intent);
                }
            }
        });

        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Clicked on Settings menu option!", Toast.LENGTH_LONG).show();
            }
        });

        mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("selected", viewpager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            String sorted = "";
            switch (position) {
                case 0:
                    sorted = getString(R.string.playing);
                    break;
                case 1:
                    sorted = getString(R.string.popular);
                    break;
                case 2:
                    sorted = getString(R.string.top);
                    break;
                case 3:
                    sorted = getString(R.string.upcoming);
                    break;
            }
            return new MoviesFragment().newInstance(sorted);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            String[] r = {"Now Playing", "Popular", "Top Rated", "Upcoming"};

            return r[position];
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

}
