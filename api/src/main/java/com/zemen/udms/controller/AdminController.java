package com.zemen.udms.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zemen.udms.dto.AccessLogDTO;
import com.zemen.udms.model.AccessLog;
import com.zemen.udms.service.AccessLogService;
import com.zemen.udms.util.UtilService;

@RestController
@RequestMapping("/udmsapi/admin")
public class AdminController {

    String client = "TestUDMS";
    // String client = "UDMS"; 
    String administratorRole = "Admin";

    @Autowired
    private AccessLogService accessLogService;

    @Autowired
    private UtilService utilService;

    @PostMapping("/index")
    public ResponseEntity<?> index(@RequestBody AccessLogDTO ald) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(utilService.checkUserRequest(authentication, new ArrayList<>(Arrays.asList(administratorRole))))
        {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(ald.getPage()),
                Integer.parseInt(ald.getRowsPerPage()));
        List<AccessLog> accessLogs;
        List<AccessLogDTO> response;
        if (checkDate(ald.getDateFrom()) && checkDate(ald.getDateTo())) {
            Calendar dateToCal = Calendar.getInstance();
            dateToCal.setTime(ald.getDateTo());
            dateToCal.set(dateToCal.get(Calendar.YEAR), dateToCal.get(Calendar.MONTH), dateToCal.get(Calendar.DATE),
                    Calendar.HOUR_OF_DAY, 59, 59);
            Date dateTo = dateToCal.getTime();
            accessLogs = accessLogService.searchAccessLogByDate(ald.getDateFrom(), dateTo, pageRequest)
                    .toList();
        } else {
            accessLogs = accessLogService.searchAccessLogs(pageRequest).toList();
        }
        response = accessLogService.copyAccessLogListToAccessLogDTOList(accessLogs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/count")
    public ResponseEntity<?> count(@RequestBody AccessLogDTO ald) {
        long accessLogCount = 0;
        if (checkDate(ald.getDateFrom()) && checkDate(ald.getDateTo())) {
            Calendar dateToCal = Calendar.getInstance();
            dateToCal.setTime(ald.getDateTo());
            dateToCal.set(dateToCal.get(Calendar.YEAR), dateToCal.get(Calendar.MONTH), dateToCal.get(Calendar.DATE),
                    Calendar.HOUR_OF_DAY, 59, 59);
            Date dateTo = dateToCal.getTime();
            accessLogCount = accessLogService.countAccessLogByDate(ald.getDateFrom(), dateTo);
        } else {
            accessLogCount = accessLogService.countAll();
        }
        return new ResponseEntity<>(accessLogCount, HttpStatus.OK);
    }

    public boolean checkString(String stringToCheck) {
        return (stringToCheck != null && !stringToCheck.isBlank());
    }

    public boolean checkDate(Date dateToCheck) {
        return (dateToCheck != null);
    }

}
