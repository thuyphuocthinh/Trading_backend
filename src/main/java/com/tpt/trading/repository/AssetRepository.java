package com.tpt.trading.repository;

import com.tpt.trading.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByUserId(Long userId);
    Asset findAssetByUserIdAndCoinId(Long userId, String coinId);
    Asset findAssetByUserIdAndId(Long userId, Long id);
}
