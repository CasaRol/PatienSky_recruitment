package com.patientsky.recruitment.rol.schedules;

import com.patientsky.recruitment.rol.domain.Appointment;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static com.patientsky.recruitment.rol.Utilities.sdf;

@Component
public class CreateSchedulesOnStartUp {

    public void populateProvidedSchedules() {

        loadJoanna();

    }

    private void loadJoanna() {

        Resource resource = new ClassPathResource("providedFiles/JoannaHef.json");
        try {
            JSONParser parser = new JSONParser();
            FileReader reader = new FileReader(resource.getFile());

            JSONObject joannaObj = (JSONObject) parser.parse(reader);

            JSONArray appointmentsArray = (JSONArray) joannaObj.get("appointments");
            ArrayList<Appointment> appointments = new ArrayList();

            if(appointmentsArray != null) {
                for(int i = 0; i < appointmentsArray.size(); i++) {
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

            //System.out.println("Joanna appointments = " + joannaObj.get("appointments"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
    }
}
