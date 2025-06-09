package com.movement.demo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.Optional;

@RestController()
@RequestMapping("/api/auth")
public class VerifyController {
    @Autowired
    UserRepository userRepository;

    @PostMapping("verify")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader){
        System.out.println("Received Authorization Header: " + authHeader);
        try {
            // Extract the token from "Bearer <token>"
            String idToken = authHeader.replace("Bearer ", "").trim();

            // Verify the token with Firebase Admin
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();

            // Look up user in MySQL by UID or email
            Optional<User> user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                System.out.println(email+"hi"
                );
                return ResponseEntity.ok().body(user.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found in database.");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token: " + e.getMessage());
        }
    }
}
