# examples-api

* 集中管理主接口和接口入参回参。
    * 此工程需要被所有其他工程（直接或间接）引用。
    * 此工程中的接口，只能在其他项目中实现。
    * 此工程不可包含任何业务逻辑。
    * 此工程不可引用其他同级工程。

# 游戏后端模块划分

```plantuml

skinparam {
' handwritten true
backgroundColor #ffffff
activityBackgroundColor #ffffff
activityBorderColor #888888
ArrowColor #009900
stateBorderColor #888888
stateBackgroundColor #ffffff
}

state client {
state "client_01" as c01
c01 : * android
state "client_02" as c02
c02 : * iOS
state "client_03" as c03
c03 : * PC
}

client --> router_0 : requset
client --> router_1
router_0 -up[#0000ff]-> client
router_1 -up[#0000ff]-> client : response
router_0 --> data_1 : get\nset
router_0 --> config : get\nset
router_0 --> log
router_1 --> data_2
router_1 --> config
router_1 --> log : set
trigger -up[#0000ff]-> router_0 : do
trigger -up[#0000ff]-> router_1

state router_0 {
state "switch" as s01
state "handler" as h01
h01 : * module 01
state "handler" as h02
h02 : * module 02
state "handler" as h03
h03 : * module 03

s01 --> s01 : get handler
s01 --> h01
h01 -[#0000ff]-> s01
s01 --> h03
h03 -[#0000ff]-> s01
s01 --> h02
h02 -[#0000ff]-> s01
}

state router_1 {
state "switch" as s11
state "handler" as h11
h11 : * module 04
state "handler" as h12
h12 : * module 05

s11 --> s11 : get handler
s11 --> h11
h11 -[#0000ff]-> s11
s11 --> h12
h12 -[#0000ff]-> s11
}

state trigger {
state "loop" as l
l : * timer
}

state log {
state "file" as f
state "database" as d
d : * mysql 04
state "remote api" as r
r : * hadoop
}

state data_1 {
state "cache" as c1
c1 : * redis 01
state "database" as d11
d11 : * mysql 02
state "database" as d12
d12 : * mysql 03

c1 --> d11 : get\nset
c1 --> d12
}

state data_2 {
state "cache" as c21
c21 : * redis 02
state "cache" as c22
c22 : * redis 03
state "database" as d1
d1 : * mysql 01

c21 --> d1
c22 --> d1 : get\nset
}

state config {
state "map" as m3
m3 : * memory
state "cache" as c3
c3 : * redis
state "file" as f3
f3 : * properties
state "database" as d3
d3 : * mysql 05

m3 --> c3 : get
c3 --> f3 : get
f3 --> d3 : get
}

```

* play-api-examples                 api

* play-base-examples                基础模块组
    * play-config-examples          配置模块
    * play-data-examples            数据模块
    * play-protoc-examples          编码依赖
    * play-route-examples           选择模块
    * play-trigger-examples

* play-handler-examples             逻辑模块组
    * play-admin-examples           管理员
    * play-general-examples         普通逻辑
    * play-im-examples              消息
    * play-login-examples           登陆
    * play-room-examples            群
    * play-sign-examples            注册

* play-client-examples              测试用客户端
    * play-client-boot-examples     客户端路由
    * play-client-handler-examples  客户端逻辑


|文件夹|作用|备注
|---|---|---
|api|放置公共接口|用于模块调用则参数不可多于 1 个
|args|放置公共接口参数 pojo 类或接口
|impl|接口实现类
|factory|工厂方法
