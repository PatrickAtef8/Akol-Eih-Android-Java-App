/*
--- File: app/src/main/java/com/example/akoleih/splash/presenter/SplashPresenter.java ---
*/
package com.example.akoleih.splash.presenter;

public interface SplashPresenter {
    void setView(com.example.akoleih.splash.view.SplashView view);
    void checkUserSession();
    void onDestroy();
}