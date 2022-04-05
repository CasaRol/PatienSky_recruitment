package com.patientsky.recruitment.rol.domain;

import java.util.List;

public class Owner {

    String name;
    List<Appointment> appointments;
    List<TimeSlot> timeSlots;
    List<TimeSlotType> timeSlotTypes;

    public Owner(String name, List<Appointment> appointments, List<TimeSlot> timeSlots, List<TimeSlotType> timeSlotTypes) {
        this.name = name;
        this.appointments = appointments;
        this.timeSlots = timeSlots;
        this.timeSlotTypes = timeSlotTypes;
    }
}
