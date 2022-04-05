package com.patientsky.recruitment.rol.domain;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Data //Lombok: Includes getters and setters automatically
public class Appointment {

    //Assignment focused attributes
    UUID id; //Appointment ID
    UUID patientId;
    UUID calendarId; //Calendar ID
    Date start; //Appointment start - ISO-8601 format
    Date end; //Appointment end - ISO-8601 format

    public Appointment(UUID id, UUID patientId, UUID calendarId, Date start, Date end) {
        this.id = id;
        this.patientId = patientId;
        this.calendarId = calendarId;
        this.start = start;
        this.end = end;
    }

    //static DateFormat df = new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ssZ")
}
