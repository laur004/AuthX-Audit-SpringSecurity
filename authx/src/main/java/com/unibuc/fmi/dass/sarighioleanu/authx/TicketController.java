package com.unibuc.fmi.dass.sarighioleanu.authx;

import com.unibuc.fmi.dass.sarighioleanu.authx.auth.UserService;
import com.unibuc.fmi.dass.sarighioleanu.authx.dto.TicketRequest;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.Ticket;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.TicketSeverityLevel;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.TicketStatus;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Controller
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;

    @Autowired
    public TicketController(TicketService ticketService, UserService userService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @GetMapping("/tickets")
    public String ticketsPage(
            @RequestParam(required = false) String query,
            Model model
    ) {

        String ownerEmail = "";

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDetails) {
            ownerEmail = ((UserDetails)principal).getUsername();
        } else{
            ownerEmail = principal.toString();
        }
        User owner = userService.loadUserByEmail(ownerEmail);


        List<Ticket> tickets;

        if (query != null && !query.trim().isEmpty()) {
            tickets = ticketService.searchTickets(query.trim());
        } else {
            tickets = ticketService.getTickets();
        }

        tickets = tickets.stream().filter(t -> Objects.equals(t.getOwner().getId(), owner.getId())).toList();

        model.addAttribute("tickets", tickets);
        model.addAttribute("query", query);

        return "tickets";
    }

    @GetMapping("/create-ticket")
    public String createTicketPage(
            @ModelAttribute("ticket") TicketRequest ticket,
            Model model
    ) {
        model.addAttribute("severityLevels", TicketSeverityLevel.values());

        return "create-ticket";
    }

    @PostMapping("/create-ticket")
    public String createTicket(
            @ModelAttribute("ticket") TicketRequest ticket
    ){
        Ticket t = new Ticket();
        t.setTitle(ticket.getTitle());
        t.setDescription(ticket.getDescription());
        t.setSeverityLevel(ticket.getSeverityLevel());
        t.setStatus(TicketStatus.OPEN);

        OffsetDateTime date = OffsetDateTime.now();

        t.setCreatedAt(date);
        t.setUpdatedAt(date);

        String ownerEmail = "";

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDetails) {
            ownerEmail = ((UserDetails)principal).getUsername();
        } else{
            ownerEmail = principal.toString();
        }
        User owner = userService.loadUserByEmail(ownerEmail);
        t.setOwner(owner);
        ticketService.save(t);

        return  "redirect:/tickets";
    }


    @GetMapping("/edit-ticket/{ticketId}")
    public String editTicketPage(
            @PathVariable Long ticketId,
            Model model
    ) {
        Ticket ticket = ticketService.getTicketById(ticketId);
        model.addAttribute("ticket", ticket);
        model.addAttribute("severityLevels", TicketSeverityLevel.values());
        model.addAttribute("statusList", TicketStatus.values());

        return  "edit-ticket";
    }

    @PostMapping("/edit-ticket")
    public String editTicket(
            @ModelAttribute("ticket") Ticket formTicket
    ){
        Ticket existingTicket = ticketService.getTicketById(formTicket.getId());

        existingTicket.setTitle(formTicket.getTitle());
        existingTicket.setDescription(formTicket.getDescription());
        existingTicket.setSeverityLevel(formTicket.getSeverityLevel());
        existingTicket.setStatus(formTicket.getStatus());
        existingTicket.setUpdatedAt(OffsetDateTime.now());

        ticketService.save(existingTicket);

        return "redirect:/tickets";
    }

    @PostMapping("/delete-ticket/{ticketId}")
    public String deleteTicket(
            @PathVariable Long ticketId
    ){
        ticketService.deleteTicketById(ticketId);

        return "redirect:/tickets";
    }

}
