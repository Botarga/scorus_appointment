package com.projects.bordarga.scorusappointment;

/**
 * Created by botarga on 19/03/2018.
 */

public class CustomerTask {
    private String name;
    private boolean free;
    private boolean pendant;
    private boolean appointment;

    public CustomerTask(String name, boolean free, boolean pendant, boolean appointment) {
        this.name = name;
        this.free = free;
        this.pendant = pendant;
        this.appointment = appointment;
    }


    public String getName() {
        return name;
    }

    public boolean isAppointment() {
        return appointment;
    }

    public void setAppointment(boolean appointment) {
        this.appointment = appointment;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void set(String name, boolean free, boolean pendant, boolean appointment){
        this.name = name;
        this.free = free;
        this.pendant = pendant;
        this.appointment = appointment;
    }

    public boolean isFree() {
        return free;
    }

    public boolean isPendant() {
        return pendant;
    }

    public void setPendant(boolean pendant) {
        this.pendant = pendant;
    }

    public void setFree(boolean free) {
        this.free = free;
    }
}
