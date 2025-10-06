package com.cosmacare.repair_service.service;

import com.cosmacare.repair_service.entity.Repair;
import com.cosmacare.repair_service.entity.RepairCreatedEvent;
import com.cosmacare.repair_service.producer.RepairEventProducer;
import com.cosmacare.repair_service.repository.RepairRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RepairService {

    private final RepairRepository repairRepository;
    private final RepairEventProducer repairEventProducer;

    public RepairService(RepairRepository repairRepository, RepairEventProducer repairEventProducer) {
        this.repairRepository = repairRepository;
        this.repairEventProducer = repairEventProducer;
    }

    public Repair createRepair(Repair repair,String userId,String roles, String username) {
        log.info("Creating new repair request");
        repair.setStatus("PENDING");
        repair.setCreatedAt(LocalDateTime.now());
        repair.setStoreWorkerId(userId);
        repair.setStoreWorkerUserName(username);

        // Saved repair in db
        log.info("Saved new repair request");
        Repair savedRepair = repairRepository.save(repair);

        // Kafka event
        RepairCreatedEvent event = new RepairCreatedEvent(
                savedRepair.getId(),
                savedRepair.getStoreWorkerId(),
                savedRepair.getStoreWorkerUserName(),
                savedRepair.getIssueType(),
                savedRepair.getDescription(),
                savedRepair.getStatus(),
                savedRepair.getAssignedTo(),
                savedRepair.getCreatedAt()
        );
        repairEventProducer.sendRepairCreatedEvent(event);
        return savedRepair;
    }

    public List<Repair> getRepairsByWorker(String workerId) {
        return repairRepository.findByStoreWorkerId(workerId);
    }

    public List<Repair> getRepairsByAssignee(String assigneeId) {
        return repairRepository.findByAssignedTo(assigneeId);
    }

    public Optional<Repair> updateRepairStatus(String repairId, String status) {
        Optional<Repair> optionalRepair = repairRepository.findById(repairId);
        optionalRepair.ifPresent(r -> {
            r.setStatus(status);
            r.setUpdatedAt(LocalDateTime.now());
            repairRepository.save(r);
        });
        return optionalRepair;
    }

    public List<Repair> getAllRepairsRequests() {
        return repairRepository.findAll();
    }
}
