package in.Welove;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TermsActivity extends BaseActivity {
     private String termsAndConditions,termsAndConditions1,termsAndConditions2,termsAndConditions3,termsAndConditions4,termsAndConditions5,termsAndConditions6,termsAndConditions7,termsAndConditions8,termsAndConditions9,termsAndConditions10,termsAndConditions11,termsAndConditions12,termsAndConditions13,termsAndConditions14,termsAndConditions15,termsAndConditions16,termsAndConditions17;
    private TextView termsTextView,termsTextView1,termsTextView2,termsTextView3,termsTextView4,termsTextView5,termsTextView6,termsTextView7,termsTextView8,termsTextView9,termsTextView10,termsTextView11,termsTextView12,termsTextView13,termsTextView14,termsTextView15,termsTextView16,termsTextView17;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

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

         termsTextView = findViewById(R.id.termsTextView);
         termsTextView1 = findViewById(R.id.termsTextView1);
         termsTextView2=findViewById(R.id.termsTextView2);
        termsTextView3=findViewById(R.id.termsTextView3);
        termsTextView4=findViewById(R.id.termsTextView4);
        termsTextView5=findViewById(R.id.termsTextView5);
        termsTextView6=findViewById(R.id.termsTextView6);
        termsTextView7=findViewById(R.id.termsTextView7);
        termsTextView8=findViewById(R.id.termsTextView8);
        termsTextView9=findViewById(R.id.termsTextView9);
        termsTextView10=findViewById(R.id.termsTextView10);
        termsTextView11=findViewById(R.id.termsTextView11);
        termsTextView12=findViewById(R.id.termsTextView12);
        termsTextView13=findViewById(R.id.termsTextView13);
        termsTextView14=findViewById(R.id.termsTextView14);
        termsTextView15=findViewById(R.id.termsTextView15);
        termsTextView16=findViewById(R.id.termsTextView16);
        termsTextView17=findViewById(R.id.termsTextView17);


        termsAndConditions = "\nThese Terms and Conditions (\"Terms\", \"Terms and Conditions\") govern your relationship with WeLove mobile application (the \"Service\") operated by Tribered Oy (\"us\", \"we\", or \"our\").\n\n" +
                "Please read these Terms and Conditions carefully before using our WeLove(powered by IB MIthra.co.in)mobile application (the \"Service\").\n\n" +
                "Your access to and use of the Service is conditioned on your acceptance of and compliance with these Terms. These Terms apply to all visitors, users, and others who access or use the Service.\n\n" +
                "By accessing or using the Service you agree to be bound by these Terms. If you disagree with any part of the terms then you may not access the Service.\n";
        termsTextView.setText(termsAndConditions);

        termsAndConditions1 = "\n• WeLove is a dating platform with the purpose to connect inspiring ambitious single people around the globe to find love, in all its forms.\n\n" +
                "• The App is meant for adult members to communicate with each other online and is not suitable for individuals under 18 years old. By using the App, you confirm that you are at least 18 years old.\n\n" +
                "• If you have an unspent conviction, or are subject to any court order, relating to assault, violence, sexual misconduct or harassment you may not use the App and we will be entitled to remove Your Account.\n\n" +
                "• If you are removed from our App, you may not use our App (again).\n\n" +
                "• When removed from our App, you will not be refunded. Your subscription will be stopped.\n\n" +
                "• Users that post objectionable content on the App will be removed from the App.\n\n" +
                "• We may change, suspend, or discontinue any aspect of the service at any time, including hours of operation or availability of the service or any feature, without notice or liability.\n\n" +
                "• In case if we get to know that your profile picture & account details are fake, we permanently block your account.\n\n" +
                "• Update your email address if there is any issue. We will mail your account; if you do not update your mail we cannot inform any details.\n";
        termsTextView1.setText(termsAndConditions1);

        termsAndConditions2 = "\n• If you want to use the App, you first need to register. In order to register, we need your Mobile Number and your email address. After registering, you can create a personal account (\"Your Account\"). When creating Your Account, we will at least need your picture. You are further invited to provide us with your first name, pictures of you, a description of yourself, your wishes regarding a partner, and basic information about yourself (such as your age, height, place of geo-location).\n\n" +
                "• You agree to provide current, complete, and accurate information when creating Your Account and when using the App. You agree to keep Your Account updated.\n\n" +
                "• After an internal screening process, we decide whether the applicant will be allowed as a member of WeLove.\n";

        termsTextView2.setText(termsAndConditions2);

        termsAndConditions3 = "• Membership subscription will automatically be extended with a successive renewal of the same duration. Discounted Full Memberships will offer a discount price upon the first payment and will then offer the standard full price for each renewal. Subscriptions can be managed under your Google Play Store account.\n\n";
        termsTextView3.setText(termsAndConditions3);


        termsAndConditions4 = "• In-App Purchases. From time to time, WeLove may offer products and services for purchase (\"in-app purchases\") through iTunes, Google Play, or other application platforms authorized by Tribered (each, a \"Software Store\"). If you choose to make an in-app purchase, you will be prompted to enter details for your account with your Play Store (\"your IAP Account\"), and your IAP Account will be charged for the in-app purchase in accordance with the terms disclosed to you at the time of purchase as well as the general terms for in-app purchases that apply to your IAP Account. Some Play Store may charge you sales tax, depending on where you live. If you purchase an auto-recurring periodic subscription through an in-app purchase, your IAP Account will continue to be billed for the subscription until you cancel. After your initial subscription commitment period, and again after any subsequent subscription period, your subscription will automatically continue for an additional equivalent period, at the price you agreed to when subscribing. If you do not wish your subscription to renew automatically, or if you want to change or terminate your subscription, you will need to log in to your IAP account and follow instructions to cancel your subscription, even if you have otherwise deleted your account with us or if you have deleted the WeLove application from your device. Deleting your account on WeLove or deleting the WeLove application from your device does not cancel your subscription; WeLove will retain all funds charged to your IAP Account until you cancel your subscription through your IAP Account.\n\n" +
                "• WeLove app may offer products and services for purchase (In-App Purchases) through the Play Store, or other application platforms authorized by WeLove (together, the \"Play Store\"). If you choose to make In-App Purchases, you will be prompted to enter details of your account with the Play Store (the \"Play Store Account\"), and your Play Store Account will be charged for the In-App Purchase in accordance with the terms disclosed to you at the time of purchase as well as the general terms for in-app purchases that apply to your Play Store Account. The App Store may charge you sales tax, depending on where you live. If you purchase an auto-recurring periodic Subscription through In-App Purchases, your Play Store Account will continue to be billed for the Subscription until you cancel. After your initial Subscription commitment period, and again after any subsequent subscription period, the Subscription will automatically continue for an additional equivalent period. If you do not wish the Subscription to renew automatically, or if you want to change or terminate the Subscription, you will need to log in to your Play Store Account and follow instructions to cancel the Subscription, even if you have otherwise deleted your account with us or if you have deleted the App from your device. Deleting your Account or deleting the App from your device does not cancel the Subscription.\n\n";
        termsTextView4.setText(termsAndConditions4);

        termsAndConditions5 =
                "WeLove may offer products and services for purchase on the App (\"In-App Purchase\"). " +
                        "If you choose to make an In-App Purchase, you acknowledge and agree that additional terms, " +
                        "disclosed to you at the point of purchase, may apply, and that such additional terms are " +
                        "incorporated herein by reference.\n\n" +

                        "You may make an In-App Purchase through the following payment methods:\n" +
                        "(a) making a purchase through a third-party platform such as the Apple App Store and Google Play Store (\"Third Party Store\"), or\n" +
                        "(b) paying with your credit card, debit card, or PayPal account, which will be processed by a third-party processor.\n" +
                        "Once you have made an In-App Purchase, you authorize us to charge your chosen Payment Method.\n" +
                        "If payment is not received by us from your chosen Payment Method, you agree to promptly pay all amounts due upon demand by us.\n\n" +

                        "Subscriptions and Auto-Renewal\n\n" +
                        "WeLove may offer some services as automatically-renewing subscriptions, e.g., a one-week subscription, one-month subscription, or " +
                        "three-month subscription (\"Premium Services\"). IF YOU PURCHASE AN AUTOMATICALLY RENEWING SUBSCRIPTION, YOUR SUBSCRIPTION WILL " +
                        "RENEW AT THE END OF THE PERIOD, UNLESS YOU CANCEL, AT WeLove THEN-CURRENT PRICE FOR SUCH SUBSCRIPTIONS.\n" +
                        "To avoid charges for a new subscription period, you must cancel, as described below, before the end of the then-current subscription period.\n" +
                        "Deleting your account or deleting the application from your device does not cancel your subscription.\n" +
                        "You will be given notice of changes in the pricing of the Premium Services to which you have subscribed and an opportunity to cancel.\n" +
                        "If WeLove changes these prices and you do not cancel your subscription, you agree that you will be charged at WeLove's then-current pricing for subscription.\n\n" +

                        "Cancelling Subscriptions\n\n" +
                        "If you purchased a subscription directly from WeLove, you may cancel or change your Payment Method via the payment settings option under your profile.\n" +
                        "If you purchased a subscription through a Third Party Store, such as the Apple App Store or the Google Play Store, you will need to access your account with that Third Party Store " +
                        "and follow instructions to change or cancel your subscription.\n" +
                        "If you cancel your subscription, you may use your subscription until the end of the period you last paid for, but\n" +
                        "(i) you will not (except as set forth in the subsection entitled \"Refunds\" below) be eligible for a prorated refund,\n" +
                        "(ii) your subscription will not be renewed when that period expires and\n" +
                        "(iii) you will then no longer be able to use the Premium Services or In-App Purchases enabled by your subscription.\n\n" +

                        "Free Trials\n\n" +
                        "If you sign up for a free trial and do not cancel, your trial may convert into a paid subscription and your Payment Method will be charged at the then-current price for such subscription.\n" +
                        "Once your free trial converts to a paid subscription, your paid subscription will continue to automatically renew at the end of each period, and your Payment Method will be charged, until you cancel.\n" +
                        "To avoid charges for a new subscription period, you must cancel before the end of the then-current subscription period or free trial period as described above.\n" +
                        "Deleting your account or deleting the application from your device does not cancel your free trial.\n" +
                        "If you have signed up for a free trial on WeLove through the Apple Store or Google Play Store previously, you will not be eligible for another free trial and you will then be automatically signed up to a subscription and charged as described in this paragraph.";
        termsTextView5.setText(termsAndConditions5);

        termsAndConditions6 ="If your transaction is done by card and you want to cancel the subscription, please cancel it 25 hours before the renewal; otherwise, the money will be deducted. There will be no refund afterward.\n\n" +
                "To cancel your subscription, follow these steps:\n\n" +
                "1. Open the Google Play app.\n" +
                "2. At the top right, tap the profile icon.\n" +
                "3. Tap Payments & subscriptions and then Subscriptions.\n" +
                "4. Select the subscription you want to cancel.\n" +
                "5. Tap Cancel subscription.\n\n" +
                "For a video guide, you can watch: https://youtu.be/NYjYA2elcJg\n\n" +
                "Note: Cancel it 25 hours (one day) before the renewal; otherwise, money will be deducted. There will be no refund afterward.";
        termsTextView6.setText(termsAndConditions6);


        termsAndConditions7 = "You acknowledge that WeLove is not required to provide a refund for any reason, and that you will not receive money or other compensation for unused In-App Purchases when the Account is closed, whether such closure was voluntary or involuntary.\n\n" +
                        "Please refer to the refund policies of the App Store for additional information on refunds.\n\n" +
                        "If the laws applicable in your jurisdiction provide for refunds, such refunds shall be processed by the Play Store via the Google Play Store Account.";

        termsTextView7.setText(termsAndConditions7);


        termsAndConditions8 ="- The information and content you post, transmit, or publish on, or share otherwise through the App; and\n" +
                "- Your interactions with other members of the App;\n" +
                "- Your friend(s) who you invite through WeLove app.\n\n" +
                "- Your Account is meant for personal use only, and you may not create more than one account at any time. You may not authorize others to use your account, and you may not assign or otherwise transfer your Account to any other person or entity. You are responsible for keeping your username, mobile number, and password confidential and acknowledge that WeLove is not responsible for third-party access to your Account that results from sharing, theft, or misappropriation of your username and password. We strongly encourage you to choose a secure password that is hard to guess. Do not reveal your password to anyone. If you think your password has been compromised, please change it immediately via the 'My Account' page.\n\n" +
                "- Please be careful with disclosing information via your Account. You may not publish any personal contact information on your Account, such as your (post) address, email address, telephone number, instant messaging contact details, or website URL. In addition, for your safety, you may not include personal contact information in messages to members who have not previously contacted you.\n\n" +
                "- After being approved as a member of WeLove, we will in principle not monitor the information or content you share via the App. We do, however, reserve the right to use filter software. In case we are notified by this software of inappropriate content, or in case we are notified of this by other members, we reserve the right to block the content in question.\n\n" +
                "By submitting, providing, transmitting, or otherwise sharing information and content via the App, you grant us a non-exclusive, royalty-free, worldwide license to any rights, including intellectual property rights, on such information and content, to (re)publish and otherwise use any such content in any format on the Website, App, or in ads or other shielded areas that are only accessible by other members of the WeLove app.";
        termsTextView8.setText(termsAndConditions8);


        termsAndConditions9 ="You will not post, transmit, communicate, or share information or content via the App that:\n\n" +
                "- Contains your personal information, mobile number, social media accounts, or nudity images. It is your responsibility to protect yourself from fake scams and sexual activity. WeLove is not responsible for such matters.\n\n" +
                "- Is intended to or tends to harass, annoy, threaten, or intimidate any other user of the App.\n\n" +
                "- Is defamatory, inaccurate, abusive, obscene, profane, offensive, or sexually explicit.\n\n" +
                "- Promotes racism, bigotry, hatred, or physical harm of any kind against any group or individual.\n\n" +
                "- Promotes or encourages illegal or unlawful activity or behavior.\n\n" +
                "- Contains chain letters, junk or spam emails, advertising, promotional messages, or commercial content.\n\n" +
                "- Contains an image of a person under 18 years of age.\n\n" +
                "- During registration, if you do not use your image within 3 to 5 hours, your account will be temporarily blocked. To retrieve your account, you must request an email. If you do not request it, your account will be deleted within 10 days.\n\n" +
                "- Is intended to solicit another member's personal contact details or solicit communication with or on behalf of a person under 18 years.\n\n" +
                "- Infringes any third party's intellectual property rights.\n\n" +
                "You will immediately cease contacting any member who asks you to stop contacting them.\n\n" +
                "You will not post, copy, modify, disclose, distribute, or share any confidential information or any other material that is subject to our or a third party's intellectual property rights, such as a picture of you taken by a professional photographer, without first obtaining our or the relevant third party's prior written consent.\n\n" +
                "You agree to compensate us for any claims or damages (including any legal fees in relation to such claims or damages) demanded by a third party in respect of any matter relating to or arising from any breach or suspected breach by you of these Terms or the rights of a third party. We reserve the right to issue warnings, suspend access to your Account, or terminate your Account if we reasonably consider that you are in breach of these Terms.";
        termsTextView9.setText(termsAndConditions9);


        termsAndConditions10 ="Tribered Oy prohibits cheating and continuously improves our anti-cheat measures. Cheating includes any action that attempts to or actually alters or interferes with the normal behavior or rules of a Service. Cheating includes, but is not limited to, any of the following behaviors, whether done on your own behalf or on behalf of others:\n\n" +
                "- Usage of Mock Location and GPS Location Emulation: This regulation is needed to protect the competitive integrity of WeLove.\n\n" +
                "- Accessing Services in an Unauthorized Manner: This includes using modified or unofficial third-party software.\n\n" +
                "Tribered Oy will not provide support and may decline service to players who attempt to cheat.";
        termsTextView10.setText(termsAndConditions10);


        termsAndConditions11 ="The rights granted by WeLove to the user are restricted to private and personal use. " +
                "As a user of WeLove, you grant WeLove a free-of-charge, non-exclusive, international, " +
                "and permanent license for the use, reproduction, representation, modification, and translation " +
                "of any basic intellectual property-related component (text, emojis, photos, videos, etc.) that you " +
                "may provide through the App for the non-exclusive purpose of communicating with other users on WeLove. " +
                "WeLove can use such content in any format on the Website, App, ads, other shielded areas accessible by other users, " +
                "and for other internal purposes.\n\n" +

                "WeLove reserves the right at any time to modify or discontinue, temporarily or permanently, the service (or any part thereof) " +
                "with or without notice. You agree that WeLove shall not be liable to you or to any third party for any modification, suspension, " +
                "or discontinuance of the service.";
        termsTextView11.setText(termsAndConditions11);


        termsAndConditions12 ="We do not conduct (criminal) background checks in relation to members of WeLove.\n\n" +

                "You are solely responsible for taking all appropriate safety precautions in connection with the use of the app and contacting other members. " +
                "You accept that there are risks interacting online or offline with other members, including dating and meeting other members.\n\n" +

                "We do not guarantee or verify the accuracy of information provided to you by other members. WeLove makes every effort to keep the information made available on the app accurate and up to date, " +
                "but we do not guarantee that the information is accurate, complete, or current. No rights can be derived from it. Any reliance on the provided information is at your own risk.\n\n" +

                "We do not warrant that the website or app will be available uninterrupted and in a fully operating condition. All content and services on the website or app are provided on an \"as is\" and \"as available\" basis. " +
                "Any decisions or actions taken by you on the basis of the information provided on or via the website and app are at your sole discretion and risk.";
        termsTextView12.setText(termsAndConditions12);


        termsAndConditions13="WeLove is always striving to improve the Service and bring you additional functionality that you will find engaging and useful. " +
                "This means WeLove may add new product features or enhancements from time to time, as well as remove some features. " +
                "If these actions do not materially affect your rights or obligations, WeLove may not provide you with notice before taking them. " +
                "WeLove may even suspend the Service entirely, in which event WeLove will notify you in advance unless extenuating circumstances, such as safety or security concerns, prevent WeLove from doing so.\n\n" +

                "We reserve the right, at our sole discretion, to modify or replace these Terms at any time. If a revision is material, we will try to provide at least 30 days' notice prior to any new terms taking effect. " +
                "What constitutes a material change will be determined at our sole discretion.\n\n" +

                "By continuing to access or use our Service after those revisions become effective, you agree to be bound by the revised terms. If you do not agree to the new terms, please stop using the Service.";
        termsTextView13.setText(termsAndConditions13);


        termsAndConditions14 = "These Terms of Use and Privacy Policy shall be governed in all respects by the laws of India. You agree to submit to the exclusive jurisdiction of the courts in the High Court of Telangana.";
        termsTextView14.setText(termsAndConditions14);


        termsAndConditions15 = "A user of WeLove may decide at any time and without notice to delete their account. If this user wishes to use WeLove again, they will be required to register once again.\n\n" +
                "• If a user deletes their account:\n" +
                "   • Their profile will be removed from the list of profiles on WeLove.\n" +
                "   • The user cannot reactivate their deleted profile.\n\n" +
                "WeLove reserves the right to terminate your membership, to suspend a profile, or to disable access with respect to a breach of any of the terms, with or without notice. Under any circumstances, purchases made on WeLove will not be refundable.";
        termsTextView15.setText(termsAndConditions15);


        termsAndConditions16 ="If you believe that your work has been copied and posted on the Service in a way that constitutes copyright infringement, please provide WeLove with the following information:\n\n" +
        "• An electronic or physical signature of the person authorized to act on behalf of the owner of the copyright interest;\n\n" +
                "• A description of the copyrighted work that you claim has been infringed;\n\n" +
                "• A description of where the material that you claim is infringing is located on the Service (and such description must be reasonably sufficient to enable WeLove to find the alleged infringing material);\n\n" +
                "• Your contact information, including address, telephone number, and email address;\n\n" +
                "• A written statement by you that you have a good faith belief that the disputed use is not authorized by the copyright owner, its agent, or the law; and\n\n" +
                "• A statement by you, made under penalty of perjury, that the above information in your notice is accurate and that you are the copyright owner or authorized to act on the copyright owner's behalf.\n\n" +
                "You may not post, distribute, or reproduce in any way any copyrighted material, trademarks, or other proprietary information without obtaining the prior written consent of the owner of such proprietary rights. Without limiting the foregoing, if you believe that your work has been copied and posted on the Service in a way that constitutes copyright infringement, please provide us with the following information at info.welove@example.com:";
        termsTextView16.setText(termsAndConditions16);


        termsAndConditions17 = "• Please report any behavior you encounter that is in breach of these Terms via info.welove@example.com, including any behavior that may be harmful, threatening, harassing, or unlawful.\n\n" +
                "• If you wish to report another member, you can inform us by selecting the appropriate option on their account.\n\n" +
                "• We are not obliged to become involved in any domestic or private disputes between members and do not provide any arbitration or settlement services. If you felt uncomfortable or unsafe during a date, remember you can always unmatch, block, or report your match after meeting up in person, which will keep them from being able to access your profile in the future.";
        termsTextView17.setText(termsAndConditions17);


    }
}
