# JVM

## 参考链接

- ###### [你确定你真的理解"双亲委派"了吗？！ ](https://www.cnblogs.com/hollischuang/p/14260801.html)

- [Java –什么是-Xms和-Xmx参数？](https://blog.csdn.net/cyan20115/article/details/106548703/)

## 日常问题

### JVM cpu过高

1. `top`命令查出过高的java进程ID
2. `top -H -p 进程ID` 查出该进程下CPU使用过高的线程ID
3. `jstack -l 进程ID|grep 线程ID -A50` 查看对应线程信息，定位有问题的代码行数

