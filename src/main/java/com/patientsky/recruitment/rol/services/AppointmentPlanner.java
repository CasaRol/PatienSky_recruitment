package com.patientsky.recruitment.rol.services;

import com.patientsky.recruitment.rol.domain.Appointment;
import com.patientsky.recruitment.rol.domain.Interval;
import com.patientsky.recruitment.rol.domain.Owner;
import com.patientsky.recruitment.rol.schedules.CreateSchedulesOnStartUp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.patientsky.recruitment.rol.Utilities.sdf;

@Service
public class AppointmentPlanner {

    @Autowired
    CreateSchedulesOnStartUp createSchedulesOnStartUp = new CreateSchedulesOnStartUp(); //TODO: Fix BeanCreationException

    private List<Owner> owners = createSchedulesOnStartUp.populateProvidedSchedules();


    public void findAvailableTime(List<UUID> calendarIds, int duration, String periodToSearch) {


        //Splitting timeInterval into start and end
        Map<String, String> intervals = startAndEndTimeInterval(periodToSearch);

        Date startOfInterval = new Date();
        Date endOfInterval = new Date();

        try {
            startOfInterval = sdf.parse(intervals.get("start"));
            endOfInterval = sdf.parse(intervals.get("end"));
        } catch (ParseException p) {
            p.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Owner owner1 = null;

        for (Owner owner : owners) {
            if(owner.getAppointments().get(0).getCalendarId().equals(calendarIds.get(0))) {
                owner1 = owner;
            }
        }

        List<Interval> tmpList = calcOwnerFreeTimeInSchedule(owner1);
        for(Interval interval: tmpList) {
            System.out.println(interval);
        }


    }

    //Splits out the timeInterval provided from REST into Start and End
    private Map<String, String> startAndEndTimeInterval(String periodToSearch) {
        Map<String, String> startAndEnd = new HashMap<>();

        String[] intervalTimes = periodToSearch.split("/");

        startAndEnd.put("start", intervalTimes[0]); //Start of interval to search
        startAndEnd.put("end", intervalTimes[1]); //End of interval to search

        return startAndEnd;
    }

    private List<Interval> calcOwnerFreeTimeInSchedule(Owner owner) {
        List<Interval> freeSlots = new ArrayList<>();

        for(int i = 0; i < 10; i++) {
            Interval freetime = new Interval();

            //Setting start and end time to open spot in schedule
            freetime.setStart(owner.getAppointments().get(i).getEnd());
            freetime.setEnd(owner.getAppointments().get(i+1).getStart());

            //Calculating interval duration
            long duration = TimeUnit.MILLISECONDS.toMinutes(freetime.getEnd().getTime() - freetime.getStart().getTime());

            int durationInMinutes = Math.toIntExact(duration);

            //Setting interval to open spot in minutes
            freetime.setIntervalMinutes(durationInMinutes);

            freeSlots.add(freetime);
        }
        return freeSlots;
    }

}
