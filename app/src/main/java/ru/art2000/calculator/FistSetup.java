package ru.art2000.calculator;

import android.os.Bundle;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;

import java.util.ArrayList;
import java.util.List;

import ru.art2000.calculator.settings.PrefsHelper;

public class FistSetup extends AhoyOnboarderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PrefsHelper.getAppTheme());
        super.onCreate(savedInstanceState);
        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("Title", "Description");
        ahoyOnboarderCard1.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard1.setTitleColor(R.color.white);
        ahoyOnboarderCard1.setDescriptionColor(R.color.grey_200);
        ahoyOnboarderCard1.setTitleTextSize(dpToPixels(10, this));
        ahoyOnboarderCard1.setDescriptionTextSize(dpToPixels(8, this));

        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard("Title", "Description");
        ahoyOnboarderCard2.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard2.setTitleColor(R.color.white);
        ahoyOnboarderCard2.setDescriptionColor(R.color.grey_200);
        ahoyOnboarderCard2.setTitleTextSize(dpToPixels(10, this));
        ahoyOnboarderCard2.setDescriptionTextSize(dpToPixels(8, this));

        List<AhoyOnboarderCard> pages = new ArrayList<>();
        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard2);

        setOnboardPages(pages);
    }

    @Override
    public void onFinishButtonPressed() {
        finish();
    }
}
