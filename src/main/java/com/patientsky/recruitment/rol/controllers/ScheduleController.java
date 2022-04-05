package com.patientsky.recruitment.rol.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ScheduleController {

    @GetMapping("/findAvailableTime")
    public ResponseEntity<List> findAvailableTime() {
        ArrayList<String> tmpList = new ArrayList();
        tmpList.add("index 0");
        tmpList.add("index 1");
        tmpList.add("index 2");
        return new ResponseEntity(tmpList, HttpStatus.OK);
    }
}
