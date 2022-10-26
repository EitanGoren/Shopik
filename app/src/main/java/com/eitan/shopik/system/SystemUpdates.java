package com.eitan.shopik.system;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

public class SystemUpdates {

    private static volatile SystemUpdates instance = null;

    private static final int CURRENT_TIME_CHECK_PERIOD = 900000;

    private static ReviewInfo reviewInfo;
    private static ReviewManager manager;
    private Task<ReviewInfo> request;
    private String greeting;

    public static final class InternetConnectionChanged{
        public boolean isConnected = true;
        public InternetConnectionChanged(boolean isConnected){
            this.isConnected = isConnected;
        }
    }
    public static final class GreetingsChangedEvent {
        public String greetings;
        public GreetingsChangedEvent(String greetings){
            this.greetings = greetings;
        }
    }

    private SystemUpdates(){}

    public void Initialize(Context context){
        EventBus.getDefault().register(this);
        requestReview(context);
        publishNewGreeting();
        checkDaytimeChanged();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                reviewInfo = task.getResult();
            }
        });

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        ConnectivityManager connectivityManager = context.getSystemService(ConnectivityManager.class);
        connectivityManager.requestNetwork(networkRequest, networkCallback);
    }

    public static SystemUpdates getInstance() {
        if (instance == null)
            instance = new SystemUpdates();
        return instance;
    }

    //GREETINGS
    private static void publishNewGreeting(){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String greeting;

        if (hour > 12 && hour < 17)
            greeting = "Good afternoon, ";
        else if (hour > 4 && hour <= 12)
            greeting = "Good morning, ";
        else if (hour >= 17 && hour < 22)
            greeting = "Good evening, ";
        else
            greeting = "Good night, ";

        calendar = null;
        EventBus.getDefault().post(new GreetingsChangedEvent(greeting));
    }
    private void checkDaytimeChanged(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                publishNewGreeting();
            }
        },0, CURRENT_TIME_CHECK_PERIOD);
    }
    public String getCurrentGreeting(){
        return greeting;
    }

    //REVIEW APP
    public void launchReview(Activity activity) {
        if (reviewInfo != null) {
            Task<Void> flow = manager.launchReviewFlow(activity, reviewInfo);
            flow.addOnCompleteListener(task -> {});
        }
    }
    private void requestReview(Context context) {
        manager = ReviewManagerFactory.create(context);
        request = manager.requestReviewFlow();
    }

    @Subscribe
    public void onDaytimeChanged(GreetingsChangedEvent event){
        this.greeting = event.greetings;
    }

    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback(){

        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            EventBus.getDefault().post(new InternetConnectionChanged(true));
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            EventBus.getDefault().post(new InternetConnectionChanged(false));
        }
    };
}
