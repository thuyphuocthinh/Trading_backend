package com.tpt.trading.repository;

import com.tpt.trading.entity.WatchList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchListRepository extends JpaRepository<WatchList, Long> {
    WatchList findByUserId(Long id);
}
