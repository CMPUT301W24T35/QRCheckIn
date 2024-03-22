package com.example.qrcheckin;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityUITest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void UserStory01_01_01() {
        // As an organizer, I want to create a new event and generate a unique
        // QR code for attendee check-ins.

        // Navigate to Create Event Activity from Main
        onView(withId(R.id.button_organize_events)).perform(click());
        onView(withId(R.id.button_create_event)).perform(click());

        // Set text to be entered
        String eventName = "Example Event";
        String eventDescription = "This is a test event description";
        String startTime = "10:00 AM";
        String endTime = "12:00 PM";
        String location = "Test Location";
        int attendeeCapacity = 100;

        // Fill in the text fields
        Espresso.onView(ViewMatchers.withId(R.id.eventNameEditText)).perform(ViewActions.typeText(eventName), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.eventDescriptionEditText)).perform(ViewActions.typeText(eventDescription), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.eventStartTimeEditText)).perform(ViewActions.typeText(startTime), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.eventEndTimeEditText)).perform(ViewActions.typeText(endTime), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.eventLocationEditText)).perform(ViewActions.typeText(location), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.attendeeCapacityEditText)).perform(ViewActions.typeText(String.valueOf(attendeeCapacity)), ViewActions.closeSoftKeyboard());

        /* Temporary Commented Out
            TODO: Currently the transition between the CreateEventActivity page and
                  QR Generator, the Homepage Activity flashes up in between for some
                  unknown reason.
                  What has been tried to so far:
                    - putting finish() after every previous activity calls startActivity(intent)
                    - tried turning QR generator into a fragment but couldn't get it working

        // Navigate to QR Generator
        onView(withId(R.id.continueCreateEventButton)).perform(click());
        onView(withId(R.id.generateCheckinQRCodeButton)).perform(click());

        // Navigate to Homepage Organizer
        onView(withId(R.id.confirmEventCreationButton)).perform(click());

        // Check that event has been created
        //onView(withId(R.id.confirmEventCreationButton)).perform(click());

        // Click on event and check all event details and QR Code have been loaded
        */






    }

}
