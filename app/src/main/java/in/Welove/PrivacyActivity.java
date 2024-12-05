package in.Welove; // Replace with your actual package name

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

public class PrivacyActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy); // Ensure this matches your layout file name


        // Initialize TextViews with terms and conditions
        TextView termsTextView1 = findViewById(R.id.termsTextView);
        TextView termsTextView2 = findViewById(R.id.terms1);
        TextView termsTextView3 = findViewById(R.id.terms2);
        TextView termsTextView4 = findViewById(R.id.terms3);
        TextView termsTextView5 = findViewById(R.id.terms4);
        TextView termsTextView6 = findViewById(R.id.terms5);
        TextView termsTextView7 = findViewById(R.id.terms6);
        TextView termsTextView8 = findViewById(R.id.terms7);
        TextView termsTextView9 = findViewById(R.id.terms8);
        TextView termsTextView10 = findViewById(R.id.terms9);
        TextView termsTextView11 = findViewById(R.id.terms10);
        TextView termsTextView12 = findViewById(R.id.terms11);
        TextView termsTextView13 = findViewById(R.id.terms12);
        TextView termsTextView14 = findViewById(R.id.terms13);
        TextView termsTextView15 = findViewById(R.id.terms14);

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
        // Set the text for each TextView (replace "Terms" with actual content)
        termsTextView1.setText("This Privacy Policy applies to WeLove(powered by IB MIthra.co.in) services, our mobile application currently titled WeLove (the \"App\"), the social media accounts, and any other product or service that WeLove chooses to apply this Privacy Policy to (collectively, the \"Service\"), and details how WeLove collects, uses, and discloses data about the users, including you.");

        termsTextView2.setText("If you are younger than eighteen years old, you cannot make use of our App.\n.");

        termsTextView3.setText("Before you can use our App, we will ask you to register and create your personal Inner WeLove account (Account). In this respect, you are invited to provide us: Your Name\n\n• Your email address\n\n• Your age/date of birth\n\n• Your Job/function\n\n• One (or more) clear picture(s) of you\n\n• A description of yourself\n\n• Geolocation\n\n• Ethnicity\n\n• Sexuality\n\n• Annual income\n\n• Educational qualification\n\n• Job title and industry\n\n• Height\n\n• Whether you smoke\n\n• Whether you drink\n\n• Marital status\n\n• Whether you have children\n\n• Zodiac Sign\n\n• Salary\n\n• Date ideas, favourite spots, travel plans, hobbies and interests");

        termsTextView4.setText("By registering you give us permission to send you SMS notifications. If you wish to not receive them, please mail us at info.welove@auglin.co.in. In conclusion, please be informed that we take photographs during events which we organize which may include pictures of you and we may use these photographs for (external) promotional purposes.");

        termsTextView5.setText("We may process these personal data, because this is necessary to perform our agreement with you (i.e. giving you access to the App through your Account).\n\n• Verify your identity when logging in.\n\n• Provide your access to your account.");

        termsTextView6  .setText("We may process these personal data because you have given us your consent to do so (i.e. by voluntarily uploading such information to your Account).\n\nWith regard to the prevention, detection, and combat of illegal activities, we process personal data on the basis of our legitimate interest to ensure the users of the App can engage with each other in a safe and pleasant way. This includes personal data included in/derived from notifications from other users regarding possible illegal or unauthorized activities performed with your account.\n\nWith regard to the act of anonymization, we do this on the basis of a legitimate interest to perform research and the development of our services in order to optimize the user experience, without using personal data to do so.\n\n• Allow you to use your Account\n\n• Show your Account to other members\n\n• Allow other members to find you via targeted searches on the basis of first name, height, age, and other information that members share via their accounts\n\n• Prevent, detect, and combat illegal or unauthorized activities on the App.\n\n• Anonymize for the purpose of (statistical) analyses and scientific research with anonymized data only.");

        termsTextView7  .setText("We may process these personal data because you have given us your consent to do so (i.e. by voluntarily uploading such information to your Account).\n\nWith regard to the prevention, detection, and combat of illegal activities, we process personal data on the basis of our legitimate interest to ensure the users of the App can engage with each other in a safe and pleasant way. This includes personal data included in/derived from notifications from other users regarding possible illegal or unauthorized activities performed with your account.\n\nWith regard to the act of anonymization, we do this on the basis of a legitimate interest to perform research and the development of our services in order to optimize the user experience, without using personal data to do so.\n\n• Allow you to use your Account and thus display to other members\n\n• Show your Account to other members\n\n• Allow other members to find you via targeted searches on the basis of first name, height, age, and other information that members share via their accounts\n\n• Include in email messages from WeLove to other members that might be a good match\n\n• Select the best quality pictures\n\n• Prevent, detect, and combat illegal or unauthorized activities on the App.\n\n• Anonymize for the purpose of (statistical) analyses and scientific research with anonymized data only.\n\n• You may always change your pictures within your account.");

        termsTextView8.setText("We may process these personal data because you have given us your consent to do so (i.e. by voluntarily providing us with such information).\n\n• Share your current residence with other members nearby, based on your residence\n\n• Show you which other members are nearby at a particular moment, based on your geolocation\n\n• Show in which area you are.\n\n• We may process these personal data, because you have given us your consent to do so (i.e. by voluntarily providing us with such information).");

        termsTextView9.setText("• Calculate and verify your age\n• Show your age on your Account\n• To search and match based on set preferences by you and other members.\n\nWe may process these personal data, because we have a legitimate interest to do so (i.e. making sure you are old enough to create an Account and show your age on your Account, so other members can take this into account).");

        termsTextView10.setText("• Process your payments\n• Include in our records for the benefit of the national tax authorities.\n\nWe may process these personal data, because this is necessary to perform our agreement with you (i.e. process your payments).");

        termsTextView11.setText("• Give you access to your chat-history\n\nWe may process these personal data, because this is necessary to perform our agreement with you (i.e. provide you the option to chat via your Account). Your (chat) messages are strictly confidential.");

        termsTextView12.setText("If you have given us permission via our App, we will send you push notifications via our App to inform you about your membership status and to notify you of new messages and other activity regarding your Account.");

        termsTextView13.setText("If you refer a friend to WeLove, you will send an email message, WhatsApp message, SMS, or Messenger message to the contact details you submitted to us. We won’t share these details with anyone else. By choosing to invite your friend to join WeLove, you confirm that your friend is happy to receive the invitation. You also hereby agree to indemnify and hold WeLove harmless from all claims arising out of your messages to any person through WeLove. You must and will not use our invite friends feature to send spam.");

        termsTextView14.setText("We work hard to protect your Personal Data from unauthorized or unlawful access, alteration, disclosure, use, or destruction. We take appropriate technical and organizational measures to secure the Personal Data. For example, we encrypt our services using SSL, the data are only accessible through a secret password and digital signatures, and our employees only have access to your data on a need-to-know basis. Although we will take all reasonable measures, we cannot guarantee the security of your data transmitted to our Website; any transmission is at your own risk.");

        termsTextView15.setText("We are constantly looking for new ways to improve WeLove and we may unilaterally update this Privacy Policy from time to time. When there is a significant change in this Privacy Policy, you will see a pop-up in the app which you will have to accept before you can continue to use WeLove.");

    }
}
