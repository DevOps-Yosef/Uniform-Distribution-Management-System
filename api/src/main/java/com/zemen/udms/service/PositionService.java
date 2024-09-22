package com.zemen.udms.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zemen.udms.dto.EntitlementDTO;
import com.zemen.udms.dto.EntitlementUniformItemsDTO;
import com.zemen.udms.dto.ImsItemDTO;
import com.zemen.udms.dto.LuStatusDTO;
import com.zemen.udms.dto.PositionCategoryDTO;
import com.zemen.udms.model.Entitlement;
import com.zemen.udms.model.EntitlementUniformItems;
import com.zemen.udms.model.LuStatus;
import com.zemen.udms.model.PositionCategory;
import com.zemen.udms.repository.EntitlementRepository;
import com.zemen.udms.repository.EntitlementUniformItemsRepository;
import com.zemen.udms.repository.LuStatusRepository;
import com.zemen.udms.repository.PositionCategoryRepository;

@Service
@Transactional
public class PositionService {

    @Autowired
    private EntitlementRepository entitlementRepository;

    @Autowired
    private LuStatusRepository luStatusRepository;

    @Autowired
    private PositionCategoryRepository positionCategoryRepository;

    @Autowired
    private EntitlementUniformItemsRepository entitlementUniformItemsRepository;

    public boolean createPositionCategory(PositionCategory positionCategory) {
        PositionCategory savedPositionCategory = positionCategoryRepository.save(positionCategory);
        if (savedPositionCategory != null) {
            return true;
        }
        return false;
    }

    public boolean createEntitlement(Entitlement entitlement) {
        Entitlement savedEntitlement = entitlementRepository.save(entitlement);
        if (savedEntitlement != null) {
            return true;
        }
        return false;
    }

    public boolean updateEntitlement(Entitlement entitlement) {
        Entitlement updatedEntitlement = entitlementRepository.save(entitlement);
        if (updatedEntitlement != null) {
            return true;
        }
        return false;
    }

    public List<Entitlement> searchApprovedEntitlements() {
        return entitlementRepository.findApprovedEntitlements();
    }

    public List<Entitlement> searchApprovedEntitlementsByPositionCategory(String positionCategory) {
        return entitlementRepository.findApprovedEntitlementsByPositionCategory(positionCategory);
    }

    public List<ImsItemDTO> getListOfAllItems(String positionCategory) {
        List<ImsItemDTO> imsItemDTOs = new ArrayList<ImsItemDTO>();
        List<ImsItemDTO> imsItemDTOsToRemove = new ArrayList<ImsItemDTO>();
        try {
            String connectionUrl = "jdbc:sqlserver://x.x.x.x:x;databaseName=IMS;user=xxx;password=xxxx;encrypt=true;trustServerCertificate=true";
            Connection con = DriverManager.getConnection(connectionUrl);
            String SQL = "select tli.item_id, tli.item_name from tbl_lu_Items tli join tbl_lu_Category tlc on tli.category_id = tlc.category_id "
                    + "where tlc.category_name = 'Uniform';";
            PreparedStatement stmt = con.prepareStatement(SQL);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ImsItemDTO imsItemDTO = new ImsItemDTO();
                imsItemDTO.setId(Integer.valueOf(rs.getString("item_id")));
                imsItemDTO.setName(rs.getString("item_name"));
                imsItemDTOs.add(imsItemDTO);
            }
            List<Entitlement> entitlements = entitlementRepository.findEntitlementsByPositionCategory(positionCategory);
            List<String> itemNameList = new ArrayList<>();
            entitlements.forEach(entitlement -> {
                entitlement.getEntitlementUniformItemsCollection().forEach(eui -> {
                    if (entitlement.getLuStatusId().getName().equals("Approved")) {
                        itemNameList.add(eui.getItemName());
                    }
                });
            });
            imsItemDTOs.forEach(iid -> {
                if (itemNameList.contains(iid.getName())) {
                    imsItemDTOsToRemove.add(iid);
                }
            });
            imsItemDTOs.removeAll(imsItemDTOsToRemove);
        } catch (Exception e) {
        }
        return imsItemDTOs;
    }

    public long countApprovedEntitlementsByPositionCategory(String positionCategory) {
        return entitlementRepository.countApprovedEntitlementsByPositionCategory(positionCategory);
    }

    public long countApprovedEntitlements() {
        return entitlementRepository.countApprovedEntitlements();
    }

    public long countPendingEntitlementsByPositionCategory(String positionCategory) {
        return entitlementRepository.countPendingEntitlementsByPositionCategory(positionCategory);
    }

    public long countPendingEntitlements() {
        return entitlementRepository.countPendingEntitlements();
    }

    public LuStatus copyLuStatusFromLuStatus(LuStatus luStatus) {
        LuStatus luStatusCopy = new LuStatus();
        luStatusCopy.setId(luStatus.getId());
        return luStatusCopy;
    }

    public List<Entitlement> searchPendingEntitlements() {
        return entitlementRepository.findPendingEntitlements();
    }

    public List<Entitlement> searchPendingEntitlementsByPositionCategory(String positionCategory) {
        return entitlementRepository.findPendingEntitlementsByPositionCategory(positionCategory);
    }

    public Entitlement copyEntitlementDTOToEntitlementForApprove(Authentication authentication,
            EntitlementDTO entitlementDTO) {
        Entitlement entitlement = entitlementRepository.findById(entitlementDTO.getId()).get();
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            String userName = (String) claims.get("preferred_username");
            entitlement.setApprovedBy(userName);
        }
        entitlement.setApprovedDate(new Date());
        LuStatus luStatus = luStatusRepository.findByName("Approved");
        entitlement.setLuStatusId(luStatus);
        entitlement.setRemark(entitlementDTO.getRemark());
        return entitlement;
    }

    public Entitlement copyEntitlementDTOToEntitlementForReject(Authentication authentication,
            EntitlementDTO entitlementDTO) {
        Entitlement entitlement = entitlementRepository.findById(entitlementDTO.getId()).get();
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            String userName = (String) claims.get("preferred_username");
            entitlement.setApprovedBy(userName);
        }
        entitlement.setApprovedDate(new Date());
        LuStatus luStatus = luStatusRepository.findByName("Rejected");
        entitlement.setLuStatusId(luStatus);
        entitlement.setRemark(entitlementDTO.getRemark());
        return entitlement;
    }

    public Entitlement copyEntitlementDTOToEntitlement(Authentication authentication, EntitlementDTO entitlementDTO) {
        Entitlement entitlement = new Entitlement();
        LuStatus luStatus = luStatusRepository.findByName("Pending");
        PositionCategory positionCategory = positionCategoryRepository
                .findByName(entitlementDTO.getPositionCategory().getName());
        entitlement.setLuStatusId(luStatus);
        entitlement.setPositionCategory(positionCategory);
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            String userName = (String) claims.get("preferred_username");
            entitlement.setCreatedBy(userName);
        }
        entitlement.setCreatedDate(new Date());
        entitlement.setLuStatusId(luStatus);
        entitlement.setEntitlementUniformItemsCollection(new ArrayList<EntitlementUniformItems>());
        for (EntitlementUniformItemsDTO entitlementUniformItemsDTO : entitlementDTO
                .getEntitlementUniformItemsCollection()) {
            EntitlementUniformItems entitlementUniformItems = new EntitlementUniformItems();
            entitlementUniformItems.setEntitlement(entitlement);
            entitlementUniformItems.setImsItemId(entitlementUniformItemsDTO.getImsItemId());
            entitlementUniformItems.setItemName(
                    entitlementUniformItemsDTO.getItemName() != null ? entitlementUniformItemsDTO.getItemName() : "");
            entitlementUniformItems.setQuantity(entitlementUniformItemsDTO.getQuantity());
            entitlementUniformItems.setPeriod(entitlementUniformItemsDTO.getPeriod());
            entitlement.getEntitlementUniformItemsCollection().add(entitlementUniformItems);
        }
        return entitlement;
    }

    public List<EntitlementDTO> copyListOfEntitlementsToEntitlementDTOList(List<Entitlement> entitlements) {
        List<EntitlementDTO> entitlementDTOs = new ArrayList<EntitlementDTO>();
        entitlements.forEach(tempEntitlement -> {
            EntitlementDTO entitlementDTO = new EntitlementDTO();
            entitlementDTO.setId(tempEntitlement.getId());
            LuStatusDTO luStatusDTO = new LuStatusDTO();
            luStatusDTO.setId(tempEntitlement.getLuStatusId().getId());
            luStatusDTO.setName(tempEntitlement.getLuStatusId().getName());
            entitlementDTO.setLuStatusDTO(luStatusDTO);
            PositionCategoryDTO positionCategoryDTO = new PositionCategoryDTO();
            positionCategoryDTO.setId(tempEntitlement.getPositionCategory().getId());
            positionCategoryDTO.setName(tempEntitlement.getPositionCategory().getName());
            entitlementDTO.setPositionCategory(positionCategoryDTO);
            entitlementDTO.setEntitlementUniformItemsCollection(new ArrayList<EntitlementUniformItemsDTO>());
            tempEntitlement.getEntitlementUniformItemsCollection().forEach(tempEntitlementUniformItems -> {
                EntitlementUniformItemsDTO entitlementUniformItemsDTO = new EntitlementUniformItemsDTO();
                entitlementUniformItemsDTO.setId(tempEntitlementUniformItems.getId());
                entitlementUniformItemsDTO.setImsItemId(tempEntitlementUniformItems.getImsItemId());
                entitlementUniformItemsDTO.setItemName(tempEntitlementUniformItems.getItemName());
                entitlementUniformItemsDTO.setQuantity(tempEntitlementUniformItems.getQuantity());
                entitlementUniformItemsDTO.setPeriod(tempEntitlementUniformItems.getPeriod());
                entitlementDTO.getEntitlementUniformItemsCollection().add(entitlementUniformItemsDTO);
            });
            entitlementDTO.setCreatedBy(changeUsernameToSentenceCase(tempEntitlement.getCreatedBy()));
            entitlementDTO.setCreatedDate(tempEntitlement.getCreatedDate());
            entitlementDTO.setApprovedBy(changeUsernameToSentenceCase(tempEntitlement.getApprovedBy()));
            entitlementDTO.setApprovedDate(tempEntitlement.getApprovedDate());
            entitlementDTO.setRemark(tempEntitlement.getRemark());
            entitlementDTOs.add(entitlementDTO);
        });
        return entitlementDTOs;
    }

    public Entitlement copyEntitlementDTOToEntitlementForEdit(Authentication authentication,
            EntitlementDTO entitlementDTO) {
        Entitlement entitlement = entitlementRepository.findEntitlementById(entitlementDTO.getId());
        LuStatus luStatus = luStatusRepository.findByName("Pending");
        entitlement.setLuStatusId(luStatus);
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            String userName = (String) claims.get("preferred_username");
            entitlement.setCreatedBy(userName);
        }
        entitlement.setCreatedDate(new Date());
        EntitlementUniformItemsDTO entitlementUniformItemsDTO = entitlementDTO.getEntitlementUniformItemsCollection()
                .iterator().next();
        if (entitlementUniformItemsDTO != null) {
            for (EntitlementUniformItems entitlementUniformItems : entitlement.getEntitlementUniformItemsCollection()) {
                if (entitlementUniformItems.getId().equalsIgnoreCase(entitlementUniformItemsDTO.getId())) {
                    entitlementUniformItems.setQuantity(entitlementUniformItemsDTO.getQuantity());
                    entitlementUniformItems.setPeriod(entitlementUniformItemsDTO.getPeriod());
                }
            }
        }
        return entitlement;
    }

    public EntitlementUniformItems copyEntitlementUniformItemsDTOToEntitlementUniformItemsForEdit(
            Authentication authentication, EntitlementUniformItemsDTO entitlementUniformItemsDTO) {
        EntitlementUniformItems entitlementUniformItems = entitlementUniformItemsRepository
                .findById(entitlementUniformItemsDTO.getId()).get();
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            String userName = (String) claims.get("preferred_username");
            entitlementUniformItems.getEntitlement().setCreatedBy(userName);
        }
        entitlementUniformItems.getEntitlement().setCreatedDate(new Date());
        LuStatus luStatus = luStatusRepository.findByName("Pending");
        entitlementUniformItems.getEntitlement().setLuStatusId(luStatus);
        entitlementUniformItems.setQuantity(entitlementUniformItemsDTO.getQuantity());
        entitlementUniformItems.setPeriod(entitlementUniformItemsDTO.getPeriod());
        return entitlementUniformItems;
    }

    public List<PositionCategoryDTO> getAllPositionCategories() {
        List<PositionCategory> allPositionCategories = positionCategoryRepository.findAll();
        List<PositionCategoryDTO> positionCategoryDTOs = new ArrayList<>();
        for (PositionCategory positionCategory : allPositionCategories) {
            PositionCategoryDTO positionCategoryDTO = new PositionCategoryDTO();
            positionCategoryDTO.setId(positionCategory.getId());
            positionCategoryDTO.setName(positionCategory.getName());
            positionCategoryDTO.setDescription(positionCategory.getDescription());
            positionCategoryDTOs.add(positionCategoryDTO);
        }
        return positionCategoryDTOs;
    }

    public PositionCategoryDTO copyPositionCategoryToPositionCategoryDTO(PositionCategory positionCategory) {
        PositionCategoryDTO positionCategoryDTO = new PositionCategoryDTO();
        positionCategoryDTO.setId(positionCategory.getId());
        positionCategoryDTO.setName(positionCategory.getName());
        positionCategoryDTO.setId(positionCategory.getDescription());
        return positionCategoryDTO;
    }

    public PositionCategory copyPositionCategoryDTOToPositionCategory(PositionCategoryDTO positionCategoryDTO) {
        PositionCategory positionCategory = new PositionCategory();
        positionCategory.setName(positionCategoryDTO.getName());
        positionCategory.setDescription(positionCategoryDTO.getDescription());
        return positionCategory;
    }

    public String changeUsernameToSentenceCase(String username) {
        username = (username != null && !username.isBlank()) ? username.split("\\.")[0] : "";
        username = (username != null && !username.isBlank())
                ? username.substring(0, 1).toUpperCase() + username.substring(1)
                : "";
        return username;
    }
}
