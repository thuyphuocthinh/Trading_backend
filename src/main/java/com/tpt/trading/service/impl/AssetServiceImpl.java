package com.tpt.trading.service.impl;

import com.tpt.trading.entity.Asset;
import com.tpt.trading.entity.Coin;
import com.tpt.trading.entity.User;
import com.tpt.trading.repository.AssetRepository;
import com.tpt.trading.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {
    private final AssetRepository assetRepository;

    @Override
    public Asset createAsset(User user, Coin coin, double quantity) {
        Asset asset = new Asset();
        asset.setUser(user);
        asset.setCoin(coin);
        asset.setQuantity(quantity);
        asset.setBuyPrice(coin.getCurrentPrice());
        return this.assetRepository.save(asset);
    }

    @Override
    public Asset getAssetById(Long assetId) throws Exception {
        return this.assetRepository.findById(assetId).orElseThrow(() -> new Exception("Asset not found"));
    }

    @Override
    public Asset getAssetByUserIdAndId(Long userId, Long assetId) {
        return this.assetRepository.findAssetByUserIdAndId(userId, assetId);
    }

    @Override
    public List<Asset> getUserAssets(Long userId) {
        return this.assetRepository.findByUserId(userId);
    }

    @Override
    public Asset updateAsset(Long assetId, double quantity) throws Exception {
        Asset oldAsset = getAssetById(assetId);
        oldAsset.setQuantity(quantity);
        return this.assetRepository.save(oldAsset);
    }

    @Override
    public Asset findAssetByUserIdAndCoinId(Long userId, String coinId) {
        return this.assetRepository.findAssetByUserIdAndCoinId(userId, coinId);
    }

    @Override
    public void deleteAsset(Long assetId) throws Exception {
        getAssetById(assetId);
        this.assetRepository.deleteById(assetId);
    }
}
