package com.picpaysimplificado.domain.services;

import com.picpaysimplificado.domain.exceptions.UserNotFoundException;
import com.picpaysimplificado.domain.exceptions.UserWithoutBalanceException;
import com.picpaysimplificado.domain.exceptions.UserWithoutPermissionException;
import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.UserDTO;
import com.picpaysimplificado.domain.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void validateTransaction(User sender, BigDecimal amount){
        if(sender.getUserType() == UserType.MERCHANT){
            throw new UserWithoutPermissionException("Usuário do tipo Logista não está autorizado a realizar transação");
        }
        if(sender.getBalance().compareTo(amount) < 0){
            throw new UserWithoutBalanceException("Usuário sem saldo insuficiente");
        }
    }

    public User findUserById(Long id) {
        return this.userRepository.findUserById(id).orElseThrow(
                () -> new UserNotFoundException("Usuário não encontrado")
        );
    }

    public void saveUser(User user){
         this.userRepository.save(user);

    }

    public User createUser(UserDTO user) {
        User newUser = new User(user);
        this.userRepository.save(newUser);
        return newUser;
    }

    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }
}
