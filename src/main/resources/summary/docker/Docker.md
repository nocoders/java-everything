# Docker

## Docker常用命令

### 命令参考链接

https://www.runoob.com/docker/docker-command-manual.html

https://zhuanlan.zhihu.com/p/98982041

### 相关命令

#### 生命周期管理命令

##### Docker查看版本

```
docker version
```

##### Docker启动命令

```sh
systemctl start docker
```

##### 列出所有容器信息

-a:显示所有容器，包括未运行的

```sh
docker ps [OPTIONS]
```

##### 关闭指定容器

```
docker stop redis
```

##### 关闭所有容器

```
docker stop $(docker ps -a | awk '{ print $1}' | tail -n +2)
```

##### 删除容器

```
# -v 删除卷，-f 强制删除
docker rm redis
# 删除所有容器
docker rm `docker ps -a -q`
```

##### 移除镜像

```
docker rmi 镜像id
```

##### Docker是否安装某个镜像

```sh
docker images |grep redis
```

#### Compose命令

**删除无效数据卷**

```
docker volume prune
```

**查看日志**

```
docker-compose  logs -f
```

**重启某个服务**

```
 docker-compose -f ./docker-compose.yaml restart mall4cloud-nacos
```



## Docker安装步骤

### 官方网址

https://docs.docker.com/engine/install/centos/

### 服务器原有Docker卸载

```shell
sudo yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine
```

### 设置当前仓库

```sh
sudo yum install -y yum-utils
sudo yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo
```

### 安装稳定版本Docker引擎

#### 查看存储库中可用版本引擎

```sh
yum list docker-ce --showduplicates | sort -r
```

#### 选择指定版本安装

该软件包名称是软件包名称（`docker-ce`）加上版本字符串（第二列），从第一个冒号（`:`）到第一个连字符，以连字符（`-`）分隔。例如，`docker-ce-18.09.1`。

```sh
sudo yum install docker-ce-<VERSION_STRING> docker-ce-cli-<VERSION_STRING> containerd.io
```

### 启动Docker

```sh
sudo systemctl start docker
```

### hello world

```
sudo docker run hello-world
```

### 安装Docker Compose

```
# 下载安装
sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
# 给与可执行权限
sudo chmod +x /usr/local/bin/docker-compose
# 测试安装
docker-compose --version
```

## Redis安装相关

### Docker安装Redis

#### 查看并删除原有Redis

##### 查看Docker中所有的容器

```
docker ps -a
```

##### 查找并删除Redis容器

查找docker安装的容器

```
docker images
```

根据 IMAGE ID 删除容器

```
docker rmi bc8d70f9ef6c
```

#### 单Redis安装

[Docker安装Redis](https://www.runoob.com/docker/docker-install-redis.html)

[最详细的docker中安装并配置redis_TrueDei-CSDN博客_docker 安装redis](https://blog.csdn.net/qq_17623363/article/details/106418353)

##### 查看可用的Redis版本

```
docker search redis
```

##### 拉取指定版本的Redis

访问 https://hub.docker.com/_/redis?tab=tags，查找指定版本redis，复制下载命令，在服务器上执行

```
docker pull redis:6.2.3-buster
```

##### 查看Redis是否安装成功

```
docker images redis
```

##### Redis配置文件下载及配置

###### 下载

在Redis中文官方网站<http://www.redis.cn/download.html>上下载指定Redis，拿到他的redis.conf。

```
wget http://download.redis.io/redis-stable/redis.conf
```

###### 配置

```
bind 127.0.0.1 #注释掉这部分，使redis可以外部访问
daemonize no#用守护线程的方式启动
requirepass 你的密码#给redis设置密码
appendonly yes#redis持久化　　默认是no
tcp-keepalive 300 #防止出现远程主机强迫关闭了一个现有的连接的错误 默认是300
```

###### 创建映射目录

创建Docker本地存放位置

```
mkdir /data/redis
mkdir /data/redis/data
```

将配置文件放到 /date/redis文件夹下

##### Docker启动Redis

```
docker run -p 6379:6379 --name redis -v /data/redis/redis.conf:/etc/redis/redis.conf  -v /data/redis/data:/data -d redis redis-server /etc/redis/redis.conf --appendonly yes
```

参数解释：

```
-p 6379:6379:把容器内的6379端口映射到宿主机6379端口
-v /data/redis/redis.conf:/etc/redis/redis.conf：把宿主机配置好的redis.conf放到容器内的这个位置中
-v /data/redis/data:/data：把redis持久化的数据在宿主机内显示，做数据备份
redis-server /etc/redis/redis.conf：这个是关键配置，让redis不是无配置启动，而是按照这个redis.conf的配置启动
–appendonly yes：redis启动后数据持久化
-- name redis 给容器命名
```

##### 查看启动是否成功

```
docker ps
```

##### 查看日志

```
docker logs redis
```

##### 进入Redis

无密码直接进入

```
docker exec -it 012233bc52dc redis-cli
```

有密码可以先进入容器内部，再登录redis-cli

```
docker exec -it redis-6379 /bin/bash
redis-cli -h host -p port -a password
```

##### 使用Docker Compose配置单Redis

```sh
创建yml文件
vim docker-compose.yml
配置yml文件
vi /usr/local/bin/docker-compose.yml
# 文件内容
version: '3.7'
services:
         redis:
                image: "redis:latest"
                container_name: redis
                restart: always
                command: redis-server /data/redis/redis.conf --appendonly yes
                ports:
                        - "6379:6379"
                volumes:
                        - ./data:/data/redis/data
                        - ./redis.conf:/data/redis/redis.conf
# 启动compose 
docker-compose up -d
# 关闭compose
docker-compose down
```

#### Redis主从复制

参考链接

[跟着软信学redis5在docker环境搭建redis集群主从复制](https://www.bilibili.com/video/BV1VJ411r7am?from=search&seid=10614932455614538638)

##### Redis配置文件下载及配置

###### 下载

在Redis中文官方网站<http://www.redis.cn/download.html>上下载指定Redis，拿到他的redis.conf。

###### 配置

```
bind 127.0.0.1 #注释掉这部分，使redis可以外部访问
daemonize no#用守护线程的方式启动
requirepass 你的密码#给redis设置密码
appendonly yes#redis持久化　　默认是no
tcp-keepalive 300 #防止出现远程主机强迫关闭了一个现有的连接的错误 默认是300
```

###### 创建映射目录

创建Redis集群存放位置，复制原有配置文件以及data存放位置

```
cd /data/redis/ && mkdir cluster && cd cluster/ && mkdir conf && cd conf
cp /data/redis/redis.conf ./redis-6380.conf 
cp /data/redis/redis.conf ./redis-6381.conf 
cp /data/redis/redis.conf ./redis-6382.conf 
cd .. && mkdir data && cd data && mkdir data-6380 data-6381 data-6382
```

修改 配置文件中的 port，以及给两个从机配置复制命令，以6380端口为master

```
replicaof <masterip> <masterport>
replicaof 192.168.73.146 6380
```

##### redis镜像运行

使用Docker启动3个Redis容器服务，分别使用6380,6381,6382端口

```
docker run --name redis-6380 -p 6380:6380 -v /data/redis/cluster/conf/redis-6380.conf:/redis.conf -v /data/redis/cluster/data-6380:/data -d redis redis-server /redis.conf --appendonly yes
docker run --name redis-6381 -p 6381:6381 -v /data/redis/cluster/conf/redis-6381.conf:/redis.conf -v /data/redis/cluster/data-6381:/data -d redis redis-server /redis.conf --appendonly yes
docker run --name redis-6382 -p 6382:6382 -v /data/redis/cluster/conf/redis-6382.conf:/redis.conf -v /data/redis/cluster/data-6382:/data -d redis redis-server /redis.conf --appendonly yes
```

##### 登录Redis查看主从

进入容器内部，查看当前Redis角色	

```
[root@localhost redis]# docker exec -it redis-6380 redis-cli -p 6380
127.0.0.1:6379> info replication
```

#### Redis哨兵

基于主从复制，给主节点设置哨兵监控，当主节点宕机后，选举一个从节点作为主节点。

##### 参考链接

[跟着软信学redis5之docker环境下redis主从复制+sentinel哨兵模式](https://www.bilibili.com/video/BV1wJ411t7Fs?p=4)
[docker 下快速部署Redis哨兵集群](https://www.jianshu.com/p/cb63401cdfdf)

##### 配置文件

从Redis官网下载哨兵配置文件

```
wget http://download.redis.io/redis-stable/sentinel.conf
```

修改配置

```
# 日志路径
logfile "/data/redis/cluster/logs/sentinel.log"
# 格式：sentinel monitor <master-name> <ip> <redis-port> <quorum>
# master-name是为这个被监控的master起的名字
# ip是被监控的master的IP或主机名。因为Docker容器之间可以使用容器名访问，所以这里写master节点的容器名
# redis-port是被监控节点所监听的端口号
# quorum设定了当几个哨兵判定这个节点失效后，才认为这个节点真的失效了
sentinel monitor mymaster 192.168.73.146  6380 
port 26380
# 连接主节点的密码
# 格式：sentinel auth-pass <master-name> <password>
sentinel auth-pass local-master 123456

# master在连续多长时间无法响应PING指令后，就会主观判定节点下线，默认是30秒
sentinel down-after-milliseconds local-master 30000
#后台执行
daemonize yes
```

启动主节点哨兵服务

```
docker run --name redis-26380 -p 26380:26380 -v /data/redis/cluster/conf/sentinel.conf:/sentinel.conf -v /data/redis/cluster/data-26380:/data redis redis-sentinel /sentinel.conf
```

#### Docker Compose 哨兵集群

##### 参考链接

https://blog.csdn.net/jtbrian/article/details/53691540

https://www.jianshu.com/p/cb63401cdfdf

##### 文件夹创建配置文件下载配置

创建文件夹

```
cd /data/redis && mkdir compose-cluster && cd compose-cluster && mkdir data && mkdir conf
# 创建data文件夹
cd data/ && mkdir data-6390 && mkdir data-6391 && mkdir data-6392 && mkdir data-26390
```

配置主节点redis-6390.conf

```
port 6390
bind 127.0.0.1 #注释掉这部分，使redis可以外部访问
daemonize no#用守护线程的方式启动
requirepass redis #给redis设置密码
appendonly yes#redis持久化　　默认是no
tcp-keepalive 300 #防止出现远程主机强迫关闭了一个现有的连接的错误 默认是300
```

配置子节点redis-6391.conf

```
port 6390
bind 127.0.0.1 #注释掉这部分，使redis可以外部访问
daemonize no#用守护线程的方式启动
requirepass redis #给redis设置密码
appendonly yes#redis持久化　　默认是no
tcp-keepalive 300 #防止出现远程主机强迫关闭了一个现有的连接的错误 默认是300
replicaof 192.168.73.146 6390 # 配置主节点
masterauth redis # 配置主节点密码
```

配置子节点redis-6392.conf

```
port 6392
bind 127.0.0.1 #注释掉这部分，使redis可以外部访问
daemonize no#用守护线程的方式启动
requirepass redis #给redis设置密码
appendonly yes#redis持久化　　默认是no
tcp-keepalive 300 #防止出现远程主机强迫关闭了一个现有的连接的错误 默认是300
replicaof 192.168.73.146 6390 # 配置主节点
masterauth redis # 配置主节点密码
```

配置哨兵文件

```
vi sentinel1.conf
# 哨兵1文件配置
port 26390
dir "/data"
sentinel auth-pass master redis
sentinel monitor master 192.168.73.146 6390 2
sentinel down-after-milliseconds master 5000
sentinel failover-timeout master 5000
sentinel parallel-syncs master 1
# 文件权限修改
chmod 777 sentinel1.conf
# 哨兵2文件配置
dir "/data"
port 26391
sentinel auth-pass master redis
sentinel monitor master 192.168.73.146 6390 2
sentinel down-after-milliseconds master 5000
sentinel failover-timeout master 5000
sentinel parallel-syncs master 1
# 哨兵3文件配置

dir "/data"
port 26392
sentinel auth-pass master redis
sentinel monitor master 192.168.73.146 6390 2
sentinel down-after-milliseconds master 5000
sentinel failover-timeout master 5000
sentinel parallel-syncs master 1
```

创建 /home/redis-sentinel-cluster/docker-compose.yml

```
version: '3.7'
services:
  master:
    image: "redis"
    container_name: redis-6390
    command: redis-server /redis.conf --appendonly yes
    restart: always
    ports:
                        - 6390:6390
    volumes:
                        - /data/redis/compose-cluster/data/data-6390:/data
                        - /data/redis/compose-cluster/conf/redis-6390.conf:/redis.conf
  slave1:
    image: "redis"
    container_name: redis-6391
    restart: always
    command: redis-server /redis.conf --appendonly yes
    ports:
                        - 6391:6391
    volumes:
                        - /data/redis/compose-cluster/data/data-6391:/data
                        - /data/redis/compose-cluster/conf/redis-6391.conf:/redis.conf
  slave2:
    image: "redis"
    container_name: redis-6392
    restart: always
    command: redis-server /redis.conf --appendonly yes
    ports:
                        - 6392:6392
    volumes:
                        - /data/redis/compose-cluster/data/data-6392:/data
                        - /data/redis/compose-cluster/conf/redis-6392.conf:/redis.conf
  sentinel1:
    image: "redis"
    container_name: sentinel1
    restart: always
    command: redis-sentinel /sentinel.conf
    ports:
                        - 26390:26390
    volumes:
                        - /data/redis/compose-cluster/conf/sentinel1.conf:/sentinel.conf
                        - /data/redis/compose-cluster/data/data-26390:/data
  sentinel2:
    image: "redis"
    container_name: sentinel2
    restart: always
    command: redis-sentinel /sentinel.conf
    ports:
                        - 26391:26391
    volumes:
                        - /data/redis/compose-cluster/conf/sentinel2.conf:/sentinel.conf
                        - /data/redis/compose-cluster/data/data-26391:/data
  sentinel3:
    image: "redis"
    container_name: sentinel3
    restart: always
    command: redis-sentinel /sentinel.conf
    ports:
                        - 26392:26392
    volumes:
                        - /data/redis/compose-cluster/conf/sentinel3.conf:/sentinel.conf
                        - /data/redis/compose-cluster/data/data-26392:/data

```

##### 集群启动

```
# 集群启动
docker-compose up -d
#集群关闭
docker-compose down
```

##### 登录redis查看配置是否成功

```
# 查看启动的容器
docker ps
# 进入redis
docker exec -it redis-6391 redis-cli -p 6391
# 登录
auth redis
# 查看角色
role
info replication
# 进入哨兵
    docker exec -it sentinel1 redis-cli  -p 26390
# master 状态
sentinel masters
# slave 状态，master是配置的名称。
sentinel slaves master
```

#### Docker Compose 配置Redis Cluster

## Minio 安装

```
docker run -p 9000:9000 \
 --net=host \
 --name minio \
 -d --restart=always \
 -e "MINIO_ACCESS_KEY=minio" \
 -e "MINIO_SECRET_KEY=minio@2021" \
 -v /mnt/data/minio/data:/mnt/data/minio/data \
 -v /mnt/data/minio/config:/mnt/data/minio/config \
 minio/minio server \
 /data 
```



## RabbitMq 安装

### 问题

#### 镜像下载太慢

[docker镜像拉取太慢](https://help.aliyun.com/document_detail/60750.html?spm=5176.21213303.J_6704733920.17.6bc53edaF1SZuS&scm=20140722.S_help%40%40%E6%96%87%E6%A1%A3%40%4060750.S_os%2Bhot.ID_60750-RL_Docker%E5%AE%98%E6%96%B9%E9%95%9C%E5%83%8F%E5%8A%A0%E9%80%9F%E5%99%A8-OR_helpmain-V_2-P0_2)

### 单机版

#### 参考链接

[docker安装RabbitMQ](https://blog.csdn.net/qq_34775355/article/details/108305396)

#### 安装步骤

1. 

## MySql安装

### 日常命令

```
登录mysql
docker exec -it 02f00b50c526 bash
密码修改
alter user 'root'@'localhost' identified by 'root';
设置远程可连接
update user set host='%' where user ='root';
FLUSH PRIVILEGES;
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%'WITH GRANT OPTION;
flush privileges;
```



### 参考链接

1. [使用docker-compose的方式部署mysql](https://zhuanlan.zhihu.com/p/384330120)
