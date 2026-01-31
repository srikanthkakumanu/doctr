package com.doctr.controller;

import com.doctr.model.Doctor;
import com.doctr.repos.DoctorRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor API", description = "API for managing doctors")
public class DoctorController {

    private final DoctorRepo doctorRepo;

    @Operation(summary = "Create a new doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Doctor created successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Doctor>> createDoctor(@Valid @RequestBody Doctor doctor) {
        try {
            Doctor newDoctor = new Doctor(doctor.getFirstName(), doctor.getLastName(), doctor.getAddress(),
                    doctor.getCity(), doctor.getPincode());
            Doctor savedDoctor = doctorRepo.save(newDoctor);
            EntityModel<Doctor> doctorModel = EntityModel.of(savedDoctor);
            doctorModel.add(linkTo(methodOn(DoctorController.class).getDoctorById(savedDoctor.getId())).withSelfRel());
            doctorModel.add(
                    linkTo(methodOn(DoctorController.class).findDoctors(null, Pageable.unpaged())).withRel("doctors"));
            return new ResponseEntity<>(doctorModel, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Find doctors by pincode or get all doctors with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved doctors"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Doctor>>> findDoctors(@RequestParam(required = false) String pincode,
            Pageable pageable) {
        try {
            Page<Doctor> doctors;
            if (pincode == null) {
                doctors = doctorRepo.findAll(pageable);
            } else {
                doctors = doctorRepo.findByPincode(pincode, pageable);
            }
            List<EntityModel<Doctor>> doctorModels = doctors.getContent().stream()
                    .map(doctor -> EntityModel.of(doctor,
                            linkTo(methodOn(DoctorController.class).getDoctorById(doctor.getId())).withSelfRel(),
                            linkTo(methodOn(DoctorController.class).updateDoctor(doctor.getId(), doctor))
                                    .withRel("update"),
                            linkTo(methodOn(DoctorController.class).deleteDoctor(doctor.getId())).withRel("delete")))
                    .collect(Collectors.toList());

            PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                    doctors.getSize(), doctors.getNumber(), doctors.getTotalElements(), doctors.getTotalPages());
            PagedModel<EntityModel<Doctor>> pagedModel = PagedModel.of(doctorModels, pageMetadata);
            pagedModel.add(linkTo(methodOn(DoctorController.class).findDoctors(pincode, pageable)).withSelfRel());

            if (doctors.hasNext()) {
                pagedModel.add(
                        linkTo(methodOn(DoctorController.class).findDoctors(pincode, pageable.next())).withRel("next"));
            }
            if (doctors.hasPrevious()) {
                pagedModel.add(linkTo(methodOn(DoctorController.class).findDoctors(pincode, pageable.previousOrFirst()))
                        .withRel("prev"));
            }
            pagedModel.add(linkTo(methodOn(DoctorController.class).createDoctor(null)).withRel("create"));

            return new ResponseEntity<>(pagedModel, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get a doctor by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved doctor"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Doctor>> getDoctorById(@PathVariable("id") long id) {
        return doctorRepo.findById(id)
                .map(doctor -> {
                    EntityModel<Doctor> doctorModel = EntityModel.of(doctor);
                    doctorModel.add(linkTo(methodOn(DoctorController.class).getDoctorById(id)).withSelfRel());
                    doctorModel
                            .add(linkTo(methodOn(DoctorController.class).updateDoctor(id, doctor)).withRel("update"));
                    doctorModel.add(linkTo(methodOn(DoctorController.class).deleteDoctor(id)).withRel("delete"));
                    doctorModel.add(linkTo(methodOn(DoctorController.class).findDoctors(null, Pageable.unpaged()))
                            .withRel("doctors"));
                    return ResponseEntity.ok(doctorModel);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Update a doctor's details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor updated successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Doctor>> updateDoctor(@PathVariable("id") long id,
            @Valid @RequestBody Doctor doctorDetails) {
        return doctorRepo.findById(id)
                .map(doctor -> {
                    doctor.setFirstName(doctorDetails.getFirstName());
                    doctor.setLastName(doctorDetails.getLastName());
                    doctor.setAddress(doctorDetails.getAddress());
                    doctor.setCity(doctorDetails.getCity());
                    doctor.setPincode(doctorDetails.getPincode());
                    Doctor updatedDoctor = doctorRepo.save(doctor);
                    EntityModel<Doctor> doctorModel = EntityModel.of(updatedDoctor);
                    doctorModel.add(linkTo(methodOn(DoctorController.class).getDoctorById(id)).withSelfRel());
                    doctorModel.add(linkTo(methodOn(DoctorController.class).deleteDoctor(id)).withRel("delete"));
                    doctorModel.add(linkTo(methodOn(DoctorController.class).findDoctors(null, Pageable.unpaged()))
                            .withRel("doctors"));
                    return new ResponseEntity<>(doctorModel, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Delete a doctor by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Doctor deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteDoctor(@PathVariable("id") long id) {
        try {
            if (!doctorRepo.existsById(id)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            doctorRepo.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Delete all doctors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "All doctors deleted successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteAllDoctors() {
        try {
            doctorRepo.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
