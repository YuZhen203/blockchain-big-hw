# 区块链课程大作业：在区块链上构建应用系统

> 课程：《区块链技术与应用》  
> 作业内容：基于 ERC20 的后端服务 + 前端 DApp + 测试网部署

本仓库包含：

1. **后端 Spring Boot + web3j 服务**（集成自定义 ERC20 合约）  
2. **前端 DApp（my-first-dapp）**，基于 Hardhat + Next.js + wagmi + RainbowKit  
3. **自定义 ERC20 合约 `ERC20Test`**，并已在测试网上完成部署与交互

---

## 一、仓库结构

```bash
blockchain-big-hw/
├── backend/
│   └── spring-boot-web3j-master/       # Spring Boot + web3j 后端工程
│       ├── src/main/java/com/wetech/demo/web3j/
│       │   ├── Application.java        # Spring Boot 启动类
│       │   ├── config/Web3jConfig.java # web3j 客户端配置
│       │   ├── contracts/ERC20Test.java# 由 .abi/.bin 生成的合约 Java 封装
│       │   ├── service/Erc20Service.java   # 业务逻辑：mint/transfer/approve/transferFrom 等
│       │   └── controller/Erc20Controller.java # 对外 REST API
│       └── src/main/resources/contracts/
│           ├── ERC20Test.abi
│           ├── ERC20Test.bin
│           └── ERC20Test.sol           # 自定义 ERC20 合约源码
│
└── my-first-dapp/                      # 前端 DApp + Hardhat 工程
    ├── packages/
    │   ├── hardhat/                    # Hardhat 项目（部署合约）
    │   │   ├── contracts/
    │   │   │   └── ERC20Test.sol       # 与后端一致的 ERC20 合约
    │   │   ├── deploy/
    │   │   │   ├── 00_deploy_your_contract.ts
    │   │   │   └── xx_deploy_ERC20_xxx.ts  # 为大作业添加的部署脚本
    │   │   └── hardhat.config.ts       # 网络配置（包含 POTOS Testnet / localhost）
    │   └── nextjs/                     # Next.js 前端
    │       ├── app/
    │       │   └── debug/              # DebugContracts 页面
    │       └── contracts/deployedContracts.ts # 部署信息（合约地址、ABI 引用）
    └── package.json / yarn.lock / ... 
```
## 二、技术栈说明

- **智能合约**：Solidity ERC20（自定义 `ERC20Test`，包含 `mint` / `transfer` / `approve` / `transferFrom` 等接口）
    
- **后端服务**：Java + Spring Boot + web3j
    
- **前端 DApp**：Next.js + React + wagmi + RainbowKit + ethers
    
- **开发与部署工具**：Hardhat、Yarn、Gradle
    
- **测试网络**：POTOS Testnet（chainId: `60600`，RPC: `https://rpc-testnet.potos.hk`）
    
    > 同时也支持 Hardhat 本地网络（`yarn chain`）。
    

---

## 三、环境准备

### 必要环境

- Node.js ≥ 18（本地实际使用为 `v22.13.0`）
    
- Yarn ≥ 3（本地实际使用为 `3.2.3`）
    
- JDK 8+（建议 8 / 11 / 17）
    
- Gradle（项目内已包含 `gradlew` / `gradlew.bat`，可直接使用）
    
- 一个浏览器钱包（推荐 MetaMask，已添加 POTOS Testnet 网络）
    

### 克隆仓库

`git clone https://github.com/YuZhen203/blockchain-big-hw.git cd blockchain-big-hw`

---

## 四、后端 Spring Boot + web3j 服务

### 1. 配置 RPC 与账户

在 `backend/spring-boot-web3j-master/src/main/resources/application.yml`（或 `application.properties`）中配置：

`web3j:   client-address: https://rpc-testnet.potos.hk   # 如果使用本地 Hardhat 节点则为 http://127.0.0.1:8545  erc20:   contract-address: 0x51c2E51BB6C404C52fDeFA58a97C9A0279F0326e  # ERC20Test 合约地址   owner-private-key: <部署者私钥>`



### 2. 启动后端服务

Windows：

`cd backend/spring-boot-web3j-master gradlew.bat bootRun`

Linux / macOS：

`cd backend/spring-boot-web3j-master ./gradlew bootRun`

服务默认监听端口（例如）`http://localhost:8080`。

### 3. REST API（示例）

控制器 `Erc20Controller` 提供了类似如下的接口（具体以代码为准）：

- `POST /erc20/deploy`  
    部署 `ERC20Test` 合约，返回合约地址。
    
- `GET /erc20/balanceOf?address=0x...`  
    查询指定地址的代币余额。
    
- `POST /erc20/mint`  
    请求体示例：
    
    `{   "to": "0x7099...79C8",   "amount": "1000" }`
    
- `POST /erc20/transfer`
    
    `{   "to": "0x7099...79C8",   "amount": "1000" }`
    
- `POST /erc20/approve`
    
    `{   "spender": "0x7099...79C8",   "amount": "1000" }`
    
- `POST /erc20/transferFrom`
    
    `{   "from": "0xf39F...2266",   "to": "0x7099...79C8",   "amount": "1000" }`
    
---

## 五、前端 DApp（my-first-dapp）

### 1. 安装依赖

`cd my-first-dapp yarn install`

> 本项目使用 Yarn 3（Plug'n'Play），依赖会被安装到 `.yarn` 目录。

### 2. 启动本地 Hardhat 节点（可选，本地调试）

`cd my-first-dapp yarn chain # 在 http://127.0.0.1:8545 启动 Hardhat 本地区块链`

### 3. 部署 ERC20 合约

#### （1）部署到本地 Hardhat 网络

`cd my-first-dapp yarn deploy # 默认会对 localhost / hardhat 网络部署并更新 nextjs/contracts/deployedContracts.ts`

#### （2）部署到 POTOS Testnet（测试网）

在 `packages/hardhat/hardhat.config.ts` 中配置好 POTOS 网络，例如：

`potos: {   url: "https://rpc-testnet.potos.hk",   chainId: 60600,   accounts: [process.env.DEPLOYER_PRIVATE_KEY!], },`

然后执行：

`cd my-first-dapp yarn deploy --network <你的 POTOS 网络名称>`

部署成功后终端会输出类似：

`deploying "ERC20Test" ... deployed at 0x51c2E51BB6C404C52fDeFA58a97C9A0279F0326e ERC20Test合约部署成功 代币名称: SATOToken 代币符号: SAT ... 📝 Updated TypeScript contract definition file on ../nextjs/contracts/deployedContracts.ts`

### 4. 启动前端

`cd my-first-dapp yarn start # 本地访问 http://localhost:3000`

浏览器中打开 `http://localhost:3000`：

1. 顶部连接钱包（MetaMask），选择 **POTOS Testnet** 或 **Hardhat 本地网络**（与部署网络保持一致）。
    
2. 进入 `DebugContracts` 页面，可以看到 `ERC20Test` 合约：
    
    - `mint`
        
    - `transfer`
        
    - `approve`
        
    - `transferFrom`
        
    - `balanceOf`
        
3. 按课程要求依次调用各方法，并在区块浏览器中查看交易详情（用于「交互证明」）。
    

---

## 六、测试网部署与交互证明

- **网络**：POTOS Testnet
    
    - RPC: `https://rpc-testnet.potos.hk`
        
    - ChainId: `60600`
        
- **ERC20Test 合约地址**（示例）：
    

`0x51c2E51BB6C404C52fDeFA58a97C9A0279F0326e`

---

## 七、注意事项

- 请确保前端 DApp 所使用的网络（POTOS / localhost）与合约部署网络一致，否则会出现 `Wallet not connected or in wrong network`。
    
- 如果本地 Node/Yarn 版本差异较大，安装依赖时可能会出现大量 peer dependency 警告，一般不影响运行。
    
- 如果更改了合约名称或路径，记得同步更新：
    
    - Hardhat 部署脚本（`deploy/xx_deploy_*.ts`）
        
    - `nextjs/contracts/deployedContracts.ts`
        
    - 后端 resources 中的 `.abi` / `.bin` / `.sol` 以及生成的 `ERC20Test.java`。
