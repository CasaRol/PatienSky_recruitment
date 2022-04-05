package com.patientsky.recruitment.rol.domain;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class TimeSlotType {

    //Assignment focused attributes
    UUID id;
    String name;
    int slotSize;

    public TimeSlotType(UUID id, String name, int slotSize) {
        this.id = id;
        this.name = name;
        this.slotSize = slotSize;
    }
}
