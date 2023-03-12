# MySql

## 1	引擎

### 1.1	InnoDB 和MyISAM的区别

- InnoDB支持事务和外键，MyISAM不支持
- InnoDB是聚集索引，使用B+Tree作为索引结构，索引和数据文件是绑定在一起的，MyISAM是非聚集索引索引和数据文件时分离的。
- InnoDB支持行级锁，MyISAM支持表级锁

## 2	索引

### 2.1	count(*)，count(字段)，count(1)的区别

1. **count(*)：**统计所有记录条数

   Mysql对其进行了优化，如果表中有二级索引就走二级索引，没有二级索引就走聚簇索引。因为聚簇索引的索引是跟数据在一起的，所以IO会高，查询会慢

2. **count(字段)：**统计该字段不为空的记录条数

   1. count（id）：走主键索引
   2. count（索引）：走索引，只统计不为空的字段
   3. count（普通字段）：不走索引，只统计不为空的字段

3. **count(1)：**同count(*)相同，统计所有记录条数

### 2.2	索引类型

- 数据结构分类：**B+tree索引、Hash索引、全文索引**
- 存储方式分类：**聚簇索引、二级索引（辅助索引）**
- 字段特性分类：**主键索引、普通索引、前缀索引、联合索引**

各种索引创建方式见 [该链接](https://www.cnblogs.com/luyucheng/p/6289714.html)

## 3	事务

### 3.1	事务的ACID特性

- 原子性：事务中的所有操作，要么全部执行，要么全部不执行。在执行过程中发生错误，就回滚到事务执行之前。
- 一致性：事务使得系统能够从一个正确的状态转移到另外一个有效的状态。同业务相关，满足业务要求。
- 隔离性：两个事务操作的对象是相互隔离的
- 持久性：事务提交后，结果是持久性的。

### 3.2	事务的隔离级别

1. 数据库的并发问题
   - 脏读：事务一可以读取事务二未提交的事务
   - 不可重复读：同一个事务中，同一条记录两次读取的数据不同
   - 幻读：同一个事务中，相同查询条件两次查询的数据不同
2. 隔离级别
   - 读未提交：允许一个事务读取另一个事务未提交的数据
   - 读提交：一个事务只能读取另一个事务已经提交的数据
   - 可重复读：锁住这条数据，对同一条数据的读写进行序列化操作
   - 序列化：锁住整张表，所有操作顺序读写

## 4	优化

### 4.1	sql优化

1. 表结构优化：字段选择最小的数据类型，定义非空字段，使用索引，使用冗余字段，数据量大时表分区或分表
2. sql语句优化：避免全表扫描，sql语句提高效率，查询线上慢sql并对其进行优化
3. sql服务器配置优化
   - 连接数设置：通过show variables like ‘max_connections’;查看最大连接数，show global status like ‘max_used_connections’;查看最大使用连接数，show status like 'Threads%';查看正在使用连接数

## 5	参考链接

1. [MySql索引最左匹配原则错误说法](https://mp.weixin.qq.com/s/8qemhRg5MgXs1So5YCv0fQ)
2. [InnoDB存储引擎对MVCC的实现](https://javaguide.cn/database/mysql/innodb-implementation-of-mvcc.html)
3. [MySQL 字符集不一致导致索引失效的一个真实案例](https://blog.csdn.net/horses/article/details/107243447)
4. [聊聊索引失效？失效的原因是什么？](https://bbs.huaweicloud.com/blogs/333163)
5. [Mysql count(*)，count(字段)，count(1)的区别](https://www.jianshu.com/p/e1229342a5e2)
6. [一文搞清楚 MySQL count(*)、count(1)、count(col) 的区别](https://developer.aliyun.com/article/897237)
7. [特性介绍 | MySQL select count(*) 、count(1)、count(列) 详解（1）：概念及区别](https://segmentfault.com/a/1190000040733649)
8. [MySQL索引类型 ](https://www.cnblogs.com/luyucheng/p/6289714.html)
9. [MySQL索引有哪些分类，你真的清楚吗？](https://segmentfault.com/a/1190000037683781)
10. [MySQL有哪些索引类型](https://segmentfault.com/a/1190000018872822)