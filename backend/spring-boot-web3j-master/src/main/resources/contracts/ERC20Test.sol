// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

contract ERC20Test {
    // === 状态变量 ===
    string private _name;
    string private _symbol;
    uint8 private _decimals;
    uint256 private _totalSupply;
    mapping(address => uint256) private _balances;
    mapping(address => mapping(address => uint256)) private _allowances;

    address public owner;

    // === 事件定义 ===
    event Transfer(address indexed _from, address indexed _to, uint256 _value);
    event Approval(address indexed _owner, address indexed _spender, uint256 _value);

    // === 构造函数 ===
    constructor() {
        _name = "SATOToken";
        _symbol = "SAT";
        _decimals = 18;
        owner = msg.sender;
        _totalSupply = 1000 * 10 ** _decimals;
        _balances[msg.sender] = _totalSupply;
    }

    // === ERC20 接口实现 ===

    function name() public view returns (string memory) {
        return _name;
    }

    function symbol() public view returns (string memory) {
        return _symbol;
    }

    function decimals() public view returns (uint8) {
        return _decimals;
    }

    function totalSupply() public view returns (uint256) {
        return _totalSupply;
    }

    function balanceOf(address _owner) public view returns (uint256 balance) {
        return _balances[_owner];
    }

    function transfer(address _to, uint256 _value) public returns (bool success) {
        require(_to != address(0), "Transfer to zero address");
        require(_balances[msg.sender] >= _value, "Insufficient balance");
        _balances[msg.sender] -= _value;
        _balances[_to] += _value;
        emit Transfer(msg.sender, _to, _value);
        return true;
    }

    function transferFrom(address _from, address _to, uint256 _value) public returns (bool success) {
        require(_to != address(0), "Transfer to zero address");
        require(_balances[_from] >= _value, "Insufficient balance");
        require(_allowances[_from][msg.sender] >= _value, "Insufficient allowance");

        _balances[_from] -= _value;
        _balances[_to] += _value;
        _allowances[_from][msg.sender] -= _value;

        emit Transfer(_from, _to, _value);
        return true;
    }

    function approve(address _spender, uint256 _value) public returns (bool success) {
        _allowances[msg.sender][_spender] = _value;
        emit Approval(msg.sender, _spender, _value);
        return true;
    }

    function allowance(address _owner, address _spender) public view returns (uint256 remaining) {
        return _allowances[_owner][_spender];
    }

    function mint(uint256 _value) public {
        require(msg.sender == owner, "Only owner can mint");
        _totalSupply += _value;
        _balances[msg.sender] += _value;
        emit Transfer(address(0), msg.sender, _value);
    }

    function burn(uint256 _value) public {
        require(_balances[msg.sender] >= _value, "Insufficient balance");
        _balances[msg.sender] -= _value;
        _totalSupply -= _value;
        emit Transfer(msg.sender, address(0), _value);
    }
}