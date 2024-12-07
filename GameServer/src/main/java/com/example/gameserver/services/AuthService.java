// package com.example.gameserver.services;

// import java.util.concurrent.CompletableFuture;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.stereotype.Service;

// import com.example.gameserver.api.dto.User.UserCreateDTO;
// import com.example.gameserver.api.dto.User.UserLoginDTO;
// import com.example.gameserver.entity.User;
// import com.example.gameserver.repository.UsersRepository;

// @Service
// public class AuthService {

//     private final UsersRepository usersRepository;

//     @Autowired
//     public AuthService(UsersRepository usersRepository) {
//         this.usersRepository = usersRepository;
//     }

//     // Add a new user to the database
//     @Async
//     public CompletableFuture<User> registerUser (UserCreateDTO request) {

//         System.out.println(request);
        
//         // Check if the user already exists
//         if(usersRepository.findAll().stream().anyMatch(user -> user.get().equals(request.getUsername()))) {
//             throw new IllegalArgumentException("User already exists");
//         }
//         // Add the user to the database
//         User user = request.toEntity();
//         usersRepository.save(user);

//         return CompletableFuture.completedFuture(user);

//     }
//     @Async
//     public CompletableFuture<User> loginUser(UserLoginDTO request) {
        
//         User user = usersRepository.findAll().stream()
//         .filter(u -> u.getUsername().equals(request.getUsername()))
//         .findFirst()
//         .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        
//         if(!user.getPassword().equals(request.getPassword())) {
//             throw new IllegalArgumentException("Invalid username or password");
//         }
//         return CompletableFuture.completedFuture(user);
        
//     }
   
    
// }
