package com.eitan.shopik;


// --Commented out by Inspection START (16-Sep-20 15:50):
//@LargeTest
//@RunWith(AndroidJUnit4.class)
//public class StandartTest {
//
//    @Rule
//    public ActivityTestRule<LandingPageActivity> mActivityTestRule = new ActivityTestRule<>(LandingPageActivity.class);
//
//    private static Matcher<View> childAtPosition(
//            final Matcher<View> parentMatcher, final int position) {
//
//        return new TypeSafeMatcher<View>() {
//            @Override
//            public void describeTo(Description description) {
//                description.appendText("Child at position " + position + " in parent ");
//                parentMatcher.describeTo(description);
//            }
//
//            @Override
//            public boolean matchesSafely(View view) {
//                ViewParent parent = view.getParent();
//                return parent instanceof ViewGroup && parentMatcher.matches(parent)
//                        && view.equals(((ViewGroup) parent).getChildAt(position));
//            }
//        };
//    }
//
//    @Test
//    public void standartTest() {
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        try {
//            Thread.sleep(7000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ViewInteraction materialButton = onView(
//                allOf(withId(R.id.facebook_sign_in), withText("Signin with Facebook"),
//                        childAtPosition(
//                                allOf(withId(R.id.facebook_card),
//                                        childAtPosition(
//                                                withClassName(is("android.widget.LinearLayout")),
//                                                1)),
//                                0),
//                        isDisplayed()));
//        materialButton.perform(click());
//
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        try {
//            Thread.sleep(7000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ViewInteraction appCompatImageView = onView(
//                allOf(withId(R.id.sub_category_image), withContentDescription("sub_category_image"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(is("androidx.cardview.widget.CardView")),
//                                        0),
//                                0),
//                        isDisplayed()));
//        appCompatImageView.perform(click());
//
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        try {
//            Thread.sleep(7000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ViewInteraction mT = onView(
//                allOf(withContentDescription("Close Ad"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(is("com.facebook.ads.redexgen.X$83")),
//                                        1),
//                                0),
//                        isDisplayed()));
//        mT.perform(click());
//
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        try {
//            Thread.sleep(7000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ViewInteraction bottomNavigationItemView = onView(
//                allOf(withId(R.id.bottom_favorites), withContentDescription("Favorites"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.bottom_nav),
//                                        0),
//                                1),
//                        isDisplayed()));
//        bottomNavigationItemView.perform(click());
//
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ViewInteraction bottomNavigationItemView2 = onView(
//                allOf(withId(R.id.bottom_search), withContentDescription("All items"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.bottom_nav),
//                                        0),
//                                2),
//                        isDisplayed()));
//        bottomNavigationItemView2.perform(click());
//
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ViewInteraction bottomNavigationItemView3 = onView(
//                allOf(withId(R.id.bottom_swipe), withContentDescription("Swipe"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.bottom_nav),
//                                        0),
//                                0),
//                        isDisplayed()));
//        bottomNavigationItemView3.perform(click());
//
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        pressBack();
//
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        try {
//            Thread.sleep(7000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ViewInteraction appCompatImageView2 = onView(
//                allOf(withId(R.id.sub_category_image), withContentDescription("sub_category_image"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(is("androidx.cardview.widget.CardView")),
//                                        0),
//                                0),
//                        isDisplayed()));
//        appCompatImageView2.perform(click());
//
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        try {
//            Thread.sleep(7000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ViewInteraction mT2 = onView(
//                allOf(withContentDescription("Close Ad"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(is("com.facebook.ads.redexgen.X$85")),
//                                        1),
//                                0),
//                        isDisplayed()));
//        mT2.perform(click());
//
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ViewInteraction mT3 = onView(
//                allOf(withContentDescription("Close Ad"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(is("com.facebook.ads.redexgen.X$85")),
//                                        1),
//                                0),
//                        isDisplayed()));
//        mT3.perform(click());
//    }
//}
// --Commented out by Inspection STOP (16-Sep-20 15:50)
