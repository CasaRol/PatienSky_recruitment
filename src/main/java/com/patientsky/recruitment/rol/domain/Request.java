package com.patientsky.recruitment.rol.domain;

import lombok.Data;

import java.util.List;

@Data
public class Request {

    List<String> calendarIds;
    int duration;
    String interval;

}
