package br.com.felipesutter.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserRepository repository;

    @PostMapping
    public ResponseEntity create(@RequestBody UserModel userModel) {

        UserModel user = this.repository.findByUsername(userModel.getUsername());
        if (user != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Este usuário já existe");
        }

        // encriptografa a senha e depois transforma ela em char pois o paramêtro do
        // hashToString pede char
        var passwdHashred = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());

        // depois só setta essa nova senha
        userModel.setPassword(passwdHashred);

        UserModel userCreated = this.repository.save(userModel);
        // return ResponseEntity.status(HttpStatus.CREATED).body("userCreated");
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);

    }

}
