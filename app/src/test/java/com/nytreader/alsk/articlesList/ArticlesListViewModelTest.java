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

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;

import static com.nytreader.alsk.rest.NytArticlesService.HTTP_TOO_MANY_REQUESTS;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
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
        onErrorHelper(HTTP_TOO_MANY_REQUESTS, "" + R.string.error_message_too_many_requests);
    }

    @Test
    public void onErrorForbidden403() throws Exception {
        onErrorHelper(HTTP_FORBIDDEN, "" + R.string.error_message_forbidden);
    }

    @Test
    public void onErrorDefault() throws Exception {
        onErrorHelper(HTTP_INTERNAL_ERROR, R.string.error_message_generic + "\n(code: " + HTTP_INTERNAL_ERROR + ")");
    }

    private void onErrorHelper(int httpCode, String expectedString) {
        doNothing().when(model).setBannerMessage(any(String.class));
        Response response = Response.error(httpCode, ResponseBody.create(MediaType.parse("*/*"), "content"));
        model.onError(new HttpException(response));
        verify(model).setBannerMessage(expectedString);
    }
}