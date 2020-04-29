---
title: redis-java-API使用
categories: redis   
toc: true  
tags: [redis]
---



官网：https://redis.io/commands

# 1.string使用
## 1.1.set、get、mset、mget、setex、expire
```
public class StringMain {
    public static void main(String[] args) throws InterruptedException {
        //创建Jedis客户端
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        //操作一个String字符串
        jedis.set("name", "liudehua"); //插入一个名字，叫做刘德华
        System.out.println(jedis.get("name")); //读取一个名字
        //对string类型数据进行增减,前提是kv对应的值是数字
        jedis.set("age", "17");//给用户刘德华设置年龄，17岁
        jedis.incr("age");//让用户刘德华年龄增加一岁
        System.out.println(jedis.get("age")); //打印结果 18
        jedis.decr("age");//让刘德华年轻一岁
        System.out.println(jedis.get("age"));//在18的基础上，减一岁，变回17
        //一次性插入多条数据 。为江湖大侠设置绝杀技能
        jedis.mset("AAA", "Mysql数据库的操作"
                , "BBB", "熟悉LINXU操作系统"
                , "CCC", "熟悉SSH、SSM框架及配置"
                , "DDD", "熟悉Spring框架，mybatis框架，Spring IOC MVC的整合，Spring和Mybatis的整合");
        List<String> results = jedis.mget("AAA", "BBB", "CCC", "DDD");
        for (String value : results) {
            System.out.println(value);
    }
        //设置字段的自动过期
        jedis.setex("wumai", 10, "我们活在仙境中"); //让仙境保持10秒钟
        while (jedis.exists("wumai")) {
            System.out.println("真是天上人间呀！");
            Thread.sleep(1000);
        }
        System.out.println();
        //对已经存在的字段设置过期时间
        jedis.set("wumai", "我们活在仙境中");
        jedis.expire("wumai", 10); //让天上人间的感觉保持更长的时间
        while (jedis.exists("wumai")) {
            System.out.println("真是天上人间呀！");
            Thread.sleep(1000);
        }
    }
}

```

## 1.2.多线程操作Counter计数器

```
package redis.string;

import redis.clients.jedis.Jedis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Describe: 擂台比武
 */
public class Counter {
    /**
     * 计算 武林大会 三个擂台的比武次数
     *
     * @param args
     */
    public static void main(String[] args) {
        //创建一个固定大小的线程池，3个擂台
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        //擂台1：天龙八部
        executorService.submit(new Arena("biwu:totalNum","天龙八部"));
        //擂台2：神雕侠侣
        executorService.submit(new Arena("biwu:totalNum","神雕侠侣"));
        //擂台3：倚天屠龙记
        executorService.submit(new Arena("biwu:totalNum","倚天屠龙记"));
        //报幕人员，一秒统计一次总共比了多少场
        executorService.submit(new BaoMu("biwu:totalNum"));
    }
}

```

```
package redis.string;
import redis.clients.jedis.Jedis;
import java.util.Random;
/**
 * Describe: 擂台
 */
public class Arena implements Runnable {
    private Random random = new Random();
    private String redisKey;
    private Jedis jedis;
    private String arenaName;
    public Arena(String redisKey, String arenaName) {
        this.redisKey = redisKey;
        this.arenaName = arenaName;
    }
    public void run() {
        jedis = new Jedis("127.0.0.1",6379);
        String[] daxias = new String[]{"郭靖", "黄蓉", "令狐冲", "杨过", "林冲",
                "鲁智深", "小女龙", "虚竹", "独孤求败", "张三丰", "王重阳", "张无忌"
                , "王重阳", "东方不败", "逍遥子", "乔峰", "虚竹", "段誉"
                , "韦小宝", "王语嫣", "周芷若", "峨眉师太", "慕容复"};
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int p1 = random.nextInt(daxias.length);
            int p2 = random.nextInt(daxias.length);
            while (p1 == p2) { //如果两个大侠出场名字一样，换一个人
                p2 = random.nextInt(daxias.length);
            }
            System.out.println("在擂台" + arenaName + "上   大侠" + daxias[p1] + " VS " + daxias[p2]);
            //多个线程，将调用redis取出同一个变量，不需要加锁，因为redis底层的increase操作时原子性的
            jedis.incr(redisKey);
        }
    }
}
```

## 1.3.保存对象到redis中
```

package redis.string;

import com.google.gson.Gson;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

/**
 * Describe: 保存Product对象到redis中
 */
public class ProductService {

    @Test
    public void saveProduct2Redis() throws Exception {
        //初始化刘德华的基本信息
        Person person = new Person("刘德华", 17);
        //将刘德华的信息保存到Redis中
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        //直接保存对象的toString方法，这种方法不反序列化对象
        jedis.set("user:liudehua:str", person.toString());
        System.out.println(jedis.get("user:liudehua:str"));

        //保存序列化之后的对象
        jedis.set("user:liudehua:obj".getBytes(), getBytesByProduct(person));
        byte[] productBytes = jedis.get("user:liudehua:obj".getBytes());
        Person pByte = getProductByBytes(productBytes);
        System.out.println(pByte.getName()+"  " +pByte.getAge());

        //保存Json化之后的对象
        jedis.set("user:liudehua:json", new Gson().toJson(person));
        String personJson = jedis.get("user:liudehua:json");
        Person pjson = new Gson().fromJson(personJson, Person.class);
        System.out.println(pjson.getName()+"  "+ pjson.getAge());


    }

    /**
     * 从字节数组中读取Java对象
     *
     * @param productBytes
     * @return
     * @throws Exception
     */
    public Person getProductByBytes(byte[] productBytes) throws Exception {
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(productBytes);
        //读对象的流
        ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
        return (Person) objectInputStream.readObject();
    }

    /**
     * 将对象转化成Byte数组
     *
     * @param product
     * @return
     * @throws Exception
     */
    public byte[] getBytesByProduct(Person product) throws Exception {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        //能够输出对象的流
        ObjectOutputStream oos = new ObjectOutputStream(ba);
        //写对象
        oos.writeObject(product);
        oos.flush();
        return ba.toByteArray();
    }
}

```
Person

```

package redis.string;
import java.io.Serializable;
/**
 */
public class Person implements Serializable{
    private static final long serialVersionUID = -9012113097419111583L;
    private String name;//姓名
    private int age;//年龄
    public Person() {
    }
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
```

# 2.map
## 2.1.map基本操作
```
package redis.map;

import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Describe: 请补充类描述
 */
public class MapMain {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.del("daxia:jingzhongyue");
        //创建一个对象
        jedis.hset("daxia:jingzhongyue", "姓名", "不为人知");
        jedis.hset("daxia:jingzhongyue", "年龄", "18");
        jedis.hset("daxia:jingzhongyue", "技能", "杀人于无形");

        //打印对象
        Map<String, String> jingzhongyue = jedis.hgetAll("daxia:jingzhongyue");
        System.out.println("hgetAll  大侠的基本信息：");
        for (Map.Entry entry : jingzhongyue.entrySet()) {
                    System.out.println(entry.getKey() + "：-----------------" + entry.getValue());
        }
    
        System.out.println();

        //获取大侠的所有字段信息
        Set<String> fields = jedis.hkeys("daxia:jingzhongyue");
        System.out.println("hkeys  ");
        for (String field : fields) {
            System.out.print(field + "  ");
        }
        System.out.println();
        //获取大侠的所有值的信息
        List<String> values = jedis.hvals("daxia:jingzhongyue");
        System.out.println("hvals " );
        for (String value : values) {
            System.out.print(value + "  ");
        }
        System.out.println();

        //值获取大侠的年龄，进行研究
        String age = jedis.hget("daxia:jingzhongyue", "年龄");
        System.out.println("对大侠的年龄有质疑：" + age);
        //给大侠的年龄增加十岁
        jedis.hincrBy("daxia:jingzhongyue", "年龄", 10);
        System.out.println("经过验核，大侠的实际年龄为：" + jedis.hget("daxia:jingzhongyue", "年龄"));
        System.out.println();

        //删除大侠的姓名
        jedis.hdel("daxia:jingzhongyue", "姓名");
        for (Map.Entry entry : jedis.hgetAll("daxia:jingzhongyue").entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }

    }
}

```

## 2.2.购物车redis<username,map<produceId, num>>
```

package redis.map;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import redis.string.StringMain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Describe: 购物车
 */
public class Cart {
    private Jedis jedis;

    public Cart() {
        jedis = new Jedis("127.0.0.1", 6379);
    }

    public Cart(Jedis jedis) {
        this.jedis = jedis;
    }

    /**
     * 修改购物车中的商品
     *
     * @param userName  用户名
     * @param productId 商品编号
     * @param num       操作商品的数量
     */
    public void updateProduct2Cart(String userName, String productId, int num) {
        jedis.hincrBy("shop:cart:" + userName, productId, num);
    }

    /**
     * 获取用户购物车的商品信息
     */
    public List<Product> getProductsByUserName(String userName) {
        List<Product> products = new ArrayList<Product>();
        Map<String, String> productMap = jedis.hgetAll("shop:cart:" + userName);
        if (productMap == null || productMap.size() == 0) {
            return products;
        }
        for (Map.Entry entry : productMap.entrySet()) {
            Product product = new Product();
            product.setId((String) entry.getKey());//获取用户购物车中商品的编号
            int num = Integer.parseInt((String) entry.getValue());//获取用户购物车中商品的数量
            /*如果商品数量大于0，返回正常的值，如果商品小于0，初始化为0
                                  这样不友好，因为商品数量为0，就应该不显示的，解决方法：
            1.在前台js判断，是否显示
            2.在此处判断，如果数量小于0，就跳出此次循环进入下一次循环
            */
            product.setNum(num > 0 ? num : 0);
            complementOtherField(product);//补全商品的其他信息
            products.add(product);
        }
        return products;
    }

    private void complementOtherField(Product product) {
        String productId = product.getId();
        String productJsonStr = jedis.get("shop:product:" + productId);
        Product productJson = (Product) new Gson().fromJson(productJsonStr, Product.class);
        if (productJson != null) {
            product.setName(productJson.getName());
            product.setPrice(productJson.getPrice());
        }
    }

    public static void main(String[] args) {
        //初始化商品的信息
        initData();
        //创建购物车对象
        Cart cart = new Cart();
        //创建用户
        String userName = "liudehua";
        //往用户购物车中添加商品：Map
        cart.updateProduct2Cart(userName, "1645080454", 10);
        cart.updateProduct2Cart(userName, "1788744384", 1000);
        cart.updateProduct2Cart(userName, "1645139266", -1000);
        //打印当前用户的购物车信息
        List<Product> products = cart.getProductsByUserName(userName);
        for (Product product : products) {
            System.out.println(product);
        }
    }

    private static void initData() {
        System.out.println("========================初始化商品信息===========================");
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        //准备数据
        Product product1 = new Product("1645139266", "战地鳄2015秋冬新款马甲可脱卸帽休闲时尚无袖男士羽绒棉外套马甲", new BigDecimal("168"));
        Product product2 = new Product("1788744384", "天乐时 爸爸装加厚马甲秋冬装中年大码男士加绒马夹中老年坎肩老年人", new BigDecimal("40"));
        Product product3 = new Product("1645080454", "战地鳄2015秋冬新款马甲可脱卸帽休闲时尚无袖男士羽绒棉外套马甲", new BigDecimal("230"));
        //将数据写入到Redis
        jedis.set("shop:product:" + product1.getId(), new Gson().toJson(product1));
        jedis.set("shop:product:" + product2.getId(), new Gson().toJson(product2));
        jedis.set("shop:product:" + product3.getId(), new Gson().toJson(product3));
        //打印所有产品信息
        Set<String> allProductKeys = jedis.keys("shop:product:*"); //获取所有的商品信息，不建议使用正则表达式*，因为耗性能
        for (String key : allProductKeys) {
            String json = jedis.get(key);
            Product product = new Gson().fromJson(json, Product.class);//从字符串中解析出对象
            System.out.println(product);
        }
        System.out.println("========================用户购物车信息如下===========================");

    }
}


```
Produce

```
public class Product {
    private String id;//商品编号
    private String name;//商品名称
    private BigDecimal price;//商品价格
    private int num;//商品数量
    public Product() {
    }
    public Product(String id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    } 
    //...............
} 
```


# 3.list
```

package redis.list;

import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * 天龙八部外传-麦当劳风云lpush/rpush/lrange/linsert/lpop/rpop/ltrim
 */
public class ListMain {
    public static void main(String[] args) {
        //创建一个Redis的客户端
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.del("柜台1");

        //鸠摩智，虚竹，段誉，乔峰 排队买肯德基
        jedis.lpush("柜台1", "乔峰", "段誉", "虚竹", "鸠摩智");//lpush(String key, String... strings)
        for (String name : jedis.lrange("柜台1", 0, -1)) {//lrange(String key, long start, long end)
            System.out.print(name + "  ");
        }
        System.out.println();

        //剧情：新来一个人 王语嫣，插队，到第一名。
        jedis.rpush("柜台1", "王语嫣");
        List<String> list = jedis.lrange("柜台1", 0, -1);
        for (String name : list) {
            System.out.print(name + "  ");
        }
        System.out.println();

        //剧情：鸠摩智很不高兴，正好慕容复来了，说：慕容兄，你插我前面
        jedis.linsert("柜台1", BinaryClient.LIST_POSITION.AFTER, "鸠摩智", "慕容复");//linsert(String key, LIST_POSITION where, String pivot, String value)
        //linsert 的意思是：以left为前面，然后进行插入
        List<String> list1 = jedis.lrange("柜台1", 0, -1);
        for (String name : list1) {
            System.out.print(name + "  ");
        }
        System.out.println();

        //剧情：看到慕容复插队大家很生气，正好阿紫和游坦之。让阿紫和游坦之依次插到虚竹的后面
        jedis.linsert("柜台1", BinaryClient.LIST_POSITION.BEFORE, "虚竹", "阿紫");
        jedis.linsert("柜台1", BinaryClient.LIST_POSITION.BEFORE, "阿紫", "游坦之");
        List<String> list2 = jedis.lrange("柜台1", 0, -1);
        for (String name : list2) {
            System.out.print(name + "  ");
        }
        System.out.println();

        //剧情：插队不文明，为了遏制这种不文明的现象，大决决定打一架。  鸠摩智被打跑了。
        jedis.lpop("柜台1");
        for (String name : jedis.lrange("柜台1", 0, -1)) {
            System.out.print(name + "  ");
        }
        System.out.println();

        //剧情：慕容复一看情况不好，以表哥的身份忽悠王语嫣，把王语嫣打伤。
        jedis.rpop("柜台1");
        for (String name : jedis.lrange("柜台1", 0, -1)) {
            System.out.print(name + "  ");
        }
        System.out.println();

        //剧情：在大家打架的时候，我偷偷插队，买了肯德基。
        jedis.rpush("柜台1", "井中月");
        for (String name : jedis.lrange("柜台1", 0, -1)) {
            System.out.print(name + "  ");
        }
        System.out.println();

        //剧情；等我买了肯德基，慕容复被打跑了
        jedis.lpop("柜台1");
        for (String name : jedis.lrange("柜台1", 0, -1)) {
            System.out.print(name + "  ");
        }
        System.out.println();

        //剧情：星宿老怪 突然来了，把 阿紫和游坦之同时弄走了。
        String result = jedis.ltrim("柜台1", 2, 5);//ltrim(String key, long start, long end)截取其中的一段，剩下的去掉
        if ("OK".equals(result)) {
            for (String name : jedis.lrange("柜台1", 0, -1)) {
                System.out.print(name + "  ");
            }
        }
        System.out.println("");

        //剧情：这时候，乔峰三人发现了我，与我大战三百回合，我全身而退
        String res = jedis.ltrim("柜台1", 0, 2);//.ltrim(String key, long start, long end)
        if ("OK".equals(res)) {
            for (String name : jedis.lrange("柜台1", 0, -1)) {
                System.out.print(name + "  ");
            }
        }
    }
}


```



# 4.set
## 4.1.基本操作
```

package redis.set;

import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * set集合的特点：无序、无重复元素
 * sadd（添加）、sismember（是否存在）、scard（统计总数）、sinter（交集）、sunion（并集）、sdiff（差集）、sdiffstore（差集保存为新的set）
 */
public class SetMain {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        //河南武林人物登记表---杜绝冒名顶替的情况
        String[] daxias = new String[]{"郭靖", "黄蓉", "令狐冲", "杨过", "林冲",
                "鲁智深", "小女龙", "虚竹", "独孤求败", "张三丰", "王重阳", "张无忌"
                , "王重阳", "东方不败", "逍遥子", "乔峰", "虚竹", "段誉"
                , "韦小宝", "王语嫣", "周芷若", "峨眉师太", "慕容复", "郭靖", "乔峰", "王重阳"};

        //创建并设置一个set的值
        jedis.sadd("biwu:dengji", daxias);
        //获取一个set中所有的元素
        Set<String> daxiaSet = jedis.smembers("biwu:dengji");
        for (String name : daxiaSet) {
            System.out.print(name + " ");  //set集合的特点：无序、无重复元素
        }
        System.out.println();


        //判断一个成员是否属于某条指定的set数据
        boolean isComing = jedis.sismember("biwu:dengji", "井中月"); //判断大侠井中月是否到来
        if (!isComing) {
            System.out.println("大侠 井中月尚未登记.");
        }
        //计算一个set中有多少元素
        long totalNum = jedis.scard("biwu:dengji");
        System.out.println("有" + totalNum + " 位大侠已经登记了！");
        System.out.println();



        //大侠井中月没有来，是因为报名参与另外一个会议 国际武林大会
        String[] daxiaArr = new String[]{"王语嫣", "周芷若", "峨眉师太", "慕容复","郭靖", "乔峰", "井中月"};
        jedis.sadd("guoji:dengji", daxiaArr); //国际武林大会登记表
        Set<String> xindaxias = jedis.smembers("guoji:dengji");
        for (String name : xindaxias) {
            System.out.print(name + "--- ");  //集合的特点：无序、无重复元素
        }
        System.out.println();


        //计算两个Set之间的交集
        Set<String> users = jedis.sinter("biwu:dengji", "guoji:dengji");
        for (String name : users) {
            System.out.print(name + " ");
        }
        System.out.println();


        //计算两个Set之间的并集
        users = jedis.sunion("biwu:dengji", "guoji:dengji");
        for (String name : users) {
            System.out.print(name + " ");
        }
        System.out.println();
        System.out.println("井中月出来了");


        //计算两个集合的差集
        users = jedis.sdiff("biwu:dengji", "guoji:dengji");
        for (String name : users) {
            System.out.print(name + " ");
        }
        System.out.println();

        System.out.println();
        //将两个集合计算出来的差集保存起来，升级为超级Vip
        jedis.sdiffstore("vipdaxia","biwu:dengji", "guoji:dengji");
        for (String name : jedis.smembers("vipdaxia")) {
            System.out.print(name + " ");
        }
    }
}


```

## 4.2.实例(浏览用户、下单、支付之间的转化率)
```
package redis.set;

import redis.clients.jedis.Jedis;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Set;

/**
 * 浏览某商品的用户---->下单用户------->支付用户 
 * 1.求浏览并下单的用户
 * 2.计算浏览某商品的用户数量 和 既浏览又下单的用户的数量
 * 3.浏览并且下单的用户，最终支付的转化
 * 4.浏览并最终支付的用户的转化
 */
public class Transform {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        //浏览某商品的用户
        jedis.sadd("viewUsers", "郭靖", "黄蓉", "令狐冲", "杨过", "林冲",
                "鲁智深", "小女龙", "虚竹", "独孤求败", "张三丰", "王重阳", "张无忌"
                , "王重阳", "东方不败", "逍遥子", "乔峰", "虚竹", "段誉");

        //下单用户
        jedis.sadd("orderUsers", "郭靖", "黄蓉", "令狐冲", "杨过", "林冲",
                "鲁智深", "小女龙", "虚竹", "独孤求败", "乔峰", "虚竹", "段誉");
        //支付用户
        jedis.sadd("paymentUsers", "郭靖", "黄蓉", "令狐冲", "杨过", "独孤求败", "段誉");

        //浏览过商品的用户，有哪些下单了。
        jedis.sinterstore("view2order", "viewUsers", "orderUsers"); //求两个集合的交集


        //计算浏览某商品的用户数量 和 既浏览又下单的用户的数量
        double viewUserNum = jedis.scard("viewUsers");
        double orderUserNum = jedis.scard("view2order");
        NumberFormat formatter = new DecimalFormat("0.00");
        Double x = new Double(orderUserNum / viewUserNum);
        System.out.print("订单" + orderUserNum + "/浏览" + viewUserNum + "转化：" + formatter.format(x) + "     他们是：");
        for (String name : jedis.smembers("view2order")) {
            System.out.print(name + "  ");
        }
        System.out.println();


        //浏览并且下单的用户，最终支付的转化
        jedis.sinterstore("order2Payment", "view2order", "paymentUsers"); //求两个集合的交集
        double paymentUserNum = jedis.scard("paymentUsers");
        x = new Double(paymentUserNum / orderUserNum);
        System.out.print("支付" + paymentUserNum + "/订单" + orderUserNum + "转化：" + formatter.format(x) + "     他们是：");
        for (String name : jedis.smembers("order2Payment")) {
            System.out.print(name + "  ");
        }
        System.out.println();
        //浏览并最终支付的用户的转化
        x = new Double(paymentUserNum / viewUserNum);
        System.out.print("支付" + paymentUserNum + "/浏览" + viewUserNum + "转化：" + formatter.format(x)+"    他们是：");
        for (String name : jedis.smembers("order2Payment")) {
            System.out.print(name + "  ");
        }
        System.out.println();
    }
}


```

# 5.有序set
## 5.1.基本操作
```
package redis.sortSet;

import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * sortedset
 * zadd、zrange（排序）、zrevrange(翻转排序)、zrank（排名）、zscore（分数比重）
 * member	score	rank
 * 张三		88		3
 * 李四		99		2
 * 王五		77		4
 * 赵六		100		1
 */
public class SortMain {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        //往redis库中插入一条sortedset数据
        jedis.zadd("比武成绩", 10, "乔峰");//zadd(String key, double score, String member)
        jedis.zadd("比武成绩", 5, "王重阳");
        jedis.zadd("比武成绩", 7, "虚竹");
        jedis.zadd("比武成绩", 2, "王语嫣");
        jedis.zadd("比武成绩", 5, "段誉");
        jedis.zadd("比武成绩", 4, "峨眉师太");
        jedis.zadd("比武成绩", 20, "张三丰");
        //获取sortSet中所有的元素
        Set<String> names = jedis.zrange("比武成绩", 0, -1);
        for (String name : names) {
            System.out.println(name + "        排名： "
                    //打印用户升序排行
                    + jedis.zrank("比武成绩", name) + "           赢的场次： "
                    //打印用户的比武成绩
                    + jedis.zscore("比武成绩", name));
        }
        System.out.println("==============================");

        names = jedis.zrevrange("比武成绩", 0, -1);
        for (String name : names) {
            System.out.println(name + "         "
                    + jedis.zrevrank("比武成绩", name) + "            "
                    + jedis.zscore("比武成绩", name));
        }
        System.out.println("==============================");

        //修改用户的分数
        jedis.zincrby("比武成绩",100,"王语嫣");    //zincrby(String key, double score, String member)
        names = jedis.zrevrange("比武成绩", 0, -1);
        for (String name : names) {
            System.out.println(name + "         "
                    + jedis.zrevrank("比武成绩", name) + "            "
                    + jedis.zscore("比武成绩", name));
        }
    }
}

```

## 5.2.示例
```
package redis.sortSet;

import redis.clients.jedis.Jedis;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 各种排行榜（bang)
 * 其实就是向有序的set中添加<member,score>，在有序set中按照score对member进行排序
 */
public class Bang {

    public static void main(String[] args) {
        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        //创建销售线程-销售商品
        executorService.submit(new Sale());
        executorService.submit(new Sale());
        //创建报表线程-周期型计算排行榜
        executorService.submit(new BangView());
    }
}

class Sale implements Runnable {
    //店铺销售排行榜
    private static final String amountBang = "tmall:amountBang";
    //店铺订单排行榜
    private static final String orderBang = "tmall:orderBang";
    //店铺名称
    private static final String[] shops = new String[]{"小米", "华为", "魅族", "苹果", "联想", "奇酷", "中兴", "一加", "oppo"};
    //Redis客户端
    private Jedis jedis = new Jedis("127.0.0.1", 6379);
    //随机获取店铺
    private Random random = new Random();
    //随机计算价格
    private Random priceRandom = new Random();

    public void run() {
        while (true) {
            try {
                int shopIndex = random.nextInt(shops.length);
                //将店铺销售的有序set中的某个商品的score自增
                /* member	score
                 * 小米		1500	
                 * 华为
                 * 魅族
                 * */
                jedis.zincrby(amountBang, priceRandom.nextInt(2500), shops[shopIndex]);
                jedis.zincrby(orderBang, 1, shops[shopIndex]);

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }

}

class BangView implements Runnable {
    //店铺销售排行榜
    private static final String amountBang = "tmall:amountBang";
    //店铺订单排行榜
    private static final String orderBang = "tmall:orderBang";
    //Redis客户端
    private Jedis jedis = new Jedis("127.0.0.1", 6379);

    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                System.out.println("==============店铺销售额排行==============================");
                Set<String> names = jedis.zrevrange(amountBang, 0, 4);
                for (String name : names) {
                    System.out.println(name + "         "
                            + jedis.zrevrank(amountBang, name) + "            "
                            + jedis.zscore(amountBang, name));
                }
                System.out.println("==============店铺订单量排行==============================");
                names = jedis.zrevrange(orderBang, 0, 1);
                for (String name : names) {
                    System.out.println(name + "         "
                            + jedis.zrevrank(orderBang, name) + "            "
                            + jedis.zscore(orderBang, name));
                }

                System.out.println();
                System.out.println();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}


```

# 6.连接池
## 6.1.简单使用

```
package redis.other;
import redis.clients.jedis.Jedis;
/**
 * Describe: 请补充类描述
 */
public class RedisQuickStart {
    public static void main(String[] args) {
        // 根据redis主机和端口号实例化Jedis对象
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        // 添加key-value对象，如果key对象存在就覆盖该对象
        jedis.set("name", "maoxiangyi");
        // 查取key的value值，如果key不存在返回null
        String name = jedis.get("name");
        String company = jedis.get("company");
        System.out.println(company+":"+name);
        // 删除key-value对象，如果key不存在则忽略此操作
        jedis.del("name");
        // 判断key是否存在，不存在返回false存在返回true
        jedis.exists("name");
    }
}

```

## 6.2.JedisPool
```
package redis.other;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Describe: 请补充类描述
 */
public class MyJedisPool {
    // jedis池
    public static JedisPool pool;

    // 静态代码初始化池配置
    static {
        //change "maxActive" -> "maxTotal" and "maxWait" -> "maxWaitMillis" in all examples
        JedisPoolConfig config = new JedisPoolConfig();
        //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
        config.setMaxIdle(5);
        //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
        //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
        //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
        config.setMaxTotal(1000 * 100);
        //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
        config.setMaxWaitMillis(5);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        try {
            //如果你遇到 java.net.SocketTimeoutException: Read timed out exception的异常信息
            //请尝试在构造JedisPool的时候设置自己的超时值. JedisPool默认的超时时间是2秒(单位毫秒)
            pool = new JedisPool(config, "127.0.0.1", 6379, 20);
        } catch (Exception e) {
            throw new RuntimeException("redis 连接池初始化失败！");
        }
    }

    public static void main(String[] args) {
        // 从jedis池中获取一个jedis实例
        Jedis jedis = MyJedisPool.pool.getResource();
        // 添加key-value对象，如果key对象存在就覆盖该对象
        jedis.set("name", "maoxiangyi");
        jedis.set("company", "aaa");
        // 查取key的value值，如果key不存在返回null
        String name = jedis.get("name");
        String company = jedis.get("company");
        System.out.println(company + ":" + name);
        // 删除key-value对象，如果key不存在则忽略此操作
        jedis.del("name");
        // 判断key是否存在，不存在返回false存在返回true
        jedis.exists("name");
        //关闭jedis链接，自动回收
        jedis.close();
    }
}


```


## 6.3.ShardedJedisPool
```

package redis.other;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.LinkedList;
import java.util.List;

/**
 * Describe: 请补充类描述
 */
public class MyShardedJedisPool {

    private static ShardedJedisPool shardedJedisPool;

    // 静态代码初始化池配置
    static {
        //change "maxActive" -> "maxTotal" and "maxWait" -> "maxWaitMillis" in all examples
        JedisPoolConfig config = new JedisPoolConfig();
        //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
        config.setMaxIdle(5);
        //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
        //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
        //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
        config.setMaxTotal(1000 * 100);
        //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
        config.setMaxWaitMillis(5);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        //创建四个redis服务实例，并封装在list中
        List<JedisShardInfo> list = new LinkedList<JedisShardInfo>();
        list.add(new JedisShardInfo("127.0.0.1", 6379));
        list.add(new JedisShardInfo("127.0.0.1", 6380));
        list.add(new JedisShardInfo("127.0.0.1", 6381));
        list.add(new JedisShardInfo("127.0.0.1", 6382));
        //创建具有分片功能的的Jedis连接池
        shardedJedisPool = new ShardedJedisPool(config, list);
    }

    public static ShardedJedisPool getShardedJedisPool() {
        return shardedJedisPool;
    }

    public static void main(String[] args) {
        ShardedJedis jedis = MyShardedJedisPool.getShardedJedisPool().getResource();
        jedis.set("1", "maoxiangyi");
    }
}


```

