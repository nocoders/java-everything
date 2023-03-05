# 读写分离环境搭建

## 历史镜像数据删除

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

## 主服务器创建

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

4. 重启docker服务

   ```
   docker restart mysql-master
   ```

## 从服务器访问账号开通

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

## 从服务器创建

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

自此，环境搭建好了，我们可以在主服务器创建一个数据库或者表检查从服务器是否同步

