package com.patientsky.recruitment.rol.services;

import com.patientsky.recruitment.rol.domain.Interval;
import com.patientsky.recruitment.rol.domain.Owner;
import com.patientsky.recruitment.rol.schedules.CreateSchedulesOnStartUp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.patientsky.recruitment.rol.Utilities.sdf;

@Service
public class AppointmentPlanner {

    @Autowired
    CreateSchedulesOnStartUp createSchedulesOnStartUp = new CreateSchedulesOnStartUp(); //TODO: Fix BeanCreationException

    private final List<Owner> owners = createSchedulesOnStartUp.populateProvidedSchedules();


    public void findAvailableTime(List<UUID> calendarIds, int duration, String periodToSearch) {

        //Splitting timeInterval into start and end
        Map<String, String> intervals = startAndEndTimeInterval(periodToSearch);

        //Interval for Needed free time lookup
        Date startOfInterval = new Date();
        Date endOfInterval = new Date();

        try {
            startOfInterval = sdf.parse(intervals.get("start"));
            endOfInterval = sdf.parse(intervals.get("end"));
        } catch (Exception p) {
            p.printStackTrace();
        }

        List<Owner> affectedOwners = new ArrayList<>();

        //Getting the owner objects for each calendarId given as parameter
        for (Owner owner : owners) {
            for (UUID calendarId : calendarIds) {
                if (owner.getAppointments().get(0).getCalendarId().equals(calendarId)) {
                    affectedOwners.add(owner); //isolating only owners who is a part of this scheduling
                }
            }
        }

        List<List<Interval>> ownerFreeTime = new ArrayList<>();

        //Adding each owners free time to the map
        for (Owner owner : affectedOwners) {
            List<Interval> tmpList = calcOwnerFreeTimeInSchedule(owner, duration, startOfInterval, endOfInterval);
            for(Interval interval : tmpList) {
                System.out.println(owner.getName() + ": " + interval);
            }
            ownerFreeTime.add(tmpList);

        }

        List<Interval> foundMatches = findCommonAvailableTimes(ownerFreeTime, duration);


    }

    private List<Interval> findCommonAvailableTimes(List<List<Interval>> possibleIntervals, int duration) {
        System.out.println("Looking for matches");
        List<Interval> matchList = possibleIntervals.get(0); //Instantiating first index as possible solutions as a starting point

        if (possibleIntervals.size() > 1) { //Early return in only one calenderID is provided

            for (int i = 1; i < possibleIntervals.size(); i++) { //Skipping first Owner as it has already been set as a complete match

                //Creating a temporary List in which to enter new proposed times due to overlap (ConcurrentModificationException handling)
                List<Interval> updatedOptions = new ArrayList<>();

                for (Interval ownerFreeTimeOptions : possibleIntervals.get(i)) {
                    for (Interval currentMatch : matchList) {

                        //ownerFreeTimeOptions is within match
                        if ((ownerFreeTimeOptions.getStart().equals(currentMatch.getStart()) ||
                                ownerFreeTimeOptions.getStart().toInstant().isAfter(currentMatch.getStart().toInstant())) &&
                                (ownerFreeTimeOptions.getEnd().equals(currentMatch.getEnd()) ||
                                        ownerFreeTimeOptions.getEnd().toInstant().isBefore(currentMatch.getEnd().toInstant()))) {

                            //Match only fits within currentMatch... replacing intervals
                            updatedOptions.add(ownerFreeTimeOptions);
                        }
                        //ownerFreeTime completely overlaps currentMatch
                        else if ((ownerFreeTimeOptions.getStart().equals(currentMatch.getStart()) ||
                                ownerFreeTimeOptions.getStart().toInstant().isBefore(currentMatch.getStart().toInstant())) &&
                                (ownerFreeTimeOptions.getEnd().equals(currentMatch.getEnd()) ||
                                        ownerFreeTimeOptions.getEnd().toInstant().isAfter(currentMatch.getEnd().toInstant()))) {
                            //Positive match - Keeping currentMatch and moving on to the next possibility
                            updatedOptions.add(currentMatch);
                        }
                        //Checking overlap of first half
                        else if (ownerFreeTimeOptions.getStart().equals(currentMatch.getStart()) ||
                                ownerFreeTimeOptions.getStart().toInstant().isBefore(currentMatch.getStart().toInstant()) &&
                                        ownerFreeTimeOptions.getEnd().toInstant().isBefore(currentMatch.getEnd().toInstant())) {
                            //Checking if overlap allows sufficient time
                            if (calcTimeBetweenTwoPoints(ownerFreeTimeOptions.getStart(), currentMatch.getEnd()) < duration) {
                                //Not a good match - skip adding for next iteration
                                continue;
                            } else {
                                //Creating compromised time due to overlap
                                Interval newTime = new Interval();
                                newTime.setStart(currentMatch.getStart());
                                newTime.setEnd(ownerFreeTimeOptions.getEnd());
                                newTime.setIntervalMinutes(calcTimeBetweenTwoPoints(newTime.getStart(), newTime.getEnd()));

                                //Adding to the list for next iteration
                                updatedOptions.add(newTime);
                            }

                        }
                        //Checking overlap of second half
                        else if (ownerFreeTimeOptions.getStart().toInstant().isAfter(currentMatch.getStart().toInstant()) &&
                                (ownerFreeTimeOptions.getEnd().equals(currentMatch.getEnd()) ||
                                        ownerFreeTimeOptions.getEnd().toInstant().isAfter(currentMatch.getEnd().toInstant()))) {
                            //Checking if overlap allows sufficient time
                            if (calcTimeBetweenTwoPoints(currentMatch.getStart(), ownerFreeTimeOptions.getStart()) < duration) {
                                //Not a good match - Removing from matches
                                continue;
                            } else {
                                //Creating compromised time due to overlap
                                Interval newTime = new Interval();
                                newTime.setStart(ownerFreeTimeOptions.getStart());
                                newTime.setEnd(currentMatch.getEnd());
                                newTime.setIntervalMinutes(calcTimeBetweenTwoPoints(newTime.getStart(), newTime.getEnd()));

                                //Adding to list for next iteration
                                updatedOptions.add(newTime);
                            }
                        }
                    }

                }
                //Setting matchList to the new proposed times
                matchList = updatedOptions;
            }

            matchList.removeIf(interval -> interval.getIntervalMinutes() <= 0);

            for (Interval matches : matchList) {
                System.out.println("Common time: " + matches);
            }

        }
        return matchList;
    }

    //Splits out the timeInterval provided from REST into Start and End
    private Map<String, String> startAndEndTimeInterval(String periodToSearch) {
        Map<String, String> startAndEnd = new HashMap<>();

        String[] intervalTimes = periodToSearch.split("/");

        startAndEnd.put("start", intervalTimes[0]); //Start of interval to search
        startAndEnd.put("end", intervalTimes[1]); //End of interval to search

        return startAndEnd;
    }

    private List<Interval> calcOwnerFreeTimeInSchedule(Owner owner, int duration, Date startInterval, Date endInterval) {
        List<Interval> freeSlots = new ArrayList<>();

        for (int i = 0; i < owner.getAppointments().size() - 1; i++) {
            Interval freetime = new Interval();

            //Setting start and end time to open spot in schedule
            Date start = owner.getAppointments().get(i).getStart();
            Date end = owner.getAppointments().get(i).getEnd();

            //Evaluating if open space between appointments are withing given interval
            if (owner.getAppointments().get(i).getEnd().toInstant().isAfter(startInterval.toInstant()) &&
                    (owner.getAppointments().get(i + 1).getStart().toInstant().isBefore(endInterval.toInstant()) ||
                            owner.getAppointments().get(i).getEnd().equals(endInterval))) {

                //Setting start and end of freeTime
                freetime.setStart(owner.getAppointments().get(i).getEnd());
                freetime.setEnd(owner.getAppointments().get(i + 1).getStart());

                //Making sure free time is NOT outside specified start and end intervals of the search
                if(freetime.getStart().toInstant().isBefore(startInterval.toInstant())) {
                    freetime.setStart(startInterval);
                } else if(freetime.getEnd().toInstant().isAfter(endInterval.toInstant())) {
                    freetime.setEnd(endInterval);
                }

                //Calculating time between start and end
                int durationInMinutes = calcTimeBetweenTwoPoints(freetime.getStart(), freetime.getEnd());

                //Setting interval to open spot in minutes
                freetime.setIntervalMinutes(durationInMinutes);

                //only adding freeTime if it fits within the freeTimeDuration of needed appointment
                if (freetime.getIntervalMinutes() >= duration) {
                    freeSlots.add(freetime);
                }
            }

        }

        return freeSlots;
    }

    private int calcTimeBetweenTwoPoints(Date start, Date end) {
        //Calculating interval freeTimeDuration
        long freeTimeDuration = TimeUnit.MILLISECONDS.toMinutes(end.getTime() - start.getTime());

        return Math.toIntExact(freeTimeDuration);
    }

}
