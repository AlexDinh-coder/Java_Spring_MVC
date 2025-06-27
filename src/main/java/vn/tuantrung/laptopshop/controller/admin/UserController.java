package vn.tuantrung.laptopshop.controller.admin;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import vn.tuantrung.laptopshop.domain.User;
import vn.tuantrung.laptopshop.services.UploadService;
import vn.tuantrung.laptopshop.services.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    // //DI: Dependency Injection
    private final UserService userService;
    private final UploadService uploadService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UploadService uploadService,
            UserService userService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.uploadService = uploadService;
        this.passwordEncoder = passwordEncoder;

    }

    @RequestMapping("/")
    public String getHomePage(Model model) {
        List<User> arrUsers = this.userService.getAllUserByEmail("1@gmail.com");
        System.out.println(arrUsers);
        model.addAttribute("trung", "test");
        model.addAttribute("tuan", "dep trai ko");
        return "hello";
    }

    @RequestMapping("/admin/user")
    public String getUserPage(Model model) {
        List<User> users = this.userService.getAllUsers();
        model.addAttribute("users1", users);
        return "admin/user/show";
    }

    @RequestMapping("/admin/user/{id}")
    public String getUserDetailPage(Model model, @PathVariable long id) {
        User user = this.userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("id", id);
        return "admin/user/detail";
    }

    @GetMapping("/admin/user/create") // GET
    public String getCreateUserPage(Model model) {
        model.addAttribute("newUser", new User());
        return "admin/user/create";
    }

    @RequestMapping("/admin/user/update/{id}") // GET
    public String getUpdateUserPage(Model model, @PathVariable long id) {
        User currentUser = this.userService.getUserById(id);
        model.addAttribute("newUser", currentUser);
        return "admin/user/update";
    }

    @PostMapping("/admin/user/update")
    public String postUpdatedUser(Model model, @ModelAttribute("newUser") User tuantrung) {
        User currentUser = this.userService.getUserById(tuantrung.getId());
        if (currentUser != null) {
            currentUser.setAddress(tuantrung.getAddress());
            currentUser.setFullName(tuantrung.getFullName());
            currentUser.setPhone(tuantrung.getPhone());
            this.userService.handleSaveUser(currentUser);
        }
        return "redirect:/admin/user";
    }

    @PostMapping("/admin/user/create")
    public String createUserPage(Model model,
            @ModelAttribute("newUser") @Valid User tuantrung,
            BindingResult newUserBindingResult,
            @RequestParam("trungFile") MultipartFile file
            ) {
        // List<FieldError> errors = newUserBindingResult.getFieldErrors();
        // for (FieldError error : errors) {
        //     System.out.println(">>>>>" + error.getField() + " - " + error.getDefaultMessage());
        // }

        // validate
        if (newUserBindingResult.hasErrors()) {
            return "admin/user/create";
        }
        //

        String avatar = this.uploadService.handleSaveUploadFile(file, "avatar");
        String hasPassword = this.passwordEncoder.encode(tuantrung.getPassword());

        tuantrung.setAvatar(avatar);
        tuantrung.setPassword(hasPassword);
        tuantrung.setRole(this.userService.getRoleByName(tuantrung.getRole().getName()));
        // save
        this.userService.handleSaveUser(tuantrung);
        return "redirect:/admin/user";
    }

    @GetMapping("/admin/user/delete/{id}")
    public String getDeleteUserPage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        model.addAttribute("newUser", new User());
        return "admin/user/delete";
    }

    @PostMapping("/admin/user/delete")
    public String postDeleteUser(Model model, @ModelAttribute("newUser") User tuantrung) {
        this.userService.deleteUser(tuantrung.getId());
        return "redirect:/admin/user";
    }

}
