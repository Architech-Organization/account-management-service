package org.account.mgmtsystem.data.repository;

import org.account.mgmtsystem.data.entities.Account;
import org.account.mgmtsystem.service.dto.StatePlaceCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByEmail(String email);

    @Query("SELECT new org.account.mgmtsystem.service.dto.StatePlaceCount(a.state, a.place, COUNT(a)) " +
            "FROM Account a WHERE a.country = :country " +
            "GROUP BY a.state, a.place ORDER BY a.state, a.place")
    List<StatePlaceCount> findCountsByCountry(@Param("country") String country);
}
