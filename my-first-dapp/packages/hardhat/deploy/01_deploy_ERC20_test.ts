import { HardhatRuntimeEnvironment } from "hardhat/types";
import { DeployFunction } from "hardhat-deploy/types";
import { Contract } from "ethers";

/**
 * Deploys a contract named "ERC20Test" which is an ERC20 token implementation
 *
 * @param hre HardhatRuntimeEnvironment object.
 */
const deployERC20Test: DeployFunction = async function (hre: HardhatRuntimeEnvironment) {
  const { deployer } = await hre.getNamedAccounts();
  const { deploy } = hre.deployments;

  await deploy("ERC20Test", {
    from: deployer,
    args: [],
    log: true,
    autoMine: true,
  });

  const erc20Test = await hre.ethers.getContract<Contract>("ERC20Test", deployer);
  
  console.log("ERC20Test合约部署成功");
  console.log("代币名称:", await erc20Test.name());
  console.log("代币符号:", await erc20Test.symbol());
  console.log("代币精度:", await erc20Test.decimals());
  console.log("总供应量:", await erc20Test.totalSupply());
  console.log("合约所有者:", await erc20Test.owner());
  console.log("部署者余额:", await erc20Test.balanceOf(deployer));
};

export default deployERC20Test;

// 部署标签
deployERC20Test.tags = ["ERC20YZ202330552931"];
