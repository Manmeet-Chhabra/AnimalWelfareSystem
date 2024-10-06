package com.manmeet.animalsys.controller;

import com.manmeet.animalsys.entity.Animal;
import com.manmeet.animalsys.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/animals")
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    // Admin and Staff can create new animals
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping("/create")
    public String createAnimalForm(Model model) {
        model.addAttribute("animal", new Animal());
        return "animal-create";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @PostMapping("/create")
    public String createAnimal(@ModelAttribute Animal animal) {
        animalService.saveAnimal(animal);
        return "redirect:/animals";
    }

    // All users can view all animals
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping
    public String getAllAnimals(Model model) {
        List<Animal> animals = animalService.getAllAnimals();
        model.addAttribute("animals", animals);
        return "animal-list";
    }

    // Admin and Staff can view details of a specific animal
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping("/{id}")
    public String getAnimalById(@PathVariable Long id, Model model) {
        Optional<Animal> animal = animalService.getAnimalById(id);
        if (animal.isPresent()) {
            model.addAttribute("animal", animal.get());
            return "animal-details";
        }
        return "error-page";
    }

    // Admin and Staff can update an animal
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping("/{id}/edit")
    public String editAnimalForm(@PathVariable Long id, Model model) {
        Optional<Animal> animal = animalService.getAnimalById(id);
        if (animal.isPresent()) {
            model.addAttribute("animal", animal.get());
            return "animal-edit";
        }
        return "error-page";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @PostMapping("/{id}/edit")
    public String updateAnimal(@PathVariable Long id, @ModelAttribute Animal animal) {
        animal.setId(id);
        animalService.updateAnimal(id, animal);
        return "redirect:/animals";
    }

    // Admin can delete an animal
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/delete")
    public String deleteAnimal(@PathVariable Long id) {
        animalService.deleteAnimal(id);
        return "redirect:/animals";
    }

    // All users can search for animals
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping("/search")
    public String searchAnimals(@RequestParam(required = false) String type, 
                                @RequestParam(required = false) String healthStatus, 
                                Model model) {
        List<Animal> animals = animalService.searchAnimals(type, healthStatus);
        model.addAttribute("animals", animals);
        return "animal-search";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping("/shelter/{shelterId}/add")
    public String addAnimalForm(@PathVariable Long shelterId, Model model) {
        model.addAttribute("animal", new Animal());
        model.addAttribute("shelterId", shelterId);
        return "animal-add";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @PostMapping("/shelter/{shelterId}/add")
    public String addAnimal(@PathVariable Long shelterId, @ModelAttribute Animal animal) {
        animalService.addAnimal(shelterId, animal);
        return "redirect:/animals/shelter/" + shelterId;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping("/shelter/{shelterId}")
    public String getAnimalsByShelter(@PathVariable Long shelterId, Model model) {
        List<Animal> animals = animalService.getAnimalsByShelter(shelterId);
        model.addAttribute("animals", animals);
        model.addAttribute("shelterId", shelterId);
        return "animal-shelter-list";
    }
}
