package com.patientsky.recruitment.rol.controllers;

import com.patientsky.recruitment.rol.schedules.CreateSchedulesOnStartUp;
import com.patientsky.recruitment.rol.services.AppointmentPlanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<List> findAvailableTime() {
        List<UUID> testList = new ArrayList<>();
        testList.add(UUID.fromString("48cadf26-975e-11e5-b9c2-c8e0eb18c1e9"));
        testList.add(UUID.fromString("48644c7a-975e-11e5-a090-c8e0eb18c1e9"));
        testList.add(UUID.fromString("452dccfc-975e-11e5-bfa5-c8e0eb18c1e9"));
        appointmentPlanner.findAvailableTime(testList, 15, "2019-04-23T10:00:00/2019-04-23T18:45:00");


        //List<Owner> owners = createSchedulesOnStartUp.populateProvidedSchedules();
        return new ResponseEntity(null, HttpStatus.OK);
    }
}
