package com.patientsky.recruitment.rol.domain;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class TimeSlot {

    //Assignment focused attributes
    UUID id;
    UUID calendarID;
    Date start;
    Date end;

    public TimeSlot(UUID id, UUID calendarID, Date start, Date end) {
        this.id = id;
        this.calendarID = calendarID;
        this.start = start;
        this.end = end;
    }
}
