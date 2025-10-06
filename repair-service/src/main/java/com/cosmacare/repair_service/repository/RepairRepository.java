package com.cosmacare.repair_service.repository;

import com.cosmacare.repair_service.entity.Repair;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RepairRepository extends MongoRepository<Repair, String> {
    List<Repair> findByStoreWorkerId(String storeWorkerId);
    List<Repair> findByAssignedTo(String assignedTo);
}
