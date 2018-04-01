package com.projects.bordarga.scorusappointment;


import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DayTaskActivity extends AppCompatActivity implements View.OnClickListener{

    TableLayout dayTable;
    Button saveTaskBt;
    List<CustomerTask> taskList;

    private String[] hoursTable = {
            "8:00", "8:30", "9:00", "9:30", "10:00", "10:30", "11:00",
        "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30",
        "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00",
        "20:30"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_task);

        dayTable = findViewById(R.id.dayTable);
        saveTaskBt = findViewById(R.id.save_task_button);

        int day = getIntent().getIntExtra("day", 1);
        int month = getIntent().getIntExtra("month", 1) + 1;
        int year = getIntent().getIntExtra("year", 1);
        final MyDay auxMyDay = new MyDay(day, month, year);

        final List<CustomerTask> auxList = MainActivity.allTasks.get(auxMyDay);
        taskList = new ArrayList<>();
        for(CustomerTask c : auxList)
            taskList.add(new CustomerTask("none", c.isFree(), c.isPendant(), c.isAppointment()));
        initTable();


        saveTaskBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do changes
                // Save locally
                MainActivity.allTasks.put(auxMyDay, taskList);

                final ProgressDialog progress = ProgressDialog.show(DayTaskActivity.this,
                        "Guardando", "Guardando tareas", true, false);

                // Save on database
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String data = "";
                        for(int i = 0; i < taskList.size(); ++i){
                            if(!taskList.get(i).isFree()){
                                data += auxMyDay.year + "-" + String.format("%02d", auxMyDay.month) + "-" +
                                    String.format("%02d", auxMyDay.day) + " " + hoursTable[i] + ":00" + ";";
                            }
                        }

                        if(data.length() > 0)
                            data = data.substring(0, data.length() - 2);
                        if(data.length() == 0)
                            data = "clear;" + auxMyDay.year + "-" + String.format("%02d", auxMyDay.month) +
                                    "-" + String.format("%02d", auxMyDay.day);

                        String url = "http://www.codedojo.es/testBernat/webServices/UploadFreeHours.php?" +
                                "data=" + data;
                        new GetWebService(progress, getApplicationContext(), GetWebService.ConnectionType.UPDATE_FREE_HOURS, false)
                            .execute(url);


                        // Return

                        /*Intent intent = new Intent(DayTaskActivity.this, CalendarActivity.class);
                        startActivity(intent);
                        finish();*/


                    }

                }).start();


            }
        });
    }

    private void initTable(){
        int hour = 8, min = 0;
        int count = 0;
        for(CustomerTask c : taskList){
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT
                    , TableRow.LayoutParams.WRAP_CONTENT));
            tr.setMinimumHeight(120);


            /* Create a Button to be the row-content. */
            TextView b = new TextView(this);

            b.setText("" + String.format("%02d", hour) + ":" + String.format("%02d", min));
            b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            b.setBackgroundColor((int)R.color.colorPrimary);

            // Task textview
            TextView tv = new TextView(this);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            tv.setTag(count++);
            tv.setOnClickListener(this);

            // libre; ocupado; cita; cita pendiente
            if(!c.isFree()){
                if(c.isPendant()){
                    tv.setText(c.getName());
                    tv.setBackgroundResource(R.color.task_pendant);

                }else if(c.getName() != null) {
                    tv.setText(c.getName());
                    tv.setBackgroundResource(R.color.task_payed);
                }else{
                    tv.setBackgroundResource(R.color.task_busy);
                }
            }else
                tv.setBackgroundResource(R.color.task_free);

            // Add to row
            tr.addView(b);
            tr.addView(tv);
            /* Add row to TableLayout. */

            dayTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            if(min == 30){
                min = 0;
                hour++;
            }else
                min = 30;
        }
    }


    @Override
    public void onClick(View v) {
        TextView tv = (TextView)v;
        CustomerTask singleTask = taskList.get((int)v.getTag());
        if(singleTask.isFree()){
            singleTask.setFree(false);
            tv.setBackgroundResource(R.color.task_busy);
        }else if(singleTask.isPendant()){
            // aprobar
        }else if(singleTask.getName() == null){ // ocupado
            singleTask.setFree(true);
            tv.setBackgroundResource(R.color.task_free);
        }

    }
}
