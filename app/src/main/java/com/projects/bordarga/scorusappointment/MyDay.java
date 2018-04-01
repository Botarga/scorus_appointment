package com.projects.bordarga.scorusappointment;

import java.util.Objects;

/**
 * Created by botarga on 20/03/2018.
 */

public class MyDay {
    public int day;
    public int month;
    public int year;

    public MyDay(int day, int month, int year){
        this.day     = day;
        this.month = month;
        this.year    = year;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + day;
        result = 31 * result + month;
        result = 31 * result + year;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof MyDay)) {
            return false;
        }
        MyDay day2 = (MyDay) o;
        return day == day2.day &&
                month == day2.month &&
                year == day2.year;
    }
}
