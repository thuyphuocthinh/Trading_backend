package com.tpt.trading.service.impl;

import com.tpt.trading.entity.Coin;
import com.tpt.trading.entity.User;
import com.tpt.trading.entity.WatchList;
import com.tpt.trading.repository.WatchListRepository;
import com.tpt.trading.service.WatchListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WatchListServiceImpl implements WatchListService {
    private final WatchListRepository watchListRepository;

    @Override
    public WatchList findUserWatchList(Long userId) throws Exception {
        WatchList watchList = this.watchListRepository.findByUserId(userId);
        if(watchList == null){
            throw new Exception("Watchlist not found");
        }
        return watchList;
    }

    @Override
    public WatchList createWatchList(User user) {
        WatchList watchList = new WatchList();
        watchList.setUser(user);
        return this.watchListRepository.save(watchList);
    }

    @Override
    public WatchList findById(Long id) throws Exception {
        return this.watchListRepository.findById(id)
                .orElseThrow(() -> new Exception("Watchlist not found"));
    }

    @Override
    public Coin addItemToWatchList(Coin coin, User user) throws Exception {
        WatchList watchList = findUserWatchList(user.getId());
        if(watchList.getCoins().contains(coin)){
            watchList.getCoins().remove(coin);
        } else {
            watchList.getCoins().add(coin);
        }
        this.watchListRepository.save(watchList);
        return coin;
    }
}
