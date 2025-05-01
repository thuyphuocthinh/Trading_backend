package com.tpt.trading.controller;

import com.tpt.trading.entity.Coin;
import com.tpt.trading.entity.User;
import com.tpt.trading.entity.WatchList;
import com.tpt.trading.service.CoinService;
import com.tpt.trading.service.UserService;
import com.tpt.trading.service.WatchListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/watchlist")
@RequiredArgsConstructor
public class WatchListController {
    private final WatchListService watchListService;

    private final UserService userService;

    private final CoinService coinService;

    @GetMapping("/get-by-user")
    public ResponseEntity<WatchList> getWatchListByUser(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = this.userService.findUserProfileByJwt(jwt.substring(7));
        return new ResponseEntity<>(
                this.watchListService.findUserWatchList(user.getId()),
                HttpStatus.OK
        );
    }

    @PostMapping
    public ResponseEntity<WatchList> watchListResponseEntity(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = this.userService.findUserProfileByJwt(jwt.substring(7));
        return new ResponseEntity<>(
                this.watchListService.createWatchList(user),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<WatchList> getWatchList(@PathVariable Long id) throws Exception {
        return new ResponseEntity<>(
                this.watchListService.findById(id),
                HttpStatus.ACCEPTED
        );
    }

    @PatchMapping("/add-coin/coins/{coinId}")
    public ResponseEntity<Coin> addCoin(
            @PathVariable String coinId,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = this.userService.findUserProfileByJwt(jwt.substring(7));
        Coin coin = this.coinService.findById(coinId);

        return new ResponseEntity<>(
                this.watchListService.addItemToWatchList(coin, user),
                HttpStatus.CREATED
        );
    }
}
