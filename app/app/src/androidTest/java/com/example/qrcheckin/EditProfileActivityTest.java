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
public class EditProfileActivityTest {
    @Rule
    public ActivityScenarioRule<EditProfileActivity> scenario = new
            ActivityScenarioRule<EditProfileActivity>(EditProfileActivity.class);


    @Test
    public void Openingtest() {

        onView(withId(R.id.edituserEmailText)).check(matches(isDisplayed()));

    }
    @Test
    public void Cancelbuttontest(){
        onView(withId(R.id.contCancelProfileButton)).perform(click());
        onView(withId(R.id.button_back)).check(matches(isDisplayed()));
    }

}