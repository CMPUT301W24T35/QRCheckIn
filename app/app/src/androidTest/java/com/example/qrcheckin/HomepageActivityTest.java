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
public class HomepageActivityTest {
    @Rule
    public ActivityScenarioRule<HomepageActivity> scenario = new
            ActivityScenarioRule<HomepageActivity>(HomepageActivity.class);


    @Test
    public void Openingtest() {

        onView(withId(R.id.button_notification)).check(matches(isDisplayed()));

    }
    @Test
    public void SignedUpEventButtonTest(){
        onView(withId(R.id.button_signed_up_events)).perform(click());
        onView(withId(R.id.button_check_in)).check(matches(isDisplayed()));
    }
@Test
    public void ProfileButtonTest(){
        onView(withId(R.id.profile_image_button)).perform(click());
        onView(withId(R.id.button_back)).check(matches(isDisplayed()));
    }

    @Test
    public void NotificationButtonTest(){
        onView(withId(R.id.button_notification)).perform(click());
        onView(withId(R.id.backArrow)).check(matches(isDisplayed()));
    }


    @Test
    public void OrganizeEventsButtonTest(){
        onView(withId(R.id.button_organize_events)).perform(click());
        onView(withId(R.id.button_create_event)).check(matches(isDisplayed()));
    }


}