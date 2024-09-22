package com.zemen.udms.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zemen.udms.dto.EmployeeProfile;
import com.zemen.udms.dto.IdNamePairDTO;
import com.zemen.udms.dto.ImsItemDTO;
import com.zemen.udms.dto.NameObjectDTO;
import com.zemen.udms.dto.PositionCategoryDTO;
import com.zemen.udms.dto.UniformIssueDTO;
import com.zemen.udms.dto.UniformIssueDetailDTO;
import com.zemen.udms.dto.UniformRequisitionDTO;
import com.zemen.udms.dto.UniformRequisitionDetailDTO;
import com.zemen.udms.model.Employee;
import com.zemen.udms.model.PositionCategory;
import com.zemen.udms.model.UniformIssue;
import com.zemen.udms.model.UniformIssueDetail;
import com.zemen.udms.model.UniformRequisition;
import com.zemen.udms.model.UniformRequisitionDetail;
import com.zemen.udms.service.EmployeeService;
import com.zemen.udms.service.PositionService;
import com.zemen.udms.service.UniformService;
import com.zemen.udms.util.UtilService;
import com.zemen.udms.util.UtilService.GenericClass;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/udmsapi/uniforms")
public class UniformController {
    String officerRole = "Officer", managerRole = "Manager", officeManagerRole = "Office Manager", csrRole = "CSR";
    String name = "", email = "", department = "";
    int itemNameStartColumnNumber = 0;
    int rowIdx = 0;
    int noOfItems = 0;
    int yearCounter = 1;
    Row row;
    Row headerRow;
    ArrayList<String> entitledItems = new ArrayList<String>();

    @Autowired
    private PositionService positionService;

    @Autowired
    private UniformService uniformService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UtilService utilService;

    @PostMapping("/csr/index")
    public ResponseEntity<?> index(@RequestBody EmployeeProfile eP) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(csrRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        PageRequest pageRequest = PageRequest.of(0, 10000);
        List<Employee> employees = new ArrayList<>();
        List<Employee> employees2 = new ArrayList<>();
        List<EmployeeProfile> response;
        if (checkString(eP.getName()) && !checkString(eP.getGender()) && !checkString(eP.getPositionCategoryName())) {
            employees = employeeService.searchEmployeeByName(eP.getName(), pageRequest).toList();
        } else if (!checkString(eP.getName()) && checkString(eP.getGender())
                && !checkString(eP.getPositionCategoryName())) {
            employees = employeeService.searchEmployeeByGender(eP.getGender(), pageRequest).toList();
        } else if (!checkString(eP.getName()) && !checkString(eP.getGender())
                && checkString(eP.getPositionCategoryName())) {
            employees = employeeService.searchEmployeeByPositionCategory(eP.getPositionCategoryName(), pageRequest)
                    .toList();
        } else if (checkString(eP.getName()) && checkString(eP.getGender())
                && !checkString(eP.getPositionCategoryName())) {
            employees = employeeService.searchEmployeeByNameGender(eP.getName(), eP.getGender(), pageRequest).toList();
        } else if (checkString(eP.getName()) && checkString(eP.getPositionCategoryName())
                && !checkString(eP.getGender())) {
            employees = employeeService
                    .searchEmployeeByNamePositionCategory(eP.getName(), eP.getPositionCategoryName(), pageRequest)
                    .toList();
        } else if (!checkString(eP.getName()) && checkString(eP.getGender())
                && checkString(eP.getPositionCategoryName())) {
            employees = employeeService
                    .searchEmployeeByGenderPositionCategory(eP.getGender(), eP.getPositionCategoryName(), pageRequest)
                    .toList();
        } else if (checkString(eP.getName()) && checkString(eP.getGender())
                && checkString(eP.getPositionCategoryName())) {
            employees = employeeService.searchEmployeeByNameGenderPositionCategory(eP.getName(), eP.getGender(),
                    eP.getPositionCategoryName(), pageRequest).toList();
        } else {
            employees = employeeService.searchEmployees(pageRequest).toList();
        }
        List<Employee> employees3 = employeeService.searchEmployees(pageRequest).toList();
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            name = (String) claims.get("name");
            email = (String) claims.get("preferred_username") + "@zemenbank.com";
        }
        department = "";
        itemNameStartColumnNumber = 0;
        employees3.forEach(employee -> {
            String tempName = "";
            String[] tempNameArray = employee.getName().trim().split("\\s+");
            tempName += tempNameArray.length > 0 ? tempNameArray[0] : "";
            tempName += tempNameArray.length > 1 ? " " + tempNameArray[1] : "";
            if (name.equalsIgnoreCase(tempName)) {
                String tempDepartment = employee.getDepartment() != null ? employee.getDepartment().trim() : "";
                department = name.equalsIgnoreCase(tempName) ? tempDepartment : "";
                itemNameStartColumnNumber++;
            }
        });
        if (itemNameStartColumnNumber == 1) {
            employees.forEach(employee -> {
                String tempDepartment = employee.getDepartment() != null ? employee.getDepartment().trim() : "";
                if (department.equalsIgnoreCase(tempDepartment)) {
                    employees2.add(employee);
                }
            });
        } else if (itemNameStartColumnNumber > 1) {
            department = "";
            employees3.forEach(employee -> {
                String tempEmail = employee.geteMail();
                String tempDepartment = employee.getDepartment() != null ? employee.getDepartment().trim() : "";
                department = email.equalsIgnoreCase(tempEmail) ? tempDepartment : "";
            });
            employees.forEach(employee -> {
                String tempDepartment = employee.getDepartment() != null ? employee.getDepartment().trim() : "";
                if (department.equalsIgnoreCase(tempDepartment)) {
                    employees2.add(employee);
                }
            });
        }
        response = employeeService.copyEmployeeListToEmployeeProfileList(employees2);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/csr/createrequest")
    public ResponseEntity<?> createRequest(@RequestBody EmployeeProfile employeeProfile,
            HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(csrRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        UniformRequisition uniformRequisition = uniformService.copyUniformRequisitionDTOToUniformRequisition(
                authentication,
                employeeProfile);
        boolean status = uniformService.createUniformRequisition(uniformRequisition);
        if (status) {
            GenericClass<UniformRequisition> uniformRequisitionInstance = utilService.new GenericClass<>();
            uniformRequisitionInstance.set(uniformRequisition);
            utilService.createAccessLog(authentication, request, uniformRequisitionInstance);
        }
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("/common/isbranchuser")
    public ResponseEntity<?> isBranchUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean status = false;
        List<String> roleList = utilService.listAllUserRoles(authentication);
        if(!roleList.contains(officerRole) && !roleList.contains(officeManagerRole))
        {
            status = true;
        }
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("/csr/items")
    public ResponseEntity<?> items() {
        List<ImsItemDTO> imsItemDTOs = uniformService.getListOfAllItems();
        return new ResponseEntity<>(imsItemDTOs, HttpStatus.OK);
    }

    @PostMapping("/common/positionCategories")
    public ResponseEntity<?> positionCategories() {
        List<PositionCategoryDTO> positionCategoryDTOs = positionService.getAllPositionCategories();
        return new ResponseEntity<>(positionCategoryDTOs, HttpStatus.OK);
    }

    @PostMapping("/common/departmentNames")
    public ResponseEntity<?> departmentNames() {
        List<IdNamePairDTO> idNamePairDTOs = uniformService.getAllDepartmentNames();
        return new ResponseEntity<>(idNamePairDTOs, HttpStatus.OK);
    }

    @PostMapping("/common/selectedDepartment")
    public ResponseEntity<?> selectedDepartment() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            name = (String) claims.get("name");
            email = (String) claims.get("preferred_username") + "@zemenbank.com";
        }
        NameObjectDTO nameObjectDTO = new NameObjectDTO();
        List<Employee> employees = employeeService.searchAllEmployees();
        for (Employee employee : employees) {
            String tempName = "";
            String[] tempNameArray = employee.getName().trim().split("\\s+");
            tempName += tempNameArray.length > 0 ? tempNameArray[0].trim() : "";
            tempName += tempNameArray.length > 1 ? " " + tempNameArray[1].trim() : "";
            String tempEmail = employee.geteMail();
            if (name.equalsIgnoreCase(tempName) || email.equalsIgnoreCase(tempEmail)) {               
                String tempDepartment = employee.getDepartment() != null ? employee.getDepartment().trim() : "";
                nameObjectDTO.setName(name.equalsIgnoreCase(tempName) ? tempDepartment : "");
                break;
            }
        }
        return new ResponseEntity<>(nameObjectDTO, HttpStatus.OK);
    }

    @PostMapping("/common/requestarchive/count")
    public ResponseEntity<?> commonRequestArchiveCount(@RequestBody UniformRequisitionDTO urd) {
        long uniformRequestCount = 0;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(csrRole, officerRole, officeManagerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            name = (String) claims.get("name");
            email = (String) claims.get("preferred_username") + "@zemenbank.com";
        }
        List<String> roleList = utilService.listAllUserRoles(authentication);
        if(roleList.contains(officerRole) || roleList.contains(officeManagerRole))
        {
            if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && !checkString(urd.getDepartment()) && !checkString(urd.getPositionCategoryName())) {
                uniformRequestCount = uniformService.countApprovedRejectedUniformRequisitionsByDate(urd.getDateFrom(),
                getDateTo(urd.getDateTo()));
            }
            else if ((!checkDate(urd.getDateFrom()) || !checkDate(urd.getDateTo())) && checkString(urd.getDepartment()) && !checkString(urd.getPositionCategoryName()))
            {
                uniformRequestCount = uniformService.countApprovedRejectedUniformRequisitionsByDepartment(urd.getDepartment());
            }
            else if ((!checkDate(urd.getDateFrom()) || !checkDate(urd.getDateTo())) && !checkString(urd.getDepartment()) && checkString(urd.getPositionCategoryName()))
            {
                uniformRequestCount = uniformService.countApprovedRejectedUniformRequisitionsByPositionCategory(urd.getPositionCategoryName());
            }
            else if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && checkString(urd.getDepartment()) && !checkString(urd.getPositionCategoryName()))
            {
                uniformRequestCount = uniformService.countApprovedRejectedUniformRequisitionsByDateDepartment(urd.getDateFrom(),
                getDateTo(urd.getDateTo()), urd.getDepartment());
            }
            else if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && !checkString(urd.getDepartment()) && checkString(urd.getPositionCategoryName()))
            {
                uniformRequestCount = uniformService.countApprovedRejectedUniformRequisitionsByDatePositionCategory(urd.getDateFrom(),
                getDateTo(urd.getDateTo()), urd.getPositionCategoryName());
            }
            else if ((!checkDate(urd.getDateFrom()) || !checkDate(urd.getDateTo())) && checkString(urd.getDepartment()) && checkString(urd.getPositionCategoryName()))
            {
                uniformRequestCount = uniformService.countApprovedRejectedUniformRequisitionsByDepartmentPositionCategory(urd.getDepartment(), urd.getPositionCategoryName());
            }
            else if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && checkString(urd.getDepartment()) && checkString(urd.getPositionCategoryName()))
            {
                uniformRequestCount = uniformService.countApprovedRejectedUniformRequisitionsByDateDepartmentPositionCategory(urd.getDateFrom(),
                getDateTo(urd.getDateTo()), urd.getDepartment(), urd.getPositionCategoryName());
            }
            else {
                uniformRequestCount = uniformService.countApprovedRejectedUniformRequisitions();
            }
        }
        else
        {
            List<Employee> employees = employeeService.searchAllEmployees();
            department = "";
            for (Employee employee : employees) {
                String tempName = "";
                String[] tempNameArray = employee.getName().trim().split("\\s+");
                tempName += tempNameArray.length > 0 ? tempNameArray[0].trim() : "";
                tempName += tempNameArray.length > 1 ? " " + tempNameArray[1].trim() : "";
                String tempEmail = employee.geteMail();
                if (name.equalsIgnoreCase(tempName) || email.equalsIgnoreCase(tempEmail)) {
                    String tempDepartment = employee.getDepartment() != null ? employee.getDepartment().trim() : "";
                    department = name.equalsIgnoreCase(tempName) ? tempDepartment : "";
                    break;
                }
            }
            if ((!checkDate(urd.getDateFrom()) || !checkDate(urd.getDateTo())) && !checkString(urd.getPositionCategoryName()))
            {
                uniformRequestCount = uniformService.countApprovedRejectedUniformRequisitionsByDepartment(department);
            }
            else if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && !checkString(urd.getPositionCategoryName()))
            {
                uniformRequestCount = uniformService.countApprovedRejectedUniformRequisitionsByDateDepartment(urd.getDateFrom(),
                getDateTo(urd.getDateTo()), department);
            }
            else if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && checkString(urd.getPositionCategoryName()))
            {
                uniformRequestCount = uniformService.countApprovedRejectedUniformRequisitionsByDateDepartmentPositionCategory(urd.getDateFrom(),
                getDateTo(urd.getDateTo()), department, urd.getPositionCategoryName());
            }
        }
        return new ResponseEntity<>(uniformRequestCount, HttpStatus.OK);
    }

    @PostMapping("/common/requestarchive")
    public ResponseEntity<?> commonRequestArchiveIndex(@RequestBody UniformRequisitionDTO urd) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(csrRole, officerRole, officeManagerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(urd.getPage()), Integer.parseInt(urd.getRowsPerPage()));
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            name = (String) claims.get("name");
            email = (String) claims.get("preferred_username") + "@zemenbank.com";
        }
        List<UniformRequisition> uniformRequisitions = new ArrayList<UniformRequisition>();
        List<String> roleList = utilService.listAllUserRoles(authentication);
        if(roleList.contains(officerRole) || roleList.contains(officeManagerRole))
        {
            if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && !checkString(urd.getDepartment()) && !checkString(urd.getPositionCategoryName())) {
                uniformRequisitions = uniformService.searchApprovedRejectedUniformRequisitionsByDate(urd.getDateFrom(),
                getDateTo(urd.getDateTo()), pageRequest).toList();
            }
            else if ((!checkDate(urd.getDateFrom()) || !checkDate(urd.getDateTo())) && checkString(urd.getDepartment()) && !checkString(urd.getPositionCategoryName()))
            {
                uniformRequisitions = uniformService.searchApprovedRejectedUniformRequisitionsByDepartment(urd.getDepartment(), pageRequest).toList();
            }
            else if ((!checkDate(urd.getDateFrom()) || !checkDate(urd.getDateTo())) && !checkString(urd.getDepartment()) && checkString(urd.getPositionCategoryName()))
            {
                uniformRequisitions = uniformService.searchApprovedRejectedUniformRequisitionsByPositionCategory(urd.getPositionCategoryName(), pageRequest).toList();
            }
            else if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && checkString(urd.getDepartment()) && !checkString(urd.getPositionCategoryName()))
            {
                uniformRequisitions = uniformService.searchApprovedRejectedUniformRequisitionsByDateDepartment(urd.getDateFrom(),
                getDateTo(urd.getDateTo()), urd.getDepartment() , pageRequest).toList();
            }
            else if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && !checkString(urd.getDepartment()) && checkString(urd.getPositionCategoryName()))
            {
                uniformRequisitions = uniformService.searchApprovedRejectedUniformRequisitionsByDatePositionCategory(urd.getDateFrom(),
                getDateTo(urd.getDateTo()), urd.getPositionCategoryName() , pageRequest).toList();
            }
            else if ((!checkDate(urd.getDateFrom()) || !checkDate(urd.getDateTo())) && checkString(urd.getDepartment()) && checkString(urd.getPositionCategoryName()))
            {
                uniformRequisitions = uniformService.searchApprovedRejectedUniformRequisitionsByDepartmentPositionCategory(urd.getDepartment(), urd.getPositionCategoryName(), pageRequest).toList();
            }
            else if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && checkString(urd.getDepartment()) && checkString(urd.getPositionCategoryName()))
            {
                uniformRequisitions = uniformService.searchApprovedRejectedUniformRequisitionsByDateDepartmentPositionCategory(urd.getDateFrom(),
                getDateTo(urd.getDateTo()), urd.getDepartment(), urd.getPositionCategoryName(), pageRequest).toList();
            }
            else {
                uniformRequisitions = uniformService.searchApprovedRejectedUniformRequisitions(pageRequest).toList();
            }
        }
        else
        {
            List<Employee> employees = employeeService.searchAllEmployees();
            department = "";
            for (Employee employee : employees) {
                String tempName = "";
                String[] tempNameArray = employee.getName().trim().split("\\s+");
                tempName += tempNameArray.length > 0 ? tempNameArray[0].trim() : "";
                tempName += tempNameArray.length > 1 ? " " + tempNameArray[1].trim() : "";
                String tempEmail = employee.geteMail();
                if (name.equalsIgnoreCase(tempName) || email.equalsIgnoreCase(tempEmail)) {  
                    String tempDepartment = employee.getDepartment() != null ? employee.getDepartment().trim() : "";
                    department = name.equalsIgnoreCase(tempName) ? tempDepartment : "";
                    break;
                }
            }
            if ((!checkDate(urd.getDateFrom()) || !checkDate(urd.getDateTo())) && !checkString(urd.getPositionCategoryName()))
            {
                uniformRequisitions = uniformService.searchApprovedRejectedUniformRequisitionsByDepartment(department, pageRequest).toList();
            }
            else if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && !checkString(urd.getPositionCategoryName()))
            {
                uniformRequisitions = uniformService.searchApprovedRejectedUniformRequisitionsByDateDepartment(urd.getDateFrom(),
                getDateTo(urd.getDateTo()), department, pageRequest).toList();
            } 
            else if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && checkString(urd.getPositionCategoryName()))
            {
                uniformRequisitions = uniformService.searchApprovedRejectedUniformRequisitionsByDateDepartmentPositionCategory(urd.getDateFrom(),
                getDateTo(urd.getDateTo()), department, urd.getPositionCategoryName(), pageRequest).toList();
            }
        }
        List<UniformRequisitionDTO> response = uniformService
                .copyListOfUniformRequisitionsToUniformRequisitionDTOList(uniformRequisitions);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    
    @PostMapping("/common/requestarchivedownloadsignature")
    public ResponseEntity<?> requestArchiveDownloadSignature(@RequestBody UniformRequisitionDTO urd) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(csrRole, officerRole, officeManagerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Uniform Needs Collection Report");
            if (authentication instanceof JwtAuthenticationToken) {
                JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
                Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
                name = (String) claims.get("name");
                email = (String) claims.get("preferred_username") + "@zemenbank.com";
            }
            List<UniformRequisition> uniformRequisitions = new ArrayList<>();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
            String title = "Uniform Needs Collection";
            String titleDate = "";
            List<String> roleList = utilService.listAllUserRoles(authentication);
            if(roleList.contains(officerRole) || roleList.contains(officeManagerRole))
            {
                if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && !checkString(urd.getDepartment()) && !checkString(urd.getPositionCategoryName())) {
                    titleDate = "(" + formatter.format(urd.getDateFrom()) + "/" + formatter.format(urd.getDateTo()) + ")";
                    uniformRequisitions = uniformService.searchApprovedUniformRequisitionsByDate(urd.getDateFrom(),
                    getDateTo(urd.getDateTo()));
                }
                else if ((!checkDate(urd.getDateFrom()) || !checkDate(urd.getDateTo())) && checkString(urd.getDepartment()) && !checkString(urd.getPositionCategoryName()))
                {
                    uniformRequisitions = uniformService.searchApprovedUniformRequisitionsByDepartment(urd.getDepartment());
                }
                else if ((!checkDate(urd.getDateFrom()) || !checkDate(urd.getDateTo())) && !checkString(urd.getDepartment()) && checkString(urd.getPositionCategoryName()))
                {
                    uniformRequisitions = uniformService.searchApprovedUniformRequisitionsByPositionCategory(urd.getPositionCategoryName());
                }
                else if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && checkString(urd.getDepartment()) && !checkString(urd.getPositionCategoryName()))
                {
                    titleDate = "(" + formatter.format(urd.getDateFrom()) + "/" + formatter.format(urd.getDateTo()) + ")";
                    uniformRequisitions = uniformService.searchApprovedUniformRequisitionsByDateDepartment(urd.getDateFrom(),
                    getDateTo(urd.getDateTo()), urd.getDepartment());
                }
                else if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && !checkString(urd.getDepartment()) && checkString(urd.getPositionCategoryName()))
                {
                    titleDate = "(" + formatter.format(urd.getDateFrom()) + "/" + formatter.format(urd.getDateTo()) + ")";
                    uniformRequisitions = uniformService.searchApprovedUniformRequisitionsByDatePositionCategory(urd.getDateFrom(),
                    getDateTo(urd.getDateTo()), urd.getPositionCategoryName());
                }
                else if ((!checkDate(urd.getDateFrom()) || !checkDate(urd.getDateTo())) && checkString(urd.getDepartment()) && checkString(urd.getPositionCategoryName()))
                {
                    uniformRequisitions = uniformService.searchApprovedUniformRequisitionsByDepartmentPositionCategory(urd.getDepartment(), urd.getPositionCategoryName());
                }
                else if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && checkString(urd.getDepartment()) && checkString(urd.getPositionCategoryName()))
                {
                    titleDate = "(" + formatter.format(urd.getDateFrom()) + "/" + formatter.format(urd.getDateTo()) + ")";
                    uniformRequisitions = uniformService.searchApprovedUniformRequisitionsByDateDepartmentPositionCategory(urd.getDateFrom(),
                    getDateTo(urd.getDateTo()), urd.getDepartment(), urd.getPositionCategoryName());
                }
                else {
                    uniformRequisitions = uniformService.searchApprovedUniformRequisitions();
                }
            }
            else
            {
                List<Employee> employees = employeeService.searchAllEmployees();
                department = "";
                for (Employee employee : employees) {
                    String tempName = "";
                    String[] tempNameArray = employee.getName().trim().split("\\s+");
                    tempName += tempNameArray.length > 0 ? tempNameArray[0].trim() : "";
                    tempName += tempNameArray.length > 1 ? " " + tempNameArray[1].trim() : "";
                    String tempEmail = employee.geteMail();
                    if (name.equalsIgnoreCase(tempName) || email.equalsIgnoreCase(tempEmail)) {  
                        String tempDepartment = employee.getDepartment() != null ? employee.getDepartment().trim() : "";
                        department = name.equalsIgnoreCase(tempName) ? tempDepartment : "";
                        break;
                    }
                }
                if ((!checkDate(urd.getDateFrom()) || !checkDate(urd.getDateTo())) && !checkString(urd.getPositionCategoryName()))
                {
                    uniformRequisitions = uniformService.searchApprovedUniformRequisitionsByDepartment(department);
                }
                else if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && !checkString(urd.getPositionCategoryName()))
                {
                    titleDate = "(" + formatter.format(urd.getDateFrom()) + "/" + formatter.format(urd.getDateTo()) + ")";
                    uniformRequisitions = uniformService.searchApprovedUniformRequisitionsByDateDepartment(urd.getDateFrom(),
                    getDateTo(urd.getDateTo()), department);
                } 
                else if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && checkString(urd.getPositionCategoryName()))
                {
                    titleDate = "(" + formatter.format(urd.getDateFrom()) + "/" + formatter.format(urd.getDateTo()) + ")";
                    uniformRequisitions = uniformService.searchApprovedUniformRequisitionsByDateDepartmentPositionCategory(urd.getDateFrom(),
                    getDateTo(urd.getDateTo()), department, urd.getPositionCategoryName());
                }
            }
            List<ImsItemDTO> imsItemDTOs = uniformService.getListOfAllItems();
            List<ImsItemDTO> selectedImsItemDTOs = new ArrayList<ImsItemDTO>();
            rowIdx = 0;
            Resource resource = new ClassPathResource("static/image/Z-logo-isr.jpg");
            InputStream inputStream = resource.getInputStream();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            inputStream.close();
            int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            CreationHelper helper = workbook.getCreationHelper();
            ClientAnchor anchor = helper.createClientAnchor();
            anchor.setCol1(0);
            anchor.setRow1(0);
            anchor.setCol2(2);
            anchor.setRow2(5);
            drawing.createPicture(anchor, pictureIdx);
            // pict.resize();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            CellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setAlignment(HorizontalAlignment.LEFT);
            titleStyle.setFont(boldFont);
            row = sheet.createRow(rowIdx);
            Cell titleCell = row.createCell(2);
            titleCell.setCellValue(title + titleDate);
            rowIdx = 5;
            row = sheet.createRow(++rowIdx);
            row.createCell(0).setCellValue("S.No.    ");
            row.getCell(0).setCellStyle(boldStyle);
            row.createCell(1).setCellValue("Name");
            row.getCell(1).setCellStyle(boldStyle);
            row.createCell(2).setCellValue("Branch/Department");
            row.getCell(2).setCellStyle(boldStyle);
            row.createCell(3).setCellValue("Position");
            row.getCell(3).setCellStyle(boldStyle);
            row.createCell(4).setCellValue("Sex");
            row.getCell(4).setCellStyle(boldStyle);
            itemNameStartColumnNumber = 4;
            for (ImsItemDTO imsItemDTO : imsItemDTOs) {
                if(urd.getPositionCategoryName() != null && !urd.getPositionCategoryName().isBlank())
                {
                    PositionCategory positionCategory = uniformService.getPositionCategoryByName(urd.getPositionCategoryName());
                    positionCategory.getEntitlementCollection().forEach(ec -> {
                        ec.getEntitlementUniformItemsCollection().forEach(euic -> {
                            String itemName = euic.getItemName();
                            if(itemName.equalsIgnoreCase(imsItemDTO.getName()))
                            {
                                if (imsItemDTO.getName().equals("Fabric for suit")) {
                                    row.createCell(++itemNameStartColumnNumber).setCellValue("Suit");
                                } else {
                                    row.createCell(++itemNameStartColumnNumber).setCellValue(imsItemDTO.getName());
                                }
                                selectedImsItemDTOs.add(imsItemDTO);
                            }
                        });
                    });
                }
                else
                {
                    if (imsItemDTO.getName().equals("Fabric for suit")) {
                        row.createCell(++itemNameStartColumnNumber).setCellValue("Suit");
                    } else {
                        row.createCell(++itemNameStartColumnNumber).setCellValue(imsItemDTO.getName());
                    }
                    selectedImsItemDTOs.add(imsItemDTO);
                }
                row.getCell(itemNameStartColumnNumber).setCellStyle(boldStyle);
            }
            sheet.addMergedRegion(new CellRangeAddress(0, 5, 0, 1));
            sheet.addMergedRegion(new CellRangeAddress(0, 5, 2, (row.getLastCellNum() - 1)));
            titleCell.setCellStyle(titleStyle);
            List<UniformRequisition> uniformRequisitionsByDistinctEmployee = new ArrayList<UniformRequisition>();
            HashSet<String> tempEmpName = new HashSet<String>();
            for (UniformRequisition uniformRequisition : uniformRequisitions) {
                Employee employee = uniformRequisition.getEmployeeId();
                if (employee != null && tempEmpName.add(employee.getName())) {
                    uniformRequisitionsByDistinctEmployee.add(uniformRequisition);
                }
            }
            int serialNumber = 1;
            for (UniformRequisition distinctEmployeeUniformRequisition : uniformRequisitionsByDistinctEmployee) {
                row = sheet.createRow(++rowIdx);
                row.createCell(0).setCellValue(serialNumber);
                Employee employee = distinctEmployeeUniformRequisition.getEmployeeId();
                if (employee != null) {
                    row.createCell(1).setCellValue(employee.getName());
                    row.createCell(2).setCellValue(employee.getDepartment());
                    row.createCell(3).setCellValue(employee.getJobPosition());
                    row.createCell(4).setCellValue(employee.getGender());
                }
                itemNameStartColumnNumber = 4;
                //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                for (ImsItemDTO selectedImsItemDTO : selectedImsItemDTOs) {
                    boolean itemFound = false;
                    outerLoop: for (UniformRequisition uniformRequisition : uniformRequisitions) {
                        for (UniformRequisitionDetail uniformRequisitionDetail : uniformRequisition
                        .getUniformRequisitionDetailCollection()) {
                            String requestItemName = uniformRequisitionDetail.getItemName();
                            String employeeId = employee.getId().toString();
                            String employeeIssueId = uniformRequisitionDetail.getUniformRequisitionId()
                                .getEmployeeId().getId().toString();
                                    if (employeeId.equalsIgnoreCase(employeeIssueId)
                                    && selectedImsItemDTO.getName().equals(requestItemName)
                                    && uniformRequisition.getApprovedDate().after(urd.getDateFrom())
                                    && uniformRequisition.getApprovedDate().before(urd.getDateTo())) {
                                row.createCell(++itemNameStartColumnNumber).setCellValue(uniformRequisitionDetail.getSize());
                                itemFound = true;
                                break outerLoop;
                            }
                        }
                    }
                    if (!itemFound) {
                        row.createCell(++itemNameStartColumnNumber).setCellValue("");
                    }
                }
                serialNumber++;
                //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            }
            for (int i = 0; i < row.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(out);
            workbook.close();
            byte[] excelBytes = out.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "Uniform Needs Collection Report.xlsx");
            headers.setContentLength(excelBytes.length);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/common/issuearchive")
    public ResponseEntity<?> csrIssueArchive(@RequestBody UniformIssueDTO uid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(csrRole, officerRole, officeManagerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(uid.getPage()), Integer.parseInt(uid.getRowsPerPage()));
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            name = (String) claims.get("name");
            email = (String) claims.get("preferred_username") + "@zemenbank.com";
        }
        List<UniformIssue> uniformIssues = new ArrayList<UniformIssue>();
        List<String> roleList = utilService.listAllUserRoles(authentication);
        if(roleList.contains(officerRole) || roleList.contains(officeManagerRole))
        {
            if (checkDate(uid.getDateFrom()) && checkDate(uid.getDateTo()) && !checkString(uid.getDepartment()) && !checkString(uid.getPositionCategoryName())) {
                uniformIssues = uniformService.searchApprovedRejectedUniformIssuesByDate(uid.getDateFrom(),
                getDateTo(uid.getDateTo()), pageRequest).toList();
            }
            else if ((!checkDate(uid.getDateFrom()) || !checkDate(uid.getDateTo())) && checkString(uid.getDepartment()) && !checkString(uid.getPositionCategoryName()))
            {
                uniformIssues = uniformService.searchApprovedRejectedUniformIssuesByDepartment(uid.getDepartment(), pageRequest).toList();
            }
            else if ((!checkDate(uid.getDateFrom()) || !checkDate(uid.getDateTo())) && !checkString(uid.getDepartment()) && checkString(uid.getPositionCategoryName()))
            {
                uniformIssues = uniformService.searchApprovedRejectedUniformIssuesByPositionCategory(uid.getPositionCategoryName(), pageRequest).toList();
            }
            else if (checkDate(uid.getDateFrom()) && checkDate(uid.getDateTo()) && checkString(uid.getDepartment()) && !checkString(uid.getPositionCategoryName()))
            {
                uniformIssues = uniformService.searchApprovedRejectedUniformIssuesByDateDepartment(uid.getDateFrom(),
                getDateTo(uid.getDateTo()), uid.getDepartment() , pageRequest).toList();
            }
            else if (checkDate(uid.getDateFrom()) && checkDate(uid.getDateTo()) && !checkString(uid.getDepartment()) && checkString(uid.getPositionCategoryName()))
            {
                uniformIssues = uniformService.searchApprovedRejectedUniformIssuesByDatePositionCategory(uid.getDateFrom(),
                getDateTo(uid.getDateTo()), uid.getPositionCategoryName() , pageRequest).toList();
            }
            else if ((!checkDate(uid.getDateFrom()) || !checkDate(uid.getDateTo())) && checkString(uid.getDepartment()) && checkString(uid.getPositionCategoryName()))
            {
                uniformIssues = uniformService.searchApprovedRejectedUniformIssuesByDepartmentPositionCategory(uid.getDepartment(), uid.getPositionCategoryName(), pageRequest).toList();
            }
            else if (checkDate(uid.getDateFrom()) && checkDate(uid.getDateTo()) && checkString(uid.getDepartment()) && checkString(uid.getPositionCategoryName()))
            {
                uniformIssues = uniformService.searchApprovedRejectedUniformIssuesByDateDepartmentPositionCategory(uid.getDateFrom(),
                getDateTo(uid.getDateTo()), uid.getDepartment(), uid.getPositionCategoryName(), pageRequest).toList();
            }
            else {
                uniformIssues = uniformService.searchApprovedRejectedUniformIssues(pageRequest).toList();
            }
        }
        else
        {
            List<Employee> employees = employeeService.searchAllEmployees();
            department = "";
            for (Employee employee : employees) {
                String tempName = "";
                String[] tempNameArray = employee.getName().trim().split("\\s+");
                tempName += tempNameArray.length > 0 ? tempNameArray[0].trim() : "";
                tempName += tempNameArray.length > 1 ? " " + tempNameArray[1].trim() : "";
                String tempEmail = employee.geteMail();
                if (name.equalsIgnoreCase(tempName) || email.equalsIgnoreCase(tempEmail)) {  
                    String tempDepartment = employee.getDepartment() != null ? employee.getDepartment().trim() : "";
                    department = name.equalsIgnoreCase(tempName) ? tempDepartment : "";
                    break;
                }
            }
            if ((!checkDate(uid.getDateFrom()) || !checkDate(uid.getDateTo())) && !checkString(uid.getPositionCategoryName()))
            {
                uniformIssues = uniformService.searchApprovedRejectedUniformIssuesByDepartment(department, pageRequest).toList();
            }
            else if (checkDate(uid.getDateFrom()) && checkDate(uid.getDateTo()) && !checkString(uid.getPositionCategoryName()))
            {
                uniformIssues = uniformService.searchApprovedRejectedUniformIssuesByDateDepartment(uid.getDateFrom(),
                getDateTo(uid.getDateTo()), department, pageRequest).toList();
            } 
            else if (checkDate(uid.getDateFrom()) && checkDate(uid.getDateTo()) && checkString(uid.getPositionCategoryName()))
            {
                uniformIssues = uniformService.searchApprovedRejectedUniformIssuesByDateDepartmentPositionCategory(uid.getDateFrom(),
                getDateTo(uid.getDateTo()), department, uid.getPositionCategoryName(), pageRequest).toList();
            }
        }
        List<UniformIssueDTO> response = uniformService.copyListOfUniformIssuesToUniformIssueDTOList(uniformIssues);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/common/issuearchive/count")
    public ResponseEntity<?> commonIssueArchiveCount(@RequestBody UniformIssueDTO uid) {
        long uniformIssueCount = 0;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(csrRole, officerRole, officeManagerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            name = (String) claims.get("name");
            email = (String) claims.get("preferred_username") + "@zemenbank.com";
        }
        List<String> roleList = utilService.listAllUserRoles(authentication);
        if(roleList.contains(officerRole) || roleList.contains(officeManagerRole))
        {
            if (checkDate(uid.getDateFrom()) && checkDate(uid.getDateTo()) && !checkString(uid.getDepartment()) && !checkString(uid.getPositionCategoryName())) {
                uniformIssueCount = uniformService.countApprovedRejectedUniformIssuesByDate(uid.getDateFrom(),
                getDateTo(uid.getDateTo()));
            }
            else if ((!checkDate(uid.getDateFrom()) || !checkDate(uid.getDateTo())) && checkString(uid.getDepartment()) && !checkString(uid.getPositionCategoryName()))
            {
                uniformIssueCount = uniformService.countApprovedRejectedUniformIssuesByDepartment(uid.getDepartment());
            }
            else if ((!checkDate(uid.getDateFrom()) || !checkDate(uid.getDateTo())) && !checkString(uid.getDepartment()) && checkString(uid.getPositionCategoryName()))
            {
                uniformIssueCount = uniformService.countApprovedRejectedUniformIssuesByPositionCategory(uid.getPositionCategoryName());
            }
            else if (checkDate(uid.getDateFrom()) && checkDate(uid.getDateTo()) && checkString(uid.getDepartment()) && !checkString(uid.getPositionCategoryName()))
            {
                uniformIssueCount = uniformService.countApprovedRejectedUniformIssuesByDateDepartment(uid.getDateFrom(),
                getDateTo(uid.getDateTo()), uid.getDepartment());
            }
            else if (checkDate(uid.getDateFrom()) && checkDate(uid.getDateTo()) && !checkString(uid.getDepartment()) && checkString(uid.getPositionCategoryName()))
            {
                uniformIssueCount = uniformService.countApprovedRejectedUniformIssuesByDatePositionCategory(uid.getDateFrom(),
                getDateTo(uid.getDateTo()), uid.getPositionCategoryName());
            }
            else if ((!checkDate(uid.getDateFrom()) || !checkDate(uid.getDateTo())) && checkString(uid.getDepartment()) && checkString(uid.getPositionCategoryName()))
            {
                uniformIssueCount = uniformService.countApprovedRejectedUniformIssuesByDepartmentPositionCategory(uid.getDepartment(), uid.getPositionCategoryName());
            }
            else if (checkDate(uid.getDateFrom()) && checkDate(uid.getDateTo()) && checkString(uid.getDepartment()) && checkString(uid.getPositionCategoryName()))
            {
                uniformIssueCount = uniformService.countApprovedRejectedUniformIssuesByDateDepartmentPositionCategory(uid.getDateFrom(),
                getDateTo(uid.getDateTo()), uid.getDepartment(), uid.getPositionCategoryName());
            }
            else {
                uniformIssueCount = uniformService.countApprovedRejectedUniformIssues();
            }
        }
        else
        {
            List<Employee> employees = employeeService.searchAllEmployees();
            department = "";
            for (Employee employee : employees) {
                String tempName = "";
                String[] tempNameArray = employee.getName().trim().split("\\s+");
                tempName += tempNameArray.length > 0 ? tempNameArray[0].trim() : "";
                tempName += tempNameArray.length > 1 ? " " + tempNameArray[1].trim() : "";
                String tempEmail = employee.geteMail();
                if (name.equalsIgnoreCase(tempName) || email.equalsIgnoreCase(tempEmail)) {  
                    String tempDepartment = employee.getDepartment() != null ? employee.getDepartment().trim() : "";
                    department = name.equalsIgnoreCase(tempName) ? tempDepartment : "";
                    break;
                }
            }
            if ((!checkDate(uid.getDateFrom()) || !checkDate(uid.getDateTo())) && !checkString(uid.getPositionCategoryName()))
            {
                uniformIssueCount = uniformService.countApprovedRejectedUniformIssuesByDepartment(department);
            }
            else if (checkDate(uid.getDateFrom()) && checkDate(uid.getDateTo()) && !checkString(uid.getPositionCategoryName()))
            {
                uniformIssueCount = uniformService.countApprovedRejectedUniformIssuesByDateDepartment(uid.getDateFrom(),
                getDateTo(uid.getDateTo()), department);
            } 
            else if (checkDate(uid.getDateFrom()) && checkDate(uid.getDateTo()) && checkString(uid.getPositionCategoryName()))
            {
                uniformIssueCount = uniformService.countApprovedRejectedUniformIssuesByDateDepartmentPositionCategory(uid.getDateFrom(),
                getDateTo(uid.getDateTo()), department, uid.getPositionCategoryName());
            }
        }
        return new ResponseEntity<>(uniformIssueCount, HttpStatus.OK);
    }

    @PostMapping("/common/issuearchivedownloadsignature")
    public ResponseEntity<?> issueArchiveDownloadSignature(@RequestBody UniformRequisitionDTO urd) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(csrRole, officerRole, officeManagerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Uniform Distribution Report");
            if (authentication instanceof JwtAuthenticationToken) {
                JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
                Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
                name = (String) claims.get("name");
                email = (String) claims.get("preferred_username") + "@zemenbank.com";
            }
            List<UniformIssue> uniformIssues = new ArrayList<UniformIssue>();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
            String title = "Uniform Distribution Collection";
            String titleDate = "";
            List<String> roleList = utilService.listAllUserRoles(authentication);
            if(roleList.contains(officerRole) || roleList.contains(officeManagerRole))
            {
                if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && !checkString(urd.getDepartment()) && !checkString(urd.getPositionCategoryName())) {
                    titleDate = "(" + formatter.format(urd.getDateFrom()) + "/" + formatter.format(urd.getDateTo()) + ")";
                    uniformIssues = uniformService.searchApprovedUniformIssuesByDate(urd.getDateFrom(), getDateTo(urd.getDateTo()));
                }
                else if ((!checkDate(urd.getDateFrom()) || !checkDate(urd.getDateTo())) && checkString(urd.getDepartment()) && !checkString(urd.getPositionCategoryName()))
                {
                    uniformIssues = uniformService.searchApprovedUniformIssuesByDepartment(urd.getDepartment());
                }
                else if ((!checkDate(urd.getDateFrom()) || !checkDate(urd.getDateTo())) && !checkString(urd.getDepartment()) && checkString(urd.getPositionCategoryName()))
                {
                    uniformIssues = uniformService.searchApprovedUniformIssuesByPositionCategory(urd.getPositionCategoryName());
                }
                else if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && checkString(urd.getDepartment()) && !checkString(urd.getPositionCategoryName()))
                {
                    titleDate = "(" + formatter.format(urd.getDateFrom()) + "/" + formatter.format(urd.getDateTo()) + ")";
                    uniformIssues = uniformService.searchApprovedUniformIssuesByDateDepartment(urd.getDateFrom(),
                    getDateTo(urd.getDateTo()), urd.getDepartment());
                }
                else if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && !checkString(urd.getDepartment()) && checkString(urd.getPositionCategoryName()))
                {
                    titleDate = "(" + formatter.format(urd.getDateFrom()) + "/" + formatter.format(urd.getDateTo()) + ")";
                    uniformIssues = uniformService.searchApprovedUniformIssuesByDatePositionCategory(urd.getDateFrom(),
                    getDateTo(urd.getDateTo()), urd.getPositionCategoryName());
                }
                else if ((!checkDate(urd.getDateFrom()) || !checkDate(urd.getDateTo())) && checkString(urd.getDepartment()) && checkString(urd.getPositionCategoryName()))
                {
                    uniformIssues = uniformService.searchApprovedUniformIssuesByDepartmentPositionCategory(urd.getDepartment(), urd.getPositionCategoryName());
                }
                else if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && checkString(urd.getDepartment()) && checkString(urd.getPositionCategoryName()))
                {
                    titleDate = "(" + formatter.format(urd.getDateFrom()) + "/" + formatter.format(urd.getDateTo()) + ")";
                    uniformIssues = uniformService.searchApprovedUniformIssuesByDateDepartmentPositionCategory(urd.getDateFrom(),
                    getDateTo(urd.getDateTo()), urd.getDepartment(), urd.getPositionCategoryName());
                }
                else {
                    uniformIssues = uniformService.searchApprovedUniformIssues();
                }
            }
            else
            {
                List<Employee> employees = employeeService.searchAllEmployees();
                department = "";
                for (Employee employee : employees) {
                    String tempName = "";
                    String[] tempNameArray = employee.getName().trim().split("\\s+");
                    tempName += tempNameArray.length > 0 ? tempNameArray[0].trim() : "";
                    tempName += tempNameArray.length > 1 ? " " + tempNameArray[1].trim() : "";
                    String tempEmail = employee.geteMail();
                    if (name.equalsIgnoreCase(tempName) || email.equalsIgnoreCase(tempEmail)) {  
                        String tempDepartment = employee.getDepartment() != null ? employee.getDepartment().trim() : "";
                        department = name.equalsIgnoreCase(tempName) ? tempDepartment : "";
                        break;
                    }
                }
                if ((!checkDate(urd.getDateFrom()) || !checkDate(urd.getDateTo())) && !checkString(urd.getPositionCategoryName()))
                {
                    uniformIssues = uniformService.searchApprovedUniformIssuesByDepartment(department);
                }
                else if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && !checkString(urd.getPositionCategoryName()))
                {
                    titleDate = "(" + formatter.format(urd.getDateFrom()) + "/" + formatter.format(urd.getDateTo()) + ")";
                    uniformIssues = uniformService.searchApprovedUniformIssuesByDateDepartment(urd.getDateFrom(),
                    getDateTo(urd.getDateTo()), department);
                }
                else if (checkDate(urd.getDateFrom()) && checkDate(urd.getDateTo()) && checkString(urd.getPositionCategoryName()))
                {
                    titleDate = "(" + formatter.format(urd.getDateFrom()) + "/" + formatter.format(urd.getDateTo()) + ")";
                    uniformIssues = uniformService.searchApprovedUniformIssuesByDateDepartmentPositionCategory(urd.getDateFrom(),
                    getDateTo(urd.getDateTo()), department, urd.getPositionCategoryName());
                }
            }
            List<ImsItemDTO> imsItemDTOs = uniformService.getListOfAllItems();
            List<ImsItemDTO> selectedImsItemDTOs = new ArrayList<ImsItemDTO>();
            rowIdx = 0;
            Resource resource = new ClassPathResource("static/image/Z-logo-isr.jpg");
            InputStream inputStream = resource.getInputStream();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            inputStream.close();
            int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            CreationHelper helper = workbook.getCreationHelper();
            ClientAnchor anchor = helper.createClientAnchor();
            anchor.setCol1(0);
            anchor.setRow1(0);
            anchor.setCol2(2);
            anchor.setRow2(5);
            drawing.createPicture(anchor, pictureIdx);
            // pict.resize();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            CellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setAlignment(HorizontalAlignment.LEFT);
            titleStyle.setFont(boldFont);
            row = sheet.createRow(rowIdx);
            Cell titleCell = row.createCell(2);
            titleCell.setCellValue(title + titleDate);
            rowIdx = 5;
            row = sheet.createRow(++rowIdx);
            row.createCell(0).setCellValue("S.No.    ");
            row.getCell(0).setCellStyle(boldStyle);
            row.createCell(1).setCellValue("Name");
            row.getCell(1).setCellStyle(boldStyle);
            row.createCell(2).setCellValue("Branch/Department");
            row.getCell(2).setCellStyle(boldStyle);
            row.createCell(3).setCellValue("Position");
            row.getCell(3).setCellStyle(boldStyle);
            row.createCell(4).setCellValue("Sex");
            row.getCell(4).setCellStyle(boldStyle);
            itemNameStartColumnNumber = 4;  
            for (ImsItemDTO imsItemDTO : imsItemDTOs) {
                if(urd.getPositionCategoryName() != null && !urd.getPositionCategoryName().isBlank())
                {
                    PositionCategory positionCategory = uniformService.getPositionCategoryByName(urd.getPositionCategoryName());
                    positionCategory.getEntitlementCollection().forEach(ec -> {
                        ec.getEntitlementUniformItemsCollection().forEach(euic -> {
                            String itemName = euic.getItemName();
                            if(itemName.equalsIgnoreCase(imsItemDTO.getName()))
                            {
                                if (imsItemDTO.getName().equals("Fabric for suit")) {
                                    row.createCell(++itemNameStartColumnNumber).setCellValue("Suit");
                                } else {
                                    row.createCell(++itemNameStartColumnNumber).setCellValue(imsItemDTO.getName());
                                }
                                selectedImsItemDTOs.add(imsItemDTO);
                            }
                        });
                    });
                }
                else
                {
                    if (imsItemDTO.getName().equals("Fabric for suit")) {
                        row.createCell(++itemNameStartColumnNumber).setCellValue("Suit");
                    } else {
                        row.createCell(++itemNameStartColumnNumber).setCellValue(imsItemDTO.getName());
                    }
                    selectedImsItemDTOs.add(imsItemDTO);
                }
                row.getCell(itemNameStartColumnNumber).setCellStyle(boldStyle);
            }
            sheet.addMergedRegion(new CellRangeAddress(0, 5, 0, 1));//TITLE
            sheet.addMergedRegion(new CellRangeAddress(0, 5, 2, (row.getLastCellNum() - 1)));//TITLE
            titleCell.setCellStyle(titleStyle);
            List<UniformIssue> uniformIssuesByDistinctEmployee = new ArrayList<UniformIssue>();
            HashSet<String> tempEmpName = new HashSet<String>();
            for (UniformIssue uniformIssue : uniformIssues) {
                uniformIssue.getUniformIssueDetailCollection().forEach(uidc -> {
                    Employee employee = uidc.getUniformRequisitionDetail().getUniformRequisitionId().getEmployeeId();
                    if (employee != null && tempEmpName.add(employee.getName())) {
                        uniformIssuesByDistinctEmployee.add(uniformIssue);
                    }
                });
            }
            int serialNumber = 1;
            SimpleDateFormat sdfDateColumn = new SimpleDateFormat("MMM dd/yyyy");
            //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            for (UniformIssue distinctEmployeeUniformIssue : uniformIssuesByDistinctEmployee) {
                row = sheet.createRow(++rowIdx);
                row.createCell(0).setCellValue(serialNumber);
                Employee employee = distinctEmployeeUniformIssue.getUniformIssueDetailCollection().iterator().next()
                .getUniformRequisitionDetail().getUniformRequisitionId().getEmployeeId();
                if (employee != null) {
                    row.createCell(1).setCellValue(employee.getName());
                    row.createCell(2).setCellValue(employee.getDepartment());
                    row.createCell(3).setCellValue(employee.getJobPosition());
                    row.createCell(4).setCellValue(employee.getGender());
                }
                itemNameStartColumnNumber = 4;
                for (ImsItemDTO selectedImsItemDTO : selectedImsItemDTOs) {
                    boolean itemFound = false;
                    outerLoop: for (UniformIssue uniformIssue : uniformIssues) {
                        for (UniformIssueDetail uniformIssueDetail : uniformIssue
                                .getUniformIssueDetailCollection()) {
                            String issueItemName = uniformIssueDetail.getUniformRequisitionDetail().getItemName();
                            String employeeId = employee.getId().toString();
                            String employeeIssueId = uniformIssueDetail
                                .getUniformRequisitionDetail().getUniformRequisitionId()
                                .getEmployeeId().getId().toString();
                                    if (employeeId.equalsIgnoreCase(employeeIssueId)
                                    && selectedImsItemDTO.getName().equals(issueItemName)
                                    && uniformIssue.getApprovedDate().after(urd.getDateFrom())
                                    && uniformIssue.getApprovedDate().before(urd.getDateTo())) {
                                row.createCell(++itemNameStartColumnNumber).setCellValue(
                                        sdfDateColumn.format(uniformIssue.getApprovedDate()));
                                itemFound = true;
                                break outerLoop;
                            }
                        }
                    }
                    if (!itemFound) {
                        row.createCell(++itemNameStartColumnNumber).setCellValue("");
                    }
                }
                serialNumber++;
            }
            //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            for (int i = 0; i < row.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(out);
            workbook.close();
            byte[] excelBytes = out.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "Uniform Distribution Report.xlsx");
            headers.setContentLength(excelBytes.length);
            return ResponseEntity.ok().headers(headers).body(excelBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/common/masterreportdownload")
    public ResponseEntity<?> masterReportDownload(@RequestBody UniformIssueDTO uid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication,
                new ArrayList<>(Arrays.asList(officerRole, officeManagerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            if (checkDate(uid.getDateFrom()) && checkDate(uid.getDateTo())) {
                List<PositionCategory> positionCategories = uniformService.getListOfAllPositionCategories();
                for (PositionCategory positionCategory : positionCategories) {
                    if (positionCategory.getEntitlementCollection().size() > 0) {
                        String invalidChars = "[/\\\\*\\[\\]:?\"]";
                        String sheetName = positionCategory.getName().replaceAll(invalidChars, "_");
                        if (workbook.getSheet(sheetName) != null) {
                            sheetName = sheetName + "_";
                        }
                        Sheet sheet = workbook.createSheet(sheetName);
                        headerRow = sheet.createRow(0);
                        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
                        Font boldFont = workbook.createFont();
                        boldFont.setBold(true);
                        CellStyle boldStyle = workbook.createCellStyle();
                        boldStyle.setFont(boldFont);
                        boldStyle.setBorderTop(BorderStyle.THIN);
                        boldStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
                        boldStyle.setBorderBottom(BorderStyle.THIN);
                        boldStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                        boldStyle.setBorderLeft(BorderStyle.THIN);
                        boldStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
                        boldStyle.setBorderRight(BorderStyle.THIN);
                        boldStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
                        rowIdx = 0;
                        row = sheet.createRow(++rowIdx);
                        row.createCell(0).setCellValue("S.No.    ");
                        row.getCell(0).setCellStyle(boldStyle);
                        row.createCell(1).setCellValue("Name");
                        row.getCell(1).setCellStyle(boldStyle);
                        row.createCell(2).setCellValue("Branch/Department");
                        row.getCell(2).setCellStyle(boldStyle);
                        row.createCell(3).setCellValue("Position");
                        row.getCell(3).setCellStyle(boldStyle);
                        row.createCell(4).setCellValue("Sex");
                        row.getCell(4).setCellStyle(boldStyle);
                        SimpleDateFormat sdfDateColumn = new SimpleDateFormat("MMM dd/yyyy");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                        int startYear = Integer.valueOf(sdf.format(uid.getDateFrom()));
                        int endYear = Integer.valueOf(sdf.format(uid.getDateTo()));
                        noOfItems = 0;
                        positionCategory.getEntitlementCollection().forEach(ec -> {
                            noOfItems = ec.getEntitlementUniformItemsCollection().size();
                        });
                        entitledItems = new ArrayList<String>();
                        itemNameStartColumnNumber = 5;
                        //create header row
                        for (yearCounter = startYear; yearCounter <= endYear; yearCounter = yearCounter+2) {
                            positionCategory.getEntitlementCollection().forEach(ec -> {
                                ec.getEntitlementUniformItemsCollection().forEach(euic -> {
                                    String itemName = euic.getItemName();
                                    if(yearCounter == startYear)
                                    {
                                        entitledItems.add(itemName);
                                    }
                                    if (itemName.equals("Fabric for suit")) {
                                        itemName = "Suit";
                                    }
                                    row.createCell(itemNameStartColumnNumber).setCellValue(itemName);
                                    row.getCell(itemNameStartColumnNumber).setCellStyle(boldStyle);
                                    itemNameStartColumnNumber++;
                                });
                            });
                            String titleHeader = String.valueOf(yearCounter) + "/" + String.valueOf(yearCounter + 1);
                            if (noOfItems > 1) {
                                sheet.addMergedRegion(new CellRangeAddress(0, 0, itemNameStartColumnNumber - noOfItems,
                                        itemNameStartColumnNumber - 1));
                            }
                            headerRow.createCell(itemNameStartColumnNumber - noOfItems).setCellValue(titleHeader);
                            headerRow.getCell(itemNameStartColumnNumber - noOfItems).setCellStyle(boldStyle);
                        }
                        List<Employee> uniformIssueEmployees = uniformService
                                .searchApprovedUniformIssueEmployeesByPositionCategoryDate(
                                        positionCategory.getName(), uid.getDateFrom(), uid.getDateTo());
                        List<UniformIssue> uniformIssues = uniformService
                                .searchApprovedUniformIssuesByPositionCategoryDate(
                                    positionCategory.getName(), uid.getDateFrom(), uid.getDateTo());
                        for (Employee employee : uniformIssueEmployees) {
                            row = sheet.createRow(++rowIdx);
                            row.createCell(0).setCellValue(rowIdx - 1);
                            row.createCell(1).setCellValue(employee.getName());
                            row.createCell(2).setCellValue(employee.getDepartment());
                            row.createCell(3).setCellValue(employee.getJobPosition());
                            row.createCell(4).setCellValue(employee.getGender());
                            itemNameStartColumnNumber = 5;
                            for (yearCounter = startYear; yearCounter <= endYear; yearCounter = yearCounter+2) {
                                for (String entitledItem : entitledItems) {
                                    boolean itemFound = false;
                                    outerLoop: for (UniformIssue uniformIssue : uniformIssues) {
                                        for (UniformIssueDetail uniformIssueDetail : uniformIssue
                                                .getUniformIssueDetailCollection()) {
                                            String issueItemName = uniformIssueDetail.getUniformRequisitionDetail().getItemName();
                                            String employeeId = employee.getId().toString();
                                            String employeeIssueId = uniformIssueDetail
                                                .getUniformRequisitionDetail().getUniformRequisitionId()
                                                .getEmployeeId().getId().toString();
                                            LocalDateTime approvedDate = LocalDateTime.ofInstant(
                                                    uniformIssue.getApprovedDate().toInstant(),
                                                    ZoneId.systemDefault());
                                                    LocalDateTime fromDate = LocalDateTime.of(yearCounter, 1, 1, 0, 0, 0);
                                                    LocalDateTime toDate = LocalDateTime.of(yearCounter + 1, 12, 31, 23, 59, 59);
                                                    if (employeeId.equalsIgnoreCase(employeeIssueId)
                                                    && entitledItem.equals(issueItemName)
                                                    && approvedDate.isAfter(fromDate)
                                                    && approvedDate.isBefore(toDate)) {
                                                row.createCell(itemNameStartColumnNumber).setCellValue(
                                                        sdfDateColumn.format(uniformIssue.getApprovedDate()));
                                                itemNameStartColumnNumber++;
                                                itemFound = true;
                                                break outerLoop;
                                            }
                                        }
                                    }
                                    if (!itemFound) {
                                        row.createCell(itemNameStartColumnNumber).setCellValue("");
                                        itemNameStartColumnNumber++;
                                    }
                                }
                            }
                        }
                        for (int i = 0; i < headerRow.getLastCellNum() + 5; i++) {
                            sheet.autoSizeColumn(i);
                        }
                        for (int i = 0; i < row.getLastCellNum() + 5; i++) {
                            sheet.autoSizeColumn(i);
                        }
                    }
                }
            }
            workbook.write(out);
            workbook.close();
            byte[] excelBytes = out.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "Uniform Master Report.xlsx");
            headers.setContentLength(excelBytes.length);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/common/employeeuniformissueitems")
    public ResponseEntity<?> employeeUniformIssueItems(@RequestBody UniformRequisitionDTO uniformRequisitionDTO) {
        List<UniformRequisitionDetailDTO> uniformRequisitionDetailDTOs = uniformService
                .searchUniformRequisitionDetailDTOList(uniformRequisitionDTO.getId());
        return new ResponseEntity<>(uniformRequisitionDetailDTOs, HttpStatus.OK);
    }

    @PostMapping("/common/employeeuniformissueitemsforissue")
    public ResponseEntity<?> employeeUniformIssueItemsForIssue(@RequestBody UniformIssueDTO uniformIssueDTO) {
        List<UniformIssueDetailDTO> uniformIssueDetailDTOs = uniformService
                .searchUniformIssueDetailDTOList(uniformIssueDTO.getId());
        return new ResponseEntity<>(uniformIssueDetailDTOs, HttpStatus.OK);
    }

    @PostMapping("/officer/createissue")
    public ResponseEntity<?> createIssue(@RequestBody UniformRequisitionDTO uniformRequisitionDTO,
            HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(officerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        UniformIssue uniformIssue = uniformService.copyUniformRequisitionToUniformIssue(authentication,
                uniformRequisitionDTO);
        boolean status = uniformService.createUniformIssue(uniformIssue);
        if (status) {
            GenericClass<UniformIssue> uniformIssueInstance = utilService.new GenericClass<>();
            uniformIssueInstance.set(uniformIssue);
            utilService.createAccessLog(authentication, request, uniformIssueInstance);
        }
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("/officer/rejectissue")
    public ResponseEntity<?> rejectIssue(@RequestBody UniformRequisitionDTO uniformRequisitionDTO,
            HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(officerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        UniformIssue uniformIssue = uniformService.copyUniformRequisitionToUniformIssueForMakerReject(authentication,
                uniformRequisitionDTO);
        boolean status = uniformService.createUniformIssue(uniformIssue);
        if (status) {
            GenericClass<UniformIssue> uniformIssueInstance = utilService.new GenericClass<>();
            uniformIssueInstance.set(uniformIssue);
            utilService.createAccessLog(authentication, request, uniformIssueInstance);
        }
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("/officer/index")
    public ResponseEntity<?> officerIndex(@RequestBody EmployeeProfile eP) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(officerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        List<UniformRequisition> uniformRequisitions;
        if (checkString(eP.getDepartment())) {
            uniformRequisitions = uniformService
                    .searchApprovedUniformRequisitionsByDepartment(eP.getDepartment());
        } else {
            uniformRequisitions = uniformService.searchApprovedUniformRequisitions();
        }
        List<UniformRequisitionDTO> response = uniformService
                .copyListOfUniformRequisitionsToUniformRequisitionDTOList(uniformRequisitions);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/manager/indexissue")
    public ResponseEntity<?> managerIndexIssue(@RequestBody EmployeeProfile eP) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(officeManagerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        List<UniformIssue> uniformIssues;
        if (checkString(eP.getDepartment())) {
            uniformIssues = uniformService.searchPendingUniformIssuesByDepartment(eP.getDepartment());
        } else {
            uniformIssues = uniformService.searchPendingUniformIssues();
        }
        List<UniformIssueDTO> response = uniformService.copyListOfUniformIssuesToUniformIssueDTOList(uniformIssues);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/manager/approveissue")
    public ResponseEntity<?> managerApproveIssue(@RequestBody UniformIssueDTO uniformIssueDTO,
            HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(officeManagerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        UniformIssue uniformIssue = uniformService.copyUniformIssueDTOToUniformIssueForApprove(authentication,
                uniformIssueDTO);
        boolean status = uniformService.updateUniformIssue(uniformIssue);
        if (status) {
            uniformService.updateIMS(uniformIssue);
            GenericClass<UniformIssue> uniformIssueInstance = utilService.new GenericClass<>();
            uniformIssueInstance.set(uniformIssue);
            utilService.createAccessLog(authentication, request, uniformIssueInstance);
        }
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("/manager/rejectissue")
    public ResponseEntity<?> managerRejectIssue(@RequestBody UniformIssueDTO uniformIssueDTO,
            HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication,
                new ArrayList<>(Arrays.asList(officeManagerRole, managerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        UniformIssue uniformIssue = uniformService.copyUniformIssueDTOToUniformIssueForReject(authentication,
                uniformIssueDTO);
        boolean status = uniformService.updateUniformIssue(uniformIssue);
        if (status) {
            GenericClass<UniformIssue> uniformIssueInstance = utilService.new GenericClass<>();
            uniformIssueInstance.set(uniformIssue);
            utilService.createAccessLog(authentication, request, uniformIssueInstance);
        }
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("/manager/index")
    public ResponseEntity<?> managerIndex() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication,
                new ArrayList<>(Arrays.asList(officeManagerRole, managerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        List<Employee> employees = employeeService.searchAllEmployees();
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            name = (String) claims.get("name");
            email = (String) claims.get("preferred_username") + "@zemenbank.com";
        }
        department = "";
        for (Employee employee : employees) {
            String tempName = "";
            String[] tempNameArray = employee.getName().trim().split("\\s+");
            tempName += tempNameArray.length > 0 ? tempNameArray[0].trim() : "";
            tempName += tempNameArray.length > 1 ? " " + tempNameArray[1].trim() : "";
            String tempEmail = employee.geteMail();
            if (name.equalsIgnoreCase(tempName) || email.equalsIgnoreCase(tempEmail)) {  
            String tempDepartment = employee.getDepartment() != null ? employee.getDepartment().trim() : "";
                department = name.equalsIgnoreCase(tempName) ? tempDepartment : "";
                break;
            }
        }
        List<UniformRequisition> uniformRequisitions = uniformService
                .searchPendingUniformRequisitionsByDepartment(department);
        List<UniformRequisitionDTO> response = uniformService
                .copyListOfUniformRequisitionsToUniformRequisitionDTOList(uniformRequisitions);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/manager/approve")
    public ResponseEntity<?> managerApprove(@RequestBody UniformRequisitionDTO uniformRequisitionDTO,
            HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication,
                new ArrayList<>(Arrays.asList(officeManagerRole, managerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        UniformRequisition uniformRequisition = uniformService.copyUniformRequisitionDTOToUniformRequisitionForApprove(
                authentication,
                uniformRequisitionDTO);
        boolean status = uniformService.updateUniformRequisition(uniformRequisition);
        if (status) {
            GenericClass<UniformRequisition> uniformRequisitionInstance = utilService.new GenericClass<>();
            uniformRequisitionInstance.set(uniformRequisition);
            utilService.createAccessLog(authentication, request, uniformRequisitionInstance);
        }
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("/manager/reject")
    public ResponseEntity<?> managerReject(@RequestBody UniformRequisitionDTO uniformRequisitionDTO,
            HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (utilService.checkUserRequest(authentication,
                new ArrayList<>(Arrays.asList(officeManagerRole, managerRole)))) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        UniformRequisition uniformRequisition = uniformService.copyUniformRequisitionDTOToUniformRequisitionForReject(
                authentication,
                uniformRequisitionDTO);
        boolean status = uniformService.updateUniformRequisition(uniformRequisition);
        if (status) {
            GenericClass<UniformRequisition> uniformRequisitionInstance = utilService.new GenericClass<>();
            uniformRequisitionInstance.set(uniformRequisition);
            utilService.createAccessLog(authentication, request, uniformRequisitionInstance);
        }
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    public boolean checkString(String stringToCheck) {
        return (stringToCheck != null && !stringToCheck.isBlank());
    }

    public boolean checkDate(Date dateToCheck) {
        return (dateToCheck != null);
    }

    private Date getDateTo(Date inputDate)
    {
        Calendar dateToCal = Calendar.getInstance();
        dateToCal.setTime(inputDate);
        dateToCal.set(dateToCal.get(Calendar.YEAR), dateToCal.get(Calendar.MONTH),
                dateToCal.get(Calendar.DATE),
                Calendar.HOUR_OF_DAY, 59, 59);
        Date dateTo = dateToCal.getTime();
        return dateTo;
    }

    public static <T> java.util.function.Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = java.util.concurrent.ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
