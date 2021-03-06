package com.example.android.newskart;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {

    private static final String TAG = "JobSchedulerService";

    private final String GLOBAL_URL= "https://newsapi.org/v2/top-headlines?country=in&apiKey=061596553c8c44aa85d0c724d3246163";
    private final String SPORTS_URL="https://newsapi.org/v2/top-headlines?country=in&category=sports&apiKey=061596553c8c44aa85d0c724d3246163";
    private final String BUSINESS_URL="https://newsapi.org/v2/top-headlines?country=in&category=business&apiKey=061596553c8c44aa85d0c724d3246163";
    private final String HEALTH_URL="https://newsapi.org/v2/top-headlines?country=in&category=health&apiKey=061596553c8c44aa85d0c724d3246163";
    private final String ENTERTAINMENT_URL="https://newsapi.org/v2/top-headlines?country=in&category=entertainment&apiKey=061596553c8c44aa85d0c724d3246163";
    private final String GAMING_URL="https://newsapi.org/v2/everything?q=gaming&apiKey=061596553c8c44aa85d0c724d3246163";
    private final String TECH_URL="https://newsapi.org/v2/top-headlines?country=in&category=technology&apiKey=061596553c8c44aa85d0c724d3246163";


    private static final String TABLE_GLOBAL_NEWS = "news";
    private static final String TABLE_SPORTS_NEWS = "sports";
    private static final String TABLE_BUSNINESS_NEWS = "business";
    private static final String TABLE_HEALTH_NEWS = "health";
    private static final String TABLE_FUN_NEWS = "fun";
    private static final String TABLE_GAMING_NEWS = "gaming";
    private static final String TABLE_TECH_NEWS = "tech";

    private ArrayList<String> urlList;
    private ArrayList<String> tableList;
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        urlList=new ArrayList<String>();urlList.add(GLOBAL_URL);
        tableList=new ArrayList<>();
        tableList.add(TABLE_GLOBAL_NEWS);tableList.add(TABLE_SPORTS_NEWS);tableList.add(TABLE_BUSNINESS_NEWS);
        tableList.add(TABLE_HEALTH_NEWS);tableList.add(TABLE_FUN_NEWS);tableList.add(TABLE_GAMING_NEWS);tableList.add(TABLE_TECH_NEWS);
        urlList.add(SPORTS_URL);urlList.add(BUSINESS_URL);urlList.add(HEALTH_URL);
        urlList.add(ENTERTAINMENT_URL);urlList.add(GAMING_URL);urlList.add(TECH_URL);
        doBackgroundWork(params);
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                    if (jobCancelled) {
                        return;
                    }
                Log.d(TAG, "run: called in thread");
                    NewsQueryUtils newsQueryUtils=new NewsQueryUtils(JobSchedulerService.this);
                    newsQueryUtils.fetchAllCategoryNewData(urlList, tableList);
                Log.d(TAG, "Job finished");
                jobFinished(params, false);

                JobUtility.scheduleNewsJob(getApplicationContext());
            }
        }).start();

    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        // this is the best place to call asynctask.cancel().
        //because we need to stop the background work ourselves.
        jobCancelled = true;
        return true;
    }


}
