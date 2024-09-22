package com.zemen.udms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zemen.udms.model.EntitlementUniformItems;

public interface EntitlementUniformItemsRepository extends JpaRepository<EntitlementUniformItems, String> {

    @Query("SELECT eu FROM EntitlementUniformItems eu JOIN eu.entitlement ei JOIN ei.luStatusId lsi WHERE lsi.name = 'Pending' ORDER BY ei.positionCategory")
    Page<EntitlementUniformItems> findPendingEntitlementUniformItems(Pageable pageable);

    @Query("SELECT eu FROM EntitlementUniformItems eu JOIN eu.entitlement ei JOIN ei.luStatusId lsi WHERE lsi.name = 'Approved' ORDER BY ei.positionCategory")
    Page<EntitlementUniformItems> findApprovedEntitlementUniformItems(Pageable pageable);

    @Query("SELECT eu FROM EntitlementUniformItems eu JOIN eu.entitlement ei JOIN ei.luStatusId lsi join ei.positionCategory pci WHERE lsi.name = 'Approved' and pci.name = :positionCategory ORDER BY pci.name ASC")
    Page<EntitlementUniformItems> findApprovedEntitlementUniformItemsByPositionCategory(@Param("positionCategory") String positionCategory, Pageable pageable);

    @Query("SELECT count(eu) FROM EntitlementUniformItems eu JOIN eu.entitlement ei JOIN ei.luStatusId lsi WHERE lsi.name = 'Pending'")
    long countPendingEntitlementUniformItems();

    @Query("SELECT count(eu) FROM EntitlementUniformItems eu JOIN eu.entitlement ei JOIN ei.luStatusId lsi WHERE lsi.name = 'Approved'")
    long countApprovedEntitlementUniformItems();

    @Query("SELECT count(eu) FROM EntitlementUniformItems eu JOIN eu.entitlement ei JOIN ei.luStatusId lsi join ei.positionCategory pci WHERE lsi.name = 'Approved' and pci.name = :positionCategory")
    long countApprovedEntitlementUniformItemsByPositionCategory(@Param("positionCategory") String positionCategory);
}
