package com.garbage.repository.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.garbage.domain.service.Fee;

public interface FeeRepository
        extends JpaRepository<Fee, Long> {

    List<Fee> findByMunicipalityId(
            Long municipalityId
    );

    List<Fee> findByMunicipalityIdAndItemId(
            Long municipalityId,
            Long itemId
    );

    void deleteByMunicipalityId(
            Long municipalityId
    );

    /*
     * master.waste_item의 품목명으로
     * item_id를 직접 조회한다.
     *
     * Entity나 별도의 WasteItemRepository가 없어도
     * 기존 테이블을 이용할 수 있다.
     */
    @Query(
        value = """
            SELECT item_id
            FROM master.waste_item
            WHERE item_name = :itemName
            LIMIT 1
            """,
        nativeQuery = true
    )
    Long findItemIdByItemName(
            @Param("itemName") String itemName
    );
}