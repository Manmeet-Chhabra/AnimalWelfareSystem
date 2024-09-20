package com.manmeet.animalsys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.manmeet.animalsys.entity.Animal;
import com.manmeet.animalsys.service.impl.AnimalService;

@Controller
@RequestMapping("/admin/animals")
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    @GetMapping
    public String listAnimals(Model model) {
        model.addAttribute("animals", animalService.getAllAnimals());
        return "admin/manage_animals";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("animal", new Animal());
        return "admin/add_animal";
    }

    @PostMapping("/save")
    public String saveAnimal(@ModelAttribute Animal animal) {
        animalService.saveAnimal(animal);
        return "redirect:/admin/animals";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("animal", animalService.getAnimalById(id).orElse(null));
        return "admin/edit_animal";
    }

    @PostMapping("/update")
    public String updateAnimal(@ModelAttribute Animal animal) {
        animalService.saveAnimal(animal);
        return "redirect:/admin/animals";
    }

    @GetMapping("/delete/{id}")
    public String deleteAnimal(@PathVariable Long id) {
        animalService.deleteAnimal(id);
        return "redirect:/admin/animals";
    }
}
