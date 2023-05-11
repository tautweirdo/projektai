package dev.taut.websitedemo.user;

import dev.taut.websitedemo.Accounts.Account;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class TransactionController {

    @Autowired
    private UserService service;

    private Set<String> usedIds = new HashSet<>();

    @GetMapping("/check")
    public String showUserList(Model model) {
        List<User> listUsers = service.listAll();
        model.addAttribute("listUsers", listUsers);
        return "check";
    }

    @GetMapping("/transactions/new")
    public String showNewForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("pageTitle", "Add New User");
        return "transaction_form";
    }

    @PostMapping("/transactions/save")
    public String saveUser(User user, RedirectAttributes ra) {
        service.save(user);
        usedIds.add(String.valueOf(user.getId()));
        ra.addFlashAttribute("message", "Sekmingai pridėtas");
        return "redirect:/transactions";
    }

    @GetMapping("/transactions/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            User user = service.get(id);
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Keisti (ID: " + id + ")");
            return "transaction_form";
        } catch (UserNotFoundException e) {
            ra.addFlashAttribute("message", "Išsaugotas");
            return "redirect:/transactions";
        }
    }

    @GetMapping("/transactions/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            service.delete(id);
            usedIds.remove(String.valueOf(id));
            ra.addFlashAttribute("message", "Pervedimas su ID " + id + " buvo ištrinta");

        } catch (UserNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/transactions";
    }

    @GetMapping("/login")
    public String displayLoginForm() {
        return "/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, Model model, HttpSession session) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/transactiondb", "root", "root");
            String sql = "SELECT * FROM account WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // account found, log user in
                Account account = new Account();
                account.setId(rs.getInt("id"));
                account.setUsername(((ResultSet) rs).getString("username"));
                account.setPassword(rs.getString("password"));

                return "redirect:/transactions";
            } else {
                // account not found, display error message
                model.addAttribute("message", "Netinkamas slapyvardis arba slaptažodis.");
                return "login";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("message", "Įvyko netikėta klaida bandant prisijungti.");
            return "login";
        }

    }


    @PostMapping("/registration")
    public String registerNewAccount(@RequestParam("username") String username,
                                     @RequestParam("password") String password,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        // create a new account object
        Account newAccount = new Account(0, username, password);

        // add the account to the database
        try {
            Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/transactiondb", "root", "root");
            String sql = "INSERT INTO account (username, password) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newAccount.getUsername());
            stmt.setString(2, newAccount.getPassword());
            int rows = stmt.executeUpdate();

            // check if the account was successfully added
            if (rows == 1) {
                // account was added successfully
                redirectAttributes.addFlashAttribute("message", "Paskyra " + newAccount.getUsername() + " buvo sukurta.");
            } else {
                // account was not added successfully
                redirectAttributes.addFlashAttribute("message", "Paskyros sukurti nepavyko");
            }

            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Įvyko klaida bandant sukurti paskyrą");
        }

        // redirect the user to the "check" view
        return "redirect:/check";
    }
}
