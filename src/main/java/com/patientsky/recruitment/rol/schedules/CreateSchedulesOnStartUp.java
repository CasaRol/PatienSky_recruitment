package com.patientsky.recruitment.rol.schedules;

import com.patientsky.recruitment.rol.domain.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.patientsky.recruitment.rol.Utilities.sdf;

@Component
public class CreateSchedulesOnStartUp {

    public List<Owner> populateProvidedSchedules() {

        Owner joanna = loadOwner("Joanna", "Joanna Hef.json");
        Owner emma = loadOwner("Emma", "Emma Win.json");
        Owner danny = loadOwner("Danny", "Danny boy.json");

        List<Owner> owners = new ArrayList<>();

        owners.add(joanna);
        owners.add(emma);
        owners.add(danny);

        return owners;
    }

    private Owner loadOwner(String name, String fileName) {
        Resource resource = new ClassPathResource("providedFiles/" + fileName);

        ArrayList<Appointment> appointments = new ArrayList<>();
        ArrayList<TimeSlot> timeslots = new ArrayList<>();
        ArrayList<TimeSlotType> timeSlotTypes = new ArrayList<>();

        try {
            JSONParser parser = new JSONParser();
            FileReader reader = new FileReader(resource.getFile());

            JSONObject ownerObj = (JSONObject) parser.parse(reader);

            //Loading in Appointments to java Object
            JSONArray appointmentsArray = (JSONArray) ownerObj.get("appointments");
            if (appointmentsArray != null) {
                for (int i = 0; i < appointmentsArray.size(); i++) {
                    JSONObject appointmentsObj = (JSONObject) appointmentsArray.get(i);

                    appointments.add(new Appointment(
                            UUID.fromString((String) appointmentsObj.get("id")),
                            UUID.fromString((String) appointmentsObj.get("patient_id")),
                            UUID.fromString((String) appointmentsObj.get("calendar_id")),
                            sdf.parse(appointmentsObj.get("start").toString()),
                            sdf.parse(appointmentsObj.get("end").toString())
                    ));

                }
            }

            //Loading in Timeslots to java Object
            JSONArray timeSlotArray = (JSONArray) ownerObj.get("timeslots");
            if (timeSlotArray != null) {
                for (int i = 0; i < timeSlotArray.size(); i++) {
                    JSONObject timeSlotObj = (JSONObject) timeSlotArray.get(i);

                    timeslots.add(new TimeSlot(
                                    UUID.fromString((String) timeSlotObj.get("id")),
                                    UUID.fromString((String) timeSlotObj.get("calendar_id")),
                                    sdf.parse(timeSlotObj.get("start").toString()),
                                    sdf.parse(timeSlotObj.get("end").toString())
                            )
                    );
                }
            }

            //Loading in TimeslotTypes to java Object
            JSONArray timeSlotTypeArray = (JSONArray) ownerObj.get("timeslottypes");
            if (timeSlotTypeArray != null) {
                for (int i = 0; i < timeSlotTypeArray.size(); i++) {
                    JSONObject timeSlotTypeObj = (JSONObject) timeSlotTypeArray.get(i);

                    timeSlotTypes.add(new TimeSlotType(
                                    UUID.fromString((String) timeSlotTypeObj.get("id")),
                                    (String) timeSlotTypeObj.get("name"),
                                    Integer.parseInt(timeSlotTypeObj.get("slot_size").toString())
                            )
                    );
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return new Owner(name, appointments, timeslots, timeSlotTypes);
    }

}
