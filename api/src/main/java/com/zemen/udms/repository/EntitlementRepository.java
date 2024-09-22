package com.zemen.udms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zemen.udms.model.Entitlement;

public interface EntitlementRepository extends JpaRepository<Entitlement, String> {

    @Query("SELECT e FROM Entitlement e JOIN e.entitlementUniformItemsCollection eui JOIN e.luStatusId lsi WHERE e.id = :id")
    Entitlement findEntitlementById(@Param("id") String id);
    
    @Query("SELECT e FROM Entitlement e JOIN e.entitlementUniformItemsCollection eui JOIN e.luStatusId lsi WHERE lsi.name = 'Pending'")
    List<Entitlement> findPendingEntitlements();

    @Query("SELECT e FROM Entitlement e JOIN e.entitlementUniformItemsCollection eui JOIN e.luStatusId lsi join e.positionCategory pci WHERE lsi.name = 'Pending' and pci.name = :positionCategory ORDER BY pci.name ASC")
    List<Entitlement> findPendingEntitlementsByPositionCategory(@Param("positionCategory") String positionCategory);

    @Query("SELECT e FROM Entitlement e JOIN e.entitlementUniformItemsCollection eui join e.positionCategory pci WHERE pci.name = :positionCategory ORDER BY pci.name ASC")
    List<Entitlement> findEntitlementsByPositionCategory(@Param("positionCategory") String positionCategory);

    @Query("SELECT e FROM Entitlement e JOIN e.entitlementUniformItemsCollection eui JOIN e.luStatusId lsi WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected')")
    List<Entitlement> findApprovedEntitlements();

    @Query("SELECT e FROM Entitlement e JOIN e.entitlementUniformItemsCollection eui JOIN e.luStatusId lsi join e.positionCategory pci WHERE (lsi.name = 'Approved' or lsi.name = 'Rejected') and pci.name = :positionCategory ORDER BY pci.name ASC")
    List<Entitlement> findApprovedEntitlementsByPositionCategory(@Param("positionCategory") String positionCategory);

    @Query("SELECT COUNT(eui) FROM Entitlement e JOIN e.entitlementUniformItemsCollection eui JOIN e.luStatusId lsi WHERE lsi.name = 'Pending'")
    long countPendingEntitlements();

    @Query("SELECT COUNT(eui) FROM Entitlement e JOIN e.entitlementUniformItemsCollection eui JOIN e.luStatusId lsi join e.positionCategory pci WHERE lsi.name = 'Pending' and pci.name = :positionCategory")
    long countPendingEntitlementsByPositionCategory(@Param("positionCategory") String positionCategory);

    @Query("SELECT COUNT(eui) FROM Entitlement e JOIN e.entitlementUniformItemsCollection eui JOIN e.luStatusId lsi WHERE lsi.name = 'Approved'")
    long countApprovedEntitlements();

    @Query("SELECT COUNT(eui) FROM Entitlement e JOIN e.entitlementUniformItemsCollection eui JOIN e.luStatusId lsi join e.positionCategory pci WHERE lsi.name = 'Approved' and pci.name = :positionCategory")
    long countApprovedEntitlementsByPositionCategory(@Param("positionCategory") String positionCategory);

}
