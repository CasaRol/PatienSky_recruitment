package com.patientsky.recruitment.rol.controllers;

import com.patientsky.recruitment.rol.domain.Interval;
import com.patientsky.recruitment.rol.domain.Request;
import com.patientsky.recruitment.rol.schedules.CreateSchedulesOnStartUp;
import com.patientsky.recruitment.rol.services.AppointmentPlanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ScheduleController {

    @Autowired
    AppointmentPlanner appointmentPlanner;

    @GetMapping("/findAvailableTime")
    public ResponseEntity<List<Interval>> findAvailableTime(@RequestBody Request request) {
        System.out.println("Call was attempted");
        List<UUID> idList = new ArrayList<>();
        for(String id : request.getCalendarIds()) {
            idList.add(UUID.fromString(id));
        }

        System.out.println(request.getCalendarIds());
        System.out.println(request.getDuration());
        System.out.println(request.getInterval());

        List<Interval> possibleTimes = appointmentPlanner.findAvailableTime(idList, request.getDuration(), request.getInterval());

        return new ResponseEntity(possibleTimes, HttpStatus.OK);
    }

}
