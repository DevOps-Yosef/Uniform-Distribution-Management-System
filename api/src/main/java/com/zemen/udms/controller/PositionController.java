package com.zemen.udms.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zemen.udms.dto.EntitlementDTO;
import com.zemen.udms.dto.ImsItemDTO;
import com.zemen.udms.dto.PositionCategoryDTO;
import com.zemen.udms.model.Entitlement;
import com.zemen.udms.model.PositionCategory;
import com.zemen.udms.service.PositionService;
import com.zemen.udms.util.UtilService;
import com.zemen.udms.util.UtilService.GenericClass;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/udmsapi/positions")
public class PositionController {
    String officerRole = "Officer", managerRole = "Office Manager", officeManagerRole = "Office Manager";
    
    @Autowired
    private PositionService positionService;

    @Autowired
    private UtilService utilService;

    @PostMapping("/officer/createposition")
    public ResponseEntity<?> createPosition(@RequestBody PositionCategoryDTO positionCategoryDTO,
            HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(officerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        PositionCategory positionCategory = positionService
                .copyPositionCategoryDTOToPositionCategory(positionCategoryDTO);
        boolean status = positionService.createPositionCategory(positionCategory);
        if (status) {
            GenericClass<PositionCategory> positionCategoryInstance = utilService.new GenericClass<>();
            positionCategoryInstance.set(positionCategory);
            utilService.createAccessLog(authentication, request, positionCategoryInstance);
        }
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("/officer/createentitlement")
    public ResponseEntity<?> createEntitlement(@RequestBody EntitlementDTO entitlementDTO,
            HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(officerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        Entitlement entitlement = positionService.copyEntitlementDTOToEntitlement(authentication, entitlementDTO);
        boolean status = positionService.createEntitlement(entitlement);
        if (status) {
            GenericClass<Entitlement> entitlementInstance = utilService.new GenericClass<>();
            entitlementInstance.set(entitlement);
            utilService.createAccessLog(authentication, request, entitlementInstance);
        }
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("/common/index")
    public ResponseEntity<?> index(@RequestBody EntitlementDTO entitlementDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(officerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        List<Entitlement> entitlements;
        if (checkString(entitlementDTO.getPositionCategoryName())) {
            entitlements = positionService
                    .searchApprovedEntitlementsByPositionCategory(entitlementDTO.getPositionCategoryName());
        } else {
            entitlements = positionService.searchApprovedEntitlements();
        }
        List<EntitlementDTO> response = positionService.copyListOfEntitlementsToEntitlementDTOList(entitlements);
        System.out.println("Controller entitlements === " + entitlements.size());
        System.out.println("Controller response === " + response.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/common/positionCategories")
    public ResponseEntity<?> positionCategories() {
        List<PositionCategoryDTO> positionCategoryDTOs = positionService.getAllPositionCategories();
        return new ResponseEntity<>(positionCategoryDTOs, HttpStatus.OK);
    }

    @PostMapping("/officer/items")
    public ResponseEntity<?> items(@RequestBody EntitlementDTO entitlementDTO) {
        List<ImsItemDTO> imsItemDTOs = positionService.getListOfAllItems(entitlementDTO.getPositionCategoryName());
        return new ResponseEntity<>(imsItemDTOs, HttpStatus.OK);
    }

    @PostMapping("/manager/index")
    public ResponseEntity<?> managerIndex(@RequestBody EntitlementDTO entitlementDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(officeManagerRole, managerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        List<Entitlement> entitlements;
        if (checkString(entitlementDTO.getPositionCategoryName())) {
            entitlements = positionService
                    .searchPendingEntitlementsByPositionCategory(entitlementDTO.getPositionCategoryName());
        } else {
            entitlements = positionService.searchPendingEntitlements();
        }
        List<EntitlementDTO> response = positionService.copyListOfEntitlementsToEntitlementDTOList(entitlements);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/manager/approve")
    public ResponseEntity<?> managerApprove(@RequestBody EntitlementDTO entitlementDTO, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(officeManagerRole, managerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        Entitlement entitlement = positionService.copyEntitlementDTOToEntitlementForApprove(authentication, entitlementDTO);
        boolean status = positionService.updateEntitlement(entitlement);
        if (status) {
            GenericClass<Entitlement> entitlementInstance = utilService.new GenericClass<>();
            entitlementInstance.set(entitlement);
            utilService.createAccessLog(authentication, request, entitlementInstance);
        }
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("/manager/reject")
    public ResponseEntity<?> managerReject(@RequestBody EntitlementDTO entitlementDTO, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(officeManagerRole, managerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        Entitlement entitlement = positionService.copyEntitlementDTOToEntitlementForReject(authentication,
                entitlementDTO);
        boolean status = positionService.updateEntitlement(entitlement);
        if (status) {
            GenericClass<Entitlement> entitlementInstance = utilService.new GenericClass<>();
            entitlementInstance.set(entitlement);
            utilService.createAccessLog(authentication, request, entitlementInstance);
        }
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("/officer/edit")
    public ResponseEntity<?> officerEdit(@RequestBody EntitlementDTO entitlementDTO, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(officerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }

        Entitlement entitlement = positionService.copyEntitlementDTOToEntitlementForEdit(authentication,
                entitlementDTO);
        boolean status = positionService.updateEntitlement(entitlement);
        if (status) {
            GenericClass<Entitlement> entitlementInstance = utilService.new GenericClass<>();
            entitlementInstance.set(entitlement);
            utilService.createAccessLog(authentication, request, entitlementInstance);
        }
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    public boolean checkString(String stringToCheck) {
        return (stringToCheck != null && !stringToCheck.isBlank());
    }

}
