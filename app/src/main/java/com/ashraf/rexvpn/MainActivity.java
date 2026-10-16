
package com.ashraf.rexvpn;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;

public class MainActivity extends Activity {

    int purple = Color.rgb(168, 85, 247);
    int cyan = Color.rgb(34, 211, 238);
    int white = Color.WHITE;
    int muted = Color.rgb(170, 160, 190);
    int panel = Color.rgb(19, 16, 29);

    LinearLayout root, content;
    TextView status, serverName, configName, pulseText;
    boolean connected = false;
    String selectedServer = "لم يتم اختيار سيرفر";
    String selectedConfig = "لم يتم اختيار كونفيج";
    Uri configUri;

    int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    GradientDrawable bg(int color, int strokeColor, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        if (strokeColor != 0) d.setStroke(dp(1), strokeColor);
        return d;
    }

    TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    LinearLayout vertical() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    LinearLayout horizontal() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        return l;
    }

    void pad(View v, int a, int b, int c, int d) {
        v.setPadding(dp(a), dp(b), dp(c), dp(d));
    }

    LinearLayout.LayoutParams lp(int w, int h) {
        return new LinearLayout.LayoutParams(
            w < 0 ? w : dp(w), h < 0 ? h : dp(h));
    }

    void space(LinearLayout l, int h) {
        View v = new View(this);
        l.addView(v, lp(1, h));
    }

    Button button(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(white);
        b.setTextSize(14);
        b.setAllCaps(false);
        b.setBackground(bg(Color.rgb(75, 30, 125), purple, 15));
        return b;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.rgb(5, 5, 10));
        getWindow().setNavigationBarColor(Color.rgb(5, 5, 10));
        getWindow().getDecorView().setSystemUiVisibility(0);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(5, 5, 10));

        root = vertical();
        pad(root, 18, 14, 18, 18);
        scroll.addView(root);
        setContentView(scroll);

        buildUI();
    }

    void buildUI() {
        root.removeAllViews();

        LinearLayout header = horizontal();
        TextView logo = text("◈", 32, purple);
        header.addView(logo, lp(45, 48));

        LinearLayout titles = vertical();
        TextView title = text("ريكس VPN", 23, white);
        title.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        TextView subtitle = text("اتصالك تحت سيطرتك", 11, muted);
        subtitle.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        titles.addView(title);
        titles.addView(subtitle);
        header.addView(titles, new LinearLayout.LayoutParams(0, -2, 1));

        status = text(connected ? "متصل" : "غير متصل", 12,
            connected ? cyan : muted);
        status.setBackground(bg(panel, connected ? cyan : purple, 20));
        pad(status, 12, 8, 12, 8);
        header.addView(status);
        root.addView(header);
        space(root, 15);

        FrameLayout wolfFrame = new FrameLayout(this);
        wolfFrame.setBackground(bg(Color.rgb(17, 12, 26), purple, 22));
        wolfFrame.setClipToOutline(true);

        ImageView wolf = new ImageView(this);
        wolf.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int imageId = getResources().getIdentifier(
            "wolf_background", "drawable", getPackageName());
        if (imageId != 0) {
            wolf.setImageResource(imageId);
        } else {
            wolf.setBackground(bg(Color.rgb(18, 12, 30), purple, 22));
        }
        wolfFrame.addView(wolf, new FrameLayout.LayoutParams(
            -1, dp(205)));

        TextView wolfLabel = text("R E X", 24, white);
        wolfLabel.setShadowLayer(dp(12), 0, 0, purple);
        FrameLayout.LayoutParams labelParams = new FrameLayout.LayoutParams(
            -1, dp(50), Gravity.BOTTOM);
        wolfFrame.addView(wolfLabel, labelParams);
        root.addView(wolfFrame, lp(-1, 205));
        space(root, 16);

        LinearLayout pulseBox = vertical();
        pulseBox.setGravity(Gravity.CENTER);
        pulseBox.setBackground(bg(panel, Color.rgb(55, 36, 77), 18));
        pad(pulseBox, 12, 12, 12, 12);

        pulseText = text("نبض الاتصال", 14, cyan);
        pulseBox.addView(pulseText);
        space(pulseBox, 8);

        TextView pulse = text(
            "━━━━╱╲━━━━╱╲╱╲━━━━╱╲━━━━", 17, purple);
        pulse.setSingleLine(true);
        pulseBox.addView(pulse);

        Animation blink = new AlphaAnimation(0.45f, 1f);
        blink.setDuration(800);
        blink.setRepeatMode(Animation.REVERSE);
        blink.setRepeatCount(Animation.INFINITE);
        pulse.startAnimation(blink);
        root.addView(pulseBox, lp(-1, 92));
        space(root, 18);

        LinearLayout connectWrap = vertical();
        connectWrap.setGravity(Gravity.CENTER);
        TextView power = text("⏻", 48, white);
        power.setBackground(bg(Color.rgb(28, 15, 43), purple, 100));
        power.setElevation(dp(5));
        connectWrap.addView(power, lp(112, 112));

        TextView connectLabel = text(
            connected ? "اضغط للفصل" : "اضغط للاتصال", 15, white);
        connectWrap.addView(connectLabel);
        connectWrap.setOnClickListener(v -> {
            Toast.makeText(this,
                "الواجهة جاهزة، لكن محرك VPN الحقيقي لم يتم تركيبه بعد",
                Toast.LENGTH_LONG).show();
        });
        root.addView(connectWrap);
        space(root, 18);

        TextView serverTitle = text("السيرفر الحالي", 17, white);
        serverTitle.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        root.addView(serverTitle);
        space(root, 8);

        LinearLayout serverCard = horizontal();
        serverCard.setBackground(bg(panel, purple, 16));
        pad(serverCard, 14, 13, 14, 13);

        TextView globe = text("◎", 30, cyan);
        serverCard.addView(globe, lp(45, 45));

        LinearLayout serverDetails = vertical();
        serverName = text(selectedServer, 14, white);
        serverName.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        TextView serverHint = text("اضغط لاختيار الدولة", 11, muted);
        serverHint.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        serverDetails.addView(serverName);
        serverDetails.addView(serverHint);
        serverCard.addView(serverDetails,
            new LinearLayout.LayoutParams(0, -2, 1));

        TextView arrow = text("⌄", 25, purple);
        serverCard.addView(arrow, lp(30, 40));
        serverCard.setOnClickListener(v -> showServers());
        root.addView(serverCard);
        space(root, 18);

        TextView configTitle = text("الكونفجات", 17, white);
        configTitle.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        root.addView(configTitle);
        space(root, 8);

        LinearLayout configCard = vertical();
        configCard.setBackground(bg(panel, Color.rgb(65, 42, 88), 16));
        pad(configCard, 13, 13, 13, 13);

        LinearLayout configRow = horizontal();
        TextView fileIcon = text("▤", 28, cyan);
        configRow.addView(fileIcon, lp(42, 42));

        configName = text(selectedConfig, 13, white);
        configName.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        configRow.addView(configName,
            new LinearLayout.LayoutParams(0, -2, 1));
        configCard.addView(configRow);
        space(configCard, 12);

        Button importButton = button("＋  تحميل كونفيج");
        importButton.setOnClickListener(v -> pickConfig());
        configCard.addView(importButton, lp(-1, 50));
        space(configCard, 7);

        Button removeButton = button("حذف الكونفيج المختار");
        removeButton.setBackground(bg(Color.rgb(35, 20, 35),
            Color.rgb(120, 55, 95), 14));
        removeButton.setOnClickListener(v -> {
            selectedConfig = "لم يتم اختيار كونفيج";
            configUri = null;
            configName.setText(selectedConfig);
            Toast.makeText(this, "تم إلغاء اختيار الملف",
                Toast.LENGTH_SHORT).show();
        });
        configCard.addView(removeButton, lp(-1, 45));
        root.addView(configCard);
        space(root, 22);

        TextView footer = text("Created by Ashraf Shawky", 11, muted);
        root.addView(footer, lp(-1, 35));
    }

    void showServers() {
        final String[] servers = {
            "ألمانيا", "هولندا", "الولايات المتحدة", "فرنسا"
        };
        new android.app.AlertDialog.Builder(this)
            .setTitle("اختار السيرفر")
            .setItems(servers, (dialog, which) -> {
                selectedServer = servers[which];
                serverName.setText(selectedServer);
                Toast.makeText(this,
                    "اخترت " + selectedServer +
                    " — يلزم إعداد سيرفر فعلي للاتصال",
                    Toast.LENGTH_LONG).show();
            })
            .setNegativeButton("إلغاء", null)
            .show();
    }

    void pickConfig() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK &&
            data != null && data.getData() != null) {
            configUri = data.getData();
            selectedConfig = configUri.getLastPathSegment();
            if (selectedConfig == null) selectedConfig = "كونفيج مختار";
            configName.setText(selectedConfig);
            Toast.makeText(this,
                "تم اختيار الملف. تشغيله يحتاج دعم نوع الكونفيج.",
                Toast.LENGTH_LONG).show();
        }
    }
}
