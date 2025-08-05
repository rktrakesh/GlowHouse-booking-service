package com.glowhouse.repository;

import com.glowhouse.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository <Booking, Long> {

    List<Booking> findByCustomerId (Long customerId);

    List<Booking> findBySalonId (Long salonId);

}
