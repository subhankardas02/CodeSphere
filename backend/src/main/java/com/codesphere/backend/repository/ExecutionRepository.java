package com.codesphere.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codesphere.backend.entity.Execution;

public interface ExecutionRepository extends JpaRepository<Execution, Long>{
    
    
}
