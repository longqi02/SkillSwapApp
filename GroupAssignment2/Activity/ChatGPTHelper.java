package com.example.GroupAssignment2.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.GroupAssignment2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatGPTHelper {
    private static final String OPENAI_API_KEY = "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"; // Replace securely
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    @SuppressLint("ClickableViewAccessibility")
    public static void setupFloatingAI(AppCompatActivity activity) {
        FrameLayout root = activity.findViewById(android.R.id.content);
        ImageView robot = root.findViewById(R.id.btnAiRobot);

        Point screen = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(screen);

        robot.setOnTouchListener((v, ev) -> {
            float dX = 0, dY = 0;
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    dX = v.getX() - ev.getRawX();
                    dY = v.getY() - ev.getRawY();
                    return false;
                case MotionEvent.ACTION_MOVE:
                    float newX = ev.getRawX() + dX;
                    float newY = ev.getRawY() + dY;
                    newX = Math.max(0, Math.min(newX, screen.x - v.getWidth()));
                    newY = Math.max(0, Math.min(newY, screen.y - v.getHeight()));
                    v.setX(newX);
                    v.setY(newY);
                    return true;
            }
            return false;
        });

        View popupView = LayoutInflater.from(activity).inflate(R.layout.popup_ai_chat, root, false);
        int minW = screen.x / 4, minH = screen.y / 4;
        int maxW = screen.x * 4 / 5, maxH = screen.y * 4 / 5;

        PopupWindow popup = new PopupWindow(popupView, minW, minH, true);
        popup.setOutsideTouchable(true);
        popup.setClippingEnabled(true);

        LinearLayout chatContainer = popupView.findViewById(R.id.chatContainer);
        ImageView closeBtn = popupView.findViewById(R.id.btnPopupClose);
        EditText input = popupView.findViewById(R.id.etPopupInput);
        Button sendBtn = popupView.findViewById(R.id.btnPopupSend);
        ProgressBar progressBar = popupView.findViewById(R.id.progressLoading);
        Handler handler = new Handler(Looper.getMainLooper());

        sendBtn.setOnClickListener(v -> {
            String q = input.getText().toString().trim();
            if (q.isEmpty()) return;
            progressBar.setVisibility(View.VISIBLE);

            // User bubble manually
            TextView userBubble = new TextView(activity);
            userBubble.setText(q);
            userBubble.setTextColor(0xFF111111);
            userBubble.setPadding(24, 16, 24, 16);
            userBubble.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams lpUser = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lpUser.setMargins(16, 8, 16, 8);
            lpUser.gravity = Gravity.END;
            userBubble.setLayoutParams(lpUser);
            chatContainer.addView(userBubble);

            input.setText("");

            // Thinking bubble manually
            TextView thinking = new TextView(activity);
            thinking.setText("Thinking...");
            thinking.setTextColor(0xFF111111);
            thinking.setPadding(24, 16, 24, 16);
            thinking.setBackgroundColor(Color.LTGRAY);
            LinearLayout.LayoutParams lpAI = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lpAI.setMargins(16, 8, 16, 8);
            lpAI.gravity = Gravity.START;
            thinking.setLayoutParams(lpAI);
            chatContainer.addView(thinking);

            sendMessageToGPT(q, new ChatCallback() {
                @Override
                public void onResponse(String reply) {
                    handler.post(() -> {
                        thinking.setText(reply);
                        progressBar.setVisibility(View.GONE);
                    });
                }

                @Override
                public void onFailure(String error) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        thinking.setText("Error: " + error);
                        progressBar.setVisibility(View.GONE);
                    });
                }
            });
        });

        closeBtn.setOnClickListener(v -> popup.dismiss());

        View handle = popupView.findViewById(R.id.popupResizeHandle);
        handle.setOnTouchListener(new View.OnTouchListener() {
            int origW = minW, origH = minH;
            float startX, startY;

            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                switch (ev.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        origW = popup.getWidth();
                        origH = popup.getHeight();
                        startX = ev.getRawX();
                        startY = ev.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float dx = ev.getRawX() - startX;
                        float dy = ev.getRawY() - startY;
                        int w = Math.round(origW + dx);
                        int h = Math.round(origH + dy);
                        w = Math.max(minW, Math.min(w, maxW));
                        h = Math.max(minH, Math.min(h, maxH));
                        popup.update(w, h);
                        return true;
                }
                return false;
            }
        });

        robot.setOnClickListener(v -> {
            if (popup.isShowing()) popup.dismiss();
            else popup.showAtLocation(root, Gravity.CENTER, 0, 0);
        });
    }

    private static void sendMessageToGPT(String message, ChatCallback callback) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo");
            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "user").put("content", message));
            jsonBody.put("messages", messages);
        } catch (JSONException e) {
            callback.onFailure(e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure("Unexpected code " + response);
                    return;
                }
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    String reply = json.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");
                    callback.onResponse(reply.trim());
                } catch (JSONException e) {
                    callback.onFailure("JSON error: " + e.getMessage());
                }
            }
        });
    }

    private static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private static int dpToPx(AppCompatActivity act, int dp) {
        float density = act.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private static void addMessageToChat(LinearLayout container, String message, boolean isAi) {
        TextView tv = new TextView(container.getContext());
        tv.setText(message);
        tv.setTextSize(16);
        tv.setPadding(16, 12, 16, 12);
        tv.setBackgroundColor(isAi ? Color.LTGRAY : Color.WHITE);
        container.addView(tv);
    }

    private static PopupWindow showPopup(Context context, String initialText) {
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_ai_chat, null);
        LinearLayout chatContainer = popupView.findViewById(R.id.chatContainer);
        TextView greeting = new TextView(context);
        greeting.setText(initialText);
        greeting.setTextSize(16);
        greeting.setPadding(16, 12, 16, 12);
        greeting.setBackgroundColor(Color.parseColor("#DDDDDD"));
        chatContainer.addView(greeting);

        PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        return popupWindow;
    }

    public interface ChatCallback {
        void onResponse(String reply);
        void onFailure(String error);
    }
}

