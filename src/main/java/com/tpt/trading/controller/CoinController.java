package com.tpt.trading.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpt.trading.entity.Coin;
import com.tpt.trading.service.CoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coins")
@RequiredArgsConstructor
public class CoinController {
    private final CoinService coinService;

    private final ObjectMapper objectMapper;

    @GetMapping()
    public ResponseEntity<List<Coin>> getCoinList(
            @RequestParam("page") int page
    ) throws Exception {
        List<Coin> coins = this.coinService.getCoinList(page);
        return new ResponseEntity<>(coins, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}/{days}/chart")
    public ResponseEntity<JsonNode> getMarketChart(
            @PathVariable String id,
            @PathVariable int days
    ) throws Exception {
        String res = this.coinService.getMarketChart(id, days);
        JsonNode jsonNode = objectMapper.readTree(res);
        return new ResponseEntity<>(jsonNode, HttpStatus.ACCEPTED);
    }

    @GetMapping("/search")
    public ResponseEntity<JsonNode> getMarketChart(
            @RequestParam("keyword") String keyword
    ) throws Exception {
        String res = this.coinService.searchCoin(keyword);
        JsonNode jsonNode = objectMapper.readTree(res);
        return new ResponseEntity<>(jsonNode, HttpStatus.ACCEPTED);
    }

    @GetMapping("/top-50")
    public ResponseEntity<JsonNode> getTop50Coins(
    ) throws Exception {
        String res = this.coinService.getTop50CoinsByMarketCapRank();
        JsonNode jsonNode = objectMapper.readTree(res);
        return new ResponseEntity<>(jsonNode, HttpStatus.ACCEPTED);
    }

    @GetMapping("/treading")
    public ResponseEntity<JsonNode> getTreadingCoins(
    ) throws Exception {
        String res = this.coinService.getTreadingCoins();
        JsonNode jsonNode = objectMapper.readTree(res);
        return new ResponseEntity<>(jsonNode, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JsonNode> getCoinDetails(
            @PathVariable String id
    ) throws Exception {
        String res = this.coinService.getCoinDetails(id);
        JsonNode jsonNode = objectMapper.readTree(res);
        return new ResponseEntity<>(jsonNode, HttpStatus.ACCEPTED);
    }
}
