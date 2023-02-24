# Redis

半原创，半转载，参考链接已标注

## 常用命令

### 基础命令

#### 模糊删除多个key

docker运行redis后，在当前目录执行

```redis
redis-cli keys "key*"|xargs redis-cli del
```

### 高级命令

## 基础知识

### Redis简介

####  简单介绍下Redis

Redis是开源的、使用C语言编写的、可基于内存亦可持久化的非关系型key-value数据库。

#### 为什么Redis是单线程的还这么快？

[Redis为什么是单线程，高并发快的3大原因详解](https://zhuanlan.zhihu.com/p/58038188)

1. Redis是基于内存的，内存的读写速度非常快。
2. Redis是单线程的，省去了很多上下文切换的时间。
3. Redis使用IO多路复用技术，可以处理并发的连接。非阻塞IO内部使用epoll的多路IO复用技术提高了IO的性能。
4. Redis数据结构高效，数据编码合理

官方回答是Redis是基于内存的操作，CPU不是Redis的瓶颈，Redis的瓶颈最有可能是机器内存的大小以及网络带宽。既然单线程容易实现，CPU又不会成为瓶颈，顺利成章的使用单线程了。

### Redis数据结构

#### 底层数据结构

##### 简单动态字符串

1. sds中包含三个元素，已使用字节数量，未使用字节数量，字节数组。
2. sds记录了本身的长度信息，可常量获取字符串长度，也可防止字符串缓冲区溢出
3. 通过空间预分配以及惰性空间释放减少内存重分配的次数
   1. 空间预分配：字符串增长时，若增长的长度大于字符串的未使用长度，判断字符串当前长度，字符串的长度小于1M，给该字符串分配相同大小的未使用空间；若长度大于1M，给该字符串分配1M的未使用空间
   2. 惰性空间释放：字符串缩短时，不立即内存重分配，而是将释放空间作为未使用空间保存下来。
4. 二进制安全：sds根据保存的len属性的值判断是否结束，二进制安全
5. sds遵循空字符结尾，兼容C字符串

##### 链表

双向无环链表，带表头和表尾指针，常数级获取链表长度，可保存多种类型的值。

##### 字典

使用hash表底层实现，一个hash表中有多个hash表节点。



#### 常用五种数据结构

- String

  二进制安全。可用于缓存，计数器，分布式锁

- List

  列表通过链表实现，在链表头部尾部添加删除元素非常快，通过索引访问的速度同访问索引成正比。可用于链表，队列，微博关注人时间轴等。

- Hash

  可用来表示对象。

- Set

  无序字符串集合，可用于检查元素是否存在，获取多个集合的交集、并集、差集

- Zset

  类似集合同hash混合的数据类型，通过分数进行排序 。

#### 其他数据结构

#### 数据结构底层实现

#### 常用命令

## 数据安全及持久化

参考链接：

[Redis持久化](https://redis.io/topics/persistence)

[硬核万字长文，看完这20道Redis面试题，女朋友都学会Redis了](https://zhuanlan.zhihu.com/p/259658504)

书籍：

Redis实战  P61

### 持久化选项

Redis提供两种不同的持久化方法将数据存储到硬盘上。一种方法是快照(RDB),快照的作用是将某一时刻的所有数据写入硬盘中。另一种方法是只追加文件(AOF)，他会在执行写命令时，将执行的命令复制到硬盘上。

1. RDB(Redis数据库):RDB持久性以指定的时间间隔执行数据集的时间点快照
2. AOF(Append Only File):AOF持久化记录服务器收到的每个写操作，在服务器启动时再次运行，重建原始数据集。当日志变得太大时，Redis在后台重写日志。
3. 无持久性：数据仅在服务运行时存在，服务关闭即丢失。
4. RDB+AOF：在同一实例中组合使用。服务重新启动时，AOF将用于重建原始数据集

### RDB持久化

把当前内存数据生成快照保存到硬盘上的过程。

#### 触发机制 

###### 手动触发

SAVE：手动执行save命令，会阻塞Redis服务器，并同步保存当前时间点的所有Redis的实例中的数据的快照到RDB文件，快照保存完成后，Redis才能恢复使用。

BGSAVE：后台保存快照，可以手动执行。

###### 自动触发

自动触发对应BGSAVE命令，Redis进程执行 fork操作创建子线程，RDB持久化过程将由子进程负责，完成后自动结束。阻塞只发生在fork阶段 。

redis.conf文件中可以配置自动触发条件

```
save <seconds> <changes>
save 300 10 # 300秒内数据修改10次就会触发
save 900 60 # 900秒内数据修改60次就会触发
save "" # 不触发
```

其他触发的情况：

​	从节点进行全量复制时，主节点会自动指定BGSAVE命令生成RDB文件并发送给从节点。

​	当执行shutdown命令时，如果没有开启AOF持久化功能，也是自动执行BGSAVE命令。

#### RDB执行流程

[RDB执行流程](https://blog.csdn.net/yidan7063/article/details/107722544)

1. Redis父进程判断当前是否有正在执行SAVE或BGSAVE/BGREWRITEOF命令的子进程，如果有的话直接返回。
2. 父进程执行fork操作（调用OS函数复制父线程）创建子进程，这个过程是阻塞的，Redis不能执行其他命令
3. fork操作完成后，BGSAVE命令返回 background saving started并不在阻塞父进程
4. 子进程创建RDB文件，根据父进程内存快照生成临时快照文件，完成后对原有文件进行原子替换
5. 子进程告诉父进程已经完成，父进程更新统计信息

#### RDB 优缺点

##### 优点

1. 数据紧凑，数据量较小
2. 可以保存不同版本的快照
3. 提高Redis性能，父进程只需要fork操作生成子进程，不会进行IO操作。
4. 主从复制时支持同步操作。

##### 缺点

1. 服务没有正确关闭的情况下，RDB会丢失几分钟的数据。
2. RDB经常需要fork操作创建子进程进行持久化。如果数据量太大，fork操作会很耗时，可能会导致Redis停止服务几毫秒甚至一秒钟。

### AOF持久化

参考链接：[Redis 的持久化机制和AOF文件重写原理](https://blog.csdn.net/h2503652646/article/details/110941710)

以独立日志的方式记录每次写命令，重启时再重新执行AOF文件中的命令恢复数据。实时性比较强。

#### AOF持久化工作机制

开启AOF需要在redis.conf文件中配置

```conf
appendonly yes # 开启AOF
appendfilename appendonly.aof # 配置aof文件名
dir ./ # 配置保存路径 
```

#### AOF持久化工作流程

1. 命令写入：将所有的写入命令追加到aof_buf(缓冲区)中
2. AOF缓冲区根据相应的策略向硬盘做同步操作
3. 随着AOF文件越来越大，触发AOF文件重写的条件时，AOF文件就会 重写
4. 服务重启时，加载AOF文件恢复数据

#### AOF持久化同步机制

```
appendfsync everysec # 每秒记录一次数据，但如果一秒内发生了宕机，将会丢失这一秒钟内修改或新增过的数据。推荐使用这种方式。
appendfsync always # 每次更改都同步一次数据 
appendfsync no # 从不同步数据
```

#### AOF文件重写

参考链接：

[Redis之AOF重写及其实现原理](https://blog.csdn.net/hezhiqiang1314/article/details/69396887)

[深入对持久化原理的AOF](https://www.jianshu.com/p/0239a3e01107)

AOF文件大小随着命令的写入会越来越大，影响包括但不限于：对Redis服务器，计算机的存储压力；AOF还原数据库的时间增加。为了解决AOF文件体积膨胀的问题，Redis提供文件重写功能：Redis服务器可以创建一个新的AOF文件来 替代原有的 AOF文件，新旧两个文件所保存的数据库状态是相同的，但是新的AOF文件不会有浪费空间的冗余命令，体积会小很多。

##### AOF重写触发条件

- 用户调用BGREWRITEAOF命令手动触发。
- Redis中ServerCron(服务器周期性操作函数)函数执行时，会检查AOF重写条件是否满足，满足的话就触发AOF重写操作。
  - 当前没有BGSAVE命令以及AOF持久化在执行。
  - 没有BGREWRITEAOF命令在执行
  - 当前AOF文件大小要大于在redis.conf中配置的auto-aof-rewrite-min-size。
  - 当前AOF文件大小同最后一次重写的文件大小的比率要大于指定的增长百分比(redis.conf文件中配置的auto-aof-rewrite-percentage)

##### AOF重写流程

1. Redis中ServerCron(服务器周期性操作函数)函数执行时，会检查AOF重写条件是否满足，满足的话就触发AOF重写操作。
2. fork操作开启 一个子进程，读取数据库通过命令记录数据库数据
3. 主进程开启AOF缓冲区，AOF重写缓冲区。主进程执行完写命令后将写入命令追加到两个缓冲区中
4. 子进程数据库数据 记录完毕后，将AOF 重写缓冲区中的数据写入新的AOF文件中
5. 新的 AOF文件替换旧的AOF文件

## 集群相关

## 底层原理

### 参考链接

[Redis 数据类型和抽象介绍](https://redis.io/topics/data-types-intro)

[Redis数据结构及底层实现](https://blog.csdn.net/erciyuan_/article/details/112364472)

[Redis内部数据结构详解](http://zhangtielei.com/posts/blog-redis-skiplist.html)

[Redis 数据结构之dict](https://www.cnblogs.com/exceptioneye/p/6855293.html)

[Redis深入浅出——字符串和SDS](https://blog.csdn.net/qq193423571/article/details/81637075)

[Redis的动态字符串SDS你不知道？抱歉我不能给你offer...](https://zhuanlan.zhihu.com/p/152074927?from_voters_page=true)



## 客户端