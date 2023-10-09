package br.com.felipesutter.todolist.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserModel {

    private String username;
    private String name;
    private String password;
}
