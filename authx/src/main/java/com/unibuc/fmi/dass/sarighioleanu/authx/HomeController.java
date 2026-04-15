package com.unibuc.fmi.dass.sarighioleanu.authx;

import com.unibuc.fmi.dass.sarighioleanu.authx.model.User;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.UserRole;
import com.unibuc.fmi.dass.sarighioleanu.authx.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final CurrentUserProvider currentUserProvider;

    @Autowired
    public HomeController(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Model model
    ) {
        User user = currentUserProvider.getCurrentUser();
        if(user.getRole() == UserRole.MANAGER){
            model.addAttribute("showLogsBtn", true);
        }

        return "dashboard";
    }
}
