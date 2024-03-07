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
public class CreateProfileActivityTest {
    @Rule
    public ActivityScenarioRule<CreateProfileActivity> scenario = new
            ActivityScenarioRule<CreateProfileActivity>(CreateProfileActivity.class);


    @Test
    public void opentest() {

        onView(withId(R.id.profileImage)).check(matches(isDisplayed()));

    }

}