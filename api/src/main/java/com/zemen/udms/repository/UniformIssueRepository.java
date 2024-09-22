package com.zemen.udms.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zemen.udms.model.Employee;
import com.zemen.udms.model.UniformIssue;

public interface UniformIssueRepository extends JpaRepository<UniformIssue, String> {

    @Query("SELECT ui FROM UniformIssue ui JOIN ui.uniformIssueDetailCollection uidc JOIN ui.luStatusId lsi WHERE lsi.name = 'Pending'")
    List<UniformIssue> findPendingUniformIssues();

    @Query("SELECT ui FROM UniformIssue ui JOIN ui.uniformIssueDetailCollection uidc JOIN ui.luStatusId lsi WHERE lsi.name = 'Pending' and ui.department = :department")
    List<UniformIssue> findPendingUniformIssuesByDepartment(@Param("department") String department);

    @Query("SELECT ui FROM UniformIssue ui JOIN ui.uniformIssueDetailCollection uidc JOIN ui.luStatusId lsi WHERE lsi.name = 'Approved' and YEAR(ui.approvedDate) >= :yearFrom AND MONTH(ui.approvedDate) >= 7")
    List<UniformIssue> findApprovedUniformIssuesByDate(@Param("yearFrom") String yearFrom);

    @Query("SELECT ui FROM UniformIssue ui JOIN ui.luStatusId lsi JOIN ui.uniformIssueDetailCollection uid JOIN uid.uniformRequisitionDetail urd JOIN urd.uniformRequisitionId ur JOIN ur.employeeId e JOIN e.positionCategory pc WHERE lsi.name = 'Approved' AND pc.name = :positionCategory AND ui.approvedDate >= :dateFrom AND ui.approvedDate <= :dateTo")
    List<UniformIssue> findApprovedUniformIssuesByPositionCategoryDate(@Param("positionCategory") String positionCategory, @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT Distinct e FROM UniformIssue ui JOIN ui.luStatusId lsi JOIN ui.uniformIssueDetailCollection uid JOIN uid.uniformRequisitionDetail urd JOIN urd.uniformRequisitionId ur JOIN ur.employeeId e JOIN e.positionCategory pc WHERE lsi.name = 'Approved' AND pc.name = :positionCategory AND ui.approvedDate >= :dateFrom AND ui.approvedDate <= :dateTo order by e.name")
    List<Employee> findApprovedUniformIssueEmployeesByPositionCategoryDate(@Param("positionCategory") String positionCategory, @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);
    
    //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    @Query("SELECT ui FROM UniformIssue ui JOIN ui.luStatusId lsi WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') and ui.department = :department ORDER BY ui.approvedDate DESC")
    Page<UniformIssue> findApprovedRejectedUniformIssuesByDepartment(@Param("department") String department, Pageable pageable);

    @Query("SELECT ui FROM UniformIssue ui JOIN ui.luStatusId lsi WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') and ui.department = :department and ui.approvedDate between :dateFrom and :dateTo ORDER BY ui.approvedDate DESC")
    Page<UniformIssue> findApprovedRejectedUniformIssuesByDateDepartment(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("department") String department, Pageable pageable);

    @Query("SELECT ui FROM UniformIssue ui JOIN ui.luStatusId lsi WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') and ui.approvedDate between :dateFrom and :dateTo ORDER BY ui.approvedDate DESC")
    Page<UniformIssue> findApprovedRejectedUniformIssuesByDate(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, Pageable pageable);

    @Query("SELECT ui FROM UniformIssue ui JOIN ui.luStatusId lsi WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') ORDER BY ui.approvedDate DESC")
    Page<UniformIssue> findApprovedRejectedUniformIssues(Pageable pageable);

    @Query("SELECT ui FROM UniformIssue ui JOIN ui.luStatusId lsi JOIN ui.uniformIssueDetailCollection uid JOIN uid.uniformRequisitionDetail urd JOIN urd.uniformRequisitionId ur JOIN ur.employeeId e JOIN e.positionCategory pc WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') AND pc.name = :positionCategory")
    Page<UniformIssue> findApprovedRejectedUniformIssuesByPositionCategory(@Param("positionCategory") String positionCategory, Pageable pageable);

    @Query("SELECT ui FROM UniformIssue ui JOIN ui.luStatusId lsi JOIN ui.uniformIssueDetailCollection uid JOIN uid.uniformRequisitionDetail urd JOIN urd.uniformRequisitionId ur JOIN ur.employeeId e JOIN e.positionCategory pc WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') AND pc.name = :positionCategory AND ui.approvedDate between :dateFrom and :dateTo")
    Page<UniformIssue> findApprovedRejectedUniformIssuesByDatePositionCategory(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("positionCategory") String positionCategory, Pageable pageable);

    @Query("SELECT ui FROM UniformIssue ui JOIN ui.luStatusId lsi JOIN ui.uniformIssueDetailCollection uid JOIN uid.uniformRequisitionDetail urd JOIN urd.uniformRequisitionId ur JOIN ur.employeeId e JOIN e.positionCategory pc WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') AND pc.name = :positionCategory and ui.department = :department")
    Page<UniformIssue> findApprovedRejectedUniformIssuesByDepartmentPositionCategory(@Param("department") String department, @Param("positionCategory") String positionCategory, Pageable pageable);

    @Query("SELECT ui FROM UniformIssue ui JOIN ui.luStatusId lsi JOIN ui.uniformIssueDetailCollection uid JOIN uid.uniformRequisitionDetail urd JOIN urd.uniformRequisitionId ur JOIN ur.employeeId e JOIN e.positionCategory pc WHERE ui.approvedDate between :dateFrom and :dateTo AND (lsi.name = 'Approved' or lsi.name = 'Rejected') AND pc.name = :positionCategory and ui.department = :department")
    Page<UniformIssue> findApprovedRejectedUniformIssuesByDateDepartmentPositionCategory(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo,@Param("department") String department, @Param("positionCategory") String positionCategory, Pageable pageable);

    @Query("SELECT count(ui) FROM UniformIssue ui JOIN ui.luStatusId lsi WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') and ui.department = :department")
    long countApprovedRejectedUniformIssuesByDepartment(@Param("department") String department);

    @Query("SELECT count(ui) FROM UniformIssue ui JOIN ui.luStatusId lsi WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') and ui.department = :department and ui.approvedDate between :dateFrom and :dateTo")
    long countApprovedRejectedUniformIssuesByDateDepartment(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("department") String department);

    @Query("SELECT count(ui) FROM UniformIssue ui JOIN ui.luStatusId lsi WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') and ui.approvedDate between :dateFrom and :dateTo")
    long countApprovedRejectedUniformIssuesByDate(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT count(ui) FROM UniformIssue ui JOIN ui.luStatusId lsi WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected')")
    long countApprovedRejectedUniformIssues();

    @Query("SELECT count(ui) FROM UniformIssue ui JOIN ui.luStatusId lsi JOIN ui.uniformIssueDetailCollection uid JOIN uid.uniformRequisitionDetail urd JOIN urd.uniformRequisitionId ur JOIN ur.employeeId e JOIN e.positionCategory pc WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') AND pc.name = :positionCategory")
    long countApprovedRejectedUniformIssuesByPositionCategory(@Param("positionCategory") String positionCategory);

    @Query("SELECT count(ui) FROM UniformIssue ui JOIN ui.luStatusId lsi JOIN ui.uniformIssueDetailCollection uid JOIN uid.uniformRequisitionDetail urd JOIN urd.uniformRequisitionId ur JOIN ur.employeeId e JOIN e.positionCategory pc WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') AND pc.name = :positionCategory AND ui.approvedDate between :dateFrom and :dateTo")
    long countApprovedRejectedUniformIssuesByDatePositionCategory(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("positionCategory") String positionCategory);

    @Query("SELECT count(ui) FROM UniformIssue ui JOIN ui.luStatusId lsi JOIN ui.uniformIssueDetailCollection uid JOIN uid.uniformRequisitionDetail urd JOIN urd.uniformRequisitionId ur JOIN ur.employeeId e JOIN e.positionCategory pc WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') AND pc.name = :positionCategory and ui.department = :department")
    long countApprovedRejectedUniformIssuesByDepartmentPositionCategory(@Param("department") String department, @Param("positionCategory") String positionCategory);

    @Query("SELECT count(ui) FROM UniformIssue ui JOIN ui.luStatusId lsi JOIN ui.uniformIssueDetailCollection uid JOIN uid.uniformRequisitionDetail urd JOIN urd.uniformRequisitionId ur JOIN ur.employeeId e JOIN e.positionCategory pc WHERE ui.approvedDate between :dateFrom and :dateTo AND (lsi.name = 'Approved' or lsi.name = 'Rejected') AND pc.name = :positionCategory and ui.department = :department")
    long countApprovedRejectedUniformIssuesByDateDepartmentPositionCategory(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo,@Param("department") String department, @Param("positionCategory") String positionCategory);

    @Query("SELECT ui FROM UniformIssue ui JOIN ui.luStatusId lsi WHERE (lsi.name = 'Approved') and ui.department = :department ORDER BY ui.approvedDate DESC")
    List<UniformIssue> findApprovedUniformIssuesByDepartment(@Param("department") String department);

    @Query("SELECT ui FROM UniformIssue ui JOIN ui.luStatusId lsi WHERE (lsi.name = 'Approved') and ui.department = :department and ui.approvedDate between :dateFrom and :dateTo ORDER BY ui.approvedDate DESC")
    List<UniformIssue> findApprovedUniformIssuesByDateDepartment(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("department") String department);


    @Query("SELECT ui FROM UniformIssue ui JOIN ui.luStatusId lsi WHERE (lsi.name = 'Approved') and ui.approvedDate between :dateFrom and :dateTo ORDER BY ui.approvedDate DESC")
    List<UniformIssue> findApprovedUniformIssuesByDate(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT ui FROM UniformIssue ui JOIN ui.luStatusId lsi WHERE (lsi.name = 'Approved') ORDER BY ui.approvedDate DESC")
    List<UniformIssue> findApprovedUniformIssues();

    @Query("SELECT ui FROM UniformIssue ui JOIN ui.luStatusId lsi JOIN ui.uniformIssueDetailCollection uid JOIN uid.uniformRequisitionDetail urd JOIN urd.uniformRequisitionId ur JOIN ur.employeeId e JOIN e.positionCategory pc WHERE (lsi.name = 'Approved') AND pc.name = :positionCategory")
    List<UniformIssue> findApprovedUniformIssuesByPositionCategory(@Param("positionCategory") String positionCategory);

    @Query("SELECT ui FROM UniformIssue ui JOIN ui.luStatusId lsi JOIN ui.uniformIssueDetailCollection uid JOIN uid.uniformRequisitionDetail urd JOIN urd.uniformRequisitionId ur JOIN ur.employeeId e JOIN e.positionCategory pc WHERE (lsi.name = 'Approved') AND pc.name = :positionCategory AND ui.approvedDate between :dateFrom and :dateTo")
    List<UniformIssue> findApprovedUniformIssuesByDatePositionCategory(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("positionCategory") String positionCategory);

    @Query("SELECT ui FROM UniformIssue ui JOIN ui.luStatusId lsi JOIN ui.uniformIssueDetailCollection uid JOIN uid.uniformRequisitionDetail urd JOIN urd.uniformRequisitionId ur JOIN ur.employeeId e JOIN e.positionCategory pc WHERE (lsi.name = 'Approved') AND pc.name = :positionCategory and ui.department = :department")
    List<UniformIssue> findApprovedUniformIssuesByDepartmentPositionCategory(@Param("department") String department, @Param("positionCategory") String positionCategory);

    @Query("SELECT ui FROM UniformIssue ui JOIN ui.luStatusId lsi JOIN ui.uniformIssueDetailCollection uid JOIN uid.uniformRequisitionDetail urd JOIN urd.uniformRequisitionId ur JOIN ur.employeeId e JOIN e.positionCategory pc WHERE ui.approvedDate between :dateFrom and :dateTo AND (lsi.name = 'Approved') AND pc.name = :positionCategory and ui.department = :department")
    List<UniformIssue> findApprovedUniformIssuesByDateDepartmentPositionCategory(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("department") String department, @Param("positionCategory") String positionCategory);

    //#######################################################################################################################
    @Query("SELECT count(ui) FROM UniformIssue ui JOIN ui.luStatusId lsi WHERE lsi.name = 'Approved' and ui.department = :department")
    long countApprovedUniformIssuesByDepartment(@Param("department") String department);

    @Query("SELECT count(ui) FROM UniformIssue ui JOIN ui.luStatusId lsi WHERE lsi.name = 'Approved' and ui.approvedDate between :dateFrom and :dateTo")
    long countApprovedUniformIssuesByDate(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);
    
    @Query("SELECT count(ui) FROM UniformIssue ui JOIN ui.luStatusId lsi WHERE lsi.name = 'Approved' and ui.department = :department and ui.approvedDate between :dateFrom and :dateTo")
    long countApprovedUniformIssuesByDepartmentDate(@Param("department") String department, @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT count(ui) FROM UniformIssue ui JOIN ui.luStatusId lsi WHERE lsi.name = 'Approved'")
    long countApprovedUniformIssues();

}
