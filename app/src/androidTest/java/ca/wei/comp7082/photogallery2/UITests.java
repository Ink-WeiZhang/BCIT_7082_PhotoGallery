package ca.wei.comp7082.photogallery2;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class UITests {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void TestFilter() {
        onView(withId(R.id.btnSearch)).perform(click());
        onView(withId(R.id.date1)).perform(typeText("31/01/18"), closeSoftKeyboard());
        onView(withId(R.id.date2)).perform(typeText("01/01/18"), closeSoftKeyboard());
        onView(withId(R.id.btnFilter)).perform(click());
        for (int i = 0; i <= 5; i++) {
            onView(withId(R.id.btnNext)).perform(click());
        }
    }
}
