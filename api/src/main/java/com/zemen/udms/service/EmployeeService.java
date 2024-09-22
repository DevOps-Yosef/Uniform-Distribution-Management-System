package com.zemen.udms.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import com.zemen.udms.dto.PositionCategoryDTO;
import com.zemen.udms.model.Employee;
import com.zemen.udms.model.LuStatus;
import com.zemen.udms.model.PositionCategory;
import com.zemen.udms.repository.EmployeeRepository;
import com.zemen.udms.repository.PositionCategoryRepository;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
@Transactional
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PositionCategoryRepository positionCategoryRepository;

    public List<EmployeeUniformItemDTO> searchEmployeeUniformItemList(String id) {
        Employee employee = employeeRepository.findById(id).get();
        List<EmployeeUniformItemDTO> employeeUniformItemDTOs = copyEmployeeUniformItemDTOFromEmployee(employee);
        return employeeUniformItemDTOs;
    }

    public List<EmployeeUniformItemDTO> copyEmployeeUniformItemDTOFromEmployee(Employee employee) {
        List<EmployeeUniformItemDTO> employeeUniformItemDTOs = new ArrayList<EmployeeUniformItemDTO>();
        employee.getUniformRequisitionCollection().forEach(urc -> {
            urc.getUniformRequisitionDetailCollection().forEach(urdc -> {
                urdc.getUniformIssueDetailCollection().forEach(uidc -> {
                    if (uidc.getUniformIssue() != null) {
                        if (uidc.getUniformIssue().getLuStatusId().getName().equalsIgnoreCase("Approved")) {
                            if (urc.getLuStatusId().getName().equalsIgnoreCase("Approved")) {
                                EmployeeUniformItemDTO employeeUniformItemDTO = new EmployeeUniformItemDTO();
                                employeeUniformItemDTO.setIdUniformIssue(String.valueOf(uidc.getUniformIssue().getId()));
                                if (urdc.getItemName().equals("Fabric for suit")) {
                                    employeeUniformItemDTO.setItemName("Suit");
                                } else {
                                    employeeUniformItemDTO.setItemName(urdc.getItemName());
                                }
                                employeeUniformItemDTO.setSize(urdc.getSize());
                                employeeUniformItemDTO.setDate(uidc.getUniformIssue().getApprovedDate());
                                employee.getPositionCategory().getEntitlementCollection().forEach(ec -> {
                                    ec.getEntitlementUniformItemsCollection()
                                            .forEach(euic -> {
                                                if (euic.getImsItemId() == urdc
                                                        .getImsItemId()) {
                                                    Calendar dateToCal = Calendar
                                                            .getInstance();
                                                    dateToCal.setTime(employeeUniformItemDTO.getDate());
                                                    dateToCal.set(
                                                            dateToCal.get(Calendar.YEAR)
                                                                    + euic.getPeriod(),
                                                            dateToCal.get(Calendar.MONTH),
                                                            dateToCal.get(Calendar.DATE),
                                                            0, 0, 0);
                                                    employeeUniformItemDTO.setNextDate(dateToCal.getTime());
                                                }
                                            });
                                });
                                employeeUniformItemDTOs.add(employeeUniformItemDTO);
                            }
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

    public Page<Employee> searchEmployees(PageRequest pageRequest) {
        return employeeRepository.findAllOrderByNameAsc(pageRequest);
    }

    public Page<Employee> searchEmployeeByName(String name, PageRequest pageRequest) {
        return employeeRepository.findByName(name, pageRequest);
    }

    public List<Employee> searchAllEmployees() {
        return employeeRepository.findAllEmployees();
    }

    public Page<Employee> searchEmployeeByGender(String gender, PageRequest pageRequest) {
        return employeeRepository.findByGender(gender, pageRequest);
    }

    public Page<Employee> searchEmployeeByPositionCategory(String positionCategory, PageRequest pageRequest) {
        System.out.println("positionCategory === " + positionCategory);
        System.out.println("employeeRepository.findByPositionCategory(positionCategory, pageRequest).getSize() ==== "
                + employeeRepository.findByPositionCategory(positionCategory, pageRequest).getSize());
        return employeeRepository.findByPositionCategory(positionCategory, pageRequest);
    }

    public Page<Employee> searchEmployeeByDepartment(String department, PageRequest pageRequest) {
        return employeeRepository.findByDepartment(department, pageRequest);
    }

    public Page<Employee> searchEmployeeByIsInactive(boolean inactive, PageRequest pageRequest) {
        System.out.println("Service inactive ==== " + inactive);
        System.out.println("Service employeeRepository.findByIsInactive(inactive, pageRequest).getSize() ==== "
                + employeeRepository.findByIsInactive(inactive, pageRequest).getSize());
        return employeeRepository.findByIsInactive(inactive, pageRequest);
    }

    // 22222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222
    public Page<Employee> searchEmployeeByNameGender(String name, String gender, PageRequest pageRequest) {
        return employeeRepository.findByNameGender(name, gender, pageRequest);
    }

    public Page<Employee> searchEmployeeByNamePositionCategory(String name, String positionCategory,
            PageRequest pageRequest) {
        return employeeRepository.findByNamePositionCategory(name, positionCategory, pageRequest);
    }

    public Page<Employee> searchEmployeeByNameDepartment(String name, String department, PageRequest pageRequest) {
        return employeeRepository.findByNameDepartment(name, department, pageRequest);
    }

    public Page<Employee> searchEmployeeByNameIsInactive(String name, boolean isInactive, PageRequest pageRequest) {
        return employeeRepository.findByNameIsInactive(name, isInactive, pageRequest);
    }

    public Page<Employee> searchEmployeeByGenderPositionCategory(String gender, String positionCategory,
            PageRequest pageRequest) {
        return employeeRepository.findByGenderPositionCategory(gender, positionCategory, pageRequest);
    }

    public Page<Employee> searchEmployeeByGenderDepartment(String gender, String department, PageRequest pageRequest) {
        return employeeRepository.findByGenderDepartment(gender, department, pageRequest);
    }

    public Page<Employee> searchEmployeeByGenderIsInactive(String gender, boolean isInactive, PageRequest pageRequest) {
        return employeeRepository.findByGenderIsInactive(gender, isInactive, pageRequest);
    }

    public Page<Employee> searchEmployeeByPositionCategoryDepartment(String positionCategory, String department,
            PageRequest pageRequest) {
        return employeeRepository.findByPositionCategoryDepartment(positionCategory, department, pageRequest);
    }

    public Page<Employee> searchEmployeeByPositionCategoryIsInactive(String positionCategory, boolean isInactive,
            PageRequest pageRequest) {
        return employeeRepository.findByPositionCategoryIsInactive(positionCategory, isInactive, pageRequest);
    }

    public Page<Employee> searchEmployeeByDepartmentIsInactive(String department, boolean isInactive,
            PageRequest pageRequest) {
        return employeeRepository.findByDepartmentIsInactive(department, isInactive, pageRequest);
    }

    // 33333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333
    public Page<Employee> searchEmployeeByNameGenderPositionCategory(String name, String gender,
            String positionCategory, PageRequest pageRequest) {
        return employeeRepository.findByNameGenderPositionCategory(name, gender, positionCategory, pageRequest);
    }

    public Page<Employee> searchEmployeeByNameGenderDepartment(String name, String gender, String department,
            PageRequest pageRequest) {
        return employeeRepository.findByNameGenderDepartment(name, gender, department, pageRequest);
    }

    public Page<Employee> searchEmployeeByNameGenderIsInactive(String name, String gender, boolean isInactive,
            PageRequest pageRequest) {
        return employeeRepository.findByNameGenderIsInactive(name, gender, isInactive, pageRequest);
    }

    public Page<Employee> searchEmployeeByNamePositionCategoryDepartment(String name, String positionCategory,
            String department, PageRequest pageRequest) {
        return employeeRepository.findByNamePositionCategoryDepartment(name, positionCategory, department, pageRequest);
    }

    public Page<Employee> searchEmployeeByNamePositionCategoryIsInactive(String name, String positionCategory,
            boolean isInactive, PageRequest pageRequest) {
        return employeeRepository.findByNamePositionCategoryIsInactive(name, positionCategory, isInactive, pageRequest);
    }

    public Page<Employee> searchEmployeeByNameDepartmentIsInactive(String name, String department, boolean isInactive,
            PageRequest pageRequest) {
        return employeeRepository.findByNameDepartmentIsInactive(name, department, isInactive, pageRequest);
    }

    public Page<Employee> searchEmployeeByGenderPositionCategoryDepartment(String gender, String positionCategory,
            String department, PageRequest pageRequest) {
        return employeeRepository.findByGenderPositionCategoryDepartment(gender, positionCategory, department,
                pageRequest);
    }

    public Page<Employee> searchEmployeeByGenderPositionCategoryIsInactive(String gender, String positionCategory,
            boolean isInactive, PageRequest pageRequest) {
        return employeeRepository.findByGenderPositionCategoryIsInactive(gender, positionCategory, isInactive,
                pageRequest);
    }

    public Page<Employee> searchEmployeeByGenderDepartmentIsInactive(String gender, String department,
            boolean isInactive, PageRequest pageRequest) {
        return employeeRepository.findByGenderDepartmentIsInactive(gender, department, isInactive, pageRequest);
    }

    public Page<Employee> searchEmployeeByPositionCategoryDepartmentIsInactive(String positionCategory,
            String department, boolean isInactive, PageRequest pageRequest) {
        return employeeRepository.findByPositionCategoryDepartmentIsInactive(positionCategory, department, isInactive,
                pageRequest);
    }

    // 44444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444
    public Page<Employee> searchEmployeeByNameGenderPositionCategoryDepartment(String name, String gender,
            String positionCategory, String department, PageRequest pageRequest) {
        return employeeRepository.findByNameGenderPositionCategoryDepartment(name, gender, positionCategory, department,
                pageRequest);
    }

    public Page<Employee> searchEmployeeByNameGenderPositionCategoryIsInactive(String name, String gender,
            String positionCategory, boolean isInactive, PageRequest pageRequest) {
        return employeeRepository.findByNameGenderPositionCategoryIsInactive(name, gender, positionCategory, isInactive,
                pageRequest);
    }

    public Page<Employee> searchEmployeeByNameGenderDepartmentIsInactive(String name, String gender, String department,
            boolean isInactive, PageRequest pageRequest) {
        return employeeRepository.findByNameGenderDepartmentIsInactive(name, gender, department, isInactive,
                pageRequest);
    }

    public Page<Employee> searchEmployeeByNamePositionCategoryDepartmentIsInactive(String name, String positionCategory,
            String department, boolean isInactive, PageRequest pageRequest) {
        return employeeRepository.findByNamePositionCategoryDepartmentIsInactive(name, positionCategory, department,
                isInactive, pageRequest);
    }

    public Page<Employee> searchEmployeeByGenderPositionCategoryDepartmentIsInactive(String gender,
            String positionCategory, String department, boolean isInactive, PageRequest pageRequest) {
        return employeeRepository.findByGenderPositionCategoryDepartmentIsInactive(gender, positionCategory, department,
                isInactive, pageRequest);
    }

    public Page<Employee> searchEmployeeByNameGenderPositionCategoryDepartmentIsInactive(String name, String gender,
            String positionCategory, String department, boolean isInactive, PageRequest pageRequest) {
        return employeeRepository.findByNameGenderPositionCategoryDepartmentIsInactive(name, gender, positionCategory,
                department, isInactive, pageRequest);
    }

    public List<IdNamePairDTO> getAllDepartmentNames() {
        List<IdNamePairDTO> idNamePairDTOs = new ArrayList<>();
        List<String> allDepartmentNames = employeeRepository.findAllDepartmentNames().stream()
                .filter(d -> d != null && !d.isBlank())
                .collect(Collectors.toList());
        for (String department : allDepartmentNames) {
            IdNamePairDTO idNamePairDTO = new IdNamePairDTO();
            idNamePairDTO.setId(department);
            idNamePairDTO.setName(department);
            idNamePairDTOs.add(idNamePairDTO);
        }
        return idNamePairDTOs;
    }

    // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    // 1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111
    public long countEmployees() {
        return employeeRepository.countAll();
    }

    public long countEmployeeByName(String name) {
        return employeeRepository.countByName(name);
    }

    public long countEmployeeByGender(String gender) {
        return employeeRepository.countByGender(gender);
    }

    public long countEmployeeByPositionCategory(String positionCategory) {
        return employeeRepository.countByPositionCategory(positionCategory);
    }

    public long countEmployeeByDepartment(String department) {
        return employeeRepository.countByDepartment(department);
    }

    public long countEmployeeByIsInactive(boolean inactive) {
        return employeeRepository.countByIsInactive(inactive);
    }

    // 22222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222
    public long countEmployeeByNameGender(String name, String gender) {
        return employeeRepository.countByNameGender(name, gender);
    }

    public long countEmployeeByNamePositionCategory(String name, String positionCategory) {
        return employeeRepository.countByNamePositionCategory(name, positionCategory);
    }

    public long countEmployeeByNameDepartment(String name, String department) {
        return employeeRepository.countByNameDepartment(name, department);
    }

    public long countEmployeeByNameIsInactive(String name, boolean isInactive) {
        return employeeRepository.countByNameIsInactive(name, isInactive);
    }

    public long countEmployeeByGenderPositionCategory(String gender, String positionCategory) {
        return employeeRepository.countByGenderPositionCategory(gender, positionCategory);
    }

    public long countEmployeeByGenderDepartment(String gender, String department) {
        return employeeRepository.countByGenderDepartment(gender, department);
    }

    public long countEmployeeByGenderIsInactive(String gender, boolean isInactive) {
        return employeeRepository.countByGenderIsInactive(gender, isInactive);
    }

    public long countEmployeeByPositionCategoryDepartment(String positionCategory, String department) {
        return employeeRepository.countByPositionCategoryDepartment(positionCategory, department);
    }

    public long countEmployeeByPositionCategoryIsInactive(String positionCategory, boolean isInactive) {
        return employeeRepository.countByPositionCategoryIsInactive(positionCategory, isInactive);
    }

    public long countEmployeeByDepartmentIsInactive(String department, boolean isInactive) {
        return employeeRepository.countByDepartmentIsInactive(department, isInactive);
    }

    // 33333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333
    public long countEmployeeByNameGenderPositionCategory(String name, String gender, String positionCategory) {
        return employeeRepository.countByNameGenderPositionCategory(name, gender, positionCategory);
    }

    public long countEmployeeByNameGenderDepartment(String name, String gender, String department) {
        return employeeRepository.countByNameGenderDepartment(name, gender, department);
    }

    public long countEmployeeByNameGenderIsInactive(String name, String gender, boolean isInactive) {
        return employeeRepository.countByNameGenderIsInactive(name, gender, isInactive);
    }

    public long countEmployeeByNamePositionCategoryDepartment(String name, String positionCategory, String department) {
        return employeeRepository.countByNamePositionCategoryDepartment(name, positionCategory, department);
    }

    public long countEmployeeByNamePositionCategoryIsInactive(String name, String positionCategory,
            boolean isInactive) {
        return employeeRepository.countByNamePositionCategoryIsInactive(name, positionCategory, isInactive);
    }

    public long countEmployeeByNameDepartmentIsInactive(String name, String department, boolean isInactive) {
        return employeeRepository.countByNameDepartmentIsInactive(name, department, isInactive);
    }

    public long countEmployeeByGenderPositionCategoryDepartment(String gender, String positionCategory,
            String department) {
        return employeeRepository.countByGenderPositionCategoryDepartment(gender, positionCategory, department);
    }

    public long countEmployeeByGenderPositionCategoryIsInactive(String gender, String positionCategory,
            boolean isInactive) {
        return employeeRepository.countByGenderPositionCategoryIsInactive(gender, positionCategory, isInactive);
    }

    public long countEmployeeByGenderDepartmentIsInactive(String gender, String department, boolean isInactive) {
        return employeeRepository.countByGenderDepartmentIsInactive(gender, department, isInactive);
    }

    public long countEmployeeByPositionCategoryDepartmentIsInactive(String positionCategory, String department,
            boolean isInactive) {
        return employeeRepository.countByPositionCategoryDepartmentIsInactive(positionCategory, department, isInactive);
    }

    // 44444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444
    public long countEmployeeByNameGenderPositionCategoryDepartment(String name, String gender, String positionCategory,
            String department) {
        return employeeRepository.countByNameGenderPositionCategoryDepartment(name, gender, positionCategory,
                department);
    }

    public long countEmployeeByNameGenderPositionCategoryIsInactive(String name, String gender, String positionCategory,
            boolean isInactive) {
        return employeeRepository.countByNameGenderPositionCategoryIsInactive(name, gender, positionCategory,
                isInactive);
    }

    public long countEmployeeByNameGenderDepartmentIsInactive(String name, String gender, String department,
            boolean isInactive) {
        return employeeRepository.countByNameGenderDepartmentIsInactive(name, gender, department, isInactive);
    }

    public long countEmployeeByNamePositionCategoryDepartmentIsInactive(String name, String positionCategory,
            String department, boolean isInactive) {
        return employeeRepository.countByNamePositionCategoryDepartmentIsInactive(name, positionCategory, department,
                isInactive);
    }

    public long countEmployeeByGenderPositionCategoryDepartmentIsInactive(String gender, String positionCategory,
            String department, boolean isInactive) {
        return employeeRepository.countByGenderPositionCategoryDepartmentIsInactive(gender, positionCategory,
                department, isInactive);
    }

    public long countEmployeeByNameGenderPositionCategoryDepartmentIsInactive(String name, String gender,
            String positionCategory, String department, boolean isInactive) {
        return employeeRepository.countByNameGenderPositionCategoryDepartmentIsInactive(name, gender, positionCategory,
                department, isInactive);
    }

    public Optional<Employee> getEmployeeById(String id) {
        return employeeRepository.findById(id);
    }

    public boolean createEmployee(Employee employee) {
        Employee savedEmployee = employeeRepository.save(employee);
        if (savedEmployee != null) {
            return true;
        }
        return false;
    }

    public boolean updateEmployee(Employee employee) {
        Employee updatedEmployee = employeeRepository.save(employee);
        if (updatedEmployee != null) {
            return true;
        }
        return false;
    }

    public void deleteEmployee(String id) {
        employeeRepository.deleteById(id);
    }

    public long getCountOfEmployee() {
        return employeeRepository.count();
    }

    public EmployeeProfile copyEmployeeToEmployeeProfile(Employee employee) {
        EmployeeProfile employeeProfile = new EmployeeProfile();
        employeeProfile.setId(employee.getId());
        PositionCategoryDTO positionCategoryDTO = new PositionCategoryDTO();
        positionCategoryDTO.setId(employee.getPositionCategory().getId());
        positionCategoryDTO.setName(employee.getPositionCategory().getName());
        positionCategoryDTO.setId(employee.getPositionCategory().getDescription());
        employeeProfile.setPositionCategory(positionCategoryDTO);
        employeeProfile.setBadgeNumber(employee.getBadgeNumber());
        employeeProfile.setName(employee.getName());
        employeeProfile.setGender(employee.getGender());
        employeeProfile.setJobPosition(employee.getJobPosition());
        employeeProfile.setJobGrade(employee.getJobGrade());
        employeeProfile.setJobCategory(employee.getJobCategory());
        employeeProfile.setDepartment(employee.getDepartment());
        return employeeProfile;
    }

    public List<EmployeeProfile> copyEmployeeListToEmployeeProfileList(List<Employee> employees) {
        List<EmployeeProfile> employeeProfiles = new ArrayList<EmployeeProfile>();
        for (Employee tempEmployee : employees) {
            EmployeeProfile employeeProfile = new EmployeeProfile();
            employeeProfile.setId(tempEmployee.getId());
            PositionCategoryDTO positionCategoryDTO = new PositionCategoryDTO();
            positionCategoryDTO.setId(tempEmployee.getPositionCategory().getId());
            positionCategoryDTO.setName(tempEmployee.getPositionCategory().getName());
            positionCategoryDTO.setId(tempEmployee.getPositionCategory().getDescription());
            employeeProfile.setPositionCategory(positionCategoryDTO);
            employeeProfile.setPositionCategoryName(positionCategoryDTO.getName());
            employeeProfile.setBadgeNumber(tempEmployee.getBadgeNumber());
            employeeProfile.setName(tempEmployee.getName());
            employeeProfile.setGender(tempEmployee.getGender());
            employeeProfile.setJobPosition(tempEmployee.getJobPosition());
            employeeProfile.setJobGrade(tempEmployee.getJobGrade());
            employeeProfile.setJobCategory(tempEmployee.getJobCategory());
            employeeProfile.setDepartment(tempEmployee.getDepartment());
            employeeProfile.seteMail(tempEmployee.geteMail());
            employeeProfile.setInactive(tempEmployee.getInactive());
            employeeProfile.setStatus(tempEmployee.getInactive() ? "1" : "0");
            employeeProfiles.add(employeeProfile);
        }
        return employeeProfiles;
    }

    public LuStatus copyLuStatusFromLuStatus(LuStatus luStatus) {
        LuStatus luStatusCopy = new LuStatus();
        luStatusCopy.setId(luStatus.getId());
        return luStatusCopy;
    }

    public Employee copyEmployeeProfileToEmployee(Authentication authentication, EmployeeProfile empProfile) {
        Employee employee = new Employee();
        PositionCategory positionCategory = positionCategoryRepository.findByName(empProfile.getPositionCategoryName());
        employee.setPositionCategory(positionCategory);
        employee.setBadgeNumber(empProfile.getBadgeNumber());
        employee.setName(empProfile.getName());
        employee.setGender(empProfile.getGender());
        employee.setJobPosition(empProfile.getJobPosition());
        employee.setJobGrade(empProfile.getJobGrade());
        employee.setJobCategory(empProfile.getJobCategory());
        employee.setDepartment(empProfile.getDepartment());
        employee.setInactive(empProfile.isInactive());
        employee.seteMail(empProfile.geteMail());
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            String userName = (String) claims.get("preferred_username");
            employee.setCreatedBy(userName);
        }
        employee.setCreatedDate(new Date());
        return employee;
    }

    public Employee copyEmployeeProfileToEmployeeForEdit(Authentication authentication, EmployeeProfile empProfile) {
        Employee employee = employeeRepository.findById(empProfile.getId()).get();
        PositionCategory positionCategory = positionCategoryRepository.findByName(empProfile.getPositionCategoryName());
        employee.setPositionCategory(positionCategory);
        employee.setBadgeNumber(empProfile.getBadgeNumber());
        employee.setName(empProfile.getName());
        employee.setGender(empProfile.getGender());
        employee.setJobPosition(empProfile.getJobPosition());
        employee.setJobGrade(empProfile.getJobGrade());
        employee.setJobCategory(empProfile.getJobCategory());
        employee.setDepartment(empProfile.getDepartment());
        employee.setInactive(empProfile.isInactive());
        employee.seteMail(empProfile.geteMail());
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            String userName = (String) claims.get("preferred_username");
            employee.setCreatedBy(userName);
        }
        employee.setCreatedDate(new Date());
        return employee;
    }

    public static byte[] generateExcel() throws IOException {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Example Sheet");
            Row header = sheet.createRow(0);
            Cell headerCell = header.createCell(0);
            headerCell.setCellValue("ID");
            headerCell = header.createCell(1);
            headerCell.setCellValue("Name");
            // Add data rows
            for (int i = 1; i <= 10; i++) {
                Row row = sheet.createRow(i);
                row.createCell(0).setCellValue(i);
                row.createCell(1).setCellValue("Name " + i);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
        }
        return null;
    }

    public String changeUsernameToSentenceCase(String username) {
        username = (username != null && !username.isBlank()) ? username.split("\\.")[0] : "";
        username = (username != null && !username.isBlank())
                ? username.substring(0, 1).toUpperCase() + username.substring(1)
                : "";
        return username;
    }
}
