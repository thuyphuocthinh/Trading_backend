package com.tpt.trading.service;

import com.tpt.trading.entity.Coin;
import com.tpt.trading.entity.User;
import com.tpt.trading.entity.WatchList;

public interface WatchListService {
    WatchList findUserWatchList(Long userId) throws Exception;
    WatchList createWatchList(User user);
    WatchList findById(Long id) throws Exception;
    Coin addItemToWatchList(Coin coin, User user) throws Exception;
}
