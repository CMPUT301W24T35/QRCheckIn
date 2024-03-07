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
public class HomepageOrganizerTest {
    @Rule
    public ActivityScenarioRule<HomepageOrganizer> scenario = new
            ActivityScenarioRule<HomepageOrganizer>(HomepageOrganizer.class);


    @Test
    public void Openingtest() {

        onView(withId(R.id.button_create_event)).check(matches(isDisplayed()));

    }
    @Test
    public void CreateEventButtonTest(){
        onView(withId(R.id.button_create_event)).perform(click());
        onView(withId(R.id.editPosterImageButton)).check(matches(isDisplayed()));
    }
    @Test
    public void BackButtonTest(){
        onView(withId(R.id.button_back_events)).perform(click());
        onView(withId(R.id.button_organize_events)).check(matches(isDisplayed()));
    }

}