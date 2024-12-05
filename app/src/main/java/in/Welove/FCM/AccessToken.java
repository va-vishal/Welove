package in.Welove.FCM;


import android.util.Log;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class AccessToken {

    private static final String firebaseMessagingScope="https://www.googleapis.com/auth/firebase.messaging";

    public String getAccessToken() {
        try{
            String  jsonString="{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"welove-20c9d\",\n" +
                    "  \"private_key_id\": \"b1100dbca31560375f271d088fd469f77ff65c4a\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC0RqNjRQXUJZhR\\n9Q7/vxRzJ5PBR6drNIJw25Dtw8wzzdJ0nDKDYpHgQ6DPBLFWhtO3QQaLRlxEU4ZM\\nBa+QQbA14Obk/SbnmIokQgRw4wVFswwPFEgerLWvdWVKPN8D5J4D9UyRXx2340hG\\noGyLMJXe6wGOTYGQYpoOjIk94ybuQLh5D/fVzCQAdLCmbe378mfq+j2OaTMTPSyD\\ni9r0PX66y811coLUqaOPe0n9Ik6Yoj7MfJc5twPAKE4kb3g66iC3svG7RISQLNjd\\n45uOgs42MFeMWYtdqvsBKxXv4M9g6J8viOKEAJUkNYDcCgtC8b0WFOS5ZByX8ayI\\nDYrvvuDbAgMBAAECggEAKOSFF3WGtvK4NI8UEes0JlLp70PjtCu7EwKbQ3PQlEex\\n34W255qqYGa2yX7VAz2eNMlpRJp3P4B6zC/V/TEJWY9kkLjj5FDztPnMrBazEXZl\\n293L5jljFSPaEBbyt51aQqeX8LnUseu6b1rghEpHFMPWHIN0Sl4zd+1lDEbEg/KE\\n71YuGD1iR4ykqHbqDmfe0Tvbzqr7tCj538F1UeizHpmiLXsBSIJHkV5Nfg2zkOIi\\naXXW9ch1yHRNCc6NdW9f533KAQQ07rE5LFiLsDKsj6ZJkRsxi1DL7LvYWvw+W7iW\\nOPt/ViHCtqJ5sBIGGmZhD6NOaoLCPsFPNclE/ZF1IQKBgQDYDJHxk3TTDgUmuFJB\\nma4sGrvo0vTuwUsfMamqaQepc7dqL6O4l0tj9Ak8XvLTtKWzVkkEOq1jzd4O/Nif\\nXaZeDB5gMSjlnVy/auEWv3ej57+8WO/hA90d5m0vTJ7SWPcquC7g+EZLCaHZEyEx\\nGF+65iXq15yRLCXXTRSh8UFswwKBgQDVnJ8hzgIieFORsdf84lqZZ7QRHZAfjUaR\\n1hRi1quRt2nJv/vt013D1RFLKGk+gWMWePyiMPPdlIF24Nq+NQpb0IGieHhwtpKo\\nsmLZhVVoMPprEBAtLvQkBs3ugqqQBp4/Fw8x1qcFu0rgQjc8qQeH0WvKFLtfuUvQ\\n+an1PZzaCQKBgQDDEPZki6KbPLlh8GI0YLFlmO24lWAYgeV10L1D43jLgnNEKKdF\\n7zReluQP4hqVEzHY7jsW2nPa/PFVzu03VrLzjzWN3FT0nFQ1cCazB0TadFAlmpdb\\nLsXiTT6fyipL+Y+QvqN09DVjYtsCx8bcMQmeElaB5xKx8I5qCmpqnQ1ZSQKBgEHa\\nYwSc6fOOfNTxqtB/R8b7PXv5TTEs8JCNGwPNgl8N2EMmOJh9DO8OhKJS9v8aDF4l\\nNl0aElRBMBhiAY2Z9HORuCVKVncJEfWH6Ql+HXJdhxXygcosK3/fySS6i7KTF74D\\ncd+/eQY/UzoHr9e3lGa+nShTc+By03i9PzQnpm9hAoGBAJmrX/KWs9TBv96fgQbA\\n/r2zW2gAWa0lE05yQaSbCeAONCTMnDPne7KzN9eSPTEW3T5GPysiJi1i0+2TqmdZ\\ncZj63mCIB70Hn1v60wf0l3AWGmkLrFAghn6NkZFKva29qRDwNAWTGLGXBML4ir6L\\nOSeB56bCd2P6NPogdvA/MjYO\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-33mfi@welove-20c9d.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"112644068961482652627\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-33mfi%40welove-20c9d.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n";

            InputStream stream=new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));

            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream)
                    .createScoped(Lists.newArrayList(firebaseMessagingScope));

            googleCredentials.refresh();

            return googleCredentials.getAccessToken().getTokenValue();
        }catch(IOException e)
        {
            Log.e("error", e.getMessage());
            return null;
        }
    }
}
