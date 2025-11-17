package com.wetech.demo.web3j.controller;

import com.wetech.demo.web3j.service.Erc20Service;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/erc20")
public class Erc20Controller {

    private final Erc20Service erc20Service;

    public Erc20Controller(Erc20Service erc20Service) {
        this.erc20Service = erc20Service;
    }

    // 0. 当前后端使用的钱包地址（credentials 对应的地址）
    @GetMapping("/owner")
    public Map<String, Object> owner() {
        Map<String, Object> res = new HashMap<>();
        res.put("owner", erc20Service.getOwnerAddress());
        return res;
    }

    // 1. 部署合约
    @PostMapping("/deploy")
    public Map<String, Object> deploy() throws Exception {
        String address = erc20Service.deploy();
        Map<String, Object> res = new HashMap<>();
        res.put("contractAddress", address);
        return res;
    }

    // 2. 获取 / 设置 当前使用的合约地址
    @GetMapping("/address")
    public Map<String, Object> getAddress() {
        Map<String, Object> res = new HashMap<>();
        res.put("contractAddress", erc20Service.getContractAddress());
        return res;
    }

    @PostMapping(value = "/address", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> setAddress(@RequestBody SetAddressRequest request) {
        erc20Service.setContractAddress(request.getContractAddress());
        Map<String, Object> res = new HashMap<>();
        res.put("contractAddress", request.getContractAddress());
        return res;
    }

    // 3. 元数据汇总
    @GetMapping("/metadata")
    public Map<String, Object> metadata() throws Exception {
        Map<String, Object> res = new HashMap<>();
        res.put("name", erc20Service.name());
        res.put("symbol", erc20Service.symbol());
        res.put("decimals", erc20Service.decimals().toString());
        res.put("totalSupply", erc20Service.totalSupply().toString());
        res.put("contractOwner", erc20Service.owner());      // Solidity 合约里记录的 owner
        res.put("contractAddress", erc20Service.getContractAddress());
        return res;
    }

    // 4. 查询余额
    @GetMapping("/balanceOf")
    public Map<String, Object> balanceOf(@RequestParam String address) throws Exception {
        Map<String, Object> res = new HashMap<>();
        res.put("address", address);
        res.put("balance", erc20Service.balanceOf(address).toString());
        return res;
    }

    // 5. mint（用 URL 参数，绕开 JSON）
    @PostMapping("/mint")
    public Map<String, Object> mint(@RequestParam("amount") String amount) throws Exception {
        System.out.println(">>> /mint called, amount = " + amount);
        BigInteger value = new BigInteger(amount);
        var receipt = erc20Service.mint(value);

        Map<String, Object> res = new HashMap<>();
        res.put("txHash", receipt.getTransactionHash());
        return res;
    }

    // 6. transfer
    @PostMapping("/transfer")
    public Map<String, Object> transfer(
            @RequestParam("to") String to,
            @RequestParam("amount") String amount
    ) throws Exception {
        BigInteger value = new BigInteger(amount);
        var receipt = erc20Service.transfer(to, value);

        Map<String, Object> res = new HashMap<>();
        res.put("txHash", receipt.getTransactionHash());
        res.put("to", to);
        res.put("amount", amount);
        return res;
    }

    // 7. approve
    @PostMapping("/approve")
    public Map<String, Object> approve(
            @RequestParam("spender") String spender,
            @RequestParam("amount") String amount
    ) throws Exception {
        BigInteger value = new BigInteger(amount);
        var receipt = erc20Service.approve(spender, value);

        Map<String, Object> res = new HashMap<>();
        res.put("txHash", receipt.getTransactionHash());
        res.put("spender", spender);
        res.put("amount", amount);
        return res;
    }

    // 8. allowance
    @GetMapping("/allowance")
    public Map<String, Object> allowance(
            @RequestParam("owner") String owner,
            @RequestParam("spender") String spender
    ) throws Exception {
        Map<String, Object> res = new HashMap<>();
        res.put("owner", owner);
        res.put("spender", spender);
        res.put("allowance", erc20Service.allowance(owner, spender).toString());
        return res;
    }

    // 9. transferFrom
    @PostMapping("/transferFrom")
    public Map<String, Object> transferFrom(
            @RequestParam("from") String from,
            @RequestParam("to") String to,
            @RequestParam("amount") String amount
    ) throws Exception {
        BigInteger value = new BigInteger(amount);
        var receipt = erc20Service.transferFrom(from, to, value);

        Map<String, Object> res = new HashMap<>();
        res.put("txHash", receipt.getTransactionHash());
        res.put("from", from);
        res.put("to", to);
        res.put("amount", amount);
        return res;
    }

    // 10. burn
    @PostMapping("/burn")
    public Map<String, Object> burn(@RequestParam("amount") String amount) throws Exception {
        BigInteger value = new BigInteger(amount);
        var receipt = erc20Service.burn(value);

        Map<String, Object> res = new HashMap<>();
        res.put("txHash", receipt.getTransactionHash());
        res.put("amount", amount);
        return res;
    }

    // ===== DTO：只给 /address 用 =====
    public static class SetAddressRequest {
        private String contractAddress;

        public SetAddressRequest() {
        }

        public String getContractAddress() {
            return contractAddress;
        }

        public void setContractAddress(String contractAddress) {
            this.contractAddress = contractAddress;
        }
    }
}
