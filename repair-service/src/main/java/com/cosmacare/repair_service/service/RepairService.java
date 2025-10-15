package com.cosmacare.repair_service.service;

import com.cosmacare.repair_service.entity.Repair;
import com.cosmacare.repair_service.entity.RepairCreatedEvent;
import com.cosmacare.repair_service.exceptions.ResourceNotFoundExceptions;
import com.cosmacare.repair_service.producer.RepairEventProducer;
import com.cosmacare.repair_service.repository.RepairRepository;
import lombok.extern.slf4j.Slf4j;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.common.errors.ResourceNotFoundException;
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

        if (repairs == null || repairs.isEmpty()) {
            log.warn("No repairs found for workerId: {}", workerId);
            throw new ResourceNotFoundExceptions("No repairs found for worker with ID: " + workerId);
        }

        // Metric for monitoring fetches
        meterRegistry.counter("repair.fetch.byWorker.total").increment();

        return repairs;
    }

    public List<Repair> getRepairsByAssignee(String assigneeId) {
        log.info("Fetching repairs assigned to assigneeId: {}", assigneeId);
        List<Repair> repairs = repairRepository.findByAssignedTo(assigneeId);
        log.info("Total repairs fetched for assigneeId {}: {}", assigneeId, repairs.size());

        if (repairs == null || repairs.isEmpty()) {
            log.warn("No repairs found for assigneeId: {}", assigneeId);
            throw new ResourceNotFoundExceptions("No repairs assigned to user with ID: " + assigneeId);
        }

        meterRegistry.counter("repair.fetch.byAssignee.total").increment();
        return repairs;
    }

    public Optional<Repair> updateRepairStatus(String repairId, String status) {
        log.info("Updating repair status for repairId: {} to status: {}", repairId, status);

        Repair repair = repairRepository.findById(repairId)
                .orElseThrow(() -> {
                    log.warn("Repair not found with id: {}", repairId);
                    return new ResourceNotFoundExceptions("Repair not found with ID: " + repairId);
                });

        repair.setStatus(status);
        repair.setUpdatedAt(LocalDateTime.now());
        Repair updatedRepair = repairRepository.save(repair);
        log.info("Repair status updated for repairId: {} to {}", repairId, status);

        meterRegistry.counter("repair.status.update.total", "status", status).increment();
        return Optional.of(updatedRepair);
    }

    public List<Repair> getAllRepairsRequests() {
        log.info("Fetching all repair requests");
        List<Repair> repairs = repairRepository.findAll();
        log.info("Total repair requests fetched: {}", repairs.size());

        if (repairs == null || repairs.isEmpty()) {
            log.warn("No repair requests found in the system");
            throw new ResourceNotFoundExceptions("No repair requests available at the moment.");
        }

        meterRegistry.counter("repair.fetch.all.total").increment();
        return repairs;
    }

    public Optional<Repair> getByRepairsId(String repairId) {
        log.info("Fetching repair by repairId: {}", repairId);
        Optional<Repair> repair = Optional.ofNullable(repairRepository.findById(repairId)
                .orElseThrow(() -> {
                    throw new ResourceNotFoundExceptions("No repairs found for worker with ID: " + repairId);
                }));

        log.info("Repair found for repairId: {}", repairId);
        meterRegistry.counter("repair.fetch.byId.total").increment();
        return repair;
    }
}
