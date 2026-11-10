package com.ahmad.keyboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** Setup screen: enable the keyboard, switch to it, and try it out. */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int pad = dp(20);
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setPadding(pad, pad * 2, pad, pad);

        TextView title = new TextView(this);
        title.setText("Ahmad Keyboard");
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        col.addView(title);

        TextView body = new TextView(this);
        body.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        body.setLineSpacing(0, 1.3f);
        body.setText("kifak → kfk · tamem → tmm · 3amel → 3ml\n\n"
                + "1. Tap \"Enable keyboard\" and switch on Ahmad Keyboard.\n"
                + "2. Tap \"Choose keyboard\" and pick Ahmad Keyboard.\n"
                + "3. Type below. Each word shortens when you press space.\n\n"
                + "Tap ⚡ on the keyboard to turn shortening on or off.");
        body.setPadding(0, dp(12), 0, dp(16));
        col.addView(body);

        Button enable = new Button(this);
        enable.setText("Enable keyboard");
        enable.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)));
        col.addView(enable);

        Button choose = new Button(this);
        choose.setText("Choose keyboard");
        choose.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) imm.showInputMethodPicker();
        });
        col.addView(choose);

        EditText test = new EditText(this);
        test.setHint("Try it: Hi kifak bro tamem?");
        test.setMinLines(3);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(16);
        col.addView(test, lp);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(col);
        setContentView(scroll);
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}
