package com.cosmacare.repair_service.service;

import com.cosmacare.repair_service.entity.Repair;
import com.cosmacare.repair_service.entity.RepairCreatedEvent;
import com.cosmacare.repair_service.exceptions.ResourceNotFoundExceptions;
import com.cosmacare.repair_service.producer.RepairEventProducer;
import com.cosmacare.repair_service.repository.RepairRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairServiceTest {

    @Mock
    private RepairRepository repairRepository;

    @Mock
    private RepairEventProducer repairEventProducer;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter mockCounter;

    @InjectMocks
    private RepairService repairService;

    private Repair mockRepair;

    @BeforeEach
    void setUp() {
        mockRepair = new Repair();
        mockRepair.setId("R001");
        mockRepair.setIssueType("Display Issue");
        mockRepair.setDescription("Screen flickering");
        mockRepair.setStatus("PENDING");
        mockRepair.setStoreWorkerId("U123");
        mockRepair.setStoreWorkerUserName("Kiran");
        mockRepair.setAssignedTo("technician001");
        mockRepair.setCreatedAt(LocalDateTime.now());

        // lenient fixes the unnecessary stubbing issue
        lenient().when(meterRegistry.counter(anyString(), any(String[].class))).thenReturn(mockCounter);
    }

    @Test
    @DisplayName("Should create new repair and send Kafka event")
    void testCreateRepair_Success(){
        when(repairRepository.save(any(Repair.class))).thenReturn(mockRepair);

        Repair result = repairService.createRepair(mockRepair, "U123", "STORE_WORKER", "Kiran");

        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        assertEquals("Kiran", result.getStoreWorkerUserName());
        assertEquals("technician001", result.getAssignedTo());

        // Verify repository save was called
        ArgumentCaptor<Repair> repairCaptor = ArgumentCaptor.forClass(Repair.class);
        verify(repairRepository, times(1)).save(repairCaptor.capture());
        assertEquals("Display Issue", repairCaptor.getValue().getIssueType());

        // Verify Kafka event was sent
        ArgumentCaptor<RepairCreatedEvent> eventCaptor = ArgumentCaptor.forClass(RepairCreatedEvent.class);
        verify(repairEventProducer, times(1)).sendRepairCreatedEvent(eventCaptor.capture());
        assertEquals("R001", eventCaptor.getValue().getId());

        // Verify metric increment
        verify(mockCounter, times(1)).increment();
    }

    @Test
    @DisplayName("Should fetch all repairs for a given workerId")
    void testGetRepairsByWorker() {
        when(repairRepository.findByStoreWorkerId("U123")).thenReturn(List.of(mockRepair));

        List<Repair> result = repairService.getRepairsByWorker("U123");

        assertEquals(1, result.size());
        assertEquals("Kiran", result.get(0).getStoreWorkerUserName());

        verify(repairRepository, times(1)).findByStoreWorkerId("U123");
        verify(meterRegistry, times(1)).counter("repair.fetch.byWorker.total");
        verify(mockCounter, times(1)).increment();
    }

    @Test
    @DisplayName("Should fetch all repairs assigned to a specific technician")
    void testGetRepairsByAssignee() {
        when(repairRepository.findByAssignedTo("technician001")).thenReturn(List.of(mockRepair));

        List<Repair> result = repairService.getRepairsByAssignee("technician001");

        assertEquals(1, result.size());
        assertEquals("technician001", result.get(0).getAssignedTo());

        verify(repairRepository, times(1)).findByAssignedTo("technician001");
        verify(meterRegistry, times(1)).counter("repair.fetch.byAssignee.total");
        verify(mockCounter, times(1)).increment();
    }

    @Test
    @DisplayName("Should update repair status successfully")
    void testUpdateRepairStatus_Success() {
        when(repairRepository.findById("R001")).thenReturn(Optional.of(mockRepair));
        when(repairRepository.save(any(Repair.class))).thenReturn(mockRepair);

        Optional<Repair> result = repairService.updateRepairStatus("R001", "APPROVED");

        assertTrue(result.isPresent());
        assertEquals("APPROVED", result.get().getStatus());

        verify(repairRepository, times(1)).save(mockRepair);
        verify(meterRegistry, times(1)).counter(eq("repair.status.update.total"), eq("status"), eq("APPROVED"));
        verify(mockCounter, times(1)).increment();
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundExceptions when trying to update non-existent repair")
    void testUpdateRepairStatus_RepairNotFound() {
        String invalidId = "InvalidID";
        when(repairRepository.findById(invalidId)).thenReturn(Optional.empty());

        ResourceNotFoundExceptions exception = assertThrows(
                ResourceNotFoundExceptions.class,
                () -> repairService.updateRepairStatus(invalidId, "COMPLETED")
        );

        assertEquals("Repair not found with ID: " + invalidId, exception.getMessage());
        verify(repairRepository, times(1)).findById(invalidId);
        verify(repairRepository, never()).save(any());
        verify(meterRegistry, never()).counter(anyString(), any(String[].class));
    }


    @Test
    @DisplayName("Should fetch all repair requests")
    void testGetAllRepairsRequests() {
        when(repairRepository.findAll()).thenReturn(List.of(mockRepair));

        List<Repair> result = repairService.getAllRepairsRequests();

        assertEquals(1, result.size());
        assertEquals("Kiran", result.get(0).getStoreWorkerUserName());

        verify(repairRepository, times(1)).findAll();
        verify(meterRegistry, times(1)).counter("repair.fetch.all.total");
        verify(mockCounter, times(1)).increment();
    }

    @Test
    @DisplayName("Should fetch repair by ID when found")
    void testGetByRepairId_Found() {
        when(repairRepository.findById("R001")).thenReturn(Optional.of(mockRepair));

        Optional<Repair> result = repairService.getByRepairsId("R001");

        assertTrue(result.isPresent());
        assertEquals("Kiran", result.get().getStoreWorkerUserName());

        verify(repairRepository, times(1)).findById("R001");
        verify(meterRegistry, times(1)).counter("repair.fetch.byId.total");
        verify(mockCounter, times(1)).increment();
    }

}
