package br.com.felipesutter.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.felipesutter.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository repository;

    @PostMapping
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        // aqui o request pega o id que foi passado no filter e atribui a uma variável
        var idUser = request.getAttribute("idUser");

        // depois coloca esse idUser no model, pois no JSON foi retirado o campo de id e
        // desse jeito o id passa automaticamente
        taskModel.setIdUser((UUID) idUser);

        // validação de data que já passou, pois caso tente colocar uma task numa data
        // inválida ele deixa
        // data atual
        var currentDate = LocalDateTime.now();

        // se a data atual for maior que a data da task, lança um erro
        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
            // return new ResponseEntity<>("A data de início deve ser maior do que a data
            // atual", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data de início / data de término deve ser maior do que a data atual");
        }

        // se a data de inicio for maior q a data de término
        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            // return new ResponseEntity<>("A data de início deve ser maior do que a data
            // atual", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data de início deve ser menor que a data de término");
        }

        var task = this.repository.save(taskModel);
        // return new ResponseEntity<>("Tudo Certo!",HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    // buscar tasks que pertencem somente aquele idUser
    @GetMapping
    public List<TaskModel> list(HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        var tasks = this.repository.findByIdUser((UUID) idUser);
        return tasks;
    }

    // atualiza uma task baseado no id da task e do user que tem essa task
    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request) {
        var task = this.repository.findById(id).orElse(null);

        // se a tarefa n existir
        if (task == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Tarefa não encontrada.");
        }

        var idUser = request.getAttribute("idUser");
        // se a task for de outro usuário sem permissão, manda um bad request
        if (!task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Usuário não tem permissão para fazer isso.");
        }

        Utils.copyNonNullProperties(taskModel, task);
        var taskUpdated = this.repository.save(task);
        return ResponseEntity.ok().body(taskUpdated);

    }

}
