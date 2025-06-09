package com.movement.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.net.http.HttpResponse;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    @Query("SELECT new com.movement.demo.ComplaintDTO(c.id, c.type, c.description, u.rollNo, u.room, c.status, u.name) " +
            "FROM Complaint c JOIN c.user u WHERE u.id = :userId")
    List<ComplaintDTO> findDTOByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Complaint c SET c.status = 'Completed' WHERE c.id = :id")
    int updateStatus(@Param("id") Long id);


    List<Complaint> findByHostelId(Long hostelId);
}
