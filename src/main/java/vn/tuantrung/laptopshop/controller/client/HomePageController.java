package vn.tuantrung.laptopshop.controller.client;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import vn.tuantrung.laptopshop.domain.Product;
import vn.tuantrung.laptopshop.domain.User;
import vn.tuantrung.laptopshop.domain.dto.RegisterDTO;
import vn.tuantrung.laptopshop.services.ProductService;
import vn.tuantrung.laptopshop.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;






@Controller
public class HomePageController {
    private final ProductService productService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public HomePageController(ProductService productService,
     UserService userService,
     PasswordEncoder passwordEncoder) {
        this.productService = productService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("/")
    public String getHomePage(Model model) {
        List<Product> products = this.productService.getProducts();
        model.addAttribute("products", products);
    
        
        return "client/homepage/show";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("registerUser", new RegisterDTO());
        return "client/auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(
        @ModelAttribute("registerUser") @Valid RegisterDTO registerDTO,
        BindingResult  bindingResult
        ) {
        // validate
        if (bindingResult.hasErrors()) {
            return  "client/auth/register";
        }
        User user = this.userService.registerDTOtoUser(registerDTO);

        String hasPassword = this.passwordEncoder.encode(user.getPassword());

        user.setPassword(hasPassword);
        user.setRole(this.userService.getRoleByName("USER"));
        // save
        this.userService.handleSaveUser(user);
        return "redirect:/login";
        
    }

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        return "client/auth/login";
    }
    
    @GetMapping("/access-deny")
    public String getDenyPage(Model model) {
        return "client/auth/deny";
    }
    
    
}
