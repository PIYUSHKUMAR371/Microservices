package com.jindalsaw.it.backend_gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@CrossOrigin(origins = "*")
public class HealthController {

    @GetMapping("/system")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        Map<String, Object> healthData = new HashMap<>();
        
        // Simulating enterprise server telemetry for Motinagar IT infrastructure
        healthData.put("serverStatus", "ONLINE");
        healthData.put("activeNode", "Jindal-Motinagar-GW-01");
        healthData.put("cpuUsagePercent", 34.8);
        healthData.put("memoryUsedMb", 1024);
        healthData.put("memoryTotalMb", 4096);
        healthData.put("uptimeSeconds", ManagementFactory.getRuntimeMXBean().getUptime() / 1000);
        healthData.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(healthData);
    }
}
