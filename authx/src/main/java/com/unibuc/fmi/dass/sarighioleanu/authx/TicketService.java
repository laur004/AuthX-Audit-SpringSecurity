package com.unibuc.fmi.dass.sarighioleanu.authx;

import com.unibuc.fmi.dass.sarighioleanu.authx.model.Ticket;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.User;
import com.unibuc.fmi.dass.sarighioleanu.authx.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> getTickets() {
        return ticketRepository.findAll();
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Ticket with id " + id + " does not exist")
        );
    }

    public List<Ticket> searchTickets(String query) {
        return ticketRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }

    public Optional<Ticket> getTicketForOwner(Long ticketId, Long ownerId) {
        return ticketRepository.findByIdAndOwnerId(ticketId, ownerId);
    }

    public void deleteTicketById(Long id) {
        ticketRepository.deleteById(id);
    }

    public void save(Ticket ticket) {
        ticketRepository.save(ticket);
    }

}
