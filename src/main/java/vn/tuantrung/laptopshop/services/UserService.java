package vn.tuantrung.laptopshop.services;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.tuantrung.laptopshop.domain.Role;
import vn.tuantrung.laptopshop.domain.User;
import vn.tuantrung.laptopshop.domain.dto.RegisterDTO;
import vn.tuantrung.laptopshop.repository.RoleRepository;
import vn.tuantrung.laptopshop.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public String handleHello(){
        return "Hello from services";
    }

    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    public List<User> getAllUserByEmail(String email) {
        return this.userRepository.findOneByEmail(email);
    }
    public User handleSaveUser(User user) {
        User alex = this.userRepository.save(user);
        System.out.println(alex);
        return alex;
       
    }
    public User getUserById(long id) {
        return this.userRepository.findById(id);
    }

    public void deleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public Role getRoleByName(String name){
        return this.roleRepository.findByName(name);
    }

    public User registerDTOtoUser(RegisterDTO registerDTO){
        User user = new User();
        user.setFullName(registerDTO.getFirstName() + " " + registerDTO.getLastName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        return user;

    }

    public boolean checkEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }
}
