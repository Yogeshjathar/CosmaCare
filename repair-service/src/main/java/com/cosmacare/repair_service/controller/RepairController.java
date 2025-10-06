package com.cosmacare.repair_service.controller;

import com.cosmacare.repair_service.entity.Repair;
import com.cosmacare.repair_service.service.RepairService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/repair")
public class RepairController {

    private final RepairService repairService;

    public RepairController(RepairService repairService) {
        this.repairService = repairService;
    }

    @PostMapping
    public ResponseEntity<Repair> createRepair(@RequestBody Repair repair,@RequestHeader("X-User-Id") String userId,
                                               @RequestHeader("X-User-Roles") String roles, @RequestHeader("X-User-Name") String username) {
        return ResponseEntity.ok(repairService.createRepair(repair, userId, roles, username));
    }

    @GetMapping("/getAllRepairsRequests")
    public ResponseEntity<List<Repair>> getAllRepairsRequests() {
        return ResponseEntity.ok(repairService.getAllRepairsRequests());
    }

    @GetMapping("/worker/{workerId}")
    public ResponseEntity<List<Repair>> getRepairsByWorker(@PathVariable String workerId) {
        return ResponseEntity.ok(repairService.getRepairsByWorker(workerId));
    }

    @GetMapping("/assignee/{assigneeId}")
    public ResponseEntity<List<Repair>> getRepairsByAssignee(@PathVariable String assigneeId) {
        return ResponseEntity.ok(repairService.getRepairsByAssignee(assigneeId));
    }

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
