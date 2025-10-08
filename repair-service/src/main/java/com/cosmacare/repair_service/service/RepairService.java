package com.cosmacare.repair_service.service;

import com.cosmacare.repair_service.entity.Repair;
import com.cosmacare.repair_service.entity.RepairCreatedEvent;
import com.cosmacare.repair_service.producer.RepairEventProducer;
import com.cosmacare.repair_service.repository.RepairRepository;
import lombok.extern.slf4j.Slf4j;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RepairService {

    private final RepairRepository repairRepository;
    private final RepairEventProducer repairEventProducer;
    private final MeterRegistry meterRegistry;

    public RepairService(RepairRepository repairRepository,
                         RepairEventProducer repairEventProducer,
                         MeterRegistry meterRegistry) {
        this.repairRepository = repairRepository;
        this.repairEventProducer = repairEventProducer;
        this.meterRegistry = meterRegistry;
    }

    public Repair createRepair(Repair repair, String userId, String roles, String username) {
        log.info("Creating new repair request for userId: {}, username: {}", userId, username);

        repair.setStatus("PENDING");
        repair.setCreatedAt(LocalDateTime.now());
        repair.setStoreWorkerId(userId);
        repair.setStoreWorkerUserName(username);

        Repair savedRepair = repairRepository.save(repair);
        log.info("Saved new repair request with id: {}", savedRepair.getId());

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
        log.info("RepairCreatedEvent sent for repairId: {}", savedRepair.getId());

        // Increment a custom metric for created repairs
        meterRegistry.counter("repair.created.total", "status", "PENDING").increment();

        return savedRepair;
    }

    public List<Repair> getRepairsByWorker(String workerId) {
        log.info("Fetching repairs for workerId: {}", workerId);
        List<Repair> repairs = repairRepository.findByStoreWorkerId(workerId);
        log.info("Total repairs fetched for workerId {}: {}", workerId, repairs.size());

        // Metric for monitoring fetches
        meterRegistry.counter("repair.fetch.byWorker.total").increment();

        return repairs;
    }

    public List<Repair> getRepairsByAssignee(String assigneeId) {
        log.info("Fetching repairs assigned to assigneeId: {}", assigneeId);
        List<Repair> repairs = repairRepository.findByAssignedTo(assigneeId);
        log.info("Total repairs fetched for assigneeId {}: {}", assigneeId, repairs.size());

        meterRegistry.counter("repair.fetch.byAssignee.total").increment();
        return repairs;
    }

    public Optional<Repair> updateRepairStatus(String repairId, String status) {
        log.info("Updating repair status for repairId: {} to status: {}", repairId, status);

        Optional<Repair> optionalRepair = repairRepository.findById(repairId);
        optionalRepair.ifPresent(r -> {
            r.setStatus(status);
            r.setUpdatedAt(LocalDateTime.now());
            repairRepository.save(r);
            log.info("Repair status updated for repairId: {} to {}", repairId, status);

            // Increment metric for status updates
            meterRegistry.counter("repair.status.update.total", "status", status).increment();
        });

        if (optionalRepair.isEmpty()) {
            log.warn("Repair not found with id: {}", repairId);
        }

        return optionalRepair;
    }

    public List<Repair> getAllRepairsRequests() {
        log.info("Fetching all repair requests");
        List<Repair> repairs = repairRepository.findAll();
        log.info("Total repair requests fetched: {}", repairs.size());

        meterRegistry.counter("repair.fetch.all.total").increment();
        return repairs;
    }

    public Optional<Repair> getByRepairsId(String repairId) {
        log.info("Fetching repair by repairId: {}", repairId);
        Optional<Repair> repair = repairRepository.findById(repairId);

        if (repair.isPresent()) {
            log.info("Repair found for repairId: {}", repairId);
        } else {
            log.warn("Repair not found for repairId: {}", repairId);
        }

        meterRegistry.counter("repair.fetch.byId.total").increment();
        return repair;
    }
}
