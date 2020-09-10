# play-common-examples

play-common 项目的示例

使用 protobuf 在 TCP 之上进行通信

数据包格式：

|位置|类型|说明
|---|---|---
|第一段|int|指令数据长度
|第二段|int|指令号 id
|第三段|protobuf (byte[])|指令数据


指令名称格式：
```
[node]_[actions_name]_[id]
e.g. C_LoginAccount_10004
```

|id|说明
|---|---
|node|C：由客户端发起，S：由服务端下发
|actions_name|操作名称，与 proto 文件对应
|id|指令号，纯数字
