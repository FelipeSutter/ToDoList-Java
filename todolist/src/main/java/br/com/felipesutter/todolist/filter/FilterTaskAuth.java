package br.com.felipesutter.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.felipesutter.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component // é a notation mais genérica, indica que o spring vai gerenciar essa classe
// filtro de http
public class FilterTaskAuth extends OncePerRequestFilter{

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        // pegar usuário e senha
        var authorization = request.getHeader("Authorization: ");


        // esse método vai extrair tudo após o basic e o trim vai remover os espaços
        var authEncoded = authorization.substring("Basic".length()).trim();

        // transforma o base64 em um array de bytes
        byte[] authDecode = Base64.getDecoder().decode(authEncoded);

        // transforma os bytes em string
        var authString = new String(authDecode);

        // quando ele faz esse split, divide o array em n posições, já que só tem somente um :
        // ele vai dividir em duas posições, antes do dois pontos(username) e após os dois pontos(password)

        String[] credentials = authString.split(":");

        String username = credentials[0];
        String password = credentials[1];

        // validar usuario
        var user = this.userRepository.findByUsername(username);
        if(user == null) {
            response.sendError(401);
        } else {
            // validar senha
            // compara a senha que eu tenho com a senha do user, tem que transformar em array neste método
            var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

            // se a senha for correta, segue viagem, senão dá error 401
            if(passwordVerify.verified == true) {
                // segue viagem
                filterChain.doFilter(request, response);
            } else {
                response.sendError(401);
            }
        }

    }

    
}
