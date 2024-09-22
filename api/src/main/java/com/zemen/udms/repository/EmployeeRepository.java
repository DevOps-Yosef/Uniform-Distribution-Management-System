package com.zemen.udms.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.zemen.udms.model.Employee;


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    @Query("SELECT distinct e.department FROM Employee e ORDER BY e.department ASC")
    List<String> findAllDepartmentNames();

    @Query("SELECT e FROM Employee e ORDER BY e.name ASC")
    Page<Employee> findAllOrderByNameAsc(Pageable pageable);

    @Query("SELECT e FROM Employee e ORDER BY e.name ASC")
    List<Employee> findAllEmployees();

    @Query("SELECT pci.name FROM Employee e JOIN e.positionCategory pci ORDER BY pci.name ASC")
    List<String> findAllPositionCategoryNames();

    @Query("SELECT e FROM Employee e WHERE e.name LIKE %:name% ORDER BY e.name ASC")
    Page<Employee> findByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.gender = :gender ORDER BY e.name ASC")
    Page<Employee> findByGender(@Param("gender") String gender, Pageable pageable);

    @Query("SELECT e FROM Employee e JOIN e.positionCategory psi WHERE psi.name = :positionCategory ORDER BY e.name ASC")
    Page<Employee> findByPositionCategory(@Param("positionCategory") String positionCategory, Pageable pageable);

    @Query("SELECT e FROM Employee e JOIN e.positionCategory psi WHERE psi.name = :positionCategory And e.department = :department ORDER BY e.name ASC")
    Page<Employee> findByPositionCategoryDepartment(@Param("positionCategory") String positionCategory, @Param("department") String department, Pageable pageable);

    @Query("SELECT e FROM Employee e JOIN e.positionCategory psi WHERE psi.name = :positionCategory And e.department = :department And e.inactive = :inactive ORDER BY e.name ASC")
    Page<Employee> findByPositionCategoryDepartmentIsInactive(@Param("positionCategory") String positionCategory, @Param("department") String department, @Param("inactive") boolean inactive, Pageable pageable);

    @Query("SELECT e FROM Employee e JOIN e.positionCategory psi WHERE psi.name = :positionCategory And e.inactive = :inactive ORDER BY e.name ASC")
    Page<Employee> findByPositionCategoryIsInactive(@Param("positionCategory") String positionCategory, @Param("inactive") boolean inactive, Pageable pageable);
    
    @Query("SELECT e FROM Employee e  WHERE e.department = :department ORDER BY e.name ASC")
    Page<Employee> findByDepartment(@Param("department") String department, Pageable pageable);

    @Query("SELECT e FROM Employee e  WHERE e.department = :department And e.inactive = :inactive ORDER BY e.name ASC")
    Page<Employee> findByDepartmentIsInactive(@Param("department") String department, @Param("inactive") boolean inactive, Pageable pageable);
    
    @Query("SELECT e FROM Employee e  WHERE e.inactive = :inactive ORDER BY e.name ASC")
    Page<Employee> findByIsInactive(@Param("inactive") boolean inactive, Pageable pageable);

    @Query("SELECT e FROM Employee e JOIN e.positionCategory psi WHERE e.name LIKE %:name% And psi.name= :positionCategory ORDER BY e.name ASC")
    Page<Employee> findByNamePositionCategory(@Param("name") String name, @Param("positionCategory") String positionCategory, Pageable pageable);

    @Query("SELECT e FROM Employee e JOIN e.positionCategory psi WHERE e.name LIKE %:name% And psi.name= :positionCategory And e.department = :department ORDER BY e.name ASC")
    Page<Employee> findByNamePositionCategoryDepartment(@Param("name") String name, @Param("positionCategory") String positionCategory, @Param("department") String department, Pageable pageable);//1

    @Query("SELECT e FROM Employee e JOIN e.positionCategory psi WHERE e.name LIKE %:name% And psi.name= :positionCategory And e.department = :department And e.inactive = :inactive ORDER BY e.name ASC")
    Page<Employee> findByNamePositionCategoryDepartmentIsInactive(@Param("name") String name, @Param("positionCategory") String positionCategory, @Param("department") String department,@Param("inactive") boolean inactive, Pageable pageable);//1
    
    @Query("SELECT e FROM Employee e JOIN e.positionCategory psi WHERE e.name LIKE %:name% And psi.name= :positionCategory And e.inactive = :inactive ORDER BY e.name ASC")
    Page<Employee> findByNamePositionCategoryIsInactive(@Param("name") String name, @Param("positionCategory") String positionCategory, @Param("inactive") boolean inactive, Pageable pageable);//2
    
    @Query("SELECT e FROM Employee e WHERE e.name LIKE %:name% And e.inactive = :inactive ORDER BY e.name ASC")
    Page<Employee> findByNameIsInactive(@Param("name") String name, @Param("inactive") boolean inactive, Pageable pageable);//

    @Query("SELECT e FROM Employee e WHERE e.name LIKE %:name% And e.department = :department ORDER BY e.name ASC")
    Page<Employee> findByNameDepartment(@Param("name") String name, @Param("department") String department, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.name LIKE %:name% And e.department = :department And e.inactive = :inactive ORDER BY e.name ASC")
    Page<Employee> findByNameDepartmentIsInactive(@Param("name") String name, @Param("department") String department, @Param("inactive") boolean inactive, Pageable pageable);//3
    
    @Query("SELECT e FROM Employee e WHERE e.name LIKE %:name% And e.gender = :gender And e.department = :department ORDER BY e.name ASC")
    Page<Employee> findByNameGenderDepartment(@Param("name") String name, @Param("gender") String gender, @Param("department") String department, Pageable pageable);//

    @Query("SELECT e FROM Employee e WHERE e.name LIKE %:name% And e.gender = :gender And e.department = :department And e.inactive = :inactive ORDER BY e.name ASC")
    Page<Employee> findByNameGenderDepartmentIsInactive(@Param("name") String name, @Param("gender") String gender, @Param("department") String department, @Param("inactive") boolean inactive, Pageable pageable);//
    
    @Query("SELECT e FROM Employee e WHERE e.name LIKE %:name% And e.gender = :gender And e.inactive = :inactive ORDER BY e.name ASC")
    Page<Employee> findByNameGenderIsInactive(@Param("name") String name, @Param("gender") String gender, @Param("inactive") boolean inactive, Pageable pageable);//
    
    @Query("SELECT e FROM Employee e JOIN e.positionCategory psi WHERE e.gender = :gender And psi.name= :positionCategory ORDER BY e.name ASC")
    Page<Employee> findByGenderPositionCategory(@Param("gender") String gender, @Param("positionCategory") String positionCategory, Pageable pageable);

    @Query("SELECT e FROM Employee e JOIN e.positionCategory psi WHERE e.gender = :gender And psi.name= :positionCategory And e.department = :department ORDER BY e.name ASC")
    Page<Employee> findByGenderPositionCategoryDepartment(@Param("gender") String gender, @Param("positionCategory") String positionCategory, @Param("department") String department, Pageable pageable);//4

    @Query("SELECT e FROM Employee e JOIN e.positionCategory psi WHERE e.gender = :gender And psi.name= :positionCategory And e.department = :department And e.inactive = :inactive ORDER BY e.name ASC")
    Page<Employee> findByGenderPositionCategoryDepartmentIsInactive(@Param("gender") String gender, @Param("positionCategory") String positionCategory, @Param("department") String department, @Param("inactive") boolean inactive, Pageable pageable);//4
    
    @Query("SELECT e FROM Employee e JOIN e.positionCategory psi WHERE e.gender = :gender And psi.name= :positionCategory And e.inactive = :inactive ORDER BY e.name ASC")
    Page<Employee> findByGenderPositionCategoryIsInactive(@Param("gender") String gender, @Param("positionCategory") String positionCategory, @Param("inactive") boolean inactive, Pageable pageable);//4
    
    @Query("SELECT e FROM Employee e WHERE e.gender = :gender And e.department = :department ORDER BY e.name ASC")
    Page<Employee> findByGenderDepartment(@Param("gender") String gender, @Param("department") String department, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.gender = :gender And e.department = :department And e.inactive = :inactive ORDER BY e.name ASC")
    Page<Employee> findByGenderDepartmentIsInactive(@Param("gender") String gender, @Param("department") String department, @Param("inactive") boolean inactive, Pageable pageable);
    
    @Query("SELECT e FROM Employee e WHERE e.gender = :gender And e.inactive = :inactive ORDER BY e.name ASC")
    Page<Employee> findByGenderIsInactive(@Param("gender") String gender, @Param("inactive") boolean inactive, Pageable pageable);
    
    @Query("SELECT e FROM Employee e JOIN e.positionCategory psi WHERE e.name LIKE %:name% And e.gender = :gender And psi.name= :positionCategory ORDER BY e.name ASC")
    Page<Employee> findByNameGenderPositionCategory(@Param("name") String name, @Param("gender") String gender, @Param("positionCategory") String positionCategory, Pageable pageable);

    @Query("SELECT e FROM Employee e JOIN e.positionCategory psi WHERE e.name LIKE %:name% And e.gender = :gender And psi.name= :positionCategory And e.department = :department ORDER BY e.name ASC")
    Page<Employee> findByNameGenderPositionCategoryDepartment(@Param("name") String name, @Param("gender") String gender, @Param("positionCategory") String positionCategory, @Param("department") String department, Pageable pageable);

    @Query("SELECT e FROM Employee e JOIN e.positionCategory psi WHERE e.name LIKE %:name% And e.gender = :gender And psi.name= :positionCategory And e.department = :department And e.inactive = :inactive ORDER BY e.name ASC")
    Page<Employee> findByNameGenderPositionCategoryDepartmentIsInactive(@Param("name") String name, @Param("gender") String gender, @Param("positionCategory") String positionCategory, @Param("department") String department, @Param("inactive") boolean inactive, Pageable pageable);
    
    @Query("SELECT e FROM Employee e JOIN e.positionCategory psi WHERE e.name LIKE %:name% And e.gender = :gender And psi.name= :positionCategory And e.inactive = :inactive ORDER BY e.name ASC")
    Page<Employee> findByNameGenderPositionCategoryIsInactive(@Param("name") String name, @Param("gender") String gender, @Param("positionCategory") String positionCategory, @Param("inactive") boolean inactive, Pageable pageable);
    
    @Query("SELECT e FROM Employee e WHERE e.name LIKE %:name% And e.gender = :gender ORDER BY e.name ASC")
    Page<Employee> findByNameGender(@Param("name") String name, @Param("gender") String gender, Pageable pageable);

    //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    //1111111111111111111111111111111111111111111111111111111111111111111111111111111111111
    @Query("SELECT count(e) FROM Employee e")
    long countAll();

    @Query("SELECT count(e) FROM Employee e WHERE e.name LIKE %:name%")
    long countByName(@Param("name") String name);

    @Query("SELECT count(e) FROM Employee e WHERE e.gender = :gender")
    long countByGender(@Param("gender") String gender);

    @Query("SELECT count(e) FROM Employee e JOIN e.positionCategory psi WHERE psi.name = :positionCategory")
    long countByPositionCategory(@Param("positionCategory") String positionCategory);

    @Query("SELECT count(e) FROM Employee e JOIN e.positionCategory psi WHERE psi.name = :positionCategory And e.department = :department")
    long countByPositionCategoryDepartment(@Param("positionCategory") String positionCategory, @Param("department") String department);

    @Query("SELECT count(e) FROM Employee e JOIN e.positionCategory psi WHERE psi.name = :positionCategory And e.department = :department And e.inactive = :inactive")
    long countByPositionCategoryDepartmentIsInactive(@Param("positionCategory") String positionCategory, @Param("department") String department, @Param("inactive") boolean inactive);

    @Query("SELECT count(e) FROM Employee e JOIN e.positionCategory psi WHERE psi.name = :positionCategory And e.inactive = :inactive")
    long countByPositionCategoryIsInactive(@Param("positionCategory") String positionCategory, @Param("inactive") boolean inactive);
    
    @Query("SELECT count(e) FROM Employee e  WHERE e.department = :department")
    long countByDepartment(@Param("department") String department);

    @Query("SELECT count(e) FROM Employee e  WHERE e.department = :department And e.inactive = :inactive")
    long countByDepartmentIsInactive(@Param("department") String department, @Param("inactive") boolean inactive);
    
    @Query("SELECT count(e) FROM Employee e  WHERE e.inactive = :inactive")
    long countByIsInactive(@Param("inactive") boolean inactive);

    @Query("SELECT count(e) FROM Employee e JOIN e.positionCategory psi WHERE e.name LIKE %:name% And psi.name= :positionCategory")
    long countByNamePositionCategory(@Param("name") String name, @Param("positionCategory") String positionCategory);

    @Query("SELECT count(e) FROM Employee e JOIN e.positionCategory psi WHERE e.name LIKE %:name% And psi.name= :positionCategory And e.department = :department")
    long countByNamePositionCategoryDepartment(@Param("name") String name, @Param("positionCategory") String positionCategory, @Param("department") String department);//1

    @Query("SELECT count(e) FROM Employee e JOIN e.positionCategory psi WHERE e.name LIKE %:name% And psi.name= :positionCategory And e.department = :department And e.inactive = :inactive")
    long countByNamePositionCategoryDepartmentIsInactive(@Param("name") String name, @Param("positionCategory") String positionCategory, @Param("department") String department,@Param("inactive") boolean inactive);//1
    
    @Query("SELECT count(e) FROM Employee e JOIN e.positionCategory psi WHERE e.name LIKE %:name% And psi.name= :positionCategory And e.inactive = :inactive")
    long countByNamePositionCategoryIsInactive(@Param("name") String name, @Param("positionCategory") String positionCategory, @Param("inactive") boolean inactive);//2
    
    @Query("SELECT count(e) FROM Employee e WHERE e.name LIKE %:name% And e.inactive = :inactive")
    long countByNameIsInactive(@Param("name") String name, @Param("inactive") boolean inactive);//

    @Query("SELECT count(e) FROM Employee e WHERE e.name LIKE %:name% And e.department = :department")
    long countByNameDepartment(@Param("name") String name, @Param("department") String department);

    @Query("SELECT count(e) FROM Employee e WHERE e.name LIKE %:name% And e.department = :department And e.inactive = :inactive")
    long countByNameDepartmentIsInactive(@Param("name") String name, @Param("department") String department, @Param("inactive") boolean inactive);//3
    
    @Query("SELECT count(e) FROM Employee e WHERE e.name LIKE %:name% And e.gender = :gender And e.department = :department")
    long countByNameGenderDepartment(@Param("name") String name, @Param("gender") String gender, @Param("department") String department);//

    @Query("SELECT count(e) FROM Employee e WHERE e.name LIKE %:name% And e.gender = :gender And e.department = :department And e.inactive = :inactive")
    long countByNameGenderDepartmentIsInactive(@Param("name") String name, @Param("gender") String gender, @Param("department") String department, @Param("inactive") boolean inactive);//
    
    @Query("SELECT count(e) FROM Employee e WHERE e.name LIKE %:name% And e.gender = :gender And e.inactive = :inactive")
    long countByNameGenderIsInactive(@Param("name") String name, @Param("gender") String gender, @Param("inactive") boolean inactive);//
    
    @Query("SELECT count(e) FROM Employee e JOIN e.positionCategory psi WHERE e.gender = :gender And psi.name= :positionCategory")
    long countByGenderPositionCategory(@Param("gender") String gender, @Param("positionCategory") String positionCategory);

    @Query("SELECT count(e) FROM Employee e JOIN e.positionCategory psi WHERE e.gender = :gender And psi.name= :positionCategory And e.department = :department")
    long countByGenderPositionCategoryDepartment(@Param("gender") String gender, @Param("positionCategory") String positionCategory, @Param("department") String department);//4

    @Query("SELECT count(e) FROM Employee e JOIN e.positionCategory psi WHERE e.gender = :gender And psi.name= :positionCategory And e.department = :department And e.inactive = :inactive")
    long countByGenderPositionCategoryDepartmentIsInactive(@Param("gender") String gender, @Param("positionCategory") String positionCategory, @Param("department") String department, @Param("inactive") boolean inactive);//4
    
    @Query("SELECT count(e) FROM Employee e JOIN e.positionCategory psi WHERE e.gender = :gender And psi.name= :positionCategory And e.inactive = :inactive")
    long countByGenderPositionCategoryIsInactive(@Param("gender") String gender, @Param("positionCategory") String positionCategory, @Param("inactive") boolean inactive);//4
    
    @Query("SELECT count(e) FROM Employee e WHERE e.gender = :gender And e.department = :department")
    long countByGenderDepartment(@Param("gender") String gender, @Param("department") String department);

    @Query("SELECT count(e) FROM Employee e WHERE e.gender = :gender And e.department = :department And e.inactive = :inactive")
    long countByGenderDepartmentIsInactive(@Param("gender") String gender, @Param("department") String department, @Param("inactive") boolean inactive);
    
    @Query("SELECT count(e) FROM Employee e WHERE e.gender = :gender And e.inactive = :inactive")
    long countByGenderIsInactive(@Param("gender") String gender, @Param("inactive") boolean inactive);
    
    @Query("SELECT count(e) FROM Employee e JOIN e.positionCategory psi WHERE e.name LIKE %:name% And e.gender = :gender And psi.name= :positionCategory")
    long countByNameGenderPositionCategory(@Param("name") String name, @Param("gender") String gender, @Param("positionCategory") String positionCategory);

    @Query("SELECT count(e) FROM Employee e JOIN e.positionCategory psi WHERE e.name LIKE %:name% And e.gender = :gender And psi.name= :positionCategory And e.department = :department")
    long countByNameGenderPositionCategoryDepartment(@Param("name") String name, @Param("gender") String gender, @Param("positionCategory") String positionCategory, @Param("department") String department);

    @Query("SELECT count(e) FROM Employee e JOIN e.positionCategory psi WHERE e.name LIKE %:name% And e.gender = :gender And psi.name= :positionCategory And e.department = :department And e.inactive = :inactive")
    long countByNameGenderPositionCategoryDepartmentIsInactive(@Param("name") String name, @Param("gender") String gender, @Param("positionCategory") String positionCategory, @Param("department") String department, @Param("inactive") boolean inactive);
    
    @Query("SELECT count(e) FROM Employee e JOIN e.positionCategory psi WHERE e.name LIKE %:name% And e.gender = :gender And psi.name= :positionCategory And e.inactive = :inactive")
    long countByNameGenderPositionCategoryIsInactive(@Param("name") String name, @Param("gender") String gender, @Param("positionCategory") String positionCategory, @Param("inactive") boolean inactive);
    
    @Query("SELECT count(e) FROM Employee e WHERE e.name LIKE %:name% And e.gender = :gender")
    long countByNameGender(@Param("name") String name, @Param("gender") String gender);
}
