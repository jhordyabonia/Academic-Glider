package util;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jhordyabonia.ag.R;

import java.util.ArrayList;

import controllers.Adapter;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {


    public static ArrayList<Integer> HISTORY= new ArrayList<>();
    private int display_now=-1;
    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;
    private View previousSelectedItem;

    private int mCurrentSelectedPosition = 0,previews;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        //setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.drawer_main, container, false);
      mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
              //  selectItem(position);
                selectItem(position,view,true);
            }
        });
       /* mDrawerListView.setAdapter(new ArrayAdapter<>(
                getActionBar().getThemedContext(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                new String[]{
                        getString(R.string.notifications),
                        getString(R.string.horarios),
                        getString(R.string.asignaturas),
                        getString(R.string.contacts),
                        getString(R.string.chats),
                        getString(R.string.groups),
                        getString(R.string.community),
                        getString(R.string.account),
                        getString(R.string.info_title),
                        getString(R.string.settings),
                        getString(R.string.exit),
                }));*/

        mDrawerListView.setAdapter(new AdapterMenu(getActionBar().getThemedContext(),
                new String[]{
                        getString(R.string.notifications),
                        getString(R.string.horarios),
                        getString(R.string.asignaturas),
                        getString(R.string.contacts),
                        getString(R.string.chats),
                        getString(R.string.groups),
                        getString(R.string.community),
                        getString(R.string.account),
                        getString(R.string.info_title),
                        getString(R.string.settings),
                        getString(R.string.exit),
                }));
        //mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        //HISTORY.add(mCurrentSelectedPosition);
        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    public void open()
    {
        if(isDrawerOpen())
             mDrawerLayout.closeDrawer(mFragmentContainerView);
        else mDrawerLayout.openDrawer(mFragmentContainerView);
    }
    public int current(){
        return mCurrentSelectedPosition;
    }
    public void previews(){
        selectItem(previews);
    }
    public void selectItem(int position) {
        selectItem(position,null,true);
    }
    private void selectItem(int position,View view,boolean add) {

        getActionBar().show();
        previews=mCurrentSelectedPosition;
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }

        if(add)
            if(position>=0)
                HISTORY.add(++display_now,position);

        if (previousSelectedItem!=null)
            previousSelectedItem.setBackgroundColor(Color.TRANSPARENT);

        if(view==null)
            if(mDrawerListView!=null)
                view=mDrawerListView.getSelectedView();
        previousSelectedItem=view;
        try {
            ///View pg=view.findViewById(R.id.item_menu);
            if (Style.STYLE != R.color.colorBlack)
                view.setBackgroundColor(getResources().getColor(Style.STYLE));
            else view.setBackgroundColor(getResources().getColor(R.color.colorMarine));
        }catch (Exception e){Log.e("ERROR",e.getMessage());}
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            //inflater.inflate(R.menu.main, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    public static class AdapterMenu extends ArrayAdapter {

        private Context context;
        private String[] locale;

        public AdapterMenu(Context c,String... l){
            super(c,R.layout.item,l);
            context=c;
            locale=l;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);

            View root = inflater.inflate(R.layout.base_menu,null);
            switch (position){
                case 0:
                    ((ImageView)root.findViewById(R.id.logo))
                            .setImageResource(R.drawable.twotone_home_white_48); break;
                case 1:
                ((ImageView)root.findViewById(R.id.logo))
                        .setImageResource(R.drawable.twotone_today_white_48); break;
                case 2:
                ((ImageView)root.findViewById(R.id.logo))
                        .setImageResource(R.drawable.twotone_collections_bookmark_white_48); break;
                case 3:
                    ((ImageView)root.findViewById(R.id.logo))
                            .setImageResource(R.drawable.twotone_supervisor_account_white_48); break;
                case 4:
                    ((ImageView)root.findViewById(R.id.logo))
                            .setImageResource(R.drawable.twotone_speaker_notes_white_48); break;
                case 5:
                    ((ImageView)root.findViewById(R.id.logo))
                            .setImageResource(R.drawable.twotone_question_answer_white_48); break;
                case 6:
                    ((ImageView)root.findViewById(R.id.logo))
                            .setImageResource(R.drawable.twotone_group_work_white_48); break;
                case 7:
                    ((ImageView)root.findViewById(R.id.logo))
                            .setImageResource(R.drawable.user); break;
                case 8:
                    ((ImageView)root.findViewById(R.id.logo))
                            .setImageResource(R.drawable.twotone_info_white_48); break;
                case 9:
                ((ImageView)root.findViewById(R.id.logo))
                        .setImageResource(R.drawable.twotone_settings_white_48); break;
                default:
                ((ImageView)root.findViewById(R.id.logo))
                        .setImageResource(android.R.drawable.ic_menu_close_clear_cancel); break;

            }
            ((TextView)root.findViewById(R.id.title))
                    .setText(locale[position]);

            return root;
        }
    }
}
