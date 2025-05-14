package com.amaranthh.autocompleteservice.repository;

import com.amaranthh.autocompleteservice.model.Suga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SugaRepository extends JpaRepository<Suga, String> {
    @Query(value =
        "SELECT SUGA_CD, " +
        "       PRSC_NM, " +
        "       PRSC_CLSF, " +
        "       PRSC_AUTH_FG, " +
        "       SUGA_APLY_DT, " +
        "       SUGA_END_DT, " +
        "       SLIP_CD " +
        "  FROM testdb.suga " +
        " WHERE CO_CD = :coCd " +
        "   AND DIV_CD = :divCd " +
        "   AND PRSC_PSBL_YN = 'Y' " +
        "   AND :today BETWEEN SUGA_APLY_DT AND SUGA_END_DT",
            nativeQuery = true
    )
    List<Suga> findAvailableSugaByDate(@Param("coCd") String coCd,
                                       @Param("divCd") String divCd,
                                       @Param("today") String today);
}
