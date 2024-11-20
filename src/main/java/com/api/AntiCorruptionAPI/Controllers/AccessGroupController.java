package com.api.AntiCorruptionAPI.Controllers;

import com.api.AntiCorruptionAPI.Models.AccessGroup;
import com.api.AntiCorruptionAPI.Responses.ServiceResponse;
import com.api.AntiCorruptionAPI.Services.AccessGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/access-groups")
public class AccessGroupController {

    private static final Logger logger = LoggerFactory.getLogger(AccessGroupController.class);

    private final AccessGroupService accessGroupService;

    public AccessGroupController(AccessGroupService accessGroupService) {
        this.accessGroupService = accessGroupService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ManageUserGroups')")
    public ResponseEntity<ServiceResponse<List<AccessGroup>>> getAllAccessGroups() {
        try {
            ServiceResponse<List<AccessGroup>> response = accessGroupService.getAllAccessGroups();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error retrieving access groups", e);
            return ResponseEntity
                    .status(500)
                    .body(new ServiceResponse<>(
                            null,
                            "An error occurred while retrieving access groups",
                            org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
}
