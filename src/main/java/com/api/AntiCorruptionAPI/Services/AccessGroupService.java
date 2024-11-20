package com.api.AntiCorruptionAPI.Services;

import com.api.AntiCorruptionAPI.Models.AccessGroup;
import com.api.AntiCorruptionAPI.Repositories.AccessGroupRepository;
import com.api.AntiCorruptionAPI.Responses.ServiceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccessGroupService {

    private final AccessGroupRepository accessGroupRepository;

    public AccessGroupService(AccessGroupRepository accessGroupRepository) {
        this.accessGroupRepository = accessGroupRepository;
    }

    public ServiceResponse<List<AccessGroup>> getAllAccessGroups() {
        List<AccessGroup> accessGroups = accessGroupRepository.findAll();
        return new ServiceResponse<>(
                accessGroups,
                "Access groups retrieved successfully",
                HttpStatus.OK
        );
    }
}