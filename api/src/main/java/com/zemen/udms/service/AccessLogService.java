package com.zemen.udms.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zemen.udms.dto.AccessLogDTO;
import com.zemen.udms.model.AccessLog;
import com.zemen.udms.repository.AccessLogRepository;

@Service
@Transactional
public class AccessLogService {

    @Autowired
    private AccessLogRepository accessLogRepository;

    public boolean createAccessLog(AccessLog accessLog) {
        AccessLog savedAccessLog = accessLogRepository.save(accessLog);
        if (savedAccessLog != null) {
            return true;
        }
        return false;
    }

    // ##B Searching
    public Page<AccessLog> searchAccessLogs(PageRequest pageRequest) {
        return accessLogRepository.findAllOrderByDate(pageRequest);
    }

    public Page<AccessLog> searchAccessLogByDate(Date dateFrom, Date dateTo, PageRequest pageRequest) {
        return accessLogRepository.findByDateBetween(dateFrom, dateTo, pageRequest);
    }

    // ##E Searching
    // ##B Counting

    public long countAll() {
        return accessLogRepository.countAll();
    }

    public long countAccessLogByDate(Date dateFrom, Date dateTo) {
        return accessLogRepository.countByDateBetween(dateFrom, dateTo);
    }

    // ##E Counting
    // ##########################################################################################################################

    public AccessLogDTO copyAccessLogToAccessLogDTO(AccessLog accessLog) {
        AccessLogDTO accessLogDTO = new AccessLogDTO();
        accessLogDTO.setId(accessLog.getId());
        accessLogDTO.setDate(accessLog.getDate());
        accessLogDTO.setUsername(accessLog.getUsername());
        accessLogDTO.setOperation(accessLog.getOperation());
        accessLogDTO.setObjectInfo(accessLog.getObjectInfo());
        return accessLogDTO;
    }

    public List<AccessLogDTO> copyAccessLogListToAccessLogDTOList(List<AccessLog> accessLogs) {
        List<AccessLogDTO> accessLogDTOs = new ArrayList<AccessLogDTO>();
        for (AccessLog tempAccessLog : accessLogs) {
            AccessLogDTO accessLogDTO = new AccessLogDTO();
            accessLogDTO.setId(tempAccessLog.getId());
            accessLogDTO.setDate(tempAccessLog.getDate());
            accessLogDTO.setUsername(tempAccessLog.getUsername());
            accessLogDTO.setOperation(tempAccessLog.getOperation());
            accessLogDTO.setObjectInfo(tempAccessLog.getObjectInfo());
            accessLogDTOs.add(accessLogDTO);
        }
        return accessLogDTOs;
    }

}
