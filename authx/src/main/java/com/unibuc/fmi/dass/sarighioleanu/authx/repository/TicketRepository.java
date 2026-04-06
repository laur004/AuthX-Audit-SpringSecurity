package com.unibuc.fmi.dass.sarighioleanu.authx.repository;

import com.unibuc.fmi.dass.sarighioleanu.authx.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title,
            String description
    );
}
