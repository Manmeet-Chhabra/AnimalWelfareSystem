package com.manmeet.animalsys.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manmeet.animalsys.entity.Role;
import com.manmeet.animalsys.entity.Shelter;
import com.manmeet.animalsys.entity.User;
import com.manmeet.animalsys.repos.RoleRepository;
import com.manmeet.animalsys.repos.ShelterRepository;
import com.manmeet.animalsys.repos.UserRepository;
import com.manmeet.animalsys.service.ShelterService;

@Service
public class ShelterServiceImpl implements ShelterService {

	@Autowired
	private ShelterRepository shelterRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository; // Add this dependency
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public Shelter saveShelter(Shelter shelter) {

		if (shelter.getCurrentOccupancy() == null) {
			shelter.setCurrentOccupancy(0); // Set to 0 if it's null
		}
		return shelterRepository.save(shelter);

	}

	@Override
	public List<Shelter> getAllShelters() {
		return shelterRepository.findAll();
	}

	@Override
	public Optional<Shelter> getShelterById(Long id) {
		return shelterRepository.findById(id);
	}

	@Override
	public void deleteShelter(Long id) {
		shelterRepository.deleteById(id);
	}

	/*
	 * @Override public Shelter updateShelter(Shelter shelter) { if
	 * (shelterRepository.existsById(shelter.getId())) { return
	 * shelterRepository.save(shelter); } throw new
	 * IllegalArgumentException("Shelter with id " + shelter.getId() +
	 * " does not exist."); }
	 */

	@Override
	public Shelter updateShelter(Shelter shelter) {
		Optional<Shelter> existingShelterOpt = shelterRepository.findById(shelter.getId());
		if (existingShelterOpt.isPresent()) {
			Shelter existingShelter = existingShelterOpt.get();

			// Update fields without reassigning the `animals` reference
			existingShelter.setName(shelter.getName());
			existingShelter.setLocation(shelter.getLocation());
			existingShelter.setCapacity(shelter.getCapacity());
			existingShelter.setCurrentOccupancy(shelter.getCurrentOccupancy());
			existingShelter.setContactDetails(shelter.getContactDetails());

			// Only update the `animals` collection if needed
			if (shelter.getAnimals() != null) {
				existingShelter.getAnimals().clear();
				existingShelter.getAnimals().addAll(shelter.getAnimals());
			}

			return shelterRepository.save(existingShelter);
		}
		throw new IllegalArgumentException("Shelter with id " + shelter.getId() + " does not exist.");
	}

	@Override
	public List<Shelter> searchShelters(String location, Integer capacity) {
		return shelterRepository.findByLocationAndCapacity(location, capacity);
	}

	@Override
	@Transactional
	public Shelter increaseCapacity(Long shelterId, int increment) {
		Optional<Shelter> shelterOptional = shelterRepository.findById(shelterId);
		if (shelterOptional.isPresent()) {
			Shelter shelter = shelterOptional.get();
			shelter.setCapacity(shelter.getCapacity() + increment);
			return shelterRepository.save(shelter);
		}
		throw new IllegalArgumentException("Shelter with id " + shelterId + " does not exist.");
	}

	@Override
	@Transactional
	public Shelter decreaseCapacity(Long shelterId, int decrement) {
		Optional<Shelter> shelterOptional = shelterRepository.findById(shelterId);
		if (shelterOptional.isPresent()) {
			Shelter shelter = shelterOptional.get();
			int newCapacity = shelter.getCapacity() - decrement;
			if (newCapacity < 0) {
				throw new IllegalArgumentException("Capacity cannot be negative.");
			}
			shelter.setCapacity(newCapacity);
			return shelterRepository.save(shelter);
		}
		throw new IllegalArgumentException("Shelter with id " + shelterId + " does not exist.");
	}

	@Override
	@Transactional
	public User addStaffToShelter(Long shelterId, User staff) {
	    Optional<Shelter> shelterOptional = shelterRepository.findById(shelterId);
	    if (shelterOptional.isPresent()) {
	        Shelter shelter = shelterOptional.get();

	        // Fetch the "ROLE_STAFF" role
	        Role staffRole = roleRepository.findByName("ROLE_STAFF");
	        if (staffRole == null) {
	            throw new IllegalStateException("The STAFF role does not exist in the database.");
	        }

	        // Check if email is null
	        if (staff.getEmail() == null) {
	            throw new IllegalArgumentException("Email cannot be null for the user.");
	        }

	        // Set a default password if not provided
	        if (staff.getPassword() == null || staff.getPassword().isEmpty()) {
	            staff.setPassword("defaultPassword"); // Replace with a proper password generation logic
	        }

	        // Encode the password
	        String encodedPassword = passwordEncoder.encode(staff.getPassword());
	        staff.setPassword(encodedPassword);

	        // Assign the STAFF role to the user
	        staff.getRoles().add(staffRole);

	        // Save the user with the new role
	        User savedStaff = userRepository.save(staff);

	        // Associate staff with the shelter
	        shelter.getStaff().add(savedStaff);
	        shelterRepository.save(shelter); // Save the updated shelter

	        return savedStaff; // Optionally return the saved staff member
	    }
	    throw new IllegalArgumentException("Shelter with id " + shelterId + " does not exist.");
	}

	@Override
	public void removeStaffFromShelter(Long shelterId, Long staffId) {
		Optional<Shelter> shelterOptional = shelterRepository.findById(shelterId);
		if (shelterOptional.isPresent()) {
			Shelter shelter = shelterOptional.get();
			Optional<User> staffOptional = userRepository.findById(staffId);
			if (staffOptional.isPresent()) {
				User staff = staffOptional.get();
				shelter.getStaff().remove(staff); // Assuming Shelter has a method to remove staff
				userRepository.delete(staff);
			} else {
				throw new IllegalArgumentException("Staff with id " + staffId + " does not exist.");
			}
		} else {
			throw new IllegalArgumentException("Shelter with id " + shelterId + " does not exist.");
		}
	}

	@Override
	public List<User> getStaffByShelter(Long shelterId) {
	    Optional<Shelter> shelterOptional = shelterRepository.findById(shelterId);
	    if (shelterOptional.isPresent()) {
	        Shelter shelter = shelterOptional.get();
	        
	        // Return staff members who have the ROLE_STAFF
	        return shelter.getStaff().stream()
	                .filter(user -> user.getRoles().stream()
	                        .anyMatch(role -> role.getName().equals("ROLE_STAFF"))) // Ensure using the correct role name
	                .collect(Collectors.toList());
	    }
	    throw new IllegalArgumentException("Shelter with id " + shelterId + " does not exist.");
	}

}
