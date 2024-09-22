package com.zemen.udms.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zemen.udms.dto.EmployeeProfile;
import com.zemen.udms.dto.EmployeeUniformItemDTO;
import com.zemen.udms.dto.IdNamePairDTO;
import com.zemen.udms.dto.ImsItemDTO;
import com.zemen.udms.dto.LuStatusDTO;
import com.zemen.udms.dto.PositionCategoryDTO;
import com.zemen.udms.dto.UniformIssueDTO;
import com.zemen.udms.dto.UniformIssueDetailDTO;
import com.zemen.udms.dto.UniformRequisitionDTO;
import com.zemen.udms.dto.UniformRequisitionDetailDTO;
import com.zemen.udms.model.Employee;
import com.zemen.udms.model.Entitlement;
import com.zemen.udms.model.LuStatus;
import com.zemen.udms.model.PositionCategory;
import com.zemen.udms.model.UniformIssue;
import com.zemen.udms.model.UniformIssueDetail;
import com.zemen.udms.model.UniformRequisition;
import com.zemen.udms.model.UniformRequisitionDetail;
import com.zemen.udms.repository.EmployeeRepository;
import com.zemen.udms.repository.EntitlementRepository;
import com.zemen.udms.repository.LuStatusRepository;
import com.zemen.udms.repository.PositionCategoryRepository;
import com.zemen.udms.repository.UniformIssueRepository;
import com.zemen.udms.repository.UniformRequisitionRepository;

@Service
@Transactional
public class UniformService {

    @Autowired
    private EntitlementRepository entitlementRepository;

    @Autowired
    private LuStatusRepository luStatusRepository;

    @Autowired
    private PositionCategoryRepository positionCategoryRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UniformRequisitionRepository uniformRequisitionRepository;

    @Autowired
    private UniformIssueRepository uniformIssueRepository;

    Date lastIssueDate = null;
    Date nextIssueDate = null;
    boolean commitQuery = true;

    public boolean createUniformRequisition(UniformRequisition uniformRequisition) {
        UniformRequisition savedUniformRequisition = uniformRequisitionRepository.save(uniformRequisition);
        if (savedUniformRequisition != null) {
            return true;
        }
        return false;
    }

    public boolean createUniformIssue(UniformIssue uniformIssue) {
        UniformIssue savedUniformIssue = uniformIssueRepository.save(uniformIssue);
        if (savedUniformIssue != null) {
            return true;
        }
        return false;
    }

    public boolean updateUniformIssue(UniformIssue uniformIssue) {
        UniformIssue updatedUniformIssue = uniformIssueRepository.save(uniformIssue);
        if (updatedUniformIssue != null) {
            return true;
        }
        return false;
    }

    public boolean updateUniformRequisition(UniformRequisition uniformRequisition) {
        UniformRequisition updatedUniformRequisition = uniformRequisitionRepository.save(uniformRequisition);
        if (updatedUniformRequisition != null) {
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

    public List<ImsItemDTO> getListOfAllItems() {
        List<ImsItemDTO> imsItemDTOs = new ArrayList<ImsItemDTO>();
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

    //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    public long countApprovedRejectedUniformRequisitions(){
        return uniformRequisitionRepository.countApprovedRejectedUniformRequisitions();
    }

    public long countApprovedRejectedUniformRequisitionsByDepartment(String department){ 
        return uniformRequisitionRepository.countApprovedRejectedUniformRequisitionsByDepartment(department);
    }

    public long countApprovedRejectedUniformRequisitionsByPositionCategory(String positionCategory){
        return uniformRequisitionRepository.countApprovedRejectedUniformRequisitionsByPositionCategory(positionCategory);
    }

    public long countApprovedRejectedUniformRequisitionsByDate(Date dateFrom, Date dateTo){
        return uniformRequisitionRepository.countApprovedRejectedUniformRequisitionsByDate(dateFrom, dateTo);
    }

    public long countApprovedRejectedUniformRequisitionsByDateDepartment(Date dateFrom, Date dateTo, String department){
        return uniformRequisitionRepository.countApprovedRejectedUniformRequisitionsByDateDepartment(dateFrom, dateTo, department);
    }

    public long countApprovedRejectedUniformRequisitionsByDatePositionCategory(Date dateFrom, Date dateTo, String positionCategory){
        return uniformRequisitionRepository.countApprovedRejectedUniformRequisitionsByDatePositionCategory(dateFrom, dateTo, positionCategory);
    }

    public long countApprovedRejectedUniformRequisitionsByDepartmentPositionCategory(String department, String positionCategory){
        return uniformRequisitionRepository.countApprovedRejectedUniformRequisitionsByDepartmentPositionCategory(department, positionCategory);
    }

    public long countApprovedRejectedUniformRequisitionsByDateDepartmentPositionCategory(Date dateFrom, Date dateTo, String department, String positionCategory){
        return uniformRequisitionRepository.countApprovedRejectedUniformRequisitionsByDateDepartmentPositionCategory(dateFrom, dateTo, department, positionCategory);
    }

    //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

    public long countApprovedRejectedUniformIssues(){
        return uniformIssueRepository.countApprovedRejectedUniformIssues();
    }

    public long countApprovedRejectedUniformIssuesByDepartment(String department){ 
        return uniformIssueRepository.countApprovedRejectedUniformIssuesByDepartment(department);
    }

    public long countApprovedRejectedUniformIssuesByPositionCategory(String positionCategory){
        return uniformIssueRepository.countApprovedRejectedUniformIssuesByPositionCategory(positionCategory);
    }

    public long countApprovedRejectedUniformIssuesByDate(Date dateFrom, Date dateTo){
        return uniformIssueRepository.countApprovedRejectedUniformIssuesByDate(dateFrom, dateTo);
    }

    public long countApprovedRejectedUniformIssuesByDateDepartment(Date dateFrom, Date dateTo, String department){
        return uniformIssueRepository.countApprovedRejectedUniformIssuesByDateDepartment(dateFrom, dateTo, department);
    }

    public long countApprovedRejectedUniformIssuesByDatePositionCategory(Date dateFrom, Date dateTo, String positionCategory){
        return uniformIssueRepository.countApprovedRejectedUniformIssuesByDatePositionCategory(dateFrom, dateTo, positionCategory);
    }

    public long countApprovedRejectedUniformIssuesByDepartmentPositionCategory(String department, String positionCategory){
        return uniformIssueRepository.countApprovedRejectedUniformIssuesByDepartmentPositionCategory(department, positionCategory);
    }

    public long countApprovedRejectedUniformIssuesByDateDepartmentPositionCategory(Date dateFrom, Date dateTo, String department, String positionCategory){
        return uniformIssueRepository.countApprovedRejectedUniformIssuesByDateDepartmentPositionCategory(dateFrom, dateTo, department, positionCategory);
    }

    //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    public List<IdNamePairDTO> getAllDepartmentNames() {
        List<IdNamePairDTO> idNamePairDTOs = new ArrayList<>();
        List<String> allDepartmentNames = employeeRepository.findAllDepartmentNames().stream()
                .filter(d -> d != null && !d.isBlank())
                .collect(Collectors.toList());
        for (String department : allDepartmentNames) {
            IdNamePairDTO idNamePairDTO = new IdNamePairDTO();
            idNamePairDTO.setId(department.trim());
            idNamePairDTO.setName(department.trim());
            idNamePairDTOs.add(idNamePairDTO);
        }
        return idNamePairDTOs;
    }

    public LuStatus copyLuStatusFromLuStatus(LuStatus luStatus) {
        LuStatus luStatusCopy = new LuStatus();
        luStatusCopy.setId(luStatus.getId());
        return luStatusCopy;
    }

    public List<UniformRequisition> searchPendingUniformRequisitions() {
        return uniformRequisitionRepository.findPendingUniformRequisitions();
    }

    public List<UniformRequisition> searchPendingUniformRequisitionsByDepartment(String department) {
        return uniformRequisitionRepository.findPendingUniformRequisitionsByDepartment(department);
    }

    public List<UniformIssue> searchPendingUniformIssues() {
        return uniformIssueRepository.findPendingUniformIssues();
    }

    public List<UniformIssue> searchPendingUniformIssuesByDepartment(String department) {
        return uniformIssueRepository.findPendingUniformIssuesByDepartment(department);
    }

    //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    public Page<UniformRequisition> searchApprovedRejectedUniformRequisitions(PageRequest pageRequest){
        return uniformRequisitionRepository.findApprovedRejectedUniformRequisitions(pageRequest);
    }

    public Page<UniformRequisition> searchApprovedRejectedUniformRequisitionsByDepartment(String department, PageRequest pageRequest){ 
        return uniformRequisitionRepository.findApprovedRejectedUniformRequisitionsByDepartment(department, pageRequest);
    }

    public Page<UniformRequisition> searchApprovedRejectedUniformRequisitionsByPositionCategory(String positionCategory, PageRequest pageRequest){
        return uniformRequisitionRepository.findApprovedRejectedUniformRequisitionsByPositionCategory(positionCategory, pageRequest);
    }

    public Page<UniformRequisition> searchApprovedRejectedUniformRequisitionsByDate(Date dateFrom, Date dateTo, PageRequest pageRequest){
        return uniformRequisitionRepository.findApprovedRejectedUniformRequisitionsByDate(dateFrom, dateTo, pageRequest);
    }

    public Page<UniformRequisition> searchApprovedRejectedUniformRequisitionsByDateDepartment(Date dateFrom, Date dateTo, String department, PageRequest pageRequest){
        return uniformRequisitionRepository.findApprovedRejectedUniformRequisitionsByDateDepartment(dateFrom, dateTo, department, pageRequest);
    }

    public Page<UniformRequisition> searchApprovedRejectedUniformRequisitionsByDatePositionCategory(Date dateFrom, Date dateTo, String positionCategory, PageRequest pageRequest){
        return uniformRequisitionRepository.findApprovedRejectedUniformRequisitionsByDatePositionCategory(dateFrom, dateTo, positionCategory, pageRequest);
    }

    public Page<UniformRequisition> searchApprovedRejectedUniformRequisitionsByDepartmentPositionCategory(String department, String positionCategory, PageRequest pageRequest){
        return uniformRequisitionRepository.findApprovedRejectedUniformRequisitionsByDepartmentPositionCategory(department, positionCategory, pageRequest);
    }

    public Page<UniformRequisition> searchApprovedRejectedUniformRequisitionsByDateDepartmentPositionCategory(Date dateFrom, Date dateTo, String department, String positionCategory, PageRequest pageRequest){
        return uniformRequisitionRepository.findApprovedRejectedUniformRequisitionsByDateDepartmentPositionCategory(dateFrom, dateTo, department, positionCategory, pageRequest);
    }
    //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

    public List<UniformRequisition> searchApprovedUniformRequisitions(){
        return uniformRequisitionRepository.findApprovedUniformRequisitions();
    }

    public List<UniformRequisition> searchApprovedUniformRequisitionsByDepartment(String department){ 
        return uniformRequisitionRepository.findApprovedUniformRequisitionsByDepartment(department);
    }

    public List<UniformRequisition> searchApprovedUniformRequisitionsByPositionCategory(String positionCategory){
        return uniformRequisitionRepository.findApprovedUniformRequisitionsByPositionCategory(positionCategory);
    }

    public List<UniformRequisition> searchApprovedUniformRequisitionsByDate(Date dateFrom, Date dateTo){
        return uniformRequisitionRepository.findApprovedUniformRequisitionsByDate(dateFrom, dateTo);
    }

    public List<UniformRequisition> searchApprovedUniformRequisitionsByDateDepartment(Date dateFrom, Date dateTo, String department){
        return uniformRequisitionRepository.findApprovedUniformRequisitionsByDateDepartment(dateFrom, dateTo, department);
    }

    public List<UniformRequisition> searchApprovedUniformRequisitionsByDatePositionCategory(Date dateFrom, Date dateTo, String positionCategory){
        return uniformRequisitionRepository.findApprovedUniformRequisitionsByDatePositionCategory(dateFrom, dateTo, positionCategory);
    }

    public List<UniformRequisition> searchApprovedUniformRequisitionsByDepartmentPositionCategory(String department, String positionCategory){
        return uniformRequisitionRepository.findApprovedUniformRequisitionsByDepartmentPositionCategory(department, positionCategory);
    }

    public List<UniformRequisition> searchApprovedUniformRequisitionsByDateDepartmentPositionCategory(Date dateFrom, Date dateTo, String department, String positionCategory){
        return uniformRequisitionRepository.findApprovedUniformRequisitionsByDateDepartmentPositionCategory(dateFrom, dateTo, department, positionCategory);
    }

    //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    public Page<UniformIssue> searchApprovedRejectedUniformIssues(PageRequest pageRequest){
        return uniformIssueRepository.findApprovedRejectedUniformIssues(pageRequest);
    }

    public Page<UniformIssue> searchApprovedRejectedUniformIssuesByDepartment(String department, PageRequest pageRequest){ 
        return uniformIssueRepository.findApprovedRejectedUniformIssuesByDepartment(department, pageRequest);
    }

    public Page<UniformIssue> searchApprovedRejectedUniformIssuesByPositionCategory(String positionCategory, PageRequest pageRequest){
        return uniformIssueRepository.findApprovedRejectedUniformIssuesByPositionCategory(positionCategory, pageRequest);
    }

    public Page<UniformIssue> searchApprovedRejectedUniformIssuesByDate(Date dateFrom, Date dateTo, PageRequest pageRequest){
        return uniformIssueRepository.findApprovedRejectedUniformIssuesByDate(dateFrom, dateTo, pageRequest);
    }

    public Page<UniformIssue> searchApprovedRejectedUniformIssuesByDateDepartment(Date dateFrom, Date dateTo, String department, PageRequest pageRequest){
        return uniformIssueRepository.findApprovedRejectedUniformIssuesByDateDepartment(dateFrom, dateTo, department, pageRequest);
    }

    public Page<UniformIssue> searchApprovedRejectedUniformIssuesByDatePositionCategory(Date dateFrom, Date dateTo, String positionCategory, PageRequest pageRequest){
        return uniformIssueRepository.findApprovedRejectedUniformIssuesByDatePositionCategory(dateFrom, dateTo, positionCategory, pageRequest);
    }

    public Page<UniformIssue> searchApprovedRejectedUniformIssuesByDepartmentPositionCategory(String department, String positionCategory, PageRequest pageRequest){
        return uniformIssueRepository.findApprovedRejectedUniformIssuesByDepartmentPositionCategory(department, positionCategory, pageRequest);
    }

    public Page<UniformIssue> searchApprovedRejectedUniformIssuesByDateDepartmentPositionCategory(Date dateFrom, Date dateTo, String department, String positionCategory, PageRequest pageRequest){
        return uniformIssueRepository.findApprovedRejectedUniformIssuesByDateDepartmentPositionCategory(dateFrom, dateTo, department, positionCategory, pageRequest);
    }
    //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    public List<UniformIssue> searchApprovedUniformIssues(){
        return uniformIssueRepository.findApprovedUniformIssues();
    }

    public List<UniformIssue> searchApprovedUniformIssuesByDepartment(String department){ 
        return uniformIssueRepository.findApprovedUniformIssuesByDepartment(department);
    }

    public List<UniformIssue> searchApprovedUniformIssuesByPositionCategory(String positionCategory){
        return uniformIssueRepository.findApprovedUniformIssuesByPositionCategory(positionCategory);
    }

    public List<UniformIssue> searchApprovedUniformIssuesByDate(Date dateFrom, Date dateTo){
        return uniformIssueRepository.findApprovedUniformIssuesByDate(dateFrom, dateTo);
    }

    public List<UniformIssue> searchApprovedUniformIssuesByDateDepartment(Date dateFrom, Date dateTo, String department){
        return uniformIssueRepository.findApprovedUniformIssuesByDateDepartment(dateFrom, dateTo, department);
    }

    public List<UniformIssue> searchApprovedUniformIssuesByDatePositionCategory(Date dateFrom, Date dateTo, String positionCategory){
        return uniformIssueRepository.findApprovedUniformIssuesByDatePositionCategory(dateFrom, dateTo, positionCategory);
    }

    public List<UniformIssue> searchApprovedUniformIssuesByDepartmentPositionCategory(String department, String positionCategory){
        return uniformIssueRepository.findApprovedUniformIssuesByDepartmentPositionCategory(department, positionCategory);
    }

    public List<UniformIssue> searchApprovedUniformIssuesByDateDepartmentPositionCategory(Date dateFrom, Date dateTo, String department, String positionCategory){
        return uniformIssueRepository.findApprovedUniformIssuesByDateDepartmentPositionCategory(dateFrom, dateTo, department, positionCategory);
    }

    public long countApprovedUniformIssuesByDepartment(String department) {
        return uniformIssueRepository.countApprovedUniformIssuesByDepartment(department);
    }

    public long countApprovedUniformIssuesByDate(Date dateFrom, Date dateTo) {
        return uniformIssueRepository.countApprovedUniformIssuesByDate(dateFrom, dateTo);
    }

    public long countApprovedUniformIssuesByDepartmentDate(String department, Date dateFrom, Date dateTo) {
        return uniformIssueRepository.countApprovedUniformIssuesByDepartmentDate(department, dateFrom, dateTo);
    }

    public long countApprovedUniformIssues() {
        return uniformIssueRepository.countApprovedUniformIssues();
    }
    
    //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

    public List<Employee> searchApprovedUniformIssueEmployeesByPositionCategoryDate(String positionCategory, Date dateFrom, Date dateTo) {
        return uniformIssueRepository.findApprovedUniformIssueEmployeesByPositionCategoryDate(positionCategory, dateFrom, dateTo);
    }

    public List<UniformIssue> searchApprovedUniformIssuesByPositionCategoryDate(String positionCategory, Date dateFrom, Date dateTo) {
        return uniformIssueRepository.findApprovedUniformIssuesByPositionCategoryDate(positionCategory, dateFrom, dateTo);
    }
    

    public UniformIssue copyUniformRequisitionToUniformIssue(Authentication authentication,
            UniformRequisitionDTO uniformRequisitionDTO) {
        UniformRequisition uniformRequisition = uniformRequisitionRepository.findById(uniformRequisitionDTO.getId()).get();
        UniformIssue uniformIssue = new UniformIssue();
        LuStatus luStatus = luStatusRepository.findByName("Pending");
        uniformIssue.setLuStatusId(luStatus);
        uniformIssue.setDepartment(uniformRequisition.getDepartment());
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            String userName = (String) claims.get("preferred_username");
            uniformIssue.setCreatedBy(userName);
        }
        uniformIssue.setCreatedDate(new Date());
        uniformIssue.setUniformIssueDetailCollection(new ArrayList<UniformIssueDetail>());
        uniformRequisition.getUniformRequisitionDetailCollection().forEach(temptUniformRequisitionDetail -> {
            UniformIssueDetail uniformIssueDetail = new UniformIssueDetail();
            UniformRequisitionDetail uniformRequisitionDetail = new UniformRequisitionDetail();
            uniformRequisitionDetail.setId(temptUniformRequisitionDetail.getId());
            uniformRequisitionDetail.setImsItemId(temptUniformRequisitionDetail.getImsItemId());
            uniformRequisitionDetail.setItemName(temptUniformRequisitionDetail.getItemName());
            uniformRequisitionDetail.setSize(temptUniformRequisitionDetail.getSize());
            uniformIssueDetail.setUniformRequisitionDetail(uniformRequisitionDetail);
            uniformIssueDetail.setUniformIssue(uniformIssue);
            uniformIssue.getUniformIssueDetailCollection().add(uniformIssueDetail);
        });
        uniformIssue.setRemark(uniformRequisitionDTO.getRemark());
        return uniformIssue;
    }

    public UniformIssue copyUniformRequisitionToUniformIssueForMakerReject(Authentication authentication,
            UniformRequisitionDTO uniformRequisitionDTO) {
        UniformRequisition uniformRequisition = uniformRequisitionRepository.findById(uniformRequisitionDTO.getId())
                .get();
        UniformIssue uniformIssue = new UniformIssue();
        LuStatus luStatus = luStatusRepository.findByName("Rejected");
        uniformIssue.setLuStatusId(luStatus);
        uniformIssue.setDepartment(uniformRequisition.getDepartment());
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            String userName = (String) claims.get("preferred_username");
            uniformIssue.setCreatedBy(userName);
        }
        uniformIssue.setCreatedDate(new Date());
        uniformIssue.setUniformIssueDetailCollection(new ArrayList<UniformIssueDetail>());
        uniformRequisition.getUniformRequisitionDetailCollection().forEach(temptUniformRequisitionDetail -> {
            UniformIssueDetail uniformIssueDetail = new UniformIssueDetail();
            UniformRequisitionDetail uniformRequisitionDetail = new UniformRequisitionDetail();
            uniformRequisitionDetail.setId(temptUniformRequisitionDetail.getId());
            uniformRequisitionDetail.setImsItemId(temptUniformRequisitionDetail.getImsItemId());
            uniformRequisitionDetail.setItemName(temptUniformRequisitionDetail.getItemName());
            uniformRequisitionDetail.setSize(temptUniformRequisitionDetail.getSize());
            uniformIssueDetail.setUniformRequisitionDetail(uniformRequisitionDetail);
            uniformIssueDetail.setUniformIssue(uniformIssue);
            uniformIssue.getUniformIssueDetailCollection().add(uniformIssueDetail);
        });
        uniformIssue.setRemark(uniformRequisitionDTO.getRemark());
        return uniformIssue;
    }

    public UniformRequisition copyUniformRequisitionDTOToUniformRequisitionForApprove(Authentication authentication,
            UniformRequisitionDTO uniformRequisitionDTO) {
        UniformRequisition uniformRequisition = uniformRequisitionRepository.findById(uniformRequisitionDTO.getId())
                .get();
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            String userName = (String) claims.get("preferred_username");
            uniformRequisition.setApprovedBy(userName);
        }
        uniformRequisition.setApprovedDate(new Date());
        LuStatus luStatus = luStatusRepository.findByName("Approved");
        uniformRequisition.setLuStatusId(luStatus);
        uniformRequisition.setRemark(uniformRequisitionDTO.getRemark());
        return uniformRequisition;
    }

    public UniformIssue copyUniformIssueDTOToUniformIssueForApprove(Authentication authentication,
            UniformIssueDTO uniformIssueDTO) {
        UniformIssue uniformIssue = uniformIssueRepository.findById(uniformIssueDTO.getId())
                .get();
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            String userName = (String) claims.get("preferred_username");
            uniformIssue.setApprovedBy(userName);
        }
        uniformIssue.setApprovedDate(new Date());
        LuStatus luStatus = luStatusRepository.findByName("Approved");
        uniformIssue.setLuStatusId(luStatus);
        uniformIssue.setRemark(uniformIssueDTO.getRemark());
        return uniformIssue;
    }

    public void updateIMS(UniformIssue uniformIssue) {
        String connectionUrl = "jdbc:sqlserver://x.x.x.x:x;databaseName=IMS;user=xxx;password=xxxx;encrypt=true;trustServerCertificate=true";
        Connection con;
        commitQuery = true;
        try {
            con = DriverManager.getConnection(connectionUrl);
            con.setAutoCommit(false);
            uniformIssue.getUniformIssueDetailCollection().forEach(uniformIssueDetail -> {
                int imsItemId = uniformIssueDetail.getUniformRequisitionDetail().getImsItemId();
                uniformIssueDetail.getUniformRequisitionDetail().getUniformRequisitionId()
                        .getEmployeeId().getPositionCategory().getEntitlementCollection().forEach(entitlement -> {
                            entitlement.getEntitlementUniformItemsCollection().forEach(entitlementUniformItems -> {
                                try {
                                    String SQL = "SELECT [GRN_Detail_Id], [item_Id], [remaining_balance], [actual_balance] FROM [GRN_Detail] "
                                            + "join [GRN_Master] on [GRN_Master].[GRN_Number] = [GRN_Detail].[GRN_Number] "
                                            + "WHERE [item_Id] = ? AND [remaining_balance] > 0 order by [GRN_Master].[created_date]";
                                    PreparedStatement stmt = con.prepareStatement(SQL);
                                    stmt.setInt(1, imsItemId);
                                    ResultSet rs = stmt.executeQuery();
                                    int count = 1;
                                    if (entitlementUniformItems.getImsItemId() == imsItemId) {
                                        while (rs.next() && commitQuery) {
                                            for (count = entitlementUniformItems.getQuantity(); count > 0; count--) {
                                                int gRNDetailId = rs.getInt("GRN_Detail_Id");
                                                int remainingBalance = rs.getInt("remaining_balance");
                                                if (remainingBalance >= 1) {
                                                    try {
                                                        String SQL2 = "UPDATE [GRN_Detail] SET [remaining_balance] = ([remaining_balance] - 1), [actual_balance] = ([actual_balance] - 1) "
                                                                + " WHERE [GRN_Detail_Id] = ?";
                                                        PreparedStatement stmt2 = con.prepareStatement(SQL2);
                                                        stmt2.setInt(1, gRNDetailId);
                                                        stmt2.executeUpdate();
                                                    } catch (Exception e) {
                                                    }
                                                }
                                            }
                                        }
                                        if (count > 0) {
                                            commitQuery = false;
                                        }
                                    }
                                } catch (Exception exc) {
                                }
                            });
                        });
            });
            if (commitQuery) {
                con.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public UniformIssue copyUniformIssueDTOToUniformIssueForReject(Authentication authentication,
            UniformIssueDTO uniformIssueDTO) {
        UniformIssue uniformIssue = uniformIssueRepository.findById(uniformIssueDTO.getId())
                .get();
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            String userName = (String) claims.get("preferred_username");
            uniformIssue.setApprovedBy(userName);
        }
        uniformIssue.setApprovedDate(new Date());
        LuStatus luStatus = luStatusRepository.findByName("Rejected");
        uniformIssue.setLuStatusId(luStatus);
        uniformIssue.setRemark(uniformIssueDTO.getRemark());
        return uniformIssue;
    }

    public UniformRequisition copyUniformRequisitionDTOToUniformRequisitionForReject(Authentication authentication,
            UniformRequisitionDTO uniformRequisitionDTO) {
        UniformRequisition uniformRequisition = uniformRequisitionRepository.findById(uniformRequisitionDTO.getId())
                .get();
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            String userName = (String) claims.get("preferred_username");
            uniformRequisition.setApprovedBy(userName);
        }
        uniformRequisition.setApprovedDate(new Date());
        LuStatus luStatus = luStatusRepository.findByName("Rejected");
        uniformRequisition.setLuStatusId(luStatus);
        uniformRequisition.setRemark(uniformRequisitionDTO.getRemark());
        return uniformRequisition;
    }

    public UniformRequisition copyUniformRequisitionDTOToUniformRequisition(Authentication authentication,
            EmployeeProfile employeeProfile) {
        UniformRequisition uniformRequisition = new UniformRequisition();
        LuStatus luStatus = luStatusRepository.findByName("Pending");
        Employee employee = employeeRepository.findById(employeeProfile.getId()).get();
        uniformRequisition.setLuStatusId(luStatus);
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            String userName = (String) claims.get("preferred_username");
            uniformRequisition.setCreatedBy(userName);
        }
        uniformRequisition.setEmployeeId(employee);
        uniformRequisition.setDepartment(employee.getDepartment());
        uniformRequisition.setCreatedDate(new Date());
        uniformRequisition.setUniformRequisitionDetailCollection(new ArrayList<UniformRequisitionDetail>());
        for (UniformRequisitionDTO uniformRequisitionDTO : employeeProfile.getUniformRequisitionCollection()) {
            for (UniformRequisitionDetailDTO uniformRequisitionDetailDTO : uniformRequisitionDTO
                    .getUniformRequisitionDetailCollection()) {
                UniformRequisitionDetail uniformRequisitionDetail = new UniformRequisitionDetail();
                uniformRequisitionDetail.setUniformRequisitionId(uniformRequisition);
                uniformRequisitionDetail.setImsItemId(uniformRequisitionDetailDTO.getImsItemId());
                uniformRequisitionDetail.setItemName(
                        uniformRequisitionDetailDTO.getItemName() != null ? uniformRequisitionDetailDTO.getItemName()
                                : "");
                uniformRequisitionDetail.setSize(uniformRequisitionDetailDTO.getSize());
                uniformRequisition.getUniformRequisitionDetailCollection().add(uniformRequisitionDetail);
            }
        }
        return uniformRequisition;
    }

    public List<EmployeeUniformItemDTO> copyEmployeeUniformItemDTOFromEmployee(Employee employee) {
        List<EmployeeUniformItemDTO> employeeUniformItemDTOs = new ArrayList<EmployeeUniformItemDTO>();
        employee.getUniformRequisitionCollection().forEach(urc -> {
            urc.getUniformRequisitionDetailCollection().forEach(urdc -> {
                urdc.getUniformIssueDetailCollection().forEach(uidc -> {
                    if (uidc.getUniformIssue().getLuStatusId().getName().equalsIgnoreCase("Approved")) {
                        if (urc.getLuStatusId().getName().equalsIgnoreCase("Approved")) {
                            EmployeeUniformItemDTO employeeUniformItemDTO = new EmployeeUniformItemDTO();
                            employeeUniformItemDTO.setIdUniformIssue(String.valueOf(uidc.getUniformIssue().getId()));
                            employeeUniformItemDTO.setItemName(urdc.getItemName());
                            employeeUniformItemDTO.setSize(urdc.getSize());
                            employeeUniformItemDTO.setDate(uidc.getUniformIssue().getApprovedDate());
                            employeeUniformItemDTOs.add(employeeUniformItemDTO);
                        }
                    }
                });
            });
        });

        Collections.sort(employeeUniformItemDTOs, new Comparator<EmployeeUniformItemDTO>() {
            public int compare(EmployeeUniformItemDTO euid1, EmployeeUniformItemDTO euid2) {
                if (euid1.getDate() == null && euid2.getDate() == null) {
                    return 0;
                } else if (euid1.getDate() == null) {
                    return 1;
                } else if (euid2.getDate() == null) {
                    return -1;
                } else {
                    return euid2.getDate().compareTo(euid1.getDate());
                }
            }
        });
        return employeeUniformItemDTOs;
    }

    public List<UniformRequisitionDetailDTO> searchUniformRequisitionDetailDTOList(String id) {
        UniformRequisition uniformRequisition = uniformRequisitionRepository.findById(id).get();
        List<UniformRequisitionDetailDTO> uniformRequisitionDetailDTOs = copyUniformRequisitionDetailDTOFromUniformRequisition(
                uniformRequisition);
        return uniformRequisitionDetailDTOs;
    }

    public List<UniformRequisitionDetailDTO> copyUniformRequisitionDetailDTOFromUniformRequisition(
            UniformRequisition uniformRequisition) {
        List<UniformRequisitionDetailDTO> uniformRequisitionDetailDTOs = new ArrayList<UniformRequisitionDetailDTO>();
        uniformRequisition.getUniformRequisitionDetailCollection().forEach(temptUniformRequisitionDetail -> {
            UniformRequisitionDetailDTO uniformRequisitionDetailDTO = new UniformRequisitionDetailDTO();
            uniformRequisitionDetailDTO.setId(temptUniformRequisitionDetail.getId());
            uniformRequisitionDetailDTO.setImsItemId(temptUniformRequisitionDetail.getImsItemId());
            uniformRequisitionDetailDTO.setItemName(temptUniformRequisitionDetail.getItemName());
            uniformRequisitionDetailDTO.setSize(temptUniformRequisitionDetail.getSize());
            UniformRequisitionDTO uniformRequisitionDTO = new UniformRequisitionDTO();
            uniformRequisitionDTO.setRemark(uniformRequisition.getRemark());
            uniformRequisitionDetailDTO.setUniformRequisitionId(uniformRequisitionDTO);
            uniformRequisitionDetailDTO
                    .setApprovedBy(changeUsernameToSentenceCase(uniformRequisition.getApprovedBy()));
            lastIssueDate = null;
            nextIssueDate = null;
            uniformRequisition.getEmployeeId().getUniformRequisitionCollection().forEach(urc -> {
                urc.getUniformRequisitionDetailCollection().forEach(urdc -> {
                    urdc.getUniformIssueDetailCollection().forEach(uidc -> {
                        if (uidc.getUniformIssue() != null) {
                            if (urc.getLuStatusId().getName().equalsIgnoreCase("Approved")) {
                                if (uidc.getUniformIssue().getLuStatusId().getName().equalsIgnoreCase("Approved")) {
                                    if (temptUniformRequisitionDetail.getImsItemId() == urdc.getImsItemId()) {
                                        if (lastIssueDate != null) {
                                            if (lastIssueDate
                                                    .compareTo(uidc.getUniformIssue().getApprovedDate()) < 0) {
                                                lastIssueDate = uidc.getUniformIssue().getApprovedDate();
                                                uniformRequisition.getEmployeeId().getPositionCategory()
                                                        .getEntitlementCollection().forEach(ec -> {
                                                            ec.getEntitlementUniformItemsCollection()
                                                                    .forEach(euic -> {
                                                                        if (euic.getImsItemId() == urdc
                                                                                .getImsItemId()) {
                                                                            Calendar dateToCal = Calendar
                                                                                    .getInstance();
                                                                            dateToCal.setTime(lastIssueDate);
                                                                            dateToCal.set(
                                                                                    dateToCal.get(Calendar.YEAR)
                                                                                            + euic.getPeriod(),
                                                                                    dateToCal.get(Calendar.MONTH),
                                                                                    dateToCal.get(Calendar.DATE),
                                                                                    0, 0, 0);
                                                                            nextIssueDate = dateToCal.getTime();
                                                                        }
                                                                    });
                                                        });
                                            }
                                        } else {
                                            lastIssueDate = uidc.getUniformIssue().getApprovedDate();
                                            uniformRequisition.getEmployeeId().getPositionCategory()
                                                    .getEntitlementCollection().forEach(ec -> {
                                                        ec.getEntitlementUniformItemsCollection().forEach(euic -> {
                                                            if (euic.getImsItemId() == urdc.getImsItemId()) {
                                                                Calendar dateToCal = Calendar.getInstance();
                                                                dateToCal.setTime(lastIssueDate);
                                                                dateToCal.set(
                                                                        dateToCal.get(Calendar.YEAR)
                                                                                + euic.getPeriod(),
                                                                        dateToCal.get(Calendar.MONTH),
                                                                        dateToCal.get(Calendar.DATE),
                                                                        0, 0, 0);
                                                                nextIssueDate = dateToCal.getTime();
                                                            }
                                                        });
                                                    });
                                        }
                                    }
                                }
                            }
                        }
                    });
                });
            });
            uniformRequisitionDetailDTO.setLastIssueDate(lastIssueDate);
            uniformRequisitionDetailDTO.setNextIssueDate(nextIssueDate);
            uniformRequisitionDetailDTOs.add(uniformRequisitionDetailDTO);
        });
        Collections.sort(uniformRequisitionDetailDTOs, new Comparator<UniformRequisitionDetailDTO>() {
            public int compare(UniformRequisitionDetailDTO urd1, UniformRequisitionDetailDTO urd2) {
                if (urd1.getLastIssueDate() == null && urd2.getLastIssueDate() == null) {
                    return 0;
                } else if (urd1.getLastIssueDate() == null) {
                    return 1;
                } else if (urd2.getLastIssueDate() == null) {
                    return -1;
                } else {
                    return urd2.getLastIssueDate().compareTo(urd1.getLastIssueDate());
                }
            }
        });
        return uniformRequisitionDetailDTOs;
    }

    public List<UniformIssueDetailDTO> searchUniformIssueDetailDTOList(String id) {
        UniformIssue uniformIssue = uniformIssueRepository.findById(id).get();
        List<UniformIssueDetailDTO> uniformIssueDetailDTOs = copyUniformIssueDetailDTOFromUniformIssue(
                uniformIssue);
        return uniformIssueDetailDTOs;
    }

    public List<UniformIssueDetailDTO> copyUniformIssueDetailDTOFromUniformIssue(
            UniformIssue uniformIssue) {
        List<UniformIssueDetailDTO> uniformIssueDetailDTOs = new ArrayList<UniformIssueDetailDTO>();
        uniformIssue.getUniformIssueDetailCollection().forEach(tempUniformIssueDetail -> {
            UniformRequisitionDetail tempUniformRequisitionDetail = tempUniformIssueDetail
                    .getUniformRequisitionDetail();
            UniformIssueDetailDTO uniformIssueDetailDTO = new UniformIssueDetailDTO();
            UniformIssueDTO uniformIssueDTO = new UniformIssueDTO();
            uniformIssueDTO.setRemark(uniformIssue.getRemark());
            uniformIssueDetailDTO.setUniformIssue(uniformIssueDTO);
            UniformRequisitionDetailDTO uniformRequisitionDetailDTO = new UniformRequisitionDetailDTO();
            uniformRequisitionDetailDTO.setId(tempUniformRequisitionDetail.getId());
            uniformRequisitionDetailDTO.setImsItemId(tempUniformRequisitionDetail.getImsItemId());
            uniformRequisitionDetailDTO.setItemName(tempUniformRequisitionDetail.getItemName().contains("Fabric for suit") ? 
            "Suit" : tempUniformRequisitionDetail.getItemName());
            uniformRequisitionDetailDTO.setSize(tempUniformRequisitionDetail.getSize());
            UniformRequisitionDTO uniformRequisitionDTO = new UniformRequisitionDTO();
            uniformRequisitionDTO.setRemark(uniformIssue.getRemark());
            uniformRequisitionDetailDTO.setUniformRequisitionId(uniformRequisitionDTO);
            uniformRequisitionDetailDTO
                    .setApprovedBy(changeUsernameToSentenceCase(uniformIssue.getApprovedBy()));
            lastIssueDate = null;
            nextIssueDate = null;
            tempUniformRequisitionDetail.getUniformRequisitionId().getEmployeeId().getUniformRequisitionCollection()
                    .forEach(urc -> {
                        urc.getUniformRequisitionDetailCollection().forEach(urdc -> {
                            urdc.getUniformIssueDetailCollection().forEach(uidc -> {
                                if (uidc.getUniformIssue() != null) {
                                    if (urc.getLuStatusId().getName().equalsIgnoreCase("Approved")) {
                                        if (uidc.getUniformIssue().getLuStatusId().getName()
                                                .equalsIgnoreCase("Approved")) {
                                            if (tempUniformRequisitionDetail.getImsItemId() == urdc.getImsItemId()) {
                                                if (lastIssueDate != null) {
                                                    if (lastIssueDate
                                                            .compareTo(uidc.getUniformIssue().getApprovedDate()) < 0) {
                                                        lastIssueDate = uidc.getUniformIssue().getApprovedDate();
                                                        tempUniformRequisitionDetail.getUniformRequisitionId()
                                                                .getEmployeeId().getPositionCategory()
                                                                .getEntitlementCollection().forEach(ec -> {
                                                                    ec.getEntitlementUniformItemsCollection()
                                                                            .forEach(euic -> {
                                                                                if (euic.getImsItemId() == urdc
                                                                                        .getImsItemId()) {
                                                                                    Calendar dateToCal = Calendar
                                                                                            .getInstance();
                                                                                    dateToCal.setTime(lastIssueDate);
                                                                                    dateToCal.set(
                                                                                            dateToCal.get(Calendar.YEAR)
                                                                                                    + euic.getPeriod(),
                                                                                            dateToCal.get(
                                                                                                    Calendar.MONTH),
                                                                                            dateToCal
                                                                                                    .get(Calendar.DATE),
                                                                                            0, 0, 0);
                                                                                    nextIssueDate = dateToCal.getTime();
                                                                                }
                                                                            });
                                                                });
                                                    }
                                                } else {
                                                    lastIssueDate = uidc.getUniformIssue().getApprovedDate();
                                                    tempUniformRequisitionDetail.getUniformRequisitionId()
                                                            .getEmployeeId().getPositionCategory()
                                                            .getEntitlementCollection().forEach(ec -> {
                                                                ec.getEntitlementUniformItemsCollection()
                                                                        .forEach(euic -> {
                                                                            if (euic.getImsItemId() == urdc
                                                                                    .getImsItemId()) {
                                                                                Calendar dateToCal = Calendar
                                                                                        .getInstance();
                                                                                dateToCal.setTime(lastIssueDate);
                                                                                dateToCal.set(
                                                                                        dateToCal.get(Calendar.YEAR)
                                                                                                + euic.getPeriod(),
                                                                                        dateToCal.get(Calendar.MONTH),
                                                                                        dateToCal.get(Calendar.DATE),
                                                                                        0, 0, 0);
                                                                                nextIssueDate = dateToCal.getTime();
                                                                            }
                                                                        });
                                                            });
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                        });
                    });
            uniformRequisitionDetailDTO.setLastIssueDate(lastIssueDate);
            uniformRequisitionDetailDTO.setNextIssueDate(nextIssueDate);
            uniformIssueDetailDTO.setUniformRequisitionDetail(uniformRequisitionDetailDTO);
            uniformIssueDetailDTOs.add(uniformIssueDetailDTO);
        });
        Collections.sort(uniformIssueDetailDTOs, new Comparator<UniformIssueDetailDTO>() {
            public int compare(UniformIssueDetailDTO urd1, UniformIssueDetailDTO urd2) {
                if (urd1.getUniformRequisitionDetail().getLastIssueDate() == null
                        && urd2.getUniformRequisitionDetail().getLastIssueDate() == null) {
                    return 0;
                } else if (urd1.getUniformRequisitionDetail().getLastIssueDate() == null) {
                    return 1;
                } else if (urd2.getUniformRequisitionDetail().getLastIssueDate() == null) {
                    return -1;
                } else {
                    return urd2.getUniformRequisitionDetail().getLastIssueDate()
                            .compareTo(urd1.getUniformRequisitionDetail().getLastIssueDate());
                }
            }
        });
        return uniformIssueDetailDTOs;
    }

    public List<UniformRequisitionDTO> copyListOfUniformRequisitionsToUniformRequisitionDTOList(
            List<UniformRequisition> uniformRequisitions) {
        List<UniformRequisitionDTO> uniformRequisitionDTOs = new ArrayList<UniformRequisitionDTO>();
        uniformRequisitions.forEach(tempUniformRequisition -> {
            UniformRequisitionDTO uniformRequisitionDTO = new UniformRequisitionDTO();
            uniformRequisitionDTO.setId(tempUniformRequisition.getId());
            LuStatusDTO luStatusDTO = new LuStatusDTO();
            luStatusDTO.setId(tempUniformRequisition.getLuStatusId().getId());
            luStatusDTO.setName(tempUniformRequisition.getLuStatusId().getName());
            uniformRequisitionDTO.setLuStatusId(luStatusDTO);
            EmployeeProfile employeeProfile = new EmployeeProfile();
            employeeProfile.setId(tempUniformRequisition.getEmployeeId().getId());
            employeeProfile.setName(tempUniformRequisition.getEmployeeId().getName());
            employeeProfile.setBadgeNumber(tempUniformRequisition.getEmployeeId().getBadgeNumber());
            employeeProfile.setGender(tempUniformRequisition.getEmployeeId().getGender());
            employeeProfile.setDepartment(tempUniformRequisition.getEmployeeId().getDepartment());
            PositionCategoryDTO positionCategoryDTO = new PositionCategoryDTO();
            positionCategoryDTO.setId(tempUniformRequisition.getEmployeeId().getPositionCategory().getId());
            positionCategoryDTO.setName(tempUniformRequisition.getEmployeeId().getPositionCategory().getName());
            employeeProfile.setPositionCategory(positionCategoryDTO);
            uniformRequisitionDTO.setEmployeeId(employeeProfile);
            uniformRequisitionDTO.setDepartment(tempUniformRequisition.getDepartment());
            uniformRequisitionDTO.setCreatedBy(changeUsernameToSentenceCase(tempUniformRequisition.getCreatedBy()));
            uniformRequisitionDTO.setCreatedDate(tempUniformRequisition.getCreatedDate());
            uniformRequisitionDTO.setApprovedBy(changeUsernameToSentenceCase(tempUniformRequisition.getApprovedBy()));
            uniformRequisitionDTO.setApprovedDate(tempUniformRequisition.getApprovedDate());
            uniformRequisitionDTOs.add(uniformRequisitionDTO);
        });
        Collections.sort(uniformRequisitionDTOs, new Comparator<UniformRequisitionDTO>() {
            public int compare(UniformRequisitionDTO urd1, UniformRequisitionDTO urd2) {
                if (urd1.getCreatedDate() == null && urd2.getCreatedDate() == null) {
                    return 0;
                } else if (urd1.getCreatedDate() == null) {
                    return 1;
                } else if (urd2.getCreatedDate() == null) {
                    return -1;
                } else {
                    return urd2.getCreatedDate().compareTo(urd1.getCreatedDate());
                }
            }
        });
        return uniformRequisitionDTOs;
    }

    public List<UniformIssueDTO> copyListOfUniformIssuesToUniformIssueDTOList(
            List<UniformIssue> uniformIssues) {
        List<UniformIssueDTO> uniformIssueDTOs = new ArrayList<UniformIssueDTO>();
        uniformIssues.forEach(tempUniformIssue -> {
            UniformIssueDTO uniformIssueDTO = new UniformIssueDTO();
            uniformIssueDTO.setId(tempUniformIssue.getId());
            LuStatusDTO luStatusDTO = new LuStatusDTO();
            luStatusDTO.setId(tempUniformIssue.getLuStatusId().getId());
            luStatusDTO.setName(tempUniformIssue.getLuStatusId().getName());
            uniformIssueDTO.setLuStatusId(luStatusDTO);
            tempUniformIssue.getUniformIssueDetailCollection().forEach(uidc -> {
                Employee employee = uidc.getUniformRequisitionDetail().getUniformRequisitionId().getEmployeeId();
                if (employee != null) {
                    uniformIssueDTO.setName(employee.getName());
                    uniformIssueDTO.setPositionCategoryName(employee.getPositionCategory().getName());
                }
            });
            uniformIssueDTO.setDepartment(tempUniformIssue.getDepartment());
            uniformIssueDTO.setCreatedBy(changeUsernameToSentenceCase(tempUniformIssue.getCreatedBy()));
            uniformIssueDTO.setCreatedDate(tempUniformIssue.getCreatedDate());
            uniformIssueDTO.setApprovedBy(changeUsernameToSentenceCase(tempUniformIssue.getApprovedBy()));
            uniformIssueDTO.setApprovedDate(tempUniformIssue.getApprovedDate());
            uniformIssueDTO.setRemark(tempUniformIssue.getRemark());
            uniformIssueDTOs.add(uniformIssueDTO);
        });
        Collections.sort(uniformIssueDTOs, new Comparator<UniformIssueDTO>() {
            public int compare(UniformIssueDTO uid1, UniformIssueDTO uid2) {
                if (uid1.getCreatedDate() == null && uid2.getCreatedDate() == null) {
                    return 0;
                } else if (uid1.getCreatedDate() == null) {
                    return 1;
                } else if (uid2.getCreatedDate() == null) {
                    return -1;
                } else {
                    return uid2.getCreatedDate().compareTo(uid1.getCreatedDate());
                }
            }
        });
        return uniformIssueDTOs;
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

    public List<PositionCategory> getListOfAllPositionCategories() {
        List<PositionCategory> allPositionCategories = positionCategoryRepository.findAll();
        return allPositionCategories;
    }

    public PositionCategory getPositionCategoryByName(String positionCategoryName) {
        PositionCategory positionCategory = positionCategoryRepository.findByName(positionCategoryName);
        return positionCategory;
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
