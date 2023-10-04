package com.ticketbooking.repo;

import com.ticketbooking.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface TripRepo extends JpaRepository<Trip, Long> {

    @Query(value = """
        select * from trip t 
        where t.source_id=:sourceId and t.dest_id=:destinationId 
        and t.departure_date_time BETWEEN :startDate AND concat(:endDate, ' 23:59') 
        and t.departure_date_time > current_timestamp()
        """, nativeQuery = true)
    List<Trip> findAllBySourceIdAndDestinationId(
            @Param("sourceId") Long sourceId,
            @Param("destinationId") Long destinationId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
            select trip from Trip trip 
            where trip.driver.id=:driverId and trip.coach.id=:coachId 
            and trip.source.id=:sourceId and trip.destination.id=:destinationId
            and trip.departureDateTime=:departureDateTime
            """)
    List<Trip> findDuplicateDepartureTimeTrip(
            @Param("driverId") Long driverId,
            @Param("coachId") Long coachId,
            @Param("sourceId") Long sourceId,
            @Param("destinationId") Long destinationId,
            @Param("departureDateTime") LocalDateTime departureDateTime);

    @Query("""
            select trip from Trip trip 
            where trip.id <> :tripId
            and trip.driver.id=:driverId and trip.coach.id=:coachId 
            and trip.source.id=:sourceId and trip.destination.id=:destinationId
            and trip.departureDateTime=:departureDateTime
            """)
    List<Trip> findOtherDuplicateDepartureTimeTrip(
            @Param("tripId") Long tripId,
            @Param("driverId") Long driverId,
            @Param("coachId") Long coachId,
            @Param("sourceId") Long sourceId,
            @Param("destinationId") Long destinationId,
            @Param("departureDateTime") LocalDateTime departureDateTime);
}
