# Mysql环境搭建

## 读写分离环境搭建

电脑：MAC M1芯片

docker版本：Docker desktop 4.16.2

docker服务版本：20.10.22

Mysql版本：8.0

### 历史镜像数据删除

为什么要删除呢，因为我之前使用docker-compose搭建，在容器内命令一直连接不上数据库，然后我就用docker命令搭建，结果navicat连不上，报错sysytem error 35，搞了好久没搞定，后来想，算了，直接删除全部镜像跟数据卷，重新搭建吧，结果就可以了

```
停止所有的容器
docker stop $(docker ps -a -q) 或者 docker stop $(docker ps -aq) 
查看所有容器
docker ps -a
删除所有容器
docker rm $(docker ps -a -q) 或者 docker rm $(docker ps -aq) 
删除数据卷
docker volume rm $(docker volume ls -q)
```

### 主服务器创建

1. 镜像下载

   ```
   docker pull mysql:8.0
   ```

2. 创建容器

   ```
   -- 指定
   docker run -d \
   -p 3306:3306 \
   -v /Users/linmeng/service/mysqlCluster/mysql-master/conf:/etc/mysql/conf.d \
   -v /Users/linmeng/service/mysqlCluster/mysql-master/data:/var/lib/mysql \
   -v /Users/linmeng/service/mysqlCluster/mysql-slave2/mysql-files:/var/lib/mysql-files/ \
   -e MYSQL_ROOT_PASSWORD=root \
   --name mysql-master \
   mysql:8.0
   ```

3. 添加配置文件

   在conf文件夹下添加配置文件my.cnf

   ```
   [mysqld]
   # 服务端使用的字符集默认为utf8mb4
   character-set-server=utf8mb4
   # 服务端使用的排序规则
   collation-server=utf8mb4_general_ci
   # 创建新表时将使用的默认存储引擎
   default-storage-engine=INNODB
   # 默认使用“mysql_native_password”插件认证
   # mysql 8.0 需要设置 mysql_native_password
   default_authentication_plugin=mysql_native_password
   # 关闭 only_full_group_by
   sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION
   
   # mysql 时区
   default-time-zone = '+8:00'
   
   # 最大连接数量
   max_connections=10000
   
   # 不再进行反解析（ip不反解成域名），这样可以加快数据库的反应时间。
   skip-name-resolve
   
   # binlog 日志存放路径
   log-bin=mysql-binlog
   # 日志中记录每一行数据被修改的形式
   binlog-format=STATEMENT
   # 当前机器的服务 ID, 如果为集群时不能重复，不要和 canal 的 slaveId 重复
   server_id=1
   
   [mysql]
   # 设置mysql客户端默认字符集
   default-character-set=utf8mb4
   ```

   `binlog格式说明：`

   - binlog_format=STATEMENT：日志记录的是主机数据库的`写指令`，性能高，但是now()之类的函数以及获取系统参数的操作会出现主从数据不同步的问题。
   - binlog_format=ROW（默认）：日志记录的是主机数据库的`写后的数据`，批量操作时性能较差，解决now()或者  user()或者  @@hostname 等操作在主从机器上不一致的问题。
   - binlog_format=MIXED：是以上两种level的混合使用，有函数用ROW，没函数用STATEMENT，但是无法识别系统变量

   

4. 重启docker服务

   ```
   docker restart mysql-master
   ```

### 从服务器访问账号开通

```
-- slave用户创建
CREATE USER 'slave'@'%';
-- 密码设置
ALTER USER 'slave'@'%' IDENTIFIED WITH mysql_native_password BY 'root';
--授予复制权限
GRANT REPLICATION SLAVE ON *.* TO 'slave'@'%';
--刷新权限
FLUSH PRIVILEGES;
```

### 从服务器创建

1. 安装并3307 3308端口启动从服务器

   ```
   # 从服务器
   docker run -d \
   -p 3307:3306 \
   -v /Users/linmeng/service/mysqlCluster/mysql-slave1/conf:/etc/mysql/conf.d \
   -v /Users/linmeng/service/mysqlCluster/mysql-slave1/data:/var/lib/mysql \
   -v /Users/linmeng/service/mysqlCluster/mysql-slave1/mysql-files:/var/lib/mysql-files \
   -e MYSQL_ROOT_PASSWORD=root \
   --name mysql-slave1 \
   mysql:8.0
   # 从服务器
   docker run -d \
   -p 3308:3306 \
   -v /Users/linmeng/service/mysqlCluster/mysql-slave2/conf:/etc/mysql/conf.d \
   -v /Users/linmeng/service/mysqlCluster/mysql-slave2/data:/var/lib/mysql \
   -v /Users/linmeng/service/mysqlCluster/mysql-slave2/mysql-files:/var/lib/mysql-files \
   -e MYSQL_ROOT_PASSWORD=root \
   --name mysql-slave2 \
   mysql:8.0
   ```

2. 配置文件

   ```
   [mysqld]
   # 服务端使用的字符集默认为utf8mb4
   character-set-server=utf8mb4
   # 服务端使用的排序规则
   collation-server=utf8mb4_general_ci
   # 创建新表时将使用的默认存储引擎
   default-storage-engine=INNODB
   # 默认使用“mysql_native_password”插件认证
   # mysql 8.0 需要设置 mysql_native_password
   default_authentication_plugin=mysql_native_password
   # 关闭 only_full_group_by
   sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION
   
   # mysql 时区
   default-time-zone = '+8:00'
   
   # 最大连接数量
   max_connections=10000
   
   # 不再进行反解析（ip不反解成域名），这样可以加快数据库的反应时间。
   skip-name-resolve
   
   # binlog 日志存放路径
   log-bin=mysql-binlog
   # 日志中记录每一行数据被修改的形式
   binlog-format=STATEMENT
   # 当前机器的服务 ID, 如果为集群时不能重复，不要和 canal 的 slaveId 重复
   server_id=3
   ```

3. 重启docker服务

   ```
   docker restart mysql-slave1
   ```

4. 获取主服务器的状态

   ```
   SHOW MASTER STATUS
   ```

   ![](/Users/linmeng/IdeaProjects/github/java-everything/src/main/resources/summary/images/数据库主服务器状态.png)

5. 从服务器绑定主服务器

   ```
   -- 绑定主服务器创建的slave账号 上一步查到的 文件和位置
   CHANGE MASTER TO MASTER_HOST='192.168.10.11',MASTER_USER='slave',MASTER_PASSWORD='root',MASTER_PORT=3306,MASTER_LOG_FILE='mysql-binlog.000003',MASTER_LOG_POS=1063;
   ```

6. 启动主从同步

   ```
   START SLAVE;
   ```

7. 查看从服务器状态

   ```
   SHOW SLAVE STATUS\G
   ```

8. 停止主从同步

   ```
   -- 从服务器停止同步
   stop slave;
   -- 从服务器清除信息
   reset slave all;
   -- 主服务器清除主从信息
   reset master;
   ```

**成功标识**

![](/Users/linmeng/IdeaProjects/github/java-everything/src/main/resources/summary/images/从服务器.png)

自此，环境搭建好了，我们可以在主服务器创建一个数据库或者表检查从服务器是否同步

## 垂直分片环境搭建

搭建User数据库和Order数据库。

### User配置文件

```
[mysqld]
# 服务端使用的字符集默认为utf8mb4
character-set-server=utf8mb4
# 服务端使用的排序规则
collation-server=utf8mb4_general_ci
# 创建新表时将使用的默认存储引擎
default-storage-engine=INNODB
# 默认使用“mysql_native_password”插件认证
# mysql 8.0 需要设置 mysql_native_password
default_authentication_plugin=mysql_native_password
# 关闭 only_full_group_by
sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION

# mysql 时区
default-time-zone = '+8:00'

# 最大连接数量
max_connections=10000

# 不再进行反解析（ip不反解成域名），这样可以加快数据库的反应时间。
skip-name-resolve

# binlog 日志存放路径
log-bin=mysql-binlog
# 日志中记录每一行数据被修改的形式
binlog-format=STATEMENT
[mysql]
# 设置mysql客户端默认字符集
default-character-set=utf8mb4
```

### User Docker命令

```
docker run -d \
-p 3301:3306 \
-v /Users/linmeng/service/mysqlCluster/mysql-user/conf:/etc/mysql/conf.d \
-v /Users/linmeng/service/mysqlCluster/mysql-user/data:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=root \
--name server-user \
mysql:8.0
```

Order同User配置文件一样，docker命令出来端口号跟目录其他也差不多

### Order配置文件

```
[mysqld]
# 服务端使用的字符集默认为utf8mb4
character-set-server=utf8mb4
# 服务端使用的排序规则
collation-server=utf8mb4_general_ci
# 创建新表时将使用的默认存储引擎
default-storage-engine=INNODB
# 默认使用“mysql_native_password”插件认证
# mysql 8.0 需要设置 mysql_native_password
default_authentication_plugin=mysql_native_password
# 关闭 only_full_group_by
sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION

# mysql 时区
default-time-zone = '+8:00'

# 最大连接数量
max_connections=10000

# 不再进行反解析（ip不反解成域名），这样可以加快数据库的反应时间。
skip-name-resolve

# binlog 日志存放路径
log-bin=mysql-binlog
# 日志中记录每一行数据被修改的形式
binlog-format=STATEMENT
[mysql]
# 设置mysql客户端默认字符集
default-character-set=utf8mb4
```

### Order Docker命令

```
docker run -d \
-p 3302:3306 \
-v /Users/linmeng/service/mysqlCluster/mysql-order/conf:/etc/mysql/conf.d \
-v /Users/linmeng/service/mysqlCluster/mysql-order/data:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=root \
--name server-order \
mysql:8.0
```

## 水平分片环境搭建

```
docker run -d \
-p 3310:3306 \
-v /Users/linmeng/service/mysqlCluster/mysql-order0/conf:/etc/mysql/conf.d \
-v /Users/linmeng/service/mysqlCluster/mysql-order0/data:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=root \
--name server-order0 \
mysql:8.0

docker run -d \
-p 3311:3306 \
-v /Users/linmeng/service/mysqlCluster/mysql-order1/conf:/etc/mysql/conf.d \
-v /Users/linmeng/service/mysqlCluster/mysql-order1/data:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=root \
--name server-order1 \
mysql:8.0
```

## Shardingsphere proxy

### 容器部署

```
docker run -d \
-v /Users/linmeng/service/shardingsphere-proxy-5.1.1/proxy-a/conf:/opt/shardingsphere-proxy/conf \
-v /Users/linmeng/service/shardingsphere-proxy-5.1.1/proxy-a/ext-lib:/opt/shardingsphere-proxy/ext-lib \
-e ES_JAVA_OPTS="-Xmx256m -Xms256m -Xmn128m" \
-p 3321:3307 \
--name server-proxy-a \
apache/shardingsphere-proxy:5.1.1
```

### 文件添加

1. 上传`mysql-connector-java-8.0.27`到`/Users/linmeng/service/shardingsphere-proxy-5.1.1/proxy-a/ext-lib`目录下

2. 上传读写分离文件`config-readwrite-splitting`到`/Users/linmeng/service/shardingsphere-proxy-5.1.1/proxy-a/conf`目录下

   ```
   #
   # Licensed to the Apache Software Foundation (ASF) under one or more
   # contributor license agreements.  See the NOTICE file distributed with
   # this work for additional information regarding copyright ownership.
   # The ASF licenses this file to You under the Apache License, Version 2.0
   # (the "License"); you may not use this file except in compliance with
   # the License.  You may obtain a copy of the License at
   #
   #     http://www.apache.org/licenses/LICENSE-2.0
   #
   # Unless required by applicable law or agreed to in writing, software
   # distributed under the License is distributed on an "AS IS" BASIS,
   # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   # See the License for the specific language governing permissions and
   # limitations under the License.
   #
   
   ######################################################################################################
   # 
   # Here you can configure the rules for the proxy.
   # This example is configuration of replica-query rule.
   # 
   ######################################################################################################
   #
   #schemaName: readwrite_splitting_db
   #
   #dataSources:
   #  primary_ds:
   #    url: jdbc:postgresql://127.0.0.1:5432/demo_primary_ds
   #    username: postgres
   #    password: postgres
   #    connectionTimeoutMilliseconds: 30000
   #    idleTimeoutMilliseconds: 60000
   #    maxLifetimeMilliseconds: 1800000
   #    maxPoolSize: 50
   #    minPoolSize: 1
   #  replica_ds_0:
   #    url: jdbc:postgresql://127.0.0.1:5432/demo_replica_ds_0
   #    username: postgres
   #    password: postgres
   #    connectionTimeoutMilliseconds: 30000
   #    idleTimeoutMilliseconds: 60000
   #    maxLifetimeMilliseconds: 1800000
   #    maxPoolSize: 50
   #    minPoolSize: 1
   #  replica_ds_1:
   #    url: jdbc:postgresql://127.0.0.1:5432/demo_replica_ds_1
   #    username: postgres
   #    password: postgres
   #    connectionTimeoutMilliseconds: 30000
   #    idleTimeoutMilliseconds: 60000
   #    maxLifetimeMilliseconds: 1800000
   #    maxPoolSize: 50
   #    minPoolSize: 1
   #
   #rules:
   #- !READWRITE_SPLITTING
   #  dataSources:
   #    readwrite_ds:
   #      type: Static
   #      props:
   #        write-data-source-name: primary_ds
   #        read-data-source-names: replica_ds_0,replica_ds_1
   
   ######################################################################################################
   #
   # If you want to connect to MySQL, you should manually copy MySQL driver to lib directory.
   #
   ######################################################################################################
   
   schemaName: readwrite_splitting_db
   
   dataSources:
     write_ds:
       url: jdbc:mysql://192.168.10.11:3306/test?serverTimezone=UTC&useSSL=false
       username: root
       password: root
       connectionTimeoutMilliseconds: 30000
       idleTimeoutMilliseconds: 60000
       maxLifetimeMilliseconds: 1800000
       maxPoolSize: 50
       minPoolSize: 1
     read_ds_0:
       url: jdbc:mysql://192.168.10.11:3307/test?serverTimezone=UTC&useSSL=false
       username: root
       password: root
       connectionTimeoutMilliseconds: 30000
       idleTimeoutMilliseconds: 60000
       maxLifetimeMilliseconds: 1800000
       maxPoolSize: 50
       minPoolSize: 1
     read_ds_1:
       url: jdbc:mysql://192.168.10.11:3308/test?serverTimezone=UTC&useSSL=false
       username: root
       password: root
       connectionTimeoutMilliseconds: 30000
       idleTimeoutMilliseconds: 60000
       maxLifetimeMilliseconds: 1800000
       maxPoolSize: 50
       minPoolSize: 1
   
   rules:
   - !READWRITE_SPLITTING
     dataSources:
       readwrite_ds:
         type: Static
         props:
           write-data-source-name: write_ds
           read-data-source-names: read_ds_0,read_ds_1
   
   ```

   

3. 上传 `server.yaml `到`/Users/linmeng/service/shardingsphere-proxy-5.1.1/proxy-a/conf`目录下

   ```
   #
   # Licensed to the Apache Software Foundation (ASF) under one or more
   # contributor license agreements.  See the NOTICE file distributed with
   # this work for additional information regarding copyright ownership.
   # The ASF licenses this file to You under the Apache License, Version 2.0
   # (the "License"); you may not use this file except in compliance with
   # the License.  You may obtain a copy of the License at
   #
   #     http://www.apache.org/licenses/LICENSE-2.0
   #
   # Unless required by applicable law or agreed to in writing, software
   # distributed under the License is distributed on an "AS IS" BASIS,
   # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   # See the License for the specific language governing permissions and
   # limitations under the License.
   #
   
   ######################################################################################################
   # 
   # If you want to configure governance, authorization and proxy properties, please refer to this file.
   # 
   ######################################################################################################
   
   #mode:
   #  type: Cluster
   #  repository:
   #    type: ZooKeeper
   #    props:
   #      namespace: governance_ds
   #      server-lists: localhost:2181
   #      retryIntervalMilliseconds: 500
   #      timeToLiveSeconds: 60
   #      maxRetries: 3
   #      operationTimeoutMilliseconds: 500
   #  overwrite: false
   #
   rules:
     - !AUTHORITY
       users:
         - root@%:root
   #      - sharding@:sharding
   #    provider:
   #      type: ALL_PRIVILEGES_PERMITTED
   #  - !TRANSACTION
   #    defaultType: XA
   #    providerType: Atomikos
   #    # When the provider type is Narayana, the following properties can be configured or not
   #    props:
   #      recoveryStoreUrl: jdbc:mysql://127.0.0.1:3306/jbossts
   #      recoveryStoreDataSource: com.mysql.jdbc.jdbc2.optional.MysqlDataSource
   #      recoveryStoreUser: root
   #      recoveryStorePassword: 12345678
   #  - !SQL_PARSER
   #    sqlCommentParseEnabled: true
   #    sqlStatementCache:
   #      initialCapacity: 2000
   #      maximumSize: 65535
   #      concurrencyLevel: 4
   #    parseTreeCache:
   #      initialCapacity: 128
   #      maximumSize: 1024
   #      concurrencyLevel: 4
   
   props:
     sql-show: true
   #  max-connections-size-per-query: 1
   #  kernel-executor-size: 16  # Infinite by default.
   #  proxy-frontend-flush-threshold: 128  # The default value is 128.
   #  proxy-hint-enabled: false
   #  sql-show: false
   #  check-table-metadata-enabled: false
   #  show-process-list-enabled: false
   #    # Proxy backend query fetch size. A larger value may increase the memory usage of ShardingSphere Proxy.
   #    # The default value is -1, which means set the minimum value for different JDBC drivers.
   #  proxy-backend-query-fetch-size: -1
   #  check-duplicate-table-enabled: false
   #  proxy-frontend-executor-size: 0 # Proxy frontend executor size. The default value is 0, which means let Netty decide.
   #    # Available options of proxy backend executor suitable: OLAP(default), OLTP. The OLTP option may reduce time cost of writing packets to client, but it may increase the latency of SQL execution
   #    # and block other clients if client connections are more than `proxy-frontend-executor-size`, especially executing slow SQL.
   #  proxy-backend-executor-suitable: OLAP
   #  proxy-frontend-max-connections: 0 # Less than or equal to 0 means no limitation.
   #  sql-federation-enabled: false
   #    # Available proxy backend driver type: JDBC (default), ExperimentalVertx
   #  proxy-backend-driver-type: JDBC
   ```

### 服务重启

```
docker restart server-proxy-a
```

### 查看日志

```
docker exec -it server-proxy-a env LANG=C.UTF-8 bash
tail -f /opt/shardingsphere-proxy/logs/stdout.log 
```



## 参考链接

1. [尚硅谷ShardingSphere5实战教程（快速入门掌握核心）](https://www.bilibili.com/video/BV1ta411g7Jf?p=21&spm_id_from=pageDriver&vd_source=5c564039a406a9474275622c457b6ffc)
2. [ShardingSphere官方文档](https://shardingsphere.apache.org/document/5.1.1/cn/) 