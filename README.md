<img src="passageImg/icon.png" align="right" />

# MyDNS
> 一款自实现的DNS服务器

采用Java语言，基于NIO通信模式，结合MySQL、Redis、RabbitMQ等技术栈编写的一款DNS服务器。

实现了DNS数据状态监控、DNS解析配置管理等功能。

结合数据可视化模式，使服务状态数据更清晰、间接、直观地展现在管理者面前。

## 安装部署

### 必要软件

* Java编程环境、工具IDEA
* 数据库 MySQL
* 缓存中间件 Redis
* 消息中间件 RabbitMQ

### 安装步骤

* clone 该项目到本地
* IDEA引入并装载此文件，引入完毕对应的依赖包(pom.xml)
* 修改主配置文件 application.yml，将对应项目软件用户名、密码修改为自己主机持有的
* 配置完毕开启运行

**注意： 如果采用的是云服务器上的软件，需要进行安全组配置同时防火墙开放部分端口！**

## 测试

### 本地测试工具测试

使用本地工具 `nslookup` 进行测试，windows环境下直接打开cmd输入 `nslookup`命令即可使用。

![image-20210601122548723](passageImg/readme.assets/image-20210601122548723.png)

![image-20210601123054751](../passageImg/readme.assets/image-20210601123054751.png)

### 浏览器在线测试

![image-20210601123226026](passageImg/readme.assets/image-20210601123226026.png)

![image-20210601123316067](../passageImg/readme.assets/image-20210601123316067.png)

知乎能够访问，因此数据库中配置规则并没有配置  www.zhihu.com 的映射，会访问更上层的DNS服务器；

![image-20210601123517290](passageImg/readme.assets/image-20210601123517290.png)

百度无法访问，因为数据库中配置 www.baidu.com 映射的IP地址为 192.168.111

## 界面截图

### DNS监控

![image-20210601123747790](../passageImg/readme.assets/image-20210601123747790.png)

### DNS域名映射管理

![image-20210601124009996](passageImg/readme.assets/image-20210601124009996.png)

![image-20210601124144521](../passageImg/readme.assets/image-20210601124144521.png)

### 使用向导

![image-20210601124053948](passageImg/readme.assets/image-20210601124053948.png)

## 作者

#### iStitches

## 版权说明

[![CC0](https://licensebuttons.net/p/zero/1.0/88x31.png)](https://creativecommons.org/publicdomain/zero/1.0/)

该项目签署了 MIT 授权许可，只可用于学习交流。

## 鸣谢

该项目参考了 [DNSPod](https://gitee.com/DNSPod) 的  [dnspod-sr](https://gitee.com/DNSPod/dnspod-sr?_from=gitee_search)

参考了作者 [黄勇萍](https://kns.cnki.net/kcms/detail/detail.aspx?dbcode=CJFD&dbname=CJFD2012&filename=DNXJ201201006&v=%25mmd2BVBjzOYK7%25mmd2BnX4ZLOa9V9dFT78%25mmd2FNlsBlSAUQiZzvV8V0EuNGALQPL20GLhDMbfPmW)的论文 **根据客户网络应答的DNS服务器设计与实现**，文章编号 **10.19414/j.cnki.1005-1228.2012.01.005**