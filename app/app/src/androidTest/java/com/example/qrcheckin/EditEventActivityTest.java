package com.example.qrcheckin;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EditEventActivityTest {
    @Rule
    public ActivityScenarioRule<EditEventActivity> scenario = new
            ActivityScenarioRule<EditEventActivity>(EditEventActivity.class);


    @Test
    public void OpenTest() {

        onView(withId(R.id.editPosterImageButton)).check(matches(isDisplayed()));

    }

    /* Moved to Part 4 (not halfway checkpoint anymore)
    @Test
    public void ConfirmButtonTest(){
        onView(withId(R.id.continueCreateEventButton)).perform(click());
        onView(withId(R.id.reuseCheckinQRCodeButton)).check(matches(isDisplayed()));
    }

     */

}