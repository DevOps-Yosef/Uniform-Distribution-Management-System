package com.zemen.udms.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zemen.udms.dto.EmployeeProfile;
import com.zemen.udms.dto.EmployeeUniformItemDTO;
import com.zemen.udms.dto.IdNamePairDTO;
import com.zemen.udms.dto.PositionCategoryDTO;
import com.zemen.udms.model.Employee;
import com.zemen.udms.service.EmployeeService;
import com.zemen.udms.service.PositionService;
import com.zemen.udms.util.UtilService;
import com.zemen.udms.util.UtilService.GenericClass;

import jakarta.servlet.http.HttpServletRequest;

//?????????????????????????
@RestController
@RequestMapping("/udmsapi/employees")
public class EmployeeController {
        String officerRole = "Officer", managerRole = "Office Manager";
        @Autowired
        private EmployeeService employeeService;

        @Autowired
        private PositionService positionService;

        @Autowired
        private UtilService utilService;

        @PostMapping("/officer/create")
        public ResponseEntity<?> createEmployee(@RequestBody EmployeeProfile eP, HttpServletRequest request) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(officerRole)))) {
                        return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
                }
                Employee employee = employeeService.copyEmployeeProfileToEmployee(authentication, eP);
                boolean status = employeeService.createEmployee(employee);
                if (status) {
                        GenericClass<Employee> employeeeInstance = utilService.new GenericClass<>();
                        employeeeInstance.set(employee);
                        utilService.createAccessLog(authentication, request, employeeeInstance);
                }
                return new ResponseEntity<>(status, HttpStatus.OK);
        }

        @PostMapping("/officer/edit")
        public ResponseEntity<?> officerEdit(@RequestBody EmployeeProfile eP, HttpServletRequest request) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(officerRole)))) {
                        return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
                }
                Employee employee = employeeService.copyEmployeeProfileToEmployeeForEdit(authentication, eP);
                boolean status = employeeService.updateEmployee(employee);
                if (status) {
                        GenericClass<Employee> employeeInstance = utilService.new GenericClass<>();
                        employeeInstance.set(employee);
                        utilService.createAccessLog(authentication, request, employeeInstance);
                }
                return new ResponseEntity<>(status, HttpStatus.OK);
        }

        @PostMapping("/common/index")
        public ResponseEntity<?> index(@RequestBody EmployeeProfile eP) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (utilService.checkUserRequest(authentication,
                                new ArrayList<>(Arrays.asList(officerRole, managerRole)))) {
                        return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
                }
                PageRequest pageRequest = PageRequest.of(Integer.parseInt(eP.getPage()),
                                Integer.parseInt(eP.getRowsPerPage()));
                List<Employee> employees = new ArrayList<Employee>();
                List<EmployeeProfile> response;
                // 111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111
                if (checkString(eP.getName()) && !checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName())
                                && !checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                        employees = employeeService.searchEmployeeByName(eP.getName(), pageRequest).toList();
                } else if (!checkString(eP.getName()) && checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName()) && !checkString(eP.getDepartment())
                                && !checkString(eP.getStatus())) {
                        employees = employeeService.searchEmployeeByGender(eP.getGender(), pageRequest).toList();
                } else if (!checkString(eP.getName()) && !checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName()) && !checkString(eP.getDepartment())
                                && !checkString(eP.getStatus())) {
                        employees = employeeService
                                        .searchEmployeeByPositionCategory(eP.getPositionCategoryName(), pageRequest)
                                        .toList();
                        System.out.println("eP.getPositionCategoryName() === " + eP.getPositionCategoryName());
                } else if (!checkString(eP.getName()) && !checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName()) && checkString(eP.getDepartment())
                                && !checkString(eP.getStatus())) {
                        employees = employeeService.searchEmployeeByDepartment(eP.getDepartment(), pageRequest)
                                        .toList();
                } else if (!checkString(eP.getName()) && !checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName()) && !checkString(eP.getDepartment())
                                && checkString(eP.getStatus())) {
                        System.out.println(
                                        "Controller checkString(eP.getStatus()) ==== " + checkString(eP.getStatus()));
                        employees = employeeService
                                        .searchEmployeeByIsInactive(eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                        .toList();
                        System.out.println("Controller employees.size() ==== " + employees.size());
                }
                // 2222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222
                else if (checkString(eP.getName()) && checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName())
                                && !checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                        employees = employeeService
                                        .searchEmployeeByNameGender(eP.getName(), eP.getGender(), pageRequest).toList();
                }

                else if (checkString(eP.getName()) && checkString(eP.getPositionCategoryName())
                                && !checkString(eP.getGender())
                                && !checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                        employees = employeeService
                                        .searchEmployeeByNamePositionCategory(eP.getName(),
                                                        eP.getPositionCategoryName(), pageRequest)
                                        .toList();
                }

                else if (checkString(eP.getName()) && checkString(eP.getDepartment())
                                && !checkString(eP.getPositionCategoryName()) && !checkString(eP.getGender())
                                && !checkString(eP.getStatus())) {
                        employees = employeeService
                                        .searchEmployeeByNameDepartment(eP.getName(), eP.getDepartment(), pageRequest)
                                        .toList();
                }

                else if (checkString(eP.getName()) && checkString(eP.getStatus()) && !checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName()) && !checkString(eP.getDepartment())) {
                        employees = employeeService
                                        .searchEmployeeByNameIsInactive(eP.getName(),
                                                        eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                        .toList();
                }

                else if (!checkString(eP.getName()) && checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName())
                                && !checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                        employees = employeeService
                                        .searchEmployeeByGenderPositionCategory(eP.getGender(),
                                                        eP.getPositionCategoryName(), pageRequest)
                                        .toList();
                }

                else if (!checkString(eP.getName()) && checkString(eP.getGender()) && checkString(eP.getDepartment())
                                && !checkString(eP.getPositionCategoryName()) && !checkString(eP.getStatus())) {
                        employees = employeeService
                                        .searchEmployeeByGenderDepartment(eP.getGender(), eP.getDepartment(),
                                                        pageRequest)
                                        .toList();
                }

                else if (!checkString(eP.getName()) && checkString(eP.getGender()) && checkString(eP.getStatus())
                                && !checkString(eP.getPositionCategoryName()) && !checkString(eP.getDepartment())) {
                        employees = employeeService
                                        .searchEmployeeByGenderIsInactive(eP.getGender(),
                                                        eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                        .toList();
                }

                else if (!checkString(eP.getName()) && checkString(eP.getPositionCategoryName())
                                && checkString(eP.getDepartment()) && !checkString(eP.getGender())
                                && !checkString(eP.getStatus())) {
                        employees = employeeService
                                        .searchEmployeeByPositionCategoryDepartment(eP.getPositionCategoryName(),
                                                        eP.getDepartment(), pageRequest)
                                        .toList();
                }

                else if (!checkString(eP.getName()) && checkString(eP.getPositionCategoryName())
                                && checkString(eP.getStatus())
                                && !checkString(eP.getDepartment()) && !checkString(eP.getGender())) {
                        employees = employeeService
                                        .searchEmployeeByPositionCategoryIsInactive(eP.getPositionCategoryName(),
                                                        eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                        .toList();
                }

                else if (!checkString(eP.getName()) && checkString(eP.getDepartment()) && checkString(eP.getStatus())
                                && !checkString(eP.getGender()) && !checkString(eP.getPositionCategoryName())) {
                        employees = employeeService.searchEmployeeByDepartmentIsInactive(eP.getDepartment(),
                                        eP.getStatus().equalsIgnoreCase("1"), pageRequest).toList();
                }
                // 333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333
                else if (checkString(eP.getName()) && checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName())
                                && !checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                        employees = employeeService
                                        .searchEmployeeByNameGenderPositionCategory(eP.getName(), eP.getGender(),
                                                        eP.getPositionCategoryName(), pageRequest)
                                        .toList();
                }

                else if (checkString(eP.getName()) && checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName())
                                && checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                        employees = employeeService
                                        .searchEmployeeByNameGenderDepartment(eP.getName(), eP.getGender(),
                                                        eP.getDepartment(), pageRequest)
                                        .toList();
                }

                else if (checkString(eP.getName()) && checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName())
                                && !checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                        employees = employeeService.searchEmployeeByNameGenderIsInactive(eP.getName(), eP.getGender(),
                                        eP.getStatus().equalsIgnoreCase("1"), pageRequest).toList();
                }

                else if (checkString(eP.getName()) && !checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName())
                                && checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                        employees = employeeService.searchEmployeeByNamePositionCategoryDepartment(eP.getName(),
                                        eP.getPositionCategoryName(), eP.getDepartment(), pageRequest).toList();
                }

                else if (checkString(eP.getName()) && !checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName())
                                && !checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                        employees = employeeService.searchEmployeeByNamePositionCategoryIsInactive(eP.getName(),
                                        eP.getPositionCategoryName(), eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                        .toList();
                }

                else if (checkString(eP.getName()) && !checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName())
                                && checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                        employees = employeeService
                                        .searchEmployeeByNameDepartmentIsInactive(eP.getName(), eP.getDepartment(),
                                                        eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                        .toList();
                }

                else if (!checkString(eP.getName()) && checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName())
                                && checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                        employees = employeeService.searchEmployeeByGenderPositionCategoryDepartment(eP.getGender(),
                                        eP.getPositionCategoryName(), eP.getDepartment(), pageRequest).toList();
                }

                else if (!checkString(eP.getName()) && checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName())
                                && !checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                        employees = employeeService.searchEmployeeByGenderPositionCategoryIsInactive(eP.getGender(),
                                        eP.getPositionCategoryName(), eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                        .toList();
                }

                else if (!checkString(eP.getName()) && checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName())
                                && checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                        employees = employeeService
                                        .searchEmployeeByGenderDepartmentIsInactive(eP.getGender(), eP.getDepartment(),
                                                        eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                        .toList();
                }

                else if (!checkString(eP.getName()) && !checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName())
                                && checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                        employees = employeeService
                                        .searchEmployeeByPositionCategoryDepartmentIsInactive(
                                                        eP.getPositionCategoryName(),
                                                        eP.getDepartment(), eP.getStatus().equalsIgnoreCase("1"),
                                                        pageRequest)
                                        .toList();
                }
                // 444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444
                else if (checkString(eP.getName()) && checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName())
                                && checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                        employees = employeeService.searchEmployeeByNameGenderPositionCategoryDepartment(eP.getName(),
                                        eP.getGender(), eP.getPositionCategoryName(), eP.getDepartment(), pageRequest)
                                        .toList();
                } else if (checkString(eP.getName()) && checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName())
                                && !checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                        employees = employeeService.searchEmployeeByNameGenderPositionCategoryIsInactive(eP.getName(),
                                        eP.getGender(), eP.getPositionCategoryName(),
                                        eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                        .toList();
                } else if (checkString(eP.getName()) && checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName()) && checkString(eP.getDepartment())
                                && checkString(eP.getStatus())) {
                        employees = employeeService.searchEmployeeByNameGenderDepartmentIsInactive(eP.getName(),
                                        eP.getGender(),
                                        eP.getDepartment(), eP.getStatus().equalsIgnoreCase("1"), pageRequest).toList();
                } else if (checkString(eP.getName()) && !checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName()) && checkString(eP.getDepartment())
                                && checkString(eP.getStatus())) {
                        employees = employeeService
                                        .searchEmployeeByNamePositionCategoryDepartmentIsInactive(eP.getName(),
                                                        eP.getPositionCategoryName(), eP.getDepartment(),
                                                        eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                        .toList();
                } else if (!checkString(eP.getName()) && checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName()) && checkString(eP.getDepartment())
                                && checkString(eP.getStatus())) {
                        employees = employeeService
                                        .searchEmployeeByGenderPositionCategoryDepartmentIsInactive(eP.getGender(),
                                                        eP.getPositionCategoryName(), eP.getDepartment(),
                                                        eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                        .toList();
                }
                // 5555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555
                else if (checkString(eP.getName()) && checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName())
                                && checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                        employees = employeeService.searchEmployeeByNameGenderPositionCategoryDepartmentIsInactive(
                                        eP.getName(),
                                        eP.getGender(), eP.getPositionCategoryName(), eP.getDepartment(),
                                        eP.getStatus().equalsIgnoreCase("1"), pageRequest).toList();
                } else {
                        employees = employeeService.searchEmployees(pageRequest).toList();
                }
                response = employeeService.copyEmployeeListToEmployeeProfileList(employees);
                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @PostMapping("/common/count")
        public ResponseEntity<?> count(@RequestBody EmployeeProfile eP) {
                long employeeCount = 0;
                if (checkString(eP.getName()) && !checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName())) {
                        employeeCount = employeeService.countEmployeeByName(eP.getName());
                }
                // 111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111
                if (checkString(eP.getName()) && !checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName())
                                && !checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByName(eP.getName());
                } else if (!checkString(eP.getName()) && checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName()) && !checkString(eP.getDepartment())
                                && !checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByGender(eP.getGender());
                } else if (!checkString(eP.getName()) && !checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName()) && !checkString(eP.getDepartment())
                                && !checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByPositionCategory(eP.getPositionCategoryName());
                } else if (!checkString(eP.getName()) && !checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName()) && checkString(eP.getDepartment())
                                && !checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByDepartment(eP.getDepartment());
                } else if (!checkString(eP.getName()) && !checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName()) && !checkString(eP.getDepartment())
                                && checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByIsInactive(eP.getStatus().equalsIgnoreCase("1"));
                }
                // 2222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222
                else if (checkString(eP.getName()) && checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName())
                                && !checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByNameGender(eP.getName(), eP.getGender());
                }

                else if (checkString(eP.getName()) && checkString(eP.getPositionCategoryName())
                                && !checkString(eP.getGender())
                                && !checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByNamePositionCategory(eP.getName(),
                                        eP.getPositionCategoryName());
                }

                else if (checkString(eP.getName()) && checkString(eP.getDepartment())
                                && !checkString(eP.getPositionCategoryName()) && !checkString(eP.getGender())
                                && !checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByNameDepartment(eP.getName(), eP.getDepartment());
                }

                else if (checkString(eP.getName()) && checkString(eP.getStatus()) && !checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName()) && !checkString(eP.getDepartment())) {
                        employeeCount = employeeService.countEmployeeByNameIsInactive(eP.getName(),
                                        eP.getStatus().equalsIgnoreCase("1"));
                }

                else if (!checkString(eP.getName()) && checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName())
                                && !checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByGenderPositionCategory(eP.getGender(),
                                        eP.getPositionCategoryName());
                }

                else if (!checkString(eP.getName()) && checkString(eP.getGender()) && checkString(eP.getDepartment())
                                && !checkString(eP.getPositionCategoryName()) && !checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByGenderDepartment(eP.getGender(),
                                        eP.getDepartment());
                }

                else if (!checkString(eP.getName()) && checkString(eP.getGender()) && checkString(eP.getStatus())
                                && !checkString(eP.getPositionCategoryName()) && !checkString(eP.getDepartment())) {
                        employeeCount = employeeService.countEmployeeByGenderIsInactive(eP.getGender(),
                                        eP.getStatus().equalsIgnoreCase("1"));
                }

                else if (!checkString(eP.getName()) && checkString(eP.getPositionCategoryName())
                                && checkString(eP.getDepartment()) && !checkString(eP.getGender())
                                && !checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByPositionCategoryDepartment(
                                        eP.getPositionCategoryName(),
                                        eP.getDepartment());
                }

                else if (!checkString(eP.getName()) && checkString(eP.getPositionCategoryName())
                                && checkString(eP.getStatus())
                                && !checkString(eP.getDepartment()) && !checkString(eP.getGender())) {
                        employeeCount = employeeService.countEmployeeByPositionCategoryIsInactive(
                                        eP.getPositionCategoryName(),
                                        eP.getStatus().equalsIgnoreCase("1"));
                }

                else if (!checkString(eP.getName()) && checkString(eP.getDepartment()) && checkString(eP.getStatus())
                                && !checkString(eP.getGender()) && !checkString(eP.getPositionCategoryName())) {
                        employeeCount = employeeService.countEmployeeByDepartmentIsInactive(eP.getDepartment(),
                                        eP.getStatus().equalsIgnoreCase("1"));
                }
                // 333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333
                else if (checkString(eP.getName()) && checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName())
                                && !checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByNameGenderPositionCategory(eP.getName(),
                                        eP.getGender(),
                                        eP.getPositionCategoryName());
                }

                else if (checkString(eP.getName()) && checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName())
                                && checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByNameGenderDepartment(eP.getName(),
                                        eP.getGender(),
                                        eP.getDepartment());
                }

                else if (checkString(eP.getName()) && checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName())
                                && !checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByNameGenderIsInactive(eP.getName(),
                                        eP.getGender(),
                                        eP.getStatus().equalsIgnoreCase("1"));
                }

                else if (checkString(eP.getName()) && !checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName())
                                && checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByNamePositionCategoryDepartment(eP.getName(),
                                        eP.getPositionCategoryName(), eP.getDepartment());
                }

                else if (checkString(eP.getName()) && !checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName())
                                && !checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByNamePositionCategoryIsInactive(eP.getName(),
                                        eP.getPositionCategoryName(), eP.getStatus().equalsIgnoreCase("1"));
                }

                else if (checkString(eP.getName()) && !checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName())
                                && checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByNameDepartmentIsInactive(eP.getName(),
                                        eP.getDepartment(),
                                        eP.getStatus().equalsIgnoreCase("1"));
                }

                else if (!checkString(eP.getName()) && checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName())
                                && checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByGenderPositionCategoryDepartment(eP.getGender(),
                                        eP.getPositionCategoryName(), eP.getDepartment());
                }

                else if (!checkString(eP.getName()) && checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName())
                                && !checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByGenderPositionCategoryIsInactive(eP.getGender(),
                                        eP.getPositionCategoryName(), eP.getStatus().equalsIgnoreCase("1"));
                }

                else if (!checkString(eP.getName()) && checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName())
                                && checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByGenderDepartmentIsInactive(eP.getGender(),
                                        eP.getDepartment(), eP.getStatus().equalsIgnoreCase("1"));
                }

                else if (!checkString(eP.getName()) && !checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName())
                                && checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByPositionCategoryDepartmentIsInactive(
                                        eP.getPositionCategoryName(), eP.getDepartment(),
                                        eP.getStatus().equalsIgnoreCase("1"));
                }
                // 444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444
                else if (checkString(eP.getName()) && checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName())
                                && checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByNameGenderPositionCategoryDepartment(
                                        eP.getName(),
                                        eP.getGender(), eP.getPositionCategoryName(), eP.getDepartment());
                } else if (checkString(eP.getName()) && checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName())
                                && !checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByNameGenderPositionCategoryIsInactive(
                                        eP.getName(),
                                        eP.getGender(), eP.getPositionCategoryName(),
                                        eP.getStatus().equalsIgnoreCase("1"));
                } else if (checkString(eP.getName()) && checkString(eP.getGender())
                                && !checkString(eP.getPositionCategoryName()) && checkString(eP.getDepartment())
                                && checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByNameGenderDepartmentIsInactive(eP.getName(),
                                        eP.getGender(),
                                        eP.getDepartment(), eP.getStatus().equalsIgnoreCase("1"));
                } else if (checkString(eP.getName()) && !checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName()) && checkString(eP.getDepartment())
                                && checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByNamePositionCategoryDepartmentIsInactive(
                                        eP.getName(),
                                        eP.getPositionCategoryName(), eP.getDepartment(),
                                        eP.getStatus().equalsIgnoreCase("1"));
                } else if (!checkString(eP.getName()) && checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName()) && checkString(eP.getDepartment())
                                && checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByGenderPositionCategoryDepartmentIsInactive(
                                        eP.getGender(),
                                        eP.getPositionCategoryName(), eP.getDepartment(),
                                        eP.getStatus().equalsIgnoreCase("1"));
                }
                // 5555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555
                else if (checkString(eP.getName()) && checkString(eP.getGender())
                                && checkString(eP.getPositionCategoryName())
                                && checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                        employeeCount = employeeService.countEmployeeByNameGenderPositionCategoryDepartmentIsInactive(
                                        eP.getName(),
                                        eP.getGender(), eP.getPositionCategoryName(), eP.getDepartment(),
                                        eP.getStatus().equalsIgnoreCase("1"));
                } else {
                        employeeCount = employeeService.countEmployees();
                }
                return new ResponseEntity<>(employeeCount, HttpStatus.OK);
        }

        @PostMapping("/common/positionCategories")
        public ResponseEntity<?> positionCategories() {
                List<PositionCategoryDTO> positionCategoryDTOs = positionService.getAllPositionCategories();
                return new ResponseEntity<>(positionCategoryDTOs, HttpStatus.OK);
        }

        @PostMapping("/common/departmentNames")
        public ResponseEntity<?> departmentNames() {
                List<IdNamePairDTO> idNamePairDTOs = employeeService.getAllDepartmentNames();
                return new ResponseEntity<>(idNamePairDTOs, HttpStatus.OK);
        }

        @PostMapping("/common/employeeuniformitems")
        public ResponseEntity<?> employeeUniformItems(@RequestBody EmployeeProfile eP) {
                List<EmployeeUniformItemDTO> employeeUniformItemDTOs = employeeService
                                .searchEmployeeUniformItemList(eP.getId());
                return new ResponseEntity<>(employeeUniformItemDTOs, HttpStatus.OK);
        }

        @PostMapping("/common/employeedetails")
        public ResponseEntity<?> employeeDetails(@RequestBody EmployeeProfile eP) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (utilService.checkUserRequest(authentication,
                                new ArrayList<>(Arrays.asList(officerRole, managerRole)))) {
                        return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
                }
                EmployeeProfile employeeProfile = employeeService
                                .copyEmployeeToEmployeeProfile(employeeService.getEmployeeById(eP.getId()).get());
                return new ResponseEntity<>(employeeProfile, HttpStatus.OK);
        }

        public boolean checkString(String stringToCheck) {
                return (stringToCheck != null && !stringToCheck.isBlank());
        }

        @PostMapping("/common/download")
        public ResponseEntity<?> downloadExcelPost(@RequestBody EmployeeProfile eP) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (utilService.checkUserRequest(authentication,
                                new ArrayList<>(Arrays.asList(officerRole, managerRole)))) {
                        return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
                }
                try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        Sheet sheet = workbook.createSheet("Entitled Staff Report");
                        /// ??????????????????????????????????????????????????????????????????????????????????????????????????
                        PageRequest pageRequest = PageRequest.of(0, 100000);
                        List<Employee> employees = new ArrayList<Employee>();
                        // 111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111
                        if (checkString(eP.getName()) && !checkString(eP.getGender())
                                        && !checkString(eP.getPositionCategoryName())
                                        && !checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                                employees = employeeService.searchEmployeeByName(eP.getName(), pageRequest).toList();
                        } else if (!checkString(eP.getName()) && checkString(eP.getGender())
                                        && !checkString(eP.getPositionCategoryName())
                                        && !checkString(eP.getDepartment())
                                        && !checkString(eP.getStatus())) {
                                employees = employeeService.searchEmployeeByGender(eP.getGender(), pageRequest)
                                                .toList();
                        } else if (!checkString(eP.getName()) && !checkString(eP.getGender())
                                        && checkString(eP.getPositionCategoryName()) && !checkString(eP.getDepartment())
                                        && !checkString(eP.getStatus())) {
                                employees = employeeService
                                                .searchEmployeeByPositionCategory(eP.getPositionCategoryName(),
                                                                pageRequest)
                                                .toList();
                                System.out.println("eP.getPositionCategoryName() === " + eP.getPositionCategoryName());
                        } else if (!checkString(eP.getName()) && !checkString(eP.getGender())
                                        && !checkString(eP.getPositionCategoryName()) && checkString(eP.getDepartment())
                                        && !checkString(eP.getStatus())) {
                                employees = employeeService.searchEmployeeByDepartment(eP.getDepartment(), pageRequest)
                                                .toList();
                        } else if (!checkString(eP.getName()) && !checkString(eP.getGender())
                                        && !checkString(eP.getPositionCategoryName())
                                        && !checkString(eP.getDepartment())
                                        && checkString(eP.getStatus())) {
                                System.out.println(
                                                "Controller checkString(eP.getStatus()) ==== "
                                                                + checkString(eP.getStatus()));
                                employees = employeeService
                                                .searchEmployeeByIsInactive(eP.getStatus().equalsIgnoreCase("1"),
                                                                pageRequest)
                                                .toList();
                                System.out.println("Controller employees.size() ==== " + employees.size());
                        }
                        // 2222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222
                        else if (checkString(eP.getName()) && checkString(eP.getGender())
                                        && !checkString(eP.getPositionCategoryName())
                                        && !checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                                employees = employeeService
                                                .searchEmployeeByNameGender(eP.getName(), eP.getGender(), pageRequest)
                                                .toList();
                        }

                        else if (checkString(eP.getName()) && checkString(eP.getPositionCategoryName())
                                        && !checkString(eP.getGender())
                                        && !checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                                employees = employeeService
                                                .searchEmployeeByNamePositionCategory(eP.getName(),
                                                                eP.getPositionCategoryName(), pageRequest)
                                                .toList();
                        }

                        else if (checkString(eP.getName()) && checkString(eP.getDepartment())
                                        && !checkString(eP.getPositionCategoryName()) && !checkString(eP.getGender())
                                        && !checkString(eP.getStatus())) {
                                employees = employeeService
                                                .searchEmployeeByNameDepartment(eP.getName(), eP.getDepartment(),
                                                                pageRequest)
                                                .toList();
                        }

                        else if (checkString(eP.getName()) && checkString(eP.getStatus())
                                        && !checkString(eP.getGender())
                                        && !checkString(eP.getPositionCategoryName())
                                        && !checkString(eP.getDepartment())) {
                                employees = employeeService
                                                .searchEmployeeByNameIsInactive(eP.getName(),
                                                                eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                                .toList();
                        }

                        else if (!checkString(eP.getName()) && checkString(eP.getGender())
                                        && checkString(eP.getPositionCategoryName())
                                        && !checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                                employees = employeeService
                                                .searchEmployeeByGenderPositionCategory(eP.getGender(),
                                                                eP.getPositionCategoryName(), pageRequest)
                                                .toList();
                        }

                        else if (!checkString(eP.getName()) && checkString(eP.getGender())
                                        && checkString(eP.getDepartment())
                                        && !checkString(eP.getPositionCategoryName()) && !checkString(eP.getStatus())) {
                                employees = employeeService
                                                .searchEmployeeByGenderDepartment(eP.getGender(), eP.getDepartment(),
                                                                pageRequest)
                                                .toList();
                        }

                        else if (!checkString(eP.getName()) && checkString(eP.getGender())
                                        && checkString(eP.getStatus())
                                        && !checkString(eP.getPositionCategoryName())
                                        && !checkString(eP.getDepartment())) {
                                employees = employeeService
                                                .searchEmployeeByGenderIsInactive(eP.getGender(),
                                                                eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                                .toList();
                        }

                        else if (!checkString(eP.getName()) && checkString(eP.getPositionCategoryName())
                                        && checkString(eP.getDepartment()) && !checkString(eP.getGender())
                                        && !checkString(eP.getStatus())) {
                                employees = employeeService
                                                .searchEmployeeByPositionCategoryDepartment(
                                                                eP.getPositionCategoryName(),
                                                                eP.getDepartment(), pageRequest)
                                                .toList();
                        }

                        else if (!checkString(eP.getName()) && checkString(eP.getPositionCategoryName())
                                        && checkString(eP.getStatus())
                                        && !checkString(eP.getDepartment()) && !checkString(eP.getGender())) {
                                employees = employeeService
                                                .searchEmployeeByPositionCategoryIsInactive(
                                                                eP.getPositionCategoryName(),
                                                                eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                                .toList();
                        }

                        else if (!checkString(eP.getName()) && checkString(eP.getDepartment())
                                        && checkString(eP.getStatus())
                                        && !checkString(eP.getGender()) && !checkString(eP.getPositionCategoryName())) {
                                employees = employeeService.searchEmployeeByDepartmentIsInactive(eP.getDepartment(),
                                                eP.getStatus().equalsIgnoreCase("1"), pageRequest).toList();
                        }
                        // 333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333
                        else if (checkString(eP.getName()) && checkString(eP.getGender())
                                        && checkString(eP.getPositionCategoryName())
                                        && !checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                                employees = employeeService
                                                .searchEmployeeByNameGenderPositionCategory(eP.getName(),
                                                                eP.getGender(),
                                                                eP.getPositionCategoryName(), pageRequest)
                                                .toList();
                        }

                        else if (checkString(eP.getName()) && checkString(eP.getGender())
                                        && !checkString(eP.getPositionCategoryName())
                                        && checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                                employees = employeeService
                                                .searchEmployeeByNameGenderDepartment(eP.getName(), eP.getGender(),
                                                                eP.getDepartment(), pageRequest)
                                                .toList();
                        }

                        else if (checkString(eP.getName()) && checkString(eP.getGender())
                                        && !checkString(eP.getPositionCategoryName())
                                        && !checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                                employees = employeeService
                                                .searchEmployeeByNameGenderIsInactive(eP.getName(), eP.getGender(),
                                                                eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                                .toList();
                        }

                        else if (checkString(eP.getName()) && !checkString(eP.getGender())
                                        && checkString(eP.getPositionCategoryName())
                                        && checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                                employees = employeeService.searchEmployeeByNamePositionCategoryDepartment(eP.getName(),
                                                eP.getPositionCategoryName(), eP.getDepartment(), pageRequest).toList();
                        }

                        else if (checkString(eP.getName()) && !checkString(eP.getGender())
                                        && checkString(eP.getPositionCategoryName())
                                        && !checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                                employees = employeeService.searchEmployeeByNamePositionCategoryIsInactive(eP.getName(),
                                                eP.getPositionCategoryName(), eP.getStatus().equalsIgnoreCase("1"),
                                                pageRequest)
                                                .toList();
                        }

                        else if (checkString(eP.getName()) && !checkString(eP.getGender())
                                        && !checkString(eP.getPositionCategoryName())
                                        && checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                                employees = employeeService
                                                .searchEmployeeByNameDepartmentIsInactive(eP.getName(),
                                                                eP.getDepartment(),
                                                                eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                                .toList();
                        }

                        else if (!checkString(eP.getName()) && checkString(eP.getGender())
                                        && checkString(eP.getPositionCategoryName())
                                        && checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                                employees = employeeService.searchEmployeeByGenderPositionCategoryDepartment(
                                                eP.getGender(),
                                                eP.getPositionCategoryName(), eP.getDepartment(), pageRequest).toList();
                        }

                        else if (!checkString(eP.getName()) && checkString(eP.getGender())
                                        && checkString(eP.getPositionCategoryName())
                                        && !checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                                employees = employeeService
                                                .searchEmployeeByGenderPositionCategoryIsInactive(eP.getGender(),
                                                                eP.getPositionCategoryName(),
                                                                eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                                .toList();
                        }

                        else if (!checkString(eP.getName()) && checkString(eP.getGender())
                                        && !checkString(eP.getPositionCategoryName())
                                        && checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                                employees = employeeService
                                                .searchEmployeeByGenderDepartmentIsInactive(eP.getGender(),
                                                                eP.getDepartment(),
                                                                eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                                .toList();
                        }

                        else if (!checkString(eP.getName()) && !checkString(eP.getGender())
                                        && checkString(eP.getPositionCategoryName())
                                        && checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                                employees = employeeService
                                                .searchEmployeeByPositionCategoryDepartmentIsInactive(
                                                                eP.getPositionCategoryName(),
                                                                eP.getDepartment(),
                                                                eP.getStatus().equalsIgnoreCase("1"),
                                                                pageRequest)
                                                .toList();
                        }
                        // 444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444
                        else if (checkString(eP.getName()) && checkString(eP.getGender())
                                        && checkString(eP.getPositionCategoryName())
                                        && checkString(eP.getDepartment()) && !checkString(eP.getStatus())) {
                                employees = employeeService
                                                .searchEmployeeByNameGenderPositionCategoryDepartment(eP.getName(),
                                                                eP.getGender(), eP.getPositionCategoryName(),
                                                                eP.getDepartment(), pageRequest)
                                                .toList();
                        } else if (checkString(eP.getName()) && checkString(eP.getGender())
                                        && checkString(eP.getPositionCategoryName())
                                        && !checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                                employees = employeeService
                                                .searchEmployeeByNameGenderPositionCategoryIsInactive(eP.getName(),
                                                                eP.getGender(), eP.getPositionCategoryName(),
                                                                eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                                .toList();
                        } else if (checkString(eP.getName()) && checkString(eP.getGender())
                                        && !checkString(eP.getPositionCategoryName()) && checkString(eP.getDepartment())
                                        && checkString(eP.getStatus())) {
                                employees = employeeService.searchEmployeeByNameGenderDepartmentIsInactive(eP.getName(),
                                                eP.getGender(),
                                                eP.getDepartment(), eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                                .toList();
                        } else if (checkString(eP.getName()) && !checkString(eP.getGender())
                                        && checkString(eP.getPositionCategoryName()) && checkString(eP.getDepartment())
                                        && checkString(eP.getStatus())) {
                                employees = employeeService
                                                .searchEmployeeByNamePositionCategoryDepartmentIsInactive(eP.getName(),
                                                                eP.getPositionCategoryName(), eP.getDepartment(),
                                                                eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                                .toList();
                        } else if (!checkString(eP.getName()) && checkString(eP.getGender())
                                        && checkString(eP.getPositionCategoryName()) && checkString(eP.getDepartment())
                                        && checkString(eP.getStatus())) {
                                employees = employeeService
                                                .searchEmployeeByGenderPositionCategoryDepartmentIsInactive(
                                                                eP.getGender(),
                                                                eP.getPositionCategoryName(), eP.getDepartment(),
                                                                eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                                .toList();
                        }
                        // 5555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555
                        else if (checkString(eP.getName()) && checkString(eP.getGender())
                                        && checkString(eP.getPositionCategoryName())
                                        && checkString(eP.getDepartment()) && checkString(eP.getStatus())) {
                                employees = employeeService
                                                .searchEmployeeByNameGenderPositionCategoryDepartmentIsInactive(
                                                                eP.getName(),
                                                                eP.getGender(), eP.getPositionCategoryName(),
                                                                eP.getDepartment(),
                                                                eP.getStatus().equalsIgnoreCase("1"), pageRequest)
                                                .toList();
                        } else {
                                employees = employeeService.searchEmployees(pageRequest).toList();
                        }
                        int rowIdx = 0;
                        Row row = sheet.createRow(rowIdx);
                        row.createCell(0).setCellValue("S.No.");
                        row.createCell(1).setCellValue("Badge Number");
                        row.createCell(2).setCellValue("Name");
                        row.createCell(3).setCellValue("Gender");
                        row.createCell(4).setCellValue("Category");
                        row.createCell(5).setCellValue("Job Position");
                        row.createCell(6).setCellValue("Job Grade");
                        row.createCell(7).setCellValue("Job Category");
                        row.createCell(8).setCellValue("Department");
                        row.createCell(9).setCellValue("Status");
                        row.createCell(10).setCellValue("EMail");
                        for (Employee employee : employees) {
                                row = sheet.createRow(++rowIdx);
                                row.createCell(0).setCellValue(rowIdx);
                                row.createCell(1).setCellValue(employee.getBadgeNumber());
                                row.createCell(2).setCellValue(employee.getName());
                                row.createCell(3).setCellValue(employee.getGender());
                                row.createCell(4).setCellValue(employee.getPositionCategory().getName());
                                row.createCell(5).setCellValue(employee.getJobPosition());
                                row.createCell(6).setCellValue(employee.getJobGrade());
                                row.createCell(7).setCellValue(employee.getJobCategory());
                                row.createCell(8).setCellValue(employee.getDepartment());
                                row.createCell(9).setCellValue(employee.getInactive() ? "Inactive" : "Active");
                                row.createCell(10).setCellValue(employee.geteMail());
                        }
                        for (int i = 0; i < row.getLastCellNum(); i++) {
                                sheet.autoSizeColumn(i);
                        }
                        workbook.write(out);
                        workbook.close();
                        byte[] excelBytes = out.toByteArray();
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                        headers.setContentDispositionFormData("attachment", "sample.xlsx");
                        headers.setContentLength(excelBytes.length);
                        return ResponseEntity.ok()
                                        .headers(headers)
                                        .body(excelBytes);
                } catch (IOException e) {
                        e.printStackTrace();
                        return ResponseEntity.status(500).build();
                }
        }
}
