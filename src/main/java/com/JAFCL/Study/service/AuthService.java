package com.JAFCL.Study.service;

import java.util.Optional;

import org.apache.catalina.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.JAFCL.Study.Repository.RolesRepository;
import com.JAFCL.Study.Repository.UsersRepository;
import com.JAFCL.Study.dto.HttpGlobalResponseDTO;
import com.JAFCL.Study.dto.LoginRequestDTO;
import com.JAFCL.Study.dto.LoginResponseDTO;
import com.JAFCL.Study.dto.RegisterRequestDTO;
import com.JAFCL.Study.dto.UserDTO;
import com.JAFCL.Study.entity.Roles;
import com.JAFCL.Study.entity.Users;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UsersRepository usersRepository;

    private final RolesRepository rolesRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    public HttpGlobalResponseDTO<UserDTO> register(RegisterRequestDTO request) {
        HttpGlobalResponseDTO<UserDTO> response = new HttpGlobalResponseDTO<>();
        UserDTO userDto = new UserDTO();

        Optional<Users> nameFound = usersRepository.findByUsername(request.getUsername());
        Optional<Users> emailFound = usersRepository.findByEmail(request.getEmail());
        Optional<Roles> rolFound = rolesRepository.findById(request.getRolId());

        if (nameFound.isPresent()) {
            response.setMessage("El nombre de usuario elegido ya está registrado.");
            return response;
        }

        if (emailFound.isPresent()) {
            response.setMessage("El email escrito ya está registrado.");
            return response;
        }

        if (rolFound.isEmpty()) {
            response.setMessage("El rol indicado no se encuentra registrado.");
            return  response;
        }

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(rolFound.get());

        usersRepository.save(user);

        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setRolId(user.getRoles().getName());

        response.setMessage("Usuario registrado correctamente.");
        response.setData(userDto);

        return response;
    }

    public HttpGlobalResponseDTO<LoginResponseDTO> login(LoginRequestDTO request) {
        HttpGlobalResponseDTO<LoginResponseDTO> response = new HttpGlobalResponseDTO<>();
        
        Optional<Users> nameFound = usersRepository.findByUsername(request.getUser());
        Optional<Users> emailFound = usersRepository.findByEmail(request.getUser());

        if (nameFound.isEmpty() && emailFound.isEmpty()) {
            response.setMessage("Usuario y/o contraseña erroneos.");
            return response;
        }

        Users user;

        if (nameFound.isPresent()) {
            user = nameFound.get();
        } else {
            user = emailFound.get();
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            response.setMessage("Usuario y/o contraseña erroneos.");
            return response;
        }

        String jwt = jwtService.generateToken(user.getId(), user.getUsername(), user.getRoles().getId());        
        LoginResponseDTO token = new LoginResponseDTO();
        UserDTO userDto = new UserDTO();            //Dedes aquí se añadió.
        userDto.setId(user.getId());                //AQUÍ
        userDto.setUsername(user.getUsername());    //AQUÍ
        userDto.setEmail(user.getEmail());          //AQUÍ
        userDto.setRolId(user.getRoles().getName());//Hasta AQUÍ.
        token.setJwt(jwt);
        token.setUser(userDto);
        response.setMessage("Inicio de sesión exitoso.");
        response.setData(token);

        return response;
    }

    public HttpGlobalResponseDTO<LoginResponseDTO> refreshToken(String token) throws Exception {
        HttpGlobalResponseDTO<LoginResponseDTO> response = new HttpGlobalResponseDTO<>();
        LoginResponseDTO refresh = new LoginResponseDTO();

        String jwt = jwtService.refreshToken(token);
        Long userId = jwtService.extractUserId(token);
        Optional<Users> userFound = usersRepository.findById(userId);

        Users user = userFound.get();
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRolId(user.getRoles().getName());

        refresh.setJwt(jwt);
        refresh.setUser(userDTO);

        response.setMessage("Token refrescado correctamente.");
        response.setData(refresh);

        return response;
    }
}