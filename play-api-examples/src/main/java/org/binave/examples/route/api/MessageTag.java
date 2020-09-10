package org.binave.examples.route.api;

import com.sun.org.glassfish.gmbal.Description;

/**
 * 状态码
 *
 * 服务器在处理指令时发给客户端
 *
 * 状态码 0 代表成功，码分 ID 段， 长度 100，段首整数为未知错误
 *
 */
public interface MessageTag {

    // 两个 int，分别代表 长度 与 指令
    int CUSTOM_HEAD_SIZE = Integer.BYTES * 2;


    @Description("成功")
    int SUCCESS = 0;//

    @Description("未知错误")
    int UNKNOWN_ERROR = 1;//客户端在遇到不能解析的状态码时默认用本错误提示

    //底层状态
    @Description("服务器不存在")
    int SERVER_NOT_FOUND = 50;//
    @Description("接口出错")
    int INTERFACE_ERROR = 51;//
    @Description("参数错误")
    int PARAMETER_ERROR = 52;//

    //通用状态
    @Description("奖励不可领取")
    int REWARD_CANT_TAKE = 101;//
    @Description("奖励已领取")
    int REWARD_ALREADY_TAKEN = 102;//
    @Description("体力不足")
    int LACK_OF_ENERGY = 103;//
    @Description("操作失败")
    int OPTION_FAILED = 104;//
    @Description("通讯超时已下线")
    int CLIENT_COMM_TIMEOUT = 105;
    @Description("金币不足")
    int LACK_OF_COIN = 106;//
    @Description("钻石不足")
    int LACK_OF_CASH = 107;//
    @Description("角色未登录")
    int PLAYER_NOT_LOGINED = 108;//
    @Description("角色不存在")
    int PLAYER_NO_EXIST = 109;//
    @Description("角色不在线")
    int PLAYER_IS_OFFLINE = 110;//
    @Description("登录Token校验失败")
    int TOKEN_VERIFY_FAIL = 111;//
    @Description("角色断线重连Token过期")
    int PLAYER_RECONN_FAIL = 112;


    //账号登录服
    @Description("账号不存在")
    int ACCOUNT_NO_EXIST = 201;//
    @Description("账号密码错误")
    int ACCOUNT_WRONG_PASSWORD = 202;//
    @Description("账号或密码不符合格式要求")
    int ACCOUNT_CREATE_WITH_INVALID_PATTERN = 203;
    @Description("账号名已被占用")
    int ACCOUNT_ALREADY_EXISTS = 204;
    @Description("账号创建失败")
    int ACCOUNT_CREATE_FAILED = 205;
    @Description("账号在别处登录")
    int ACCOUNT_LOGINED_FROM_OTHERWERE = 206;

    //游戏服
    @Description("服务器已关闭")
    int SERVER_CLOSED = 301;//
    @Description("进入游戏失败")
    int ENTER_GAME_FAILED = 302;//
    @Description("服务器已满")
    int ONLINE_PLAYER_MAXIUM = 303;//
    @Description("角色数已达上限")
    int PLAYER_NUM_MAXIUM = 304;//
    @Description("角色名已被占用")
    int PLAYER_NICKNAME_DUPLICATE = 305;//
    @Description("昵称含不文明用语")
    int NICKNAME_DIRTY_WORD = 306;//
    @Description("昵称不符合要求")
    int NICKNAME_INVALID = 307;//
    @Description("没有剩余次数")
    int NO_MORE_TIMES = 308;//
    @Description("角色登录失败")
    int PLAYER_LOGIN_FAIL = 309;

    //道具出售
    @Description("道具出售,遇到未知错误")
    int SELL_PROP_UNKNOWN = 500;//
    @Description("道具出售,道具不可卖")
    int SELL_PROP_NOT_FOR_SALE = 501;//
    @Description("道具出售,格子不存在")
    int SELL_PROP_GRID_NO_EXIST = 502;//
    @Description("道具出售,提交的格子ID重复")
    int SELL_PROP_GRID_DUPLICATE = 504;//
    @Description("道具出售,页签不存在")
    int SELL_PROP_TAB_NO_EXIST = 505;//

    //关卡UI
    @Description("未知错误")
    int STAGE_CHAPTER_UNKNOWN = 600;//
    @Description("章节不存在")
    int STAGE_CHAPTER_NO_EXIST = 601;//
    @Description("章节未开启")
    int STAGE_CHAPTER_IS_LOCKED = 602;//
    @Description("关卡不存在")
    int STAGE_STAGE_NO_EXIST = 603;//
    @Description("关卡未开启")
    int STAGE_STAGE_IS_LOCKED = 604;//
    @Description("关卡挑战次数不足")
    int STAGE_NO_TIMES = 605;//

    //背包、装备、时装
    @Description("背包错误请重新登录")
    int INVENTORY_DATA_WRONG = 701;

    @Description("DNA突破数据异常")
    int DNA_BREAKTHROUGH_EXCEPTION = 702;
    @Description("角色未达到DNA突破等级")
    int DNA_BREAKTHROUGH_LEVEL_LACK = 703;
    @Description("DNA突破石不足")
    int DNA_BREAKTHROUGH_STONE_LACK = 704;
    @Description("DNA当前不需要突破")
    int DNA_NONEED_BREAKTHROUGH = 706;
    @Description("角色还未达到DNA宝石镶嵌开启等级")
    int DNA_EMBEDGEM_LEVEL_LACK = 707;
    @Description("DNA已达品质上限")
    int DNA_GRADE_MAX = 708;
    @Description("DNA需要突破才能继续强化")
    int DNA_NEED_BREAKTHROUGH = 709;
    @Description("DNA已达到强化等级上限")
    int DNA_INTENSIFY_MAX_LEVEL = 710;
    @Description("DNA强化道具不足")
    int DNA_INTENSIFY_STONE_LACK = 711;

    @Description("已达到最高锻造等级")
    int FASHION_FORGING_MAX_LEVEL = 720;
    @Description("时装代币不足")
    int LACK_OF_FASHION_COIN = 721;
    @Description("时装未锻造不能重置")
    int FASHION_NO_FORGING = 722;
    @Description("道具使用失败")
    int INVENTORY_USE_PROP_FAIL = 723;
    @Description("宝石合成材料不足")
    int GEM_COMPOUND_LACK = 724;
    @Description("宝石已达最高等级")
    int GEM_COMPOUND_MAXLEVEL = 725;

    @Description("已到达最高进阶等级")
    int FASHION_ADVANCED_MAX_LEVEL = 726;
    @Description("时装进阶材料不足")
    int FASHION_ADVANCED_PROP_LACK = 727;
    @Description("时装锻造材料不足")
    int FASHION_FORGING_PROP_LACK = 728;

    //社交
    @Description("聊天未知错误")
    int CHAT_UNKNOWN = 800;
    @Description("聊天发送太频繁")
    int CHAT_TALK_TOO_MORE = 801;
    @Description("聊天信息发送失败")
    int CHAT_SEND_FAIL = 802;

    //pvp
    @Description("PK匹配超时")
    int PK_SEARCHING_TIMEOUT = 901;
    @Description("PK匹配请求失败")
    int PK_SEARCHING_REQUEST_FAIL = 902;
    @Description("PK已在匹配队列中")
    int PK_SEARCHING_ALREADY = 903;
    @Description("PK比赛等待准备超时")
    int PK_MATCH_LOADING_TIMEOUT = 904;
    @Description("PK比赛中断结束")
    int PK_MATCH_INTERRUPTED = 905;

    //pet
    @Description("宠物未知错误")
    int PET_UNKNOWN = 1000;
    @Description("宠物不存在")
    int PET_NOT_FOUND = 1001;
    @Description("宠物升级材料不足")
    int PET_UPGRADE_ITEM_LACK = 1002;
    @Description("宠物等级已达最终上限")
    int PET_LEVEL_REAL_MAX = 1003;
    @Description("宠物等级已达角色等级上限")
    int PET_LEVEL_PLAYER_MAX = 1004;
    @Description("宠物经验已达上限")
    int PET_EXP_MAX = 1005;
    @Description("宠物经验道具不足")
    int PET_EXP_ITEM_LACK = 1006;
    @Description("宠物品质达到上限")
    int PET_GRADE_MAX = 1007;
}
