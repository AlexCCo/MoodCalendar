package es.ucm.fdi.moodcalendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.List;
import java.util.Observable;

import es.ucm.fdi.moodcalendar.customView.CalendarAdapter;
import es.ucm.fdi.moodcalendar.customView.CalendarView;
import es.ucm.fdi.moodcalendar.customView.DateParcelable;
import es.ucm.fdi.moodcalendar.dataModel.MoodSelection;
import es.ucm.fdi.moodcalendar.dataModel.entities.DateWithBackground;
import es.ucm.fdi.moodcalendar.viewModel.MoodCalendarViewModel;

/**
 * Main activity where the user will see a calendar with all the dates it has marked,
 * the current month and the current year. Users can also change months, mark
 * days and see what information has been entered in an already marked day.<br>
 *
 * Marked days will change the background color
 *
 * */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String CURRENT_DATE_BUNDLE = "currentDatesList";
    private static final String INTENT_EXTRA = "calItem";
    private static final String INTENT_EXTRA_VIEW = "calItemView";
    private static final String INTENT_REPLY = "reply";
    private static final int MARKED_DAY_REQUEST = 1;
    /**
     * Top bar of the main activity where you can access settings options and see
     * a open a left nav bar with the three horizontal lines icon.<br><br>
     * Currently, the settings option does not work.
     *
     * */
    private Toolbar topBar;
    /**
     * Left navigation bar containing a bunch of options.<br><br>
     * Options not available right now.
     *
     * */
    private NavigationView leftNav;
    /**
     * The DrawerLayout is a View which contains all the
     * views a user will see when the navigation view is not displayed
     * */
    private DrawerLayout dlLayout;
    /**
     * Custom view for displaying a calendar to the user
     * */
    private CalendarView calendar;
    /**
     * Adapter needed to display the dates in the CalendarView
     * */
    private CalendarAdapter adapter;
    /**
     * ViewModel from which obtain the data that does not belongs to
     * any UI and should be persisted
     * */
    private MoodCalendarViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create view model an associate it with this activity
        viewModel = new ViewModelProvider(this).get(MoodCalendarViewModel.class);
        //Obtain data from google's drive
        userLoggedWithGoogle();

        leftNav = findViewById(R.id.dl_navigation_view);
        dlLayout = findViewById(R.id.drawer_layout);

        //Create handlers for the leftNav
        setupDrawerContent(leftNav);
        //Create and associate the Top toolbar
        setUpToolbar();

        //we start configuring our calendar view
        calendar = new CalendarView(getApplicationContext(), null);
        adapter = new CalendarAdapter(getApplicationContext(), 0);

        calendar.attachAdapter(adapter);

        //we set what to do once a user has clicked a date
        calendar.getCalendarBody().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView text = view.findViewById(R.id.dateNumber);

                if(text.getText().toString().isEmpty()){
                    //end execution
                    return;
                }
                //trigger an activity to let the user insert how i felt that day and some thoughts
                if(!adapter.isItemInViewMark(position)) {
                    Intent intent = new Intent(MainActivity.this, InsertMoodActivity.class);
                    Log.d(TAG, "calendarItems clicked stringify: " + adapter.getStringifiedIn(position));

                    intent.putExtra(INTENT_EXTRA, adapter.getStringifiedIn(position));

                    startActivityForResult(intent, MARKED_DAY_REQUEST);
                }else {
                    //it will trigger an activity to see what a user has given as input for the date
                    Intent intent = new Intent(MainActivity.this, ViewMoodActivity.class);

                    CalendarAdapter.CalendarAdapterItemTransfer toQuery = adapter.getDateOf(position);

                    Log.d(TAG, "onItemClick: ====>"+ toQuery.getYear()+", " +toQuery.getMonth() +", "+ toQuery.getDay());
                    //obtain the corresponding data stored for the clicked day
                    DateWithBackground result = viewModel.getDateByYearMonthDay(toQuery.getYear(), toQuery.getMonth(), toQuery.getDay());

                    if(result == null){
                        //error end execution
                        return;
                    }

                    Log.d(TAG, "onItemClick: =>" + result.getMood()+ "&" + result.getLog());

                    intent.putExtra(INTENT_EXTRA_VIEW, MoodSelection.nameOf(result.getMood())+ "&" + result.getLog());

                    startActivity(intent);
                }

            }
        });
        
        Calendar cal = calendar.getCurrentCalendar();

        final LiveData<List<DateWithBackground>> previousLive = viewModel.getDatesByYearAndMonth(cal.get(Calendar.YEAR),
                                  cal.get(Calendar.MONTH) +1, this);

        //when data has changed, update calendar view
        previousLive.observe(this, new Observer<List<DateWithBackground>>() {
            @Override
            public void onChanged(List<DateWithBackground> dateWithBackgrounds) {
                Log.d(TAG, "onChanged: MAIN observer");
                adapter.updateBackground(dateWithBackgrounds);
            }
        });

        //It will create button listeners for the right arrow of CalendarView
        //The right arrow corresponds with "change to next month"
        calendar.createBtnListeners(true, new java.util.Observer() {
            @Override
            public void update(Observable o, Object arg) {
                /*
                * The purpose of this observer is to get notified when a user clicked the right arrow
                * for us to delete the first LiveData observer we request and request the viewModel
                * for the next data
                * */
                CalendarView.UpdateDateTransfer result = (CalendarView.UpdateDateTransfer) arg;
                Log.d(TAG, "BTN NEXT: (arg.year, arg.month) = ("+result.getYear() + ", " +result.getMonth() + ")" );

                LiveData<List<DateWithBackground>> data =viewModel.getDatesByYearAndMonth(result.getYear(), result.getMonth(), MainActivity.this);

                if(previousLive.hasObservers()) {
                    previousLive.removeObservers(MainActivity.this);
                }

                data.observe(MainActivity.this, new Observer<List<DateWithBackground>>() {
                    @Override
                    public void onChanged(List<DateWithBackground> dateWithBackgrounds) {
                        Log.d(TAG, "onChanged: button next observer!");
                        adapter.updateBackground(dateWithBackgrounds);
                    }
                });
            }
        });

        //It will create button listeners for the left arrow of CalendarView
        //The right arrow corresponds with "change to next month"
        calendar.createBtnListeners(false, new java.util.Observer() {
            @Override
            public void update(Observable o, Object arg) {
                /*
                 * The purpose of this observer is to get notified when a user clicked the left arrow
                 * for us to delete the first LiveData observer we request and request the viewModel
                 * for the previous data
                 * */
                Log.d(TAG, "update: previous Button CLicked!");
                CalendarView.UpdateDateTransfer result = (CalendarView.UpdateDateTransfer) arg;
                Log.d(TAG, "update: (arg.year, arg.month) = ("+result.getYear() + ", " +result.getMonth() + ")" );

                LiveData<List<DateWithBackground>> data =viewModel.getDatesByYearAndMonth(result.getYear(), result.getMonth(), MainActivity.this);

                if(previousLive.hasObservers()) {
                    previousLive.removeObservers(MainActivity.this);
                }

                data.observe(MainActivity.this, new Observer<List<DateWithBackground>>() {
                    @Override
                    public void onChanged(List<DateWithBackground> dateWithBackgrounds) {
                        Log.d(TAG, "onChanged: button prev observer!");
                        adapter.updateBackground(dateWithBackgrounds);
                    }
                });;
            }
        });

        drawCalendarView();
    }

    /**
     * Will obtain the account logged by the user and request the ViewModel to
     * fetch the information inside user's drive backup file
     * */
    private void userLoggedWithGoogle() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account == null){
            Log.i(TAG, "No account found");
            return;
        }

        Log.d(TAG, "Trying to read user's file record from google drive");
        Scope driveScope = new Scope(Scopes.DRIVE_FULL);
        Scope email = new Scope(Scopes.EMAIL);
        if(GoogleSignIn.hasPermissions(account, driveScope, email)) {
            Log.i(TAG, "userLoggedWithGoogle: user has accepted google drive storage");
            viewModel.driveSetUp(getApplicationContext(), account, driveScope, "root", "drive");
        }
    }


    /**
     * It will associate our Toolbar as the ActionBar for this view and
     * create an icon for open the left bar
     * */
    private void setUpToolbar() {
        //we obtain our custom toolbar
        topBar = findViewById(R.id.top_toolbar);

        Drawable hamburguerIcon = ContextCompat.getDrawable(this, R.drawable.ic_menu_black_18dp);

        topBar.setNavigationIcon(hamburguerIcon);
        //we mark our toolbar as the app bar
        setSupportActionBar(topBar);
        //we set a listener to the navigation button which will have a hamburger icon
        //this will open the left navigation view. We do it like this because the icon
        //is not implemented by default
        topBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * It will create constraints to our CalendarView manually because that view
     * was not implemented to be used in the xml files
     * */
    private void drawCalendarView() {

        ConstraintLayout visibleContent = findViewById(R.id.dl_content_layout);
        //we will insert a new child to visibleContent
        visibleContent.addView(calendar);

        //reference https://developer.android.com/reference/android/support/constraint/ConstraintSet
        ConstraintSet layoutConstraints = new ConstraintSet();

        //This will clone all the constraints defined inside visibleContet
        layoutConstraints.clone(visibleContent);
        //This will add constraints to calendar like we do inside our layout xml file
        layoutConstraints.connect(calendar.getId(), ConstraintSet.TOP, topBar.getId(), ConstraintSet.BOTTOM);
        layoutConstraints.connect(calendar.getId(), ConstraintSet.BOTTOM, visibleContent.getId(), ConstraintSet.BOTTOM);
        layoutConstraints.connect(calendar.getId(), ConstraintSet.START, visibleContent.getId(), ConstraintSet.START);
        layoutConstraints.connect(calendar.getId(), ConstraintSet.END, visibleContent.getId(), ConstraintSet.END);
        layoutConstraints.setHorizontalBias(calendar.getId(), 0.50f);
        //we make effective each constraints
        layoutConstraints.applyTo(visibleContent);
        /*
        What we do up there is the same as below
        <view
                app:layout_constraintBottom_toBottomOf="parent"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintHorizontal_bias="0.50"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@+id/top_toolbar" />
        * */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
        * When a user clicked an empty day (day with white background) it will launch a new
        * activity where the user can insert how it felt a some comments, once that
        * activity has finished, it will trigger this method for us to obtain the information
        * given by the user
        * */
        if(requestCode == MARKED_DAY_REQUEST && resultCode == RESULT_OK){
            Log.d(TAG, "ACTIVITY RETURNED: " + data.getStringExtra(INTENT_REPLY));
            //the answer will be in the form of yyy-mm-dd&moodOrdinal&comments
            viewModel.insertEntityDate(data.getStringExtra(INTENT_REPLY));
        }
    }

    //when creating the view, the system will call this function once it reaches the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_top_bar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * this method will be invoked when the user selects one toolbar's option
     *
     * @param item MenuItem clicked by the user.
     *
     * @return it will return a boolean indicating if we want to handle
     * that click event or not
     */
    //You can find each menu items in res/menu/ folder
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_t_action_settings:
                settingsHandler();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Activity call when its state change to "stop"
    @Override
    protected void onStop() {
        super.onStop();
        viewModel.storeInDrive();
    }

    /**
     * This function will set up handlers for each menu item inside the NavigationView.<br>
     *
     * @param navView Current NavigationView in our main Activity
     * @see NavigationView
     * @see <a href="https://material.io/develop/android/components/navigation-view/">Material design web page</a>
     * */
    private void setupDrawerContent(NavigationView navView){

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_lnb_action_settings:
                        settingsHandler();
                        menuItem.setChecked(false);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }


    private void settingsHandler(){
        Log.d(TAG, "settings selected!");
    }

    //              TESTING
    //   it will help with changing screen orientation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        DateParcelable parc = new DateParcelable();
        //parc.setDateList(calendar.getDateLists());

        outState.putParcelable(CURRENT_DATE_BUNDLE, parc);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


    }
}
