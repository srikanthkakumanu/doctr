package com.doctr.repos;

import com.doctr.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepo extends JpaRepository<Doctor, Long> {
    Page<Doctor> findByPincode(String pincode, Pageable pageable);
}
