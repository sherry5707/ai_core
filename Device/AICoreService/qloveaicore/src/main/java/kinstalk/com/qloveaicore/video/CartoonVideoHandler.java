package kinstalk.com.qloveaicore.video;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.tencent.xiaowei.util.QLog;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import kinstalk.com.qloveaicore.QAIApplication;
import kinstalk.com.qloveaicore.qlovenlp.model.ModelVideoSkill;

public class CartoonVideoHandler {

    private static final String TAG = "CartoonVideoHandler";

    private static final String BASE_URL = "qqlivekid://v.qq.com/JumpAction?";

    private static String[] channels;

    private static Map<String, String> channelMap;

    public static boolean handleCartoonVideo(ModelVideoSkill intentInfo) {
        if (intentInfo == null) {
            return false;
        }
        HashMap<String, String> slotsMap = intentInfo.slotsMap;

        if (isCartoonTag(slotsMap)) {
            if (hasParams(slotsMap, "play_name")) {
                openVideoByPlayName(slotsMap.get("play_name"));
            } else if (hasParams(slotsMap, "actor_name")) {
                openAppByActor(slotsMap);
            } else {
                openApp();
            }
            return true;
        }

        if (isCartoonChannel(slotsMap)) {
            String videoChannel = slotsMap.get("video_channel");
            if (hasParams(slotsMap, "play_name")) {
                openVideoByPlayName(slotsMap.get("play_name"));
            } else if (hasParams(slotsMap, "video_tag")) {
                openAppByTag(videoChannel, slotsMap.get("video_tag"));
            } else if (hasParams(slotsMap, "language")) {
                openAppByTag(videoChannel, slotsMap.get("language"));
            } else {
                openApp();
            }
            return true;
        }

        return false;
    }

    private static boolean isCartoonTag(Map<String, String> map) {
        return hasParams(map, "video_tag") && isCartoon(map.get("video_tag"));
    }

    private static boolean isCartoonChannel(Map<String, String> map) {
        return hasParams(map, "video_channel") && isCartoon(map.get("video_channel"));
    }

    /**
     * @param name 片名
     * @return 是否是动画频道
     */
    private static boolean isCartoon(String name) {
        for (String channel : channels) {
            if (name.contains(channel)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据演员搜索
     *
     * @param slotsMap
     */
    private static void openAppByActor(HashMap<String, String> slotsMap) {
        String videoTag = slotsMap.get("video_tag");
        String actorName = slotsMap.get("actor_name");
        openBySearchKey(videoTag + actorName);
    }

    /**
     * 根据片名搜索
     *
     * @param playName
     */
    private static void openVideoByPlayName(String playName) {
        String cid = getVideoCid(playName);
        if (cid != null) {
            QLog.e(TAG, "openByCid: " + cid);
            openByCid(cid);
        } else {
            QLog.e(TAG, "openBySearchKey: " + playName);
            openBySearchKey(playName);
        }
    }

    /**
     * 根据类别搜索
     *
     * @param title
     * @param tag
     */
    private static void openAppByTag(String title, String tag) {
        CartoonCountly.recordEvent(CartoonCountly.V_QIE);
        String extStr = "{\"title\":\"" + title + "\",\"tag\":\"" + tag + "\"}";
        String url = BASE_URL + "cht=7&ext=" + extStr + "&jump_source=QROBOT";
        startActivity(url);
    }

    /**
     * 关键词搜索
     */
    private static void openApp() {
        CartoonCountly.recordEvent(CartoonCountly.V_QIE);
        String url = BASE_URL + "cht=1&chid=100186&jump_source=QROBOT";

        startActivity(url);
    }


    /**
     * 根据cid打开专辑
     *
     * @param cid
     */
    private static void openByCid(String cid) {
        CartoonCountly.recordEvent(CartoonCountly.V_QIE);

        String search = URLEncoder.encode("{\"cid\":\"" + cid + "\"}");

        String url = BASE_URL + "cht=5&ext=" + search + "&jump_source=QROBOT";

        startActivity(url);
    }

    /**
     * 根据关键词搜索
     *
     * @param searchKey
     */
    private static void openBySearchKey(String searchKey) {
        CartoonCountly.recordEvent(CartoonCountly.V_QIE);

        String search = URLEncoder.encode("{\"key\":\"" + searchKey + "\"}");

        String url = BASE_URL + "cht=2&ext=" + search + "&jump_source=QROBOT";

        startActivity(url);
    }

    private static void startActivity(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            QAIApplication.getInstance().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getVideoCid(String keyword) {
        for (String key : channelMap.keySet()) {
            String title = channelMap.get(key);
            if (title.contains(keyword)) {
                return key;
            }
        }
        return null;
    }

    private static boolean hasParams(Map<String, String> map, String params) {
        return map != null && map.containsKey(params) && !TextUtils.isEmpty(map.get(params));
    }

    static {
        channelMap = new HashMap<>();
        channelMap.put("6r1q2cj2zrdr8kz", "豆乐儿歌");
        channelMap.put("bnc0cvuskzxnrcl", "可可小爱公益动画");
        channelMap.put("m2lbjjlfidqc8iu", "熊出没之夏日连连看");
        channelMap.put("bzfkv5se8qaqel2", "小猪佩奇");
        channelMap.put("ur9fe19iufs04wt", "最强战士 迷你特工队");
        channelMap.put("v18fkxuc65s8fad", "芭比之梦想豪宅");
        channelMap.put("bxt4sqjz46y2wyk", "超级飞侠（第三季）");
        channelMap.put("2d53ndie923okvp", "猪猪侠之光明守卫者 （上）");
        channelMap.put("apls9p0cjo1syaz", "螺丝钉");
        channelMap.put("hmc0z7kb4pldg4d", "倒霉熊");
        channelMap.put("r4b1l611ot2jlrf", "愤怒的小鸟合集");
        channelMap.put("ipm2meuu857sw3e", "蜡笔小新");
        channelMap.put("fmy3srlfpa5wr53", "海底小纵队");
        channelMap.put("ygyx5buye1dyob1", "乐高好朋友之微短剧全集");
        channelMap.put("lesch825u8n3f7q", "熊猫和小鼹鼠");
        channelMap.put("w26s73tpe5y42ke", "《愤怒的小鸟：猪猪传》第二季");
        channelMap.put("cwp9lw0ybdz6qw2", "功夫兔与菜包狗短片");
        channelMap.put("l4z4mjtn43rvlvt", "猫和老鼠");
        channelMap.put("akjksf0ts6uohyw", "艾克斯奥特曼");
        channelMap.put("jo9b2oxhk5f3jlx", "乐高幻影忍者");
        channelMap.put("e51zela13gm1zno", "贝瓦儿歌");
        channelMap.put("e8qnszysext3nj5", "乐高城市");
        channelMap.put("m31r496nqq429yh", "斗龙战士5");
        channelMap.put("fcgcs61wc7yn6bb", "超级飞侠 精华版");
        channelMap.put("j294cmmcbgxq2at", "魔法俏佳人");
        channelMap.put("0tg7zf5klth24d1", "爆裂飞车2");
        channelMap.put("q4qca3sh7xch16f", "跳跳鱼世界");
        channelMap.put("jbq1kko6pokgy4a", "小公主苏菲亚 第二季");
        channelMap.put("lgh4ord3vek9xhe", "小鸡Jaki在哪儿");
        channelMap.put("oafghfff0co7f8v", "猪猪侠之五灵守卫者");
        channelMap.put("kn7jk6npfmgejif", "熊出没之过年");
        channelMap.put("p8523is4ct895z7", "大头儿子小头爸爸第二季");
        channelMap.put("hzgtnf6tbvfekfv", "名侦探柯南（国语版）");
        channelMap.put("3amvb5ih605qbfm", "熊出没之雪岭熊风");
        channelMap.put("91ydqgaa9f00rht", "爱探险的朵拉第6季");
        channelMap.put("96r2urhbyn48gji", "开心超人联盟之超时空保卫战");
        channelMap.put("k8sp9mbn2qv8wiv", "大头儿子和小头爸爸");
        channelMap.put("ofjceyiy408uhsw", "比得兔");
        channelMap.put("srxivmpmvaqeznv", "巴啦啦小魔仙之梦幻旋律");
        channelMap.put("rtysyfvpmj4ica2", "疯狂的兔子 第二季");
        channelMap.put("lvieikko9oub8fl", "猪猪侠10（上）");
        channelMap.put("au82hc6hpfpjr7i", "铠甲勇士捕将");
        channelMap.put("pktm8pglw2cd0c2", "变形警车珀利");
        channelMap.put("80txiqki0mj6bfa", "企鹅家族");
        channelMap.put("kwyhtukiev0rt9a", "芭比系列大电影");
        channelMap.put("8ipfiuy7xy00n80", "精灵梦叶罗丽 第三季");
        channelMap.put("wfxcys58fim8o71", "可爱巧虎岛");
        channelMap.put("2iqrhqekbtgwp1s", "葫芦兄弟");
        channelMap.put("7mweipoaj2y84q1", "超级飞侠地理课堂");
        channelMap.put("2dtmgrk6zq0bexf", "愤怒的小鸟");
        channelMap.put("mbe26qtwqt9b6sh", "超能玩具白白侠");
        channelMap.put("aavz5kh7g01wiep", "世界童话全集");
        channelMap.put("3x7rf42yye8fpif", "小马宝莉友情的魔力");
        channelMap.put("cfrygt95a6j2vor", "迪迦奥特曼");
        channelMap.put("7164t10wnfsrg7p", "宝宝爱玩具");
        channelMap.put("p3ryqwjy4mvebtp", "乐高生化战士");
        channelMap.put("42d8kpnd2mndj5v", "喜羊羊与灰太狼嘻哈闯世界");
        channelMap.put("ltwypmftc4oqc50", "精灵梦叶罗丽合集");
        channelMap.put("72ryl3dljd9o96t", "赛尔号合集");
        channelMap.put("qpz749hwize3ats", "亲宝儿歌");
        channelMap.put("s8mkeengqez5p2z", "猫和老鼠爆笑特辑");
        channelMap.put("2hbl5r63pcyktqd", "猪猪侠之光明守卫者 精华版");
        channelMap.put("6tt5rdyqhese1u6", "巴啦啦小魔仙之梦幻旋律特辑");
        channelMap.put("zoz9o8lhvsxu8k1", "大头儿子小头爸爸集锦");
        channelMap.put("7xjb3eflneei42w", "托马斯和他的朋友们");
        channelMap.put("sa8bpj4g1et16wp", "可可小爱旅游篇");
        channelMap.put("5tjh0uw46q6z00g", "《愤怒的小鸟：Stella 》合集");
        channelMap.put("nlws32dvpkj5z1k", "乐高好朋友微短剧 第3季");
        channelMap.put("udupsqzfpudt2lc", "小猫米思蒂");
        channelMap.put("rtzre6uamrqmwag", "赛尔号");
        channelMap.put("im8whb83ebuyp54", "变形金刚酷垒");
        channelMap.put("9clgf10wqwfo3x3", "小花仙");
        channelMap.put("c7l19byizt1zj95", "小伴龙儿歌");
        channelMap.put("jru4jixrll5us91", "超人泰罗");
        channelMap.put("nx1luxv22hnetfy", "灵动蹦蹦兔");
        channelMap.put("v0nu26r0g7dn5zc", "巴啦啦小魔仙");
        channelMap.put("3x99tmae8uikylw", "哈利儿歌");
        channelMap.put("mdcu90m9e49ny1k", "愤怒的小鸟 第三季");
        channelMap.put("gfe6l3lwlyhqkcz", "猪猪侠10（下）");
        channelMap.put("6k42t0ezm7qqs8z", "图图的智慧王国");
        channelMap.put("8gp7y7dhst16zcj", "咕力咕力");
        channelMap.put("eysky7xhhl4tntc", "银河奥特曼 中文版");
        channelMap.put("xrre294guu1kvpa", "大耳朵图图 第5季");
        channelMap.put("qld6xq5njgvodre", "迪士尼公主精彩世界");
        channelMap.put("tz0fwmxgw8l31qh", "消防员山姆");
        channelMap.put("of5959nj7ksm3f0", "猪猪侠之终极决战前夜篇");
        channelMap.put("3k9457j6wnmopkh", "芭比之狗狗大冒险");
        channelMap.put("sumcta2g0dk6coo", "小黄人番外篇");
        channelMap.put("k6ox2oiuvwfrt9x", "功夫兔与菜包狗：推倒小伙伴");
        channelMap.put("kxkm98jda99sbvu", "米老鼠和唐老鸭");
        channelMap.put("ttjl9f6cp3cshr2", "芭比与神秘之门");
        channelMap.put("ctj7w5k0yoa4n2y", "贝瓦儿歌之动物儿歌");
        channelMap.put("82nt9vxsm4x03eu", "小羊肖恩");
        channelMap.put("7j192rwr1s2735k", "铠甲勇士");
        channelMap.put("sbsq39s6xqlyyhr", "奇积乐园");
        channelMap.put("6q3lql8pt9m6j99", "闲画部落");
        channelMap.put("hey43dp4gzdj1zn", "外星猴子");
        channelMap.put("79uyvtgrg9s16qe", "魔幻车神完整版");
        channelMap.put("lch764a4gvpkvrl", "愤怒的小鸟混搭版");
        channelMap.put("jlx1fqhlqwjyh2g", "企鹅家族 第3季");
        channelMap.put("taeh2beehfukkvh", "泥团的奇幻旅行");
        channelMap.put("o8tini9xcgdjs7o", "精灵梦叶罗丽第一季");
        channelMap.put("vysttylt1my199a", "消防员山姆 第8季");
        channelMap.put("3xnu35pv1qe6s6v", "熊出没歌曲集");
        channelMap.put("g4s7pf08aq6dcp3", "恐龙世界");
        channelMap.put("e26cdiurkbuwwj8", "小马宝莉");
        channelMap.put("ax6cm81c0gcsqs2", "黑猫警长");
        channelMap.put("ezrgqb8tfns49o5", "星学院");
        channelMap.put("sl6d7h9ncbkw4ij", "《愤怒的小鸟：Stella》第一季");
        channelMap.put("xfcunlfmw8c88k9", "小鸡彩虹");
        channelMap.put("3csdc0oko7pahsy", "贝瓦儿歌专区");
        channelMap.put("wph9qc9mu81wqoc", "小马宝莉友谊的魔力 第5季 中文版");
        channelMap.put("954skjykc5nyl03", "《愤怒的小鸟：Stella》第二季");
        channelMap.put("r6o98876ra7ww8i", "加菲猫合集");
        channelMap.put("g6fehguyynrynyn", "疯狂小糖2");
        channelMap.put("y7fiv3wbp91hw35", "疯狂的兔子 第一季");
        channelMap.put("2x0gm16rlzi6523", "帮帮龙，出动！");
        channelMap.put("j324wyy9rpo9dqn", "海底小纵队精华版");
        channelMap.put("8n736yb1pyk9yml", "龙背天空艺术创想");
        channelMap.put("vsvozocugifepqr", "米奇妙妙屋合集");
        channelMap.put("5zj69rmem1bo6fg", "变形金刚：领袖之证全季");
        channelMap.put("7rbeh1fevklt3d7", "超人赛文");
        channelMap.put("nbctc5nnsxqsx9z", "喜羊羊与灰太狼之原始世界历险记");
        channelMap.put("c7s2709g79bxcgx", "巴塔木儿歌");
        channelMap.put("yzd10o9e4tekrud", "赛尔号 第3季");
        channelMap.put("mzk3aroyeq9mqja", "喜羊羊与灰太狼之洋洋得意喜羊羊（下）");
        channelMap.put("qzq5df8nh8xjyuc", "喜羊羊与灰太狼之洋洋得意喜羊羊（上）");
        channelMap.put("ub532hyt73p1elj", "格林童话剧场");
        channelMap.put("vu5tm5th9ni07h6", "电击小子");
        channelMap.put("xreg7yvzeeplzsq", "小伶玩具");
        channelMap.put("ecibpcx9arfxo6d", "倒霉熊爱运动");
        channelMap.put("m233r5usnpelcxx", "方块熊乐园");
        channelMap.put("vcf5z08br9wnojf", "比得兔精彩特辑");
        channelMap.put("hq7qye92q905ar8", "铠甲勇士捕将之英雄见英雄");
        channelMap.put("2mgrnf25kjle4gi", "梦想三国");
        channelMap.put("nfs904a07wynolo", "动画大电影");
        channelMap.put("w49zoc96hlk36lr", "铠甲勇士拿瓦特辑");
        channelMap.put("kxg5yzd9rusxu13", "乐高好朋友系列心湖城的故事");
        channelMap.put("cu4q15d3rzebjgn", "超人杰克");
        channelMap.put("2f6hftn98mzx07u", "可可小爱童谣");
        channelMap.put("53d7u0fnozntwv1", "海底小纵队特别篇");
        channelMap.put("p8wtjcqlvjwr7ly", "小黄人爆笑翻唱特辑");
        channelMap.put("695e6c43a9d3wis", "猪猪侠之积木世界里的童话");
        channelMap.put("cby23fnnys1slcm", "巴啦啦小魔仙魔法优等生");
        channelMap.put("39jcvm64pdhc28m", "神奇阿呦");
        channelMap.put("12hx78lt9kdqvou", "星际精灵蓝多多");
        channelMap.put("apnh6ydbctb9ok0", "海绵宝宝 第7季");
        channelMap.put("jfr6ftebdby9py6", "乐高未来骑士团");
        channelMap.put("iw0rztm671da1jk", "巴啦啦小魔仙之彩虹心石");
        channelMap.put("li58udst546xjjb", "超级飞侠英文版");
        channelMap.put("cyn2pu80g4czx71", "袋鼠跳跳");
        channelMap.put("huac5s63rn5etal", "数学荒岛历险记3 地球我来了");
        channelMap.put("abzxu12z6jh19ga", "巴布工程师");
        channelMap.put("27t532cgkxf26zy", "逗逗迪迪爱探险");
        channelMap.put("98mxp4ttqxf4a5u", "新版鼹鼠的故事");
        channelMap.put("w64m872k5yp4irz", "玩疯了手工帝");
        channelMap.put("5e7vu987xmvgvn9", "螺丝钉MV");
        channelMap.put("jnaa1zlp0qy40ec", "我们一起唱歌跳舞");
        channelMap.put("viio9va8e4e3k3u", "奥特曼和小怪兽");
        channelMap.put("ooh1egwqgup2dxi", "超人艾斯");
        channelMap.put("zsu8trnkjznowsg", "萌萌卡通小明星");
        channelMap.put("g01r1g17yet15l1", "兔兔之真假公主");
        channelMap.put("jhhjvy22tcwg89s", "猪猪侠之幸福救援队");
        channelMap.put("dror6uet5w5jhku", "儿歌");
        channelMap.put("wfvyhbnqq0p8k46", "可可小爱贺岁篇");
        channelMap.put("javyoz41i867vjy", "大耳朵图图");
        channelMap.put("at2c19n801uitfp", "小小羊提米");
        channelMap.put("c7g7pwx2753o4wn", "巴巴爸爸`");
        channelMap.put("4m9uz9szlt7ort3", "起司公主");
        channelMap.put("8emp4wx27ngv4w3", "嘟拉3D儿歌");
        channelMap.put("5ty73m4hw6ojg1n", "功夫兔与菜包狗");
        channelMap.put("iu5c3naz3qvxi5e", "倒霉熊爆笑特辑");
        channelMap.put("oizhpbnmf4keqwe", "RABBYCC之卡罗的心灵世界");
        channelMap.put("bxxzbfkw0ltk9mw", "嘟拉唱儿歌");
        channelMap.put("lzuvjnnlfb29cp7", "儿童歌曲");
        channelMap.put("0d3smkzdrzdouc4", "星际小蚂蚁");
        channelMap.put("5v8je3p4zr2qjx7", "海底小纵队儿歌");
        channelMap.put("hvgee9v9w1jfigc", "儿童睡前故事");
        channelMap.put("h72f50dgt2vombg", "魔法俏佳人7");
        channelMap.put("akv3lniqxw9m7bi", "艾可魔法少女");
        channelMap.put("nnjrt62wfa51161", "爆笑虫子合集");
        channelMap.put("zsfymr17g4saqgq", "喜羊羊与灰太狼之智趣羊学堂（上）");
        channelMap.put("y28fs28lwv7qiu5", "喜羊羊与灰太狼之智趣羊学堂");
    }

    static {
        channels = new String[]{
                "动画",
                "卡通",
                "动漫",
                "儿歌",
                "益智",
                "冒险",
                "动物",
                "手工",
                "绘画",
                "早教",
                "玩具",
                "英语",
                "数学",
                "真人",
                "国学",
                "探索",
                "魔幻"
        };
    }
}
