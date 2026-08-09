package com.nageshwarsaini.dynamic.workflows.controller;

import com.nageshwarsaini.dynamic.workflows.worker.DSLWorkflowWorkers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Internal REST controller for managing tenants dynamically.
 *
 * @author nageshwarsaini
 */
@RestController
@RequestMapping("/api/internal/tenants")
public class TenantManagementController {

    private final DSLWorkflowWorkers workers;

    public TenantManagementController(DSLWorkflowWorkers workers) {
        this.workers = workers;
    }

    /**
     * Registers a new tenant by dynamically provisioning their Temporal namespace
     * and starting the worker polling loop.
     *
     * @param request body containing the tenantId
     * @return a success message
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerTenant(@RequestBody Map<String, String> request) {
        String tenantId = request.get("tenantId");
        if (tenantId == null || tenantId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("tenantId is required in the request body");
        }

        try {
            workers.registerAndStartTenant(tenantId.trim());
            return ResponseEntity.ok("Successfully registered tenant: " + tenantId.trim());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to register tenant: " + e.getMessage());
        }
    }
}
