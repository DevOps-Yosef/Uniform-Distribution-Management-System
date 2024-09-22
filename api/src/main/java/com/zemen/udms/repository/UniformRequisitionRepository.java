package com.zemen.udms.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zemen.udms.model.AccessLog;
import com.zemen.udms.model.UniformRequisition;

public interface UniformRequisitionRepository extends JpaRepository<UniformRequisition, String> {

    @Query("SELECT al FROM AccessLog al WHERE al.date between :dateFrom and :dateTo ORDER BY al.date DESC")
    Page<AccessLog> findByDateBetween(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, Pageable pageable);

    @Query("SELECT ur FROM UniformRequisition ur JOIN ur.uniformRequisitionDetailCollection urdc JOIN ur.luStatusId lsi WHERE lsi.name = 'Pending'")
    List<UniformRequisition> findPendingUniformRequisitions();

    @Query("SELECT ur FROM UniformRequisition ur JOIN ur.uniformRequisitionDetailCollection urdc JOIN ur.luStatusId lsi WHERE lsi.name = 'Pending' and ur.department = :department")
    List<UniformRequisition> findPendingUniformRequisitionsByDepartment(@Param("department") String department);

    //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    @Query("SELECT ur FROM UniformRequisition ur JOIN ur.luStatusId lsi WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') ORDER BY ur.approvedDate DESC")
    Page<UniformRequisition> findApprovedRejectedUniformRequisitions(Pageable pageable);

    @Query("SELECT ur FROM UniformRequisition ur JOIN ur.luStatusId lsi WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') and ur.department = :department ORDER BY ur.approvedDate DESC")
    Page<UniformRequisition> findApprovedRejectedUniformRequisitionsByDepartment(@Param("department") String department, Pageable pageable);

    @Query("SELECT ur FROM UniformRequisition ur JOIN ur.luStatusId lsi JOIN ur.employeeId e JOIN e.positionCategory pc WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') and pc.name= :positionCategory ORDER BY ur.approvedDate DESC")
    Page<UniformRequisition> findApprovedRejectedUniformRequisitionsByPositionCategory(@Param("positionCategory") String positionCategory, Pageable pageable);

    @Query("SELECT ur FROM UniformRequisition ur JOIN ur.luStatusId lsi WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') and ur.approvedDate between :dateFrom and :dateTo ORDER BY ur.approvedDate DESC")
    Page<UniformRequisition> findApprovedRejectedUniformRequisitionsByDate(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, Pageable pageable);

    @Query("SELECT ur FROM UniformRequisition ur JOIN ur.luStatusId lsi WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') and ur.department = :department and ur.approvedDate between :dateFrom and :dateTo ORDER BY ur.approvedDate DESC")
    Page<UniformRequisition> findApprovedRejectedUniformRequisitionsByDateDepartment(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("department") String department, Pageable pageable);
    
    @Query("SELECT ur FROM UniformRequisition ur JOIN ur.luStatusId lsi JOIN ur.employeeId e JOIN e.positionCategory pc WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') and pc.name= :positionCategory and ur.approvedDate between :dateFrom and :dateTo ORDER BY ur.approvedDate DESC")
    Page<UniformRequisition> findApprovedRejectedUniformRequisitionsByDatePositionCategory(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("positionCategory") String positionCategory, Pageable pageable);

    @Query("SELECT ur FROM UniformRequisition ur JOIN ur.luStatusId lsi JOIN ur.employeeId e JOIN e.positionCategory pc WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') and ur.department = :department and pc.name= :positionCategory ORDER BY ur.approvedDate DESC")
    Page<UniformRequisition> findApprovedRejectedUniformRequisitionsByDepartmentPositionCategory(@Param("department") String department, @Param("positionCategory") String positionCategory, Pageable pageable);

    @Query("SELECT ur FROM UniformRequisition ur JOIN ur.luStatusId lsi JOIN ur.employeeId e JOIN e.positionCategory pc WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') and ur.department = :department and pc.name= :positionCategory and ur.approvedDate between :dateFrom and :dateTo ORDER BY ur.approvedDate DESC")
    Page<UniformRequisition> findApprovedRejectedUniformRequisitionsByDateDepartmentPositionCategory(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("department") String department, @Param("positionCategory") String positionCategory, Pageable pageable);
    //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    
    @Query("SELECT count(ur) FROM UniformRequisition ur JOIN ur.luStatusId lsi WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected')")
    long countApprovedRejectedUniformRequisitions();

    @Query("SELECT count(ur) FROM UniformRequisition ur JOIN ur.luStatusId lsi WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') and ur.department = :department")
    long countApprovedRejectedUniformRequisitionsByDepartment(@Param("department") String department);

    @Query("SELECT count(ur) FROM UniformRequisition ur JOIN ur.luStatusId lsi JOIN ur.employeeId e JOIN e.positionCategory pc WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') and pc.name= :positionCategory")
    long countApprovedRejectedUniformRequisitionsByPositionCategory(@Param("positionCategory") String positionCategory);

    @Query("SELECT count(ur) FROM UniformRequisition ur JOIN ur.luStatusId lsi WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') and ur.approvedDate between :dateFrom and :dateTo")
    long countApprovedRejectedUniformRequisitionsByDate(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT count(ur) FROM UniformRequisition ur JOIN ur.luStatusId lsi WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') and ur.department = :department and ur.approvedDate between :dateFrom and :dateTo")
    long countApprovedRejectedUniformRequisitionsByDateDepartment(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("department") String department);
    
    @Query("SELECT count(ur) FROM UniformRequisition ur JOIN ur.luStatusId lsi JOIN ur.employeeId e JOIN e.positionCategory pc WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') and pc.name= :positionCategory and ur.approvedDate between :dateFrom and :dateTo")
    long countApprovedRejectedUniformRequisitionsByDatePositionCategory(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("positionCategory") String positionCategory);

    @Query("SELECT count(ur) FROM UniformRequisition ur JOIN ur.luStatusId lsi JOIN ur.employeeId e JOIN e.positionCategory pc WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') and ur.department = :department and pc.name= :positionCategory")
    long countApprovedRejectedUniformRequisitionsByDepartmentPositionCategory(@Param("department") String department, @Param("positionCategory") String positionCategory);

    @Query("SELECT count(ur) FROM UniformRequisition ur JOIN ur.luStatusId lsi JOIN ur.employeeId e JOIN e.positionCategory pc WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') and ur.department = :department and pc.name= :positionCategory and ur.approvedDate between :dateFrom and :dateTo")
    long countApprovedRejectedUniformRequisitionsByDateDepartmentPositionCategory(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("department") String department, @Param("positionCategory") String positionCategory);

    











    
    @Query("SELECT ur FROM UniformRequisition ur JOIN ur.luStatusId lsi WHERE lsi.name = 'Approved' AND NOT EXISTS (SELECT 1 FROM ur.uniformRequisitionDetailCollection urdc JOIN urdc.uniformIssueDetailCollection uidc WHERE uidc IS NOT NULL) ORDER BY ur.approvedDate DESC")
    List<UniformRequisition> findApprovedUniformRequisitions();
    
    @Query("SELECT ur FROM UniformRequisition ur JOIN ur.luStatusId lsi WHERE lsi.name = 'Approved' and ur.department = :department AND NOT EXISTS (SELECT 1 FROM ur.uniformRequisitionDetailCollection urdc JOIN urdc.uniformIssueDetailCollection uidc WHERE uidc IS NOT NULL) ORDER BY ur.approvedDate DESC")
    List<UniformRequisition> findApprovedUniformRequisitionsByDepartment(@Param("department") String department);











    @Query("SELECT ur FROM UniformRequisition ur JOIN ur.luStatusId lsi JOIN ur.employeeId e JOIN e.positionCategory pc WHERE (lsi.name = 'Approved') and pc.name= :positionCategory ORDER BY ur.approvedDate DESC")
    List<UniformRequisition> findApprovedUniformRequisitionsByPositionCategory(@Param("positionCategory") String positionCategory);

    @Query("SELECT ur FROM UniformRequisition ur JOIN ur.luStatusId lsi WHERE (lsi.name = 'Approved') and ur.approvedDate between :dateFrom and :dateTo ORDER BY ur.approvedDate DESC")
    List<UniformRequisition> findApprovedUniformRequisitionsByDate(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT ur FROM UniformRequisition ur JOIN ur.luStatusId lsi WHERE (lsi.name = 'Approved') and ur.department = :department and ur.approvedDate between :dateFrom and :dateTo ORDER BY ur.approvedDate DESC")
    List<UniformRequisition> findApprovedUniformRequisitionsByDateDepartment(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("department") String department);
    
    @Query("SELECT ur FROM UniformRequisition ur JOIN ur.luStatusId lsi JOIN ur.employeeId e JOIN e.positionCategory pc WHERE (lsi.name = 'Approved') and pc.name= :positionCategory and ur.approvedDate between :dateFrom and :dateTo ORDER BY ur.approvedDate DESC")
    List<UniformRequisition> findApprovedUniformRequisitionsByDatePositionCategory(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("positionCategory") String positionCategory);

    @Query("SELECT ur FROM UniformRequisition ur JOIN ur.luStatusId lsi JOIN ur.employeeId e JOIN e.positionCategory pc WHERE (lsi.name = 'Approved') and ur.department = :department and pc.name= :positionCategory ORDER BY ur.approvedDate DESC")
    List<UniformRequisition> findApprovedUniformRequisitionsByDepartmentPositionCategory(@Param("department") String department, @Param("positionCategory") String positionCategory);

    @Query("SELECT ur FROM UniformRequisition ur JOIN ur.luStatusId lsi JOIN ur.employeeId e JOIN e.positionCategory pc WHERE (lsi.name = 'Approved') and ur.department = :department and pc.name= :positionCategory and ur.approvedDate between :dateFrom and :dateTo ORDER BY ur.approvedDate DESC")
    List<UniformRequisition> findApprovedUniformRequisitionsByDateDepartmentPositionCategory(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("department") String department, @Param("positionCategory") String positionCategory);

}
