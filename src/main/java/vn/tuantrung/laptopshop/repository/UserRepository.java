package vn.tuantrung.laptopshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.tuantrung.laptopshop.domain.User;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository <User, Long> {
    User save(User alex);

    void deleteById(long id);
    List<User> findOneByEmail(String email);

    List<User> findAll();

    User findById(long id);

}
