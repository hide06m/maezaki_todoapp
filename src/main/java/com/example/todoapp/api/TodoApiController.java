package com.example.todoapp.api;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.todoapp.Todo;
import com.example.todoapp.TodoService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class TodoApiController {

    private final TodoService todoService;

    public TodoApiController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/api/todos")
    public List<TodoDto> todos(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "asc") String order) {
        return todoService.search(keyword, category, order)
                .stream()
                .map(TodoDto::from)
                .toList();
    }

    @GetMapping("/api/todos/{id}")
    public ResponseEntity<?> todo(@PathVariable Long id) {
        Todo todo = todoService.findById(id);
        if (todo == null) {
            return notFound(id);
        }
        return ResponseEntity.ok(TodoDto.from(todo));
    }

    @PostMapping("/api/todos")
    public ResponseEntity<?> create(
            @Valid @RequestBody TodoRequest todoRequest,
            BindingResult bindingResult,
            HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            return badRequest(bindingResult, httpServletRequest);
        }

        Todo todo = toTodo(todoRequest);
        todoService.create(todo);

        Todo createdTodo = todoService.findById(todo.getId());
        URI location = URI.create("/api/todos/" + todo.getId());
        return ResponseEntity.created(location).body(TodoDto.from(createdTodo));
    }

    @PutMapping("/api/todos/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody TodoRequest todoRequest,
            BindingResult bindingResult,
            HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            return badRequest(bindingResult, httpServletRequest);
        }

        Todo existingTodo = todoService.findById(id);
        if (existingTodo == null) {
            return notFound(id);
        }

        Todo todo = toTodo(todoRequest);
        todo.setId(id);
        todoService.update(todo);

        Todo updatedTodo = todoService.findById(id);
        return ResponseEntity.ok(TodoDto.from(updatedTodo));
    }

    @DeleteMapping("/api/todos/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Todo todo = todoService.findById(id);
        if (todo == null) {
            return notFound(id);
        }

        todoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<ProblemDetail> notFound(Long id) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Todo not found");
        problemDetail.setDetail("Todo id=" + id + " was not found.");
        problemDetail.setInstance(URI.create("/api/todos/" + id));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    private ResponseEntity<ProblemDetail> badRequest(
            BindingResult bindingResult,
            HttpServletRequest httpServletRequest) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Bad Request");
        problemDetail.setDetail("入力に誤りがあります");
        problemDetail.setInstance(URI.create(httpServletRequest.getRequestURI()));
        problemDetail.setProperty("errors", bindingResult.getFieldErrors()
                .stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", error.getDefaultMessage()))
                .toList());
        return ResponseEntity.badRequest().body(problemDetail);
    }

    private Todo toTodo(TodoRequest todoRequest) {
        Todo todo = new Todo();
        todo.setTitle(todoRequest.getTitle());
        todo.setDetail(todoRequest.getDetail());
        todo.setCategory(todoRequest.getCategory());
        todo.setPriority(todoRequest.getPriority());
        todo.setDueDate(todoRequest.getDueDate());
        todo.setCompleted(todoRequest.getCompleted());
        return todo;
    }
}
