package com.patientsky.recruitment.rol.domain;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Data //Lombok: Includes getters and setters automatically
public class Appointment {

    //Assignment focused attributes
    UUID id; //Appointment ID
    UUID patientId; //Patient ID
    UUID calendarId; //Calendar ID
    Date start; //Appointment start
    Date end; //Appointment end

    public Appointment(UUID id, UUID patientId, UUID calendarId, Date start, Date end) {
        this.id = id;
        this.patientId = patientId;
        this.calendarId = calendarId;
        this.start = start;
        this.end = end;
    }
}
