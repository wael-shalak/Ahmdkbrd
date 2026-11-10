package com.ahmad.keyboard;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Ahmad Keyboard: a QWERTY keyboard that shortens Lebanese Arabizi words when you press space. */
public class AhmadIME extends InputMethodService {

    private static final String SHIFT = "⇧", DEL = "⌫", ENTER = "⏎", SPACE = "space", TOGGLE = "⚡";
    private static final String[][] ROWS = {
            {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"},
            {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p"},
            {"a", "s", "d", "f", "g", "h", "j", "k", "l"},
            {SHIFT, "z", "x", "c", "v", "b", "n", "m", DEL},
            {TOGGLE, ",", SPACE, ".", "?", ENTER}
    };
    private static final Pattern LAST_WORD = Pattern.compile("([A-Za-z0-9']+)$");

    private static final int BG = 0xFF18213A, KEY = 0xFF2B3550, KEY_DOWN = 0xFF46527A,
            KEY_FN = 0xFF222B42, ACCENT = 0xFFFF6A4F, TEXT = 0xFFF5F7FB, MUTED = 0xFF99A3BD;

    private boolean shift = false, shortening = true;
    private TextView suggestion;
    private final List<TextView> letterKeys = new ArrayList<>();
    private TextView toggleKey, shiftKey;
    private final Handler repeat = new Handler(Looper.getMainLooper());
    private Runnable repeatTask;

    @Override
    public View onCreateInputView() {
        letterKeys.clear();
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        root.setPadding(dp(3), dp(4), dp(3), dp(6));

        suggestion = new TextView(this);
        suggestion.setTextColor(MUTED);
        suggestion.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        suggestion.setGravity(Gravity.CENTER);
        suggestion.setPadding(dp(8), dp(6), dp(8), dp(6));
        suggestion.setOnClickListener(v -> {
            shortenLastWord();
            updateSuggestion();
        });
        root.addView(suggestion, new LinearLayout.LayoutParams(-1, dp(36)));

        for (String[] row : ROWS) {
            LinearLayout line = new LinearLayout(this);
            line.setOrientation(LinearLayout.HORIZONTAL);
            line.setGravity(Gravity.CENTER);
            for (String k : row) line.addView(makeKey(k), keyParams(k, row.length));
            root.addView(line, new LinearLayout.LayoutParams(-1, dp(50)));
        }
        updateSuggestion();
        return root;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        shift = false;
        refreshLabels();
        updateSuggestion();
    }

    @Override
    public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd,
                                  int candStart, int candEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candStart, candEnd);
        updateSuggestion();
    }

    private LinearLayout.LayoutParams keyParams(String k, int rowLen) {
        float weight = 1f;
        if (k.equals(SPACE)) weight = 4.5f;
        else if (k.equals(SHIFT) || k.equals(DEL) || k.equals(ENTER) || k.equals(TOGGLE)) weight = 1.5f;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -1, weight);
        lp.setMargins(dp(2), dp(3), dp(2), dp(3));
        return lp;
    }

    private TextView makeKey(String k) {
        TextView t = new TextView(this);
        boolean fn = k.equals(SHIFT) || k.equals(DEL) || k.equals(ENTER) || k.equals(TOGGLE);
        t.setGravity(Gravity.CENTER);
        t.setTextColor(TEXT);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, k.equals(SPACE) ? 14 : 20);
        t.setTypeface(Typeface.DEFAULT, k.matches("[0-9]") ? Typeface.BOLD : Typeface.NORMAL);
        t.setBackground(keyBackground(fn ? KEY_FN : KEY));
        t.setTag(k);
        t.setText(k.equals(SPACE) ? "Ahmad Keyboard" : k);
        if (k.equals(TOGGLE)) toggleKey = t;
        if (k.equals(SHIFT)) shiftKey = t;
        if (k.length() == 1 && Character.isLetter(k.charAt(0))) letterKeys.add(t);
        if (k.matches("[0-9]")) t.setTextColor(ACCENT);

        t.setOnTouchListener((v, e) -> {
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    v.setPressed(true);
                    v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                    onKey(k);
                    if (k.equals(DEL)) startRepeat(k);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.setPressed(false);
                    stopRepeat();
                    return true;
            }
            return true;
        });
        refreshKey(t);
        return t;
    }

    private StateListDrawable keyBackground(int color) {
        StateListDrawable s = new StateListDrawable();
        s.addState(new int[]{android.R.attr.state_pressed}, rounded(KEY_DOWN));
        s.addState(new int[]{}, rounded(color));
        return s;
    }

    private GradientDrawable rounded(int color) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(dp(7));
        return g;
    }

    private void onKey(String k) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        switch (k) {
            case DEL: {
                CharSequence sel = ic.getSelectedText(0);
                if (sel != null && sel.length() > 0) ic.commitText("", 1);
                else ic.deleteSurroundingText(1, 0);
                break;
            }
            case SHIFT:
                shift = !shift;
                refreshLabels();
                break;
            case TOGGLE:
                shortening = !shortening;
                refreshLabels();
                break;
            case SPACE:
                shortenLastWord();
                ic.commitText(" ", 1);
                break;
            case ENTER:
                shortenLastWord();
                pressEnter(ic);
                break;
            case ",":
            case ".":
            case "?":
                shortenLastWord();
                ic.commitText(k, 1);
                break;
            default:
                ic.commitText(shift ? k.toUpperCase() : k, 1);
                if (shift) {
                    shift = false;
                    refreshLabels();
                }
        }
        updateSuggestion();
    }

    private void pressEnter(InputConnection ic) {
        EditorInfo ei = getCurrentInputEditorInfo();
        int action = ei == null ? EditorInfo.IME_ACTION_NONE : ei.imeOptions & EditorInfo.IME_MASK_ACTION;
        boolean noAction = ei == null || (ei.imeOptions & EditorInfo.IME_FLAG_NO_ENTER_ACTION) != 0
                || action == EditorInfo.IME_ACTION_NONE || action == EditorInfo.IME_ACTION_UNSPECIFIED;
        if (noAction) ic.commitText("\n", 1);
        else ic.performEditorAction(action);
    }

    /** Replaces the word just before the cursor with its short form. */
    private void shortenLastWord() {
        if (!shortening) return;
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        String w = currentWord(ic);
        if (w == null) return;
        String s = Shortener.shorten(w);
        if (!s.equals(w)) {
            ic.deleteSurroundingText(w.length(), 0);
            ic.commitText(s, 1);
        }
    }

    private String currentWord(InputConnection ic) {
        CharSequence before = ic.getTextBeforeCursor(48, 0);
        if (before == null) return null;
        Matcher m = LAST_WORD.matcher(before);
        return m.find() ? m.group(1) : null;
    }

    private void updateSuggestion() {
        if (suggestion == null) return;
        InputConnection ic = getCurrentInputConnection();
        String w = ic == null ? null : currentWord(ic);
        String s = w == null ? null : Shortener.shorten(w);
        if (!shortening) {
            suggestion.setText("Shortening off · tap ⚡ to turn it on");
            suggestion.setTextColor(MUTED);
        } else if (w != null && !s.equals(w)) {
            suggestion.setText(w + "  →  " + s);
            suggestion.setTextColor(ACCENT);
        } else {
            suggestion.setText("Type Arabizi · words shorten when you press space");
            suggestion.setTextColor(MUTED);
        }
    }

    private void refreshLabels() {
        for (TextView t : letterKeys) refreshKey(t);
        if (toggleKey != null) refreshKey(toggleKey);
        if (shiftKey != null) refreshKey(shiftKey);
        updateSuggestion();
    }

    private void refreshKey(TextView t) {
        String k = (String) t.getTag();
        if (k.equals(TOGGLE)) t.setTextColor(shortening ? ACCENT : MUTED);
        else if (k.equals(SHIFT)) t.setTextColor(shift ? ACCENT : TEXT);
        else if (letterKeys.contains(t)) t.setText(shift ? k.toUpperCase() : k);
    }

    private void startRepeat(String k) {
        stopRepeat();
        repeatTask = new Runnable() {
            @Override
            public void run() {
                onKey(k);
                repeat.postDelayed(this, 60);
            }
        };
        repeat.postDelayed(repeatTask, 400);
    }

    private void stopRepeat() {
        if (repeatTask != null) repeat.removeCallbacks(repeatTask);
        repeatTask = null;
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}
