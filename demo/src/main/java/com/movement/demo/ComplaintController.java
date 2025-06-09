package com.movement.demo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/complaints")
public class ComplaintController {
    @Autowired
    UserRepository userRepository;


    @Autowired
    ComplaintRepository complaintRepository;

    @Autowired
    HostelRepository hostelRepository;

    @GetMapping("/user")
    public List<ComplaintDTO> getComplaints(@RequestHeader("Authorization") String token) throws FirebaseAuthException {
        token = token.replace("Bearer ", "");

        FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(token);


        Optional<User> user = userRepository.findByEmail(decoded.getEmail());
        //List<ComplaintDTO> complaintsForUser = complaintRepository.findDTOByUserId(user.get().getId());
        if(user.isPresent()){
            List<ComplaintDTO> complaintsForUser = complaintRepository.findDTOByUserId(user.get().getId());
            System.out.println(complaintsForUser.get(0).toString());
            return complaintRepository.findDTOByUserId(user.get().getId());
        }else {

                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        }


    }

    @PostMapping("/status")
    public ResponseEntity<?> updateStatus(
            @RequestHeader("Authorization") String token,
            @RequestParam Long id) throws FirebaseAuthException {

        token = token.replace("Bearer ", "");
        FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(token);
        if(decoded.isEmailVerified()){
            int updatedRows = complaintRepository.updateStatus(id);

            if (updatedRows > 0) {
                return ResponseEntity.ok("Status updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Complaint not found or update failed");
            }
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User authentication failed");
        }

    }

    @PostMapping("/register")
    public ResponseEntity<?>register(@RequestHeader("Authorization")String token,@RequestBody ComplaintDTO complaint) throws FirebaseAuthException {
        token=token.replace("Bearer ","");
        FirebaseToken decoded=FirebaseAuth.getInstance().verifyIdToken(token);
        if(decoded.isEmailVerified()){
            Optional<User> user=userRepository.findByEmail(decoded.getEmail());
            System.out.println(complaint.getId());
            Hostel hostel=user.get().getHostel();
            Complaint comp=toEntity(complaint,user.get(),hostel);
            Complaint saved=complaintRepository.save(comp);

            return ResponseEntity.ok("Complaint registered");
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User authentication failed");
        }

    }

    public static Complaint toEntity(ComplaintDTO dto,User user,Hostel hostel ) {
        Complaint complaint = new Complaint();
        complaint.setType(dto.getComplaintType());
        complaint.setDescription(dto.getComplaintText());

        complaint.setUser(user);
        complaint.setHostel(hostel);
        complaint.setStatus(dto.getStatus());

        return complaint;
    }




}
