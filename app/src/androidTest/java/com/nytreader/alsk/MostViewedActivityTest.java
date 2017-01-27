package com.nytreader.alsk;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.UiThreadTestRule;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.flextrade.jfixture.annotations.Fixture;
import com.flextrade.jfixture.rules.FixtureRule;
import com.nytreader.alsk.ioc.AppComponent;
import com.nytreader.alsk.ioc.AppModule;
import com.nytreader.alsk.ioc.IoC;
import com.nytreader.alsk.rest.MostViewedDataModel;
import com.nytreader.alsk.rest.NytArticlesService;
import com.nytreader.alsk.utils.ThumbLoader;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.List;

import it.cosenonjaviste.daggermock.DaggerMockRule;
import rx.Observable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.nytreader.alsk.articlesList.MostViewedViewModel.COUNT_OF_NEWS_TO_SHOW;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

public class MostViewedActivityTest {

    @Rule
    public FixtureRule fixtureRule = FixtureRule.initFixtures();

    {
        fixtureRule.customise().repeatCount(COUNT_OF_NEWS_TO_SHOW);
    }

    @Rule
    public final ActivityTestRule<MostViewedActivity> activityRule = new ActivityTestRule<>(MostViewedActivity.class, false, false);

    @Rule
    public UiThreadTestRule uiThreadTestRule = new UiThreadTestRule();

    @Rule
    public RxSchedulersOverrideRule rxRule = new RxSchedulersOverrideRule();

    @Spy
    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();

    @Mock
    NytArticlesService nytArticlesService;

    @Mock
    ThumbLoader thumbLoader;

    @Mock
    TimeFormatter timeFormatter;

    @Rule
    public MethodRule mockitoRule = new DaggerMockRule<>(AppComponent.class, new AppModule(context))
            .set(appComponent -> IoC.getInstance().setAppComponent(appComponent));

    @Fixture
    MostViewedDataModel mostViewedDataModel;

    @Before
    public void setUp() throws Exception {
        doAnswer(inv -> inv.getArgument(0)).when(timeFormatter).formatDate(any(String.class));
    }

    @Test
    public void checkListState() {

        doReturn(Observable.just(mostViewedDataModel)).when(nytArticlesService).mostViewed(any());

        activityRule.launchActivity(null);

        onView(withId(R.id.list_of_articles)).check(matches(isDisplayed()));

        List<MostViewedDataModel.Result> results = mostViewedDataModel.getResults();
        for (int i = 0; i < results.size(); i++) {
            final int position = i;
            MostViewedDataModel.Result doc = results.get(i);

            onView(withId(R.id.list_of_articles))
                    .perform(RecyclerViewActions.scrollToPosition(position))
                    .check((view, e) -> {
                        RecyclerView recyclerView = (RecyclerView) view;
                        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForLayoutPosition(position);
                        if (holder == null || holder.itemView == null) {
                            throw (new PerformException.Builder())
                                    .withActionDescription(toString())
                                    .withViewDescription(HumanReadables.describe(view))
                                    .withCause(new IllegalStateException("No view holder at position: " + position))
                                    .build();
                        }

                        List<MostViewedDataModel.Medium> media = doc == null ? null : doc.getMedia();
                        MostViewedDataModel.Medium medium = media == null || media.isEmpty() ? null : media.get(0);
                        List<MostViewedDataModel.MediaMetadatum> mediaMetadata = medium == null ? null : medium.getMediaMetadata();
                        MostViewedDataModel.MediaMetadatum mediaMetadatum = mediaMetadata == null || mediaMetadata.isEmpty() ? null : mediaMetadata.get(0);

                        String expectedHeaderTitle = doc == null ? null : doc.getTitle();
                        String expectedAbstractTitle = doc == null ? null : doc.getSnippet();
                        String expectedImageCaption = medium == null ? null : medium.getCaption();
                        String expectedImageUrl = mediaMetadatum == null ? null : mediaMetadatum.getUrl();
                        String expectedPublishDate = doc == null ? null : doc.getPublishedDate();

                        matches(allOf(withText(expectedHeaderTitle))).check(holder.itemView.findViewById(R.id.tv_title), e);
                        matches(allOf(withText(expectedAbstractTitle))).check(holder.itemView.findViewById(R.id.tv_abstract), e);
                        matches(allOf(withText(expectedPublishDate))).check(holder.itemView.findViewById(R.id.tv_publish_date), e);
                        matches(allOf(withContentDescription(expectedImageCaption))).check(holder.itemView.findViewById(R.id.iv_photo), e);

                        verify(thumbLoader).loadImage(any(ImageView.class), eq(expectedImageUrl));
                    });
        }
    }
}
