package com.tpt.trading.controller;

import com.tpt.trading.entity.Asset;
import com.tpt.trading.entity.User;
import com.tpt.trading.service.AssetService;
import com.tpt.trading.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
public class AssetController {
    private final AssetService assetService;

    private final UserService userService;

    @GetMapping("/{assetId}")
    public ResponseEntity<Asset> getAssetById(
            @PathVariable Long assetId
    ) throws Exception {
        return new ResponseEntity<>(
                this.assetService.getAssetById(assetId),
                HttpStatus.ACCEPTED
        );
    }

    @GetMapping("/coins/{coinId}")
    public ResponseEntity<Asset> getAssetByCoinIdAndUserId(
            @PathVariable String coinId,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user = this.userService.findUserProfileByJwt(jwt.substring(7));
        return new ResponseEntity<>(
                this.assetService.findAssetByUserIdAndCoinId(user.getId(), coinId),
                HttpStatus.ACCEPTED
        );
    }

    @GetMapping("/get-by-users")
    public ResponseEntity<List<Asset>> getAssetsByUsers(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = this.userService.findUserProfileByJwt(jwt.substring(7));
        return new ResponseEntity<>(
                this.assetService.getUserAssets(user.getId()),
                HttpStatus.ACCEPTED
        );
    }
}
