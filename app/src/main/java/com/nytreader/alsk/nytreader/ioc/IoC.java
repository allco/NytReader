package com.nytreader.alsk.nytreader.ioc;

import android.app.Application;
import android.support.annotation.NonNull;

public enum IoC {
    INSTANCE;

    public static IoC getInstance() {
        return INSTANCE;
    }

    private AppComponent appComponent;

    @NonNull
    public AppComponent getAppComponent() {
        return appComponent;
    }

    public void setAppComponent(@NonNull AppComponent appComponent) {
        this.appComponent = appComponent;
    }

    public void initDependencyGraph(Application application) {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(application))
                .build();

        //noinspection ConstantConditions
        if (appComponent == null) {
            throw new IllegalStateException("DI initialization failed");
        }
    }
}
