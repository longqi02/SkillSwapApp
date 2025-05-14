package com.example.GroupAssignment2.Activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.GroupAssignment2.R;

import java.util.ArrayList;
import java.util.List;

public class ViewSessionRequestsActivity extends AppCompatActivity {

    ListView listViewRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);

        listViewRequests = findViewById(R.id.listViewRequests);

        SkillUser currentUser = simulateCurrentUser();

        List<SessionRequest> requests = currentUser.getIncomingRequests();

        if (requests.isEmpty()) {
            Toast.makeText(this, "No new session requests!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        List<String> displayList = new ArrayList<>();
        for (SessionRequest req : requests) {
            displayList.add("From: " + req.getRequesterName() + "\nSkill: " + req.getRequestedSkill());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listViewRequests.setAdapter(adapter);

     //   incomingRequests.removeIf(r -> now - r.getTimestampCreated() > 24 * 60 * 60 * 1000);
    }

    private SkillUser simulateCurrentUser() {
        // (later replace with real login user)
        SkillUser user = new SkillUser("Alice",
                List.of("Python", "Guitar"),
                List.of("Photography", "Cooking"));

        // Simulated incoming request
        user.addIncomingRequest(new SessionRequest("Bob", "Photography"));
        return user;
    }

}
