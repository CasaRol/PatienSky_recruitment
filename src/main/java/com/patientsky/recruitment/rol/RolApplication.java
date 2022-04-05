package com.patientsky.recruitment.rol;

import com.patientsky.recruitment.rol.schedules.CreateSchedulesOnStartUp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RolApplication {

	public static void main(String[] args) {
		SpringApplication.run(RolApplication.class, args);

		//Loads in the 3 provided files with Danny, Emma and Joanna
		CreateSchedulesOnStartUp createSchedulesOnStartUp = new CreateSchedulesOnStartUp();
		createSchedulesOnStartUp.populateProvidedSchedules();
	}

}
