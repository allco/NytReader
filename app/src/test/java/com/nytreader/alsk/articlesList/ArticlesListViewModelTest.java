package com.nytreader.alsk.articlesList;

import android.content.Context;
import android.support.annotation.NonNull;

import com.flextrade.jfixture.annotations.Fixture;
import com.flextrade.jfixture.rules.FixtureRule;
import com.nytreader.alsk.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;

import static com.nytreader.alsk.rest.NytArticlesService.HTTP_TOO_MANY_REQUESTS;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class ArticlesListViewModelTest {

    @Rule
    public FixtureRule fixtureRule = FixtureRule.initFixtures();

    @Mock
    Context context;

    @Mock
    ArticlesListDataSource dataSource;

    @Mock
    Subscription subscription;

    @Fixture
    Integer stringResourceId;

    private DummyArticlesListViewModel model;

    class DummyArticlesListViewModel extends ArticlesListViewModel {
        DummyArticlesListViewModel(@NonNull Context context, @NonNull ArticlesListDataSource dataSource) {
            super(context, dataSource);
        }

        @Override
        protected Subscription tryLoadNextPage() {
            return subscription;
        }
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        model = spy(new DummyArticlesListViewModel(context, dataSource));
        Whitebox.setInternalState(model, "articlesList", spy(model.articlesList));
        Whitebox.setInternalState(model, "errorMessage", spy(model.errorMessage));
        doAnswer(inv -> inv.getArguments()[0].toString()).when(context).getString(any(int.class));
    }

    @Test
    public void tearDownWithSubscription() throws Exception {
        Whitebox.setInternalState(model, "subscriptionCall", subscription);
        model.tearDown();
        verify(subscription).unsubscribe();
        assertNull(Whitebox.getInternalState(model, "subscriptionCall"));
    }

    @Test
    public void tearDownWithoutSubscription() throws Exception {
        model.tearDown();
        verifyNoMoreInteractions(subscription);
        assertNull(Whitebox.getInternalState(model, "subscriptionCall"));
    }

    @Test
    public void setBannerMessage() throws Exception {
        model.setBannerMessage(stringResourceId);
        assertEquals(model.errorMessage.get(), stringResourceId.toString());
        verify(model.articlesList).clear();
    }

    @Test
    public void reload() throws Exception {
        model.reload();
        verify(model.articlesList).clear();
        verify(model.errorMessage).set(null);
        verify(model).tryLoadNextPage();
        assertEquals(model.subscriptionCall, subscription);
    }

    @Test
    public void onErrorTooManyRequests429() throws Exception {
        doNothing().when(model).setBannerMessage(any(int.class));
        HttpException httpException = mock(HttpException.class);
        doReturn(HTTP_TOO_MANY_REQUESTS).when(httpException).code();
        model.onError(httpException);
        verify(model).setBannerMessage(R.string.error_message_too_many_requests);
    }

    @Test
    public void onErrorForbidden403() throws Exception {
        doNothing().when(model).setBannerMessage(any(int.class));
        HttpException httpException = mock(HttpException.class);
        doReturn(HTTP_TOO_MANY_REQUESTS).when(httpException).code();
        model.onError(httpException);
        verify(model).setBannerMessage(R.string.error_message_forbidden);
    }

    @Test
    public void onErrorDefault() throws Exception {
        doNothing().when(model).setBannerMessage(any(String.class));
        HttpException httpException = mock(HttpException.class);
        doReturn(HTTP_INTERNAL_ERROR).when(httpException).code();
        model.onError(httpException);
        verify(model).setBannerMessage(any(String.class));
    }
}