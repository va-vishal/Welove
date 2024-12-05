package in.Welove.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import in.Welove.Adapter.ImageAdapter;
import in.Welove.BaseActivity;
import in.Welove.R;


public class FullScreenImageActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        ImageView fullScreenImageView = findViewById(R.id.fullScreenImageView);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewImages);
        TextView text=findViewById(R.id.text);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get data from Intent
        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("imageUrl");
        List<String> imageUrls = getIntent().getStringArrayListExtra("imageUrls");

        // Check if imageUrls is null or empty
        if (imageUrls != null && !imageUrls.isEmpty()) {
            // Show RecyclerView with image URLs
            fullScreenImageView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            text.setVisibility(View.VISIBLE);
            ImageAdapter adapter = new ImageAdapter(this, imageUrls);
            recyclerView.setAdapter(adapter);
        } else if (imageUrl != null) {
            // Show FullScreenImageView with a single image URL
            fullScreenImageView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            text.setVisibility(View.GONE);
            Glide.with(this).load(imageUrl).into(fullScreenImageView);
        } else {
            // Handle case where neither imageUrl nor imageUrls are provided
            fullScreenImageView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }
    }
        @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
