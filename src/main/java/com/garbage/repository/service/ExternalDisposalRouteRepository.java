package com.garbage.repository.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.garbage.domain.service.ExternalDisposalRoute;

public interface ExternalDisposalRouteRepository
        extends JpaRepository<ExternalDisposalRoute, Long> {

    List<ExternalDisposalRoute> findByItemId(Long itemId);

    List<ExternalDisposalRoute> findByRouteType(String routeType);

    void deleteAllByItemIdIn(List<Long> itemIds);

    void deleteAll();

    @Query(value = """
        SELECT
            wi.item_id,
            wi.item_name,
            dr.disposal_method,
            dr.instruction,
            dr.is_collectable
        FROM master.waste_item wi
        JOIN service.disposal_rule dr
          ON wi.item_id = dr.item_id
        WHERE dr.category_id IS NULL
        ORDER BY wi.item_id
        """, nativeQuery = true)
    List<Object[]> findSpecialDisposalItems();
}