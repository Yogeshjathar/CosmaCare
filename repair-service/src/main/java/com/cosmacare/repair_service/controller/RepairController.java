package com.cosmacare.repair_service.controller;

import com.cosmacare.repair_service.entity.Repair;
import com.cosmacare.repair_service.service.RepairService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/repair")
public class RepairController {

    private final RepairService repairService;
    private final MeterRegistry meterRegistry;

    public RepairController(RepairService repairService, MeterRegistry meterRegistry) {
        this.repairService = repairService;
        this.meterRegistry = meterRegistry;
    }


    @Timed(value = "repair.create.timer", description = "Time taken to create a repair")
    @Counted(value = "repair.create.count", description = "Number of repair creation requests")
    @PostMapping
    public ResponseEntity<Repair> createRepair(@RequestBody Repair repair,@RequestHeader("X-User-Id") String userId,
                                               @RequestHeader("X-User-Roles") String roles, @RequestHeader("X-User-Name") String username) {
        return ResponseEntity.ok(repairService.createRepair(repair, userId, roles, username));
    }

    @Timed(value = "repair.getAll.timer", description = "Time taken to fetch all repair requests")
    @GetMapping("/getAllRepairsRequests")
    public ResponseEntity<List<Repair>> getAllRepairsRequests() {
        return ResponseEntity.ok(repairService.getAllRepairsRequests());
    }

    @Timed(value = "repair.byRepair.timer", description = "Time taken to fetch repairs by repair id")
    @GetMapping("/getRepairRequests/{repairId}")
    public ResponseEntity<Optional<Repair>> getByRepairsId(@PathVariable String repairId) {
        return ResponseEntity.ok(repairService.getByRepairsId(repairId));
    }
    @Timed(value = "repair.byWorker.timer", description = "Time taken to fetch repairs by worker")
    @GetMapping("/worker/{workerId}")
    public ResponseEntity<List<Repair>> getRepairsByWorker(@PathVariable String workerId) {
        return ResponseEntity.ok(repairService.getRepairsByWorker(workerId));
    }

    @Timed(value = "repair.byAssignee.timer", description = "Time taken to fetch repairs by assignee")
    @GetMapping("/assignee/{assigneeId}")
    public ResponseEntity<List<Repair>> getRepairsByAssignee(@PathVariable String assigneeId) {
        return ResponseEntity.ok(repairService.getRepairsByAssignee(assigneeId));
    }

    @Timed(value = "repair.statusUpdate.timer", description = "Time taken to update repair status")
    @PatchMapping("/{repairId}/status")
    public ResponseEntity<Repair> updateRepairStatus( @PathVariable String repairId, @RequestParam String status){
        return repairService.updateRepairStatus(repairId, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/test")
    public ResponseEntity<String> testHeaders(@RequestHeader("X-User-Id") String userId,
                                              @RequestHeader("X-User-Roles") String roles,
                                              @RequestHeader("X-User-Name") String username) {
        log.info("Current User ID: " + userId);
        log.info("Currently logged in user : " + username);
        log.info("Current Roles of user: " + roles);

        return ResponseEntity.ok("Headers received and logged successfully");
    }

}
