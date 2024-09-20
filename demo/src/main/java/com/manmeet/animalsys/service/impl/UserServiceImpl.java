package com.manmeet.animalsys.service.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.manmeet.animalsys.dto.UserDto;
import com.manmeet.animalsys.entity.Role;
import com.manmeet.animalsys.entity.User;
import com.manmeet.animalsys.repos.RoleRepository;
import com.manmeet.animalsys.repos.UserRepository;
import com.manmeet.animalsys.service.UserService;



@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());

        // Encrypt the password using the PasswordEncoder
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Fetch role based on the userDto's role
        Role role = roleRepository.findByName(userDto.getRole());
        if (role == null) {
            role = checkRoleExist(userDto.getRole());
        }

        // Assign the role dynamically
        user.setRoles(new HashSet<>(Collections.singletonList(role)));

        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    private UserDto convertEntityToDto(User user) {
        UserDto userDto = new UserDto();
        String[] nameParts = user.getName().split(" ");
        userDto.setFirstName(nameParts[0]);
        userDto.setLastName(nameParts.length > 1 ? nameParts[nameParts.length - 1] : "");
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    private Role checkRoleExist(String roleName) {
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
        return role;
    }

    @Override
    public Role findRoleByName(String role) {
        return roleRepository.findByName(role);
    }
}
