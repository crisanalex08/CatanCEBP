package com.example.gameserver.api.controllers;
import com.example.gameserver.api.dto.User.UserLoginDTO;
import com.example.gameserver.entity.User;
import com.example.gameserver.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "Operations to manage users")
public class UserController {
    private final UserService usersService;

    @Autowired
    public UserController(UserService usersService) {
        this.usersService = usersService;
    }

    @Operation(summary = "Check username")
    @GetMapping("/check")
    public ResponseEntity<?> check(String username) {
        try {
            if (username == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
//            var user = usersService.retrieveOrCreateUser(username);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Create user")
    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody UserLoginDTO userLoginDTO) {
        try {
            if (userLoginDTO.getUsername() == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            User user = usersService.createUser(userLoginDTO.getUsername());
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Retrieve user")
    @GetMapping("/retrieve")
    public ResponseEntity<User> retrieveUser(String username) {
        try {
            if (username == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            User user = usersService.retrieveUser(username);
            if(user == null) {
                return new ResponseEntity<User>((User) null, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Check if user exists")
    @GetMapping("/exists")
    public ResponseEntity<Boolean> userExists(String username) {
        try {
            if (username == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            User user = usersService.retrieveUser(username);
            if(user == null) {
                return new ResponseEntity<Boolean>(false, HttpStatus.OK);
            }
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}












