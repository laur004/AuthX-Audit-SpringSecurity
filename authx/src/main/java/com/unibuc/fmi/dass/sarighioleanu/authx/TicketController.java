package com.unibuc.fmi.dass.sarighioleanu.authx;

import com.unibuc.fmi.dass.sarighioleanu.authx.auth.UserService;
import com.unibuc.fmi.dass.sarighioleanu.authx.dto.TicketRequest;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Controller
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;
    private final AuditLogService auditLogService;
    private final CurrentUserProvider currentUserProvider;


    @Autowired
    public TicketController(
            TicketService ticketService,
            UserService userService,
            AuditLogService auditLogService,
            CurrentUserProvider currentUserProvider
    ) {
        this.ticketService = ticketService;
        this.userService = userService;
        this.auditLogService = auditLogService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/tickets")
    public String ticketsPage(
            @RequestParam(required = false) String query,
            Model model
    ) {

        User owner = currentUserProvider.getCurrentUser();


        List<Ticket> tickets;

        if (query != null && !query.trim().isEmpty()) {
            tickets = ticketService.searchTickets(query.trim());
        } else {
            tickets = ticketService.getTickets();
        }

        if(owner.getRole() == UserRole.MANAGER) {
            model.addAttribute("showOwner", true);
        }else{
            tickets = tickets.stream().filter(t -> Objects.equals(t.getOwner().getId(), owner.getId())).toList();
        }

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
            @ModelAttribute("ticket") TicketRequest ticket,
            HttpServletRequest request
    ){
        Ticket t = new Ticket();
        t.setTitle(ticket.getTitle());
        t.setDescription(ticket.getDescription());
        t.setSeverityLevel(ticket.getSeverityLevel());
        t.setStatus(TicketStatus.OPEN);

        OffsetDateTime date = OffsetDateTime.now();

        t.setCreatedAt(date);
        t.setUpdatedAt(date);

        User owner = currentUserProvider.getCurrentUser();
        t.setOwner(owner);

        Ticket savedTicket = ticketService.save(t);

        auditLogService.logTicket(
                AuditAction.CREATE_TICKET,
                AuditStatus.SUCCESS,
                AuditResource.TICKET,
                savedTicket.getId().toString(),
                request.getRemoteAddr()
        );

        return  "redirect:/tickets";
    }


    @GetMapping("/edit-ticket/{ticketId}")
    public String editTicketPage(
            @PathVariable Long ticketId,
            Model model
    ) {
        Ticket ticket = getTicketAndCheckOwnership(ticketId);

        model.addAttribute("ticket", ticket);
        model.addAttribute("severityLevels", TicketSeverityLevel.values());
        model.addAttribute("statusList", TicketStatus.values());

        return  "edit-ticket";
    }

    @PostMapping("/edit-ticket")
    public String editTicket(
            @ModelAttribute("ticket") Ticket formTicket,
            HttpServletRequest request
    ){
        try{

            Ticket existingTicket = getTicketAndCheckOwnership(formTicket.getId());

            existingTicket.setTitle(formTicket.getTitle());
            existingTicket.setDescription(formTicket.getDescription());
            existingTicket.setSeverityLevel(formTicket.getSeverityLevel());
            existingTicket.setStatus(formTicket.getStatus());
            existingTicket.setUpdatedAt(OffsetDateTime.now());

            Ticket updatedTicket = ticketService.save(existingTicket);

            auditLogService.logTicket(
                    AuditAction.EDIT_TICKET,
                    AuditStatus.SUCCESS,
                    AuditResource.TICKET,
                    updatedTicket.getId().toString(),
                    request.getRemoteAddr()
            );

        } catch (ResponseStatusException e){
            auditLogService.logTicket(
                    AuditAction.EDIT_TICKET,
                    AuditStatus.FORBIDDEN,
                    AuditResource.TICKET,
                    formTicket.getId().toString(),
                    request.getRemoteAddr()
            );
            throw e;
        }

        return "redirect:/tickets";
    }

    @PostMapping("/delete-ticket/{ticketId}")
    public String deleteTicket(
            @PathVariable Long ticketId,
            HttpServletRequest request
    ){
        try{
            Ticket ticket = getTicketAndCheckOwnership(ticketId);
            ticketService.deleteTicketById(ticket.getId());

            auditLogService.logTicket(
                    AuditAction.DELETE_TICKET,
                    AuditStatus.SUCCESS,
                    AuditResource.TICKET,
                    ticketId.toString(),
                    request.getRemoteAddr()
            );
        } catch (ResponseStatusException e){
            auditLogService.logTicket(
                    AuditAction.DELETE_TICKET,
                    AuditStatus.FORBIDDEN,
                    AuditResource.TICKET,
                    ticketId.toString(),
                    request.getRemoteAddr()
            );
            throw e;
        }

        return "redirect:/tickets";
    }

    private Ticket getTicketAndCheckOwnership(@PathVariable Long ticketId) {
        User  owner = currentUserProvider.getCurrentUser();
        if(owner.getRole() == UserRole.MANAGER) {
            return ticketService.getTicketById(ticketId);
        }
        return ticketService.getTicketForOwner(ticketId, owner.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"));
    }

}
