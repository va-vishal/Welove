package in.Welove.Profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import in.Welove.BaseActivity;
import in.Welove.R;


public class SafetyTipsActivity extends BaseActivity {

    private TextView termsTextView;
    private TextView termsTextView1;
    private TextView termsTextView2;
    private TextView termsTextView3;
    private TextView termsTextView4;
    private TextView termsTextView5;
    private TextView termsTextView6;
    private TextView termsTextView7;
    private TextView termsTextView8;
    private TextView termsTextView9;
    private TextView termsTextView10;
    private TextView termsTextView11;
    private TextView termsTextView12;
    private TextView termsTextView13;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_tips);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Initialize TextViews
        termsTextView = findViewById(R.id.termsTextView);
        termsTextView1 = findViewById(R.id.termsTextView1);
        termsTextView2 = findViewById(R.id.termsTextView2);
        termsTextView3 = findViewById(R.id.termsTextView3);
        termsTextView4 = findViewById(R.id.termsTextView4);
        termsTextView5 = findViewById(R.id.termsTextView5);
        termsTextView6 = findViewById(R.id.termsTextView6);
        termsTextView7 = findViewById(R.id.termsTextView7);
        termsTextView8 = findViewById(R.id.termsTextView8);
        termsTextView9 = findViewById(R.id.termsTextView9);
        termsTextView10 = findViewById(R.id.termsTextView10);
        termsTextView11 = findViewById(R.id.termsTextView11);
        termsTextView12 = findViewById(R.id.termsTextView12);
        termsTextView13 = findViewById(R.id.termsTextView13);

        // Update TextView content if needed
        updateSafetyTips();
    }

    private void updateSafetyTips() {
        // Example of updating text in TextViews dynamically


        termsTextView.setText("Below are some steps you can take to increase your safety when interacting with others through WeLove dating apps and services whether you are interacting virtually or in person. Like any safety tips, they are not a guarantee, but they may help you feel more secure.");

        termsTextView1.setText("It's easy to do a reverse image search with Google. If your dating profile has a photo that also shows up on your Instagram or Facebook account, it will be easier for someone to find you on WeLove social media.");

        termsTextView2.setText("If you know your match's name or handles on social media—or better yet, if you have mutual friends online—look them up and make sure they aren't 'catfishing' you by using a fake social media account to create their dating profile.");

        termsTextView3.setText("Block and report suspicious users.\n\n" +
                "You can block and report another user if you feel their profile is suspicious or if they have acted inappropriately toward you. This can often be done anonymously before or after you've matched. As with any personal interaction, it is always possible for people to misrepresent themselves. Trust your instincts about whether you feel someone is representing themself truthfully or not.\n\n" +
                "The list below offers a few examples of some common stories or suspicious behaviors scammers may use to build trust and sympathy so they can manipulate another user in an unhealthy way.\n\n" +
                "• Asks for financial assistance in any way, often because of a sudden personal crisis\n" +
                "• Claims to be recently widowed with children\n" +
                "• Disappears suddenly from the site then reappears under a different name\n" +
                "• Gives vague answers to specific questions\n" +
                "• Overly complimentary and romantic too early in your communication\n" +
                "• Pressures you to provide your phone number or talk outside the dating app or site\n" +
                "• Requests your home or work address under the guise of sending flowers or gifts\n" +
                "• Tells inconsistent or grandiose stories\n\n" +
                "Examples of user behavior you may want to report can include:\n\n" +
                "• Requests financial assistance\n" +
                "• Requests photographs\n" +
                "• Is a minor\n" +
                "• Sends harassing or offensive messages\n" +
                "• Attempts to threaten or intimidate you in any way\n" +
                "• Seems to have created a fake profile\n" +
                "• Tries to sell you products or services\n" +
                "• Any sort of fake profile\n" +
                "• Threats or intimidation\n" +
                "• Someone trying to get you to buy something or a service");

        termsTextView4.setText("Never give someone you haven't met with in person your personal information, including your social security number, credit card details, bank information, or work or home address. WeLove dating app and websites will never send you an email asking for your username and password information, so if you receive a request for your login information, delete it and consider reporting.");

        termsTextView5.setText("No matter how convincing and compelling Someone's reason may seem valid, but never respond to a request to send money, especially overseas or via wire transfer. If you do get such a request, report it to the app you're using immediately.");

        termsTextView6.setText("Once you have matched with a potential date and chatted, consider scheduling a video chat with them before meeting up in person for the first time. This can be a good way to help ensure your match is who they claim to be in their profile. If they strongly resist a video call, that could be a sign of suspicious activity.");

        termsTextView7.setText("Take a screenshot of your date's profile and send it to a friend. Let at least one friend know where and when you plan to go on your date. If you continue your date in another place you hadn't planned on, text a friend to let them know your new location. It may also be helpful to arrange to text or call a friend partway through the date or when you get home to check in.");

        termsTextView8.setText("For your first date, avoid meeting someone you don't know well yet in your home, apartment, or workplace. It may make both you and your date feel more comfortable to meet in a coffee shop, restaurant, or bar with plenty of other people around. Avoid meeting in public parks and other isolated locations for first dates.");

        termsTextView9.setText("It's important that you are in control of your own transportation to and from the date so that you can leave whenever you want and do not have to rely on your date in case you start feeling uncomfortable. Even if the person you're meeting volunteers to pick you up, avoid getting into a vehicle with someone you don't know and trust, especially if it's the first meeting.");

        termsTextView10.setText("There's nothing wrong with having a few drinks on a date. Try to keep your limits in mind and do not feel pressured to drink just because your date is drinking. It can also be a good idea to avoid taking drugs before or during a first date with someone new because drugs could alter your perception of reality or have unexpected interactions with alcohol.");

        termsTextView11.setText("If you block or report someone on a dating app, it's likely that you will not see your past interactions or their profile again. Should something negative happen to you on the date or you need to report something, having a backup of your interactions can be very helpful.");

        termsTextView12.setText("If you feel uncomfortable in a situation, it can help to find an advocate nearby. You can enlist the help of a waiter or bartender to help you create a distraction, call the police, or get a safe ride home.");

        termsTextView13.setText("If you feel uncomfortable, trust your instincts and feel free to leave a date or cut off communication with whoever is making you feel unsafe. Do not worry about feeling rude—your safety is most important, and your date should understand that.\n\nIf you felt uncomfortable or unsafe during the date, remember you can always unmatch, block, or report your match after meeting up in person, which will keep them from being able to access your profile in the future.");
    }
}
