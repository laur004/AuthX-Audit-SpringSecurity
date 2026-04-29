package com.unibuc.fmi.dass.sarighioleanu.authx.repository;

import com.unibuc.fmi.dass.sarighioleanu.authx.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title,
            String description
    );
    Optional<Ticket> findByIdAndOwnerId(Long ticketId, Long ownerId);
}
