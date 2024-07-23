package com.hodbenor.project.accumulation.service.rest;

import com.hodbenor.project.accumulation.service.rest.beans.WinSlotMachineRequest;
import com.hodbenor.project.accumulation.service.rest.beans.WinSlotMachineResponse;
import com.hodbenor.project.accumulation.service.service.MissionsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accumulation")
public class AccumulationController {

    private final MissionsManager missionsManager;

    @Autowired
    public AccumulationController(MissionsManager missionsManager) {
        this.missionsManager = missionsManager;
    }

    @PostMapping(value = "/win-slot-machine", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WinSlotMachineResponse> winSlotMachine(@RequestBody WinSlotMachineRequest request) {
        try {
            return ResponseEntity.ok(missionsManager.winSlotMachine(request.userId(), request.digits()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
