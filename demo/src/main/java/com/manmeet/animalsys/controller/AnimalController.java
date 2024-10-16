package com.manmeet.animalsys.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.manmeet.animalsys.entity.Animal;
import com.manmeet.animalsys.entity.Shelter;
import com.manmeet.animalsys.service.AnimalService;
import com.manmeet.animalsys.service.ShelterService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/animals")
public class AnimalController {

    private static final Logger logger = LoggerFactory.getLogger(AnimalController.class);

    private final AnimalService animalService;

    @Autowired
    private ShelterService shelterService;

    @Autowired
    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping("/create")
    public String createAnimalForm(Model model) {
        model.addAttribute("animal", new Animal());
        model.addAttribute("shelters", shelterService.getAllShelters()); // Fetch and add shelters to the model
        return "animal-create";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @PostMapping("/create")
    public String createAnimal(@ModelAttribute Animal animal) {
        animalService.saveAnimal(animal);
        return "redirect:/animals";
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping
    public String getAllAnimals(Model model) {
        List<Animal> animals = animalService.getAllAnimals();
        model.addAttribute("animals", animals);
        return "animal-list";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping("/{id}")
    public String getAnimalById(@PathVariable Long id, Model model) {
        Optional<Animal> animal = animalService.getAnimalById(id);
        if (animal.isPresent()) {
            model.addAttribute("animal", animal.get());
            return "animal-details";
        }
        logger.warn("Animal with ID {} not found", id);
        return "error-page"; // Use a more descriptive error page if necessary
    }
    
    

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping("/{id}/edit")
    public String editAnimalForm(@PathVariable Long id, Model model) {
        Optional<Animal> animal = animalService.getAnimalById(id);
        if (animal.isPresent()) {
            model.addAttribute("animal", animal.get());

            // Fetch all shelters to populate the dropdown
            List<Shelter> shelters = shelterService.getAllShelters();
            model.addAttribute("shelters", shelters);

            return "animal-edit"; // Thymeleaf template for editing the animal
        }
        logger.warn("Attempt to edit non-existent animal with ID {}", id);
        return "error-page"; // Return an error page if the animal does not exist
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @PostMapping("/{id}/edit")
    public String updateAnimal(@PathVariable Long id, @ModelAttribute Animal animal) {
        animal.setId(id); // Ensure the ID is set in the animal object
        animalService.updateAnimal(id, animal); // Update the animal in the database
        return "redirect:/animals"; // Redirect to the animal list after updating
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/delete")
    public String deleteAnimal(@PathVariable Long id) {
        animalService.deleteAnimal(id);
        return "redirect:/animals";
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping("/search")
    public String searchAnimals(@RequestParam(required = false) String type, 
                                @RequestParam(required = false) String healthStatus, 
                                Model model) {
        List<Animal> animals = animalService.searchAnimals(type, healthStatus);
        model.addAttribute("animals", animals);
        return "animal-search"; // Ensure this matches your template name
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
    public String addAnimal(@PathVariable Long shelterId, @ModelAttribute @Valid Animal animal, BindingResult result) {
    	  if (result.hasErrors()) {
    	        return "animal-add"; // The name of your form view
    	    }
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
