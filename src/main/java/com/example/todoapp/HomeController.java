package com.example.todoapp;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final TodoMapper todoMapper;

    public HomeController(TodoMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "Todo\u30A2\u30D7\u30EA");
        return "index";
    }

    @GetMapping("/todos")
    public String todos(Model model) {
        List<Todo> todos = todoMapper.findAll();
        model.addAttribute("todos", todos);
        return "todos";
    }

    @GetMapping("/todos/new")
    public String newTodo(Model model) {
        model.addAttribute("todo", new Todo());
        return "create";
    }

    @PostMapping("/todos/confirm")
    public String confirmTodo(@ModelAttribute Todo todo, Model model) {
        model.addAttribute("todo", todo);
        return "create-confirm";
    }

    @PostMapping("/todos/new")
    public String backToNew(@ModelAttribute Todo todo, Model model) {
        model.addAttribute("todo", todo);
        return "create";
    }

    @PostMapping("/todos")
    public String create(@ModelAttribute Todo todo, RedirectAttributes redirectAttributes) {
        todoMapper.insert(todo);
        redirectAttributes.addFlashAttribute("message", "\u767B\u9332\u3057\u307E\u3057\u305F");
        return "redirect:/todos";
    }

    @GetMapping("/todos/{id}/edit")
    public String edit(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Todo todo = todoMapper.findById(id);
        if (todo == null) {
            redirectAttributes.addFlashAttribute("message", "\u898B\u3064\u304B\u308A\u307E\u305B\u3093\u3067\u3057\u305F");
            return "redirect:/todos";
        }
        model.addAttribute("todo", todo);
        return "edit";
    }

    @PostMapping("/todos/{id}/confirm")
    public String editConfirm(@PathVariable Long id, @ModelAttribute Todo todo, Model model) {
        todo.setId(id);
        model.addAttribute("todo", todo);
        return "edit-confirm";
    }

    @PostMapping("/todos/{id}/edit")
    public String backToEdit(@PathVariable Long id, @ModelAttribute Todo todo, Model model) {
        todo.setId(id);
        model.addAttribute("todo", todo);
        return "edit";
    }

    @PostMapping("/todos/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Todo todo, RedirectAttributes redirectAttributes) {
        todo.setId(id);
        todoMapper.update(todo);
        redirectAttributes.addFlashAttribute("message", "保存しました");
        return "redirect:/todos";
    }
}
