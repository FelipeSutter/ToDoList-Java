package br.com.felipesutter.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private IUserRepository repository; 

    @PostMapping
    public ResponseEntity create(@RequestBody UserModel userModel) {
        UserModel user = this.repository.findByUsername(userModel.getUsername());
        if(user != null) {
            //return new ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Este usuário já existe");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Este usuário já existe");
        }
        //return new ResponseEntity<UserModel>(this.repository.save(userModel), HttpStatus.CREATED);
        UserModel userCreated = this.repository.save(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);

    }

}
