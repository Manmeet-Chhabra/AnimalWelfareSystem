
package com.manmeet.animalsys.service;



import java.util.List;

import com.manmeet.animalsys.dto.UserDto;
import com.manmeet.animalsys.entity.Role;
import com.manmeet.animalsys.entity.User;

public interface UserService {
    void saveUser(UserDto userDto);

    User findByEmail(String email);

    List<UserDto> findAllUsers();

	Role findRoleByName(String role);
}
