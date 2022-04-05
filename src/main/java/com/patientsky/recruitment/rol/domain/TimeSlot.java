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

}
