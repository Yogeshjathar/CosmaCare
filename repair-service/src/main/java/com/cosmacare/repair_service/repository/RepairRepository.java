package com.cosmacare.repair_service.repository;

import com.cosmacare.repair_service.entity.Repair;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairRepository extends MongoRepository<Repair, String> {
    List<Repair> findByStoreWorkerId(String storeWorkerId);
    List<Repair> findByAssignedTo(String assignedTo);
}
