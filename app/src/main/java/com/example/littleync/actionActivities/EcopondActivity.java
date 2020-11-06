package com.example.littleync.actionActivities;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.littleync.R;

import java.util.Locale;

/**
 * Ecopond Activity page where the user can idly fish for fish to gain fish resource
 */
public class EcopondActivity extends ActionActivity {

    /**
     * Sets the tag for the Log
     */
    public EcopondActivity() {
        super("EcopondActivity");
    }

    /**
     * Sets the correct content view
     */
    @Override
    protected void settingContentView() {
        setContentView(R.layout.ecopond_page);
    }

    /**
     * On this page, since it's the Ecopond, we want to fish
     */
    @Override
    protected void action() {
        user.fishFish();
        updateGainText();
        Animation animFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        gainDisplay.startAnimation(animFadeOut);
    }

    /**
     * Sets the toast-message-like display depending on the action
     */
    private void updateGainText() {
        int gain = user.getFishingGearLevel();

        String fishText = String.format(Locale.getDefault(), "+%s Fish", gain);
        String expText = String.format(Locale.getDefault(), "+%s Exp", gain);

        SpannableStringBuilder fishSpan = new SpannableStringBuilder(fishText);
        SpannableStringBuilder expSpan = new SpannableStringBuilder(expText);

        int fishColor = Color.parseColor("#8CEAFF");
        int expColor = Color.parseColor("#FF9999");

        ForegroundColorSpan fishToColor = new ForegroundColorSpan(fishColor);
        ForegroundColorSpan expToColor = new ForegroundColorSpan(expColor);

        fishSpan.setSpan(fishToColor, 0, fishText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        expSpan.setSpan(expToColor, 0, expText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        fishSpan.append(" and ");
        fishSpan.append(expSpan);

        gainDisplay.setText(fishSpan);
    }

}