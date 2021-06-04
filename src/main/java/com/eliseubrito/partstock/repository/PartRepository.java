package com.eliseubrito.partstock.repository;

import com.eliseubrito.partstock.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartRepository extends JpaRepository<Part, Long> {

    Optional<Part> findByName(String name);

}
