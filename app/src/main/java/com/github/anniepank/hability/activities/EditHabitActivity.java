package com.github.anniepank.hability.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.anniepank.hability.CustomCalendarView;
import com.github.anniepank.hability.R;
import com.github.anniepank.hability.data.Habit;
import com.github.anniepank.hability.data.Settings;
import com.samsistemas.calendarview.decor.DayDecorator;
import com.samsistemas.calendarview.widget.DayView;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class EditHabitActivity extends AppCompatActivity {
    Habit currentHabit;
    ImageView imageView;
    CustomCalendarView calendarView;
    private FloatingActionButton button;
    private CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_habit);

        button = (FloatingActionButton) findViewById(R.id.editButton);
        calendarView = (CustomCalendarView) findViewById(R.id.calendar_view);
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);
        imageView = (ImageView) findViewById(R.id.backdrop);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        int habit_number = intent.getIntExtra("habit_number", -1);
        currentHabit = Settings.getSettings(this).habits.get(habit_number);
        imageView.setImageDrawable(getResources().getDrawable(Habit.namesAndImages.get(currentHabit.type).image));
        collapsingToolbar.setTitle(currentHabit.habitName);

        DayDecorator dayDecorator = new DayDecorator() {
            @Override
            public void decorate(@NonNull DayView dayView) {
                Date date = dayView.getDate();
                long date_long = date.getTime() / (24 * 60 * 60 * 1000);
                dayView.setBackgroundColor(0xffffffff);
                if (currentHabit.days.contains(date_long))
                    dayView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
        };
        List<DayDecorator> listOfDayDecoratorsForThecalendarViewOfmyProjectInAndroidStudio = new LinkedList<>();
        listOfDayDecoratorsForThecalendarViewOfmyProjectInAndroidStudio.add(dayDecorator);
        calendarView.setDecoratorsList(listOfDayDecoratorsForThecalendarViewOfmyProjectInAndroidStudio);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText nameView = new EditText(EditHabitActivity.this);
                new AlertDialog.Builder(EditHabitActivity.this)
                        .setTitle("Edit name")
                        .setView(nameView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String habitName = nameView.getText().toString();
                                rename(habitName);
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create().show();
                nameView.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });

        calendarView.setOnDayOfMonthClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DayView dayView = (DayView) ((RelativeLayout) view).getChildAt(0);
                long date = dayView.getDate().getTime() / (24 * 60 * 60 * 1000);
                currentHabit.toggleDay(date);
                dayView.decorate();
            }
        });
        calendarView.refreshCalendar(Calendar.getInstance());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            /*
            a = new B().c.d.e
            a = new B()
            .c
            .d
            .e

            B().c

            (new B()).c.d.e
             */
            new AlertDialog.Builder(this)
                    .setTitle("Delete")
                    .setMessage("Do you want to delete your habit?")

                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Settings.getSettings(EditHabitActivity.this).habits.remove(currentHabit);
                            Settings.getSettings(EditHabitActivity.this).save(EditHabitActivity.this);
                            finish();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        }
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    public void rename(String habitName) {
        currentHabit.habitName = habitName;
        Settings.getSettings(this).save(this);
        collapsingToolbar.setTitle(currentHabit.habitName);
    }

}

