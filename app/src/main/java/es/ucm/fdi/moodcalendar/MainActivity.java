package es.ucm.fdi.moodcalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.moodcalendar.customView.CalendarView;
import es.ucm.fdi.moodcalendar.dataModel.DateParcelable;
import es.ucm.fdi.moodcalendar.dataModel.DateWithBackground;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String CURRENT_DATE_BUNDLE = "currentDatesList";
    private NavigationView leftNav;
    private DrawerLayout dlLayout;
    private CalendarView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        leftNav = findViewById(R.id.dl_navigation_view);
        dlLayout = findViewById(R.id.drawer_layout);

        setupDrawerContent(leftNav);

        //we obtain our custom toolbar
        Toolbar topBar = findViewById(R.id.top_toolbar);

        Drawable hamburguerIcon = ContextCompat.getDrawable(this, R.drawable.baseline_menu_black_18dp);

        topBar.setNavigationIcon(hamburguerIcon);
        //we mark our toolbar as the app bar
        setSupportActionBar(topBar);
        //we set a listener to the navigation button which will have a hamburger icon
        //this will open the left navigation view. We do this like this because the icon
        //is not implemented by default
        topBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlLayout.openDrawer(GravityCompat.START);
            }
        });

        calendar = new CalendarView(getApplicationContext(), null);

        ArrayList<DateWithBackground> savedListDates;
        if(savedInstanceState == null){
            savedListDates = new ArrayList<>();
        } else{
            DateParcelable parcDate = savedInstanceState.getParcelable(CURRENT_DATE_BUNDLE);
            savedListDates = parcDate.getDateList();
        }

        calendar.createView(savedListDates);


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

    //when creating the view, the system will call this function once it reaches the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_top_bar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    //this method will be invoked when the user selects one toolbar's option
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
                    case R.id.menu_t_action_settings:
                        settingsHandler();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        DateParcelable parc = new DateParcelable();
        parc.setDateList(calendar.getDateLists());

        outState.putParcelable(CURRENT_DATE_BUNDLE, parc);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


    }
}
