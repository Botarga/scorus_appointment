package com.projects.bordarga.scorusappointment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Button calendarBt;
    Button pendantBt;
    private boolean firstLoad = true;
    public static ConcurrentHashMap<MyDay, List<CustomerTask>> allTasks = new ConcurrentHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarBt = (Button)findViewById(R.id.calendar_button);
        pendantBt = (Button)findViewById(R.id.pendant_button);

        pendantBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        calendarBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Cargando tareas...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(firstLoad) {
                    firstLoad = false;
                    try {
                        Calendar dayStart = Calendar.getInstance();
                        Calendar dayEnd = Calendar.getInstance();
                        dayEnd.add(Calendar.MONTH, 3);

                        // Init tasks
                        while (dayStart.getTimeInMillis() < dayEnd.getTimeInMillis()) {
                            int day = dayStart.get(Calendar.DAY_OF_MONTH);
                            int month = dayStart.get(Calendar.MONTH) + 1;
                            int year = dayStart.get(Calendar.YEAR);

                            List<CustomerTask>  auxListTask =  new ArrayList<>();
                            for(int i = 0; i < 26; ++i)
                                auxListTask.add(new CustomerTask(null, true, false, false));

                            allTasks.put(
                                    new MyDay(day, month, year),
                                    auxListTask
                            );
                            dayStart.add(Calendar.DAY_OF_MONTH, 1);
                        }

                    } catch (Exception ex) { }

                    // Update busy from database
                    new  GetWebService(progressDialog, getApplicationContext(),
                        GetWebService.ConnectionType.DOWNLOAD_BUSY_HOURS, false).execute("http://www.codedojo.es/testBernat/webServices/DownloadBusyHours.php");


                }
                // Update from database

            }
        }).start();

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                new  GetWebService(null, getApplicationContext(),
                        GetWebService.ConnectionType.DOWNLOAD_APPOINTMENTS, false)
                    .execute("http://www.codedojo.es/testBernat/webServices/GetAppointments.php");

            }
        }, 0, 15, TimeUnit.SECONDS);
    }
}
