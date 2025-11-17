package com.wetech.demo.web3j.service;

import com.wetech.demo.web3j.contracts.ERC20Test;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class Erc20Service {

    private final Web3j web3j;
    private final Credentials credentials;

    // 从配置里读取合约地址；如果没配，启动时就是空字符串
    @Value("${erc20.contract-address:}")
    private String contractAddress;

    private ContractGasProvider gasProvider() {
        return new DefaultGasProvider();
    }

    /** 部署合约 */
    public String deploy() throws Exception {
        ERC20Test contract = ERC20Test.deploy(
                web3j,
                credentials,
                gasProvider()
        ).send();

        this.contractAddress = contract.getContractAddress();
        return this.contractAddress;
    }

    /** 手动设置合约地址 */
    public void setContractAddress(String address) {
        this.contractAddress = address;
    }

    /** 获取当前合约地址 */
    public String getContractAddress() {
        return this.contractAddress;
    }

    /** 内部工具：加载已部署合约 */
    private ERC20Test loadContract() {
        if (this.contractAddress == null || this.contractAddress.isEmpty()) {
            throw new IllegalStateException("合约还没有部署，请先调用 /api/erc20/deploy 或手动设置地址");
        }
        return ERC20Test.load(
                this.contractAddress,
                web3j,
                credentials,
                gasProvider()
        );
    }

    // ===== 元数据相关 =====

    /** 合约里记录的 owner（Solidity 里的 owner 状态变量） */
    public String owner() throws Exception {
        return loadContract().owner().send();
    }

    /** 合约名字 */
    public String name() throws Exception {
        return loadContract().name().send();
    }

    /** 代币符号 */
    public String symbol() throws Exception {
        return loadContract().symbol().send();
    }

    /** 精度 */
    public BigInteger decimals() throws Exception {
        return loadContract().decimals().send();
    }

    /** 总供应量 */
    public BigInteger totalSupply() throws Exception {
        return loadContract().totalSupply().send();
    }

    // ===== 余额 & 核心操作 =====

    public BigInteger balanceOf(String address) throws Exception {
        return loadContract().balanceOf(address).send();
    }

    public TransactionReceipt mint(BigInteger amount) throws Exception {
        return loadContract().mint(amount).send();
    }

    public TransactionReceipt transfer(String to, BigInteger amount) throws Exception {
        return loadContract().transfer(to, amount).send();
    }

    public TransactionReceipt approve(String spender, BigInteger amount) throws Exception {
        return loadContract().approve(spender, amount).send();
    }

    public BigInteger allowance(String owner, String spender) throws Exception {
        return loadContract().allowance(owner, spender).send();
    }

    public TransactionReceipt transferFrom(String from, String to, BigInteger amount) throws Exception {
        return loadContract().transferFrom(from, to, amount).send();
    }

    public TransactionReceipt burn(BigInteger amount) throws Exception {
        return loadContract().burn(amount).send();
    }

    /** 当前后端钱包（用于发交易的钱包地址） */
    public String getOwnerAddress() {
        return credentials.getAddress();
    }
}
