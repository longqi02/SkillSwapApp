package com.example.GroupAssignment2.Activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.GroupAssignment2.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewBookedSkillActivity extends AppCompatActivity {

    private TextView tvTitle, tvType, tvDate;
    private ImageView ivSkillImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewholder_booked_skill);

        tvTitle = findViewById(R.id.tvBookedSkillTitle);
        tvType = findViewById(R.id.tvBookedSkillType);
        tvDate = findViewById(R.id.tvBookedSkillDate);
        ivSkillImage = findViewById(R.id.ivBookedSkillImage);

        // Retrieve data passed from previous screen
        String skillTitle = getIntent().getStringExtra("title");
        String skillType = getIntent().getStringExtra("type");
        long dateMillis = getIntent().getLongExtra("date", 0L);

        // Set text
        tvTitle.setText(skillTitle != null ? skillTitle : "Unknown Skill");
        tvType.setText(skillType != null ? skillType : "Unknown Type");

        if (dateMillis > 0) {
            String formattedDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(new Date(dateMillis));
            tvDate.setText("Booked for: " + formattedDate);
        } else {
            tvDate.setText("Date not available");
        }

        // Set icon (optional)
        ivSkillImage.setImageResource(R.drawable.ic_person);
    }
}
