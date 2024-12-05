package in.Welove.Message;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import in.Welove.Adapter.TabsAccessorAdapter;
import in.Welove.BaseActivity;
import in.Welove.Fragments.AllFragment;
import in.Welove.Fragments.UsersFragment;
import in.Welove.HomeActivity;
import in.Welove.R;


public class MessageActivity extends BaseActivity {

    private ViewPager viewPager;
    private Button allButton, matchedUsersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeUI();
        setupViewPager();
        setupButtonListeners();
        setDefaultTab();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private void initializeUI() {
        viewPager = findViewById(R.id.view_pager);
        allButton = findViewById(R.id.all_button);

        matchedUsersButton = findViewById(R.id.matched_users_button);
    }

    private void setupViewPager() {
        TabsAccessorAdapter adapter = new TabsAccessorAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllFragment(), "All");
        adapter.addFragment(new UsersFragment(), "Connections");

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateTabSelection(position);
            }
        });
    }

    private void setupButtonListeners() {
        allButton.setOnClickListener(v -> {
            viewPager.setCurrentItem(0);
            updateTabSelection(0);
        });
        matchedUsersButton.setOnClickListener(v -> {
            viewPager.setCurrentItem(1);
            updateTabSelection(1);
        });
    }

    private void setDefaultTab() {
        viewPager.setCurrentItem(0);
        updateTabSelection(0);
    }

    private void updateTabSelection(int position) {
        boolean isAllSelected = position == 0;
        boolean isMatchedUsersSelected = position == 1;

        allButton.setSelected(isAllSelected);
        matchedUsersButton.setSelected(isMatchedUsersSelected);

        allButton.setTextColor(isAllSelected ? getColor(R.color.tab_text_selected) : getColor(R.color.tab_text_unselected));
        matchedUsersButton.setTextColor(isMatchedUsersSelected ? getColor(R.color.tab_text_selected) : getColor(R.color.tab_text_unselected));

        allButton.setBackgroundResource(isAllSelected ? R.drawable.rounded_button_background_selected : R.drawable.rounded_button_background);
        matchedUsersButton.setBackgroundResource(isMatchedUsersSelected ? R.drawable.rounded_button_background_selected : R.drawable.rounded_button_background);
    }
}
