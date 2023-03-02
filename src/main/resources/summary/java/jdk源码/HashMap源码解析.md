# HashMap
HashMap基于数组加链表或红黑树实现。map初始化时，底层数据结构初始化为数组，数组中存储元素类型为链表。有元素添加时，根据key的hash值获取对应存储数组角标，根据负载因子判断当前元素数量是否到达数组扩容标准，到达扩容标准会进行扩容，同时，会根据添加元素链表及map容量判断是否需要将链表转为红黑树.
## 参考链接

[Java8 HashMap resize()方法解析](https://blog.csdn.net/java672627493/article/details/79557775)

[红黑树原理解析以及Java实现](https://blog.csdn.net/u010853261/article/details/54312932)

[红黑树的原理_关于红黑树原理的一些介绍](https://blog.csdn.net/weixin_39959298/article/details/111343310)

[Jdk1.8集合框架之HashMap源码解析（详细解析红黑树)](https://blog.csdn.net/weixin_40255793/article/details/80748946)

[ConcurrentHashMap源码分析(1.8)](https://www.cnblogs.com/zerotomax/p/8687425.html)

## 说明

## 变量定义

```
// 最大容量
static final int MAXIMUM_CAPACITY = 1 << 30;
// 默认容量
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
// 默认负载因子 添加元素时 当前map中元素数量 > 数组长度 * 负载因子 时，map扩容；
static final float DEFAULT_LOAD_FACTOR = 0.75f;
// 链表转红黑树的阈值，当链表长度大于64且当前节点的链表长度大于8时，将链表转为红黑树
static final int TREEIFY_THRESHOLD = 8;
// 红黑树转链表的阈值
static final int UNTREEIFY_THRESHOLD = 6；
// 容量大于64时，才会将链表转为红黑树
static final int MIN_TREEIFY_CAPACITY = 64;
```

## 主要方法

###  构造

```java
// 无参构造，使用默认初始化容量和默认负载因子
HashMap()
// 指定初始化容量及负载因子，对初始化容量判断是否满足条件并调用tableSizeFor方法设置初始化容量，判断负载因子是否为正常数字
HashMap(int initialCapacity, float loadFactor)
// 指定初始化容量，使用默认负载因子，调用HashMap(int initialCapacity, float loadFactor)初始化
HashMap(int initialCapacity)
// map初始化，设置默认负载因子，调用putMapEntries方法进行数据插入
HashMap(Map<? extends K, ? extends V> m)
```

### put方法解析

```java
 /**
     * Implements Map.put and related methods.
     * 该方法可以添加元素，如果元素已存在，根据onlyIfAbsent判断是否修改，如果为true，不修改已经存在的值，否则修改并返回原来的值
     * 流程：
     *      判断表数组是否初始化，未初始化则初始化表数组
     *      将数组长度减一同hash值相与获取存放元素的角标，判断角标下是否有链表
     *          没有链表直接新建链表存入key-value
     *          有链表判断链表是否为红黑树，
     *              是红黑树调用putTreeVal(红黑树的插入方法)
     *              不是红黑树调用链表的插入方法，判断是否需要转红黑树
     *      判断size+1是否大于阈值，大于阈值直接resize方法进行扩容
     * @param hash hash for key key的hash值
     * @param key the key
     * @param value the value to put
     * @param onlyIfAbsent if true, don't change existing value 如果为true，不修改已经存在的值
     * @param evict if false, the table is in creation mode.  false时为创建模式
     * @return previous value, or null if none 为空返回空，不为空返回之前的值
     */
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        if ((tab = table) == null || (n = tab.length) == 0)// 表为空的情况下，map为刚初始化的
            n = (tab = resize()).length;
        if ((p = tab[i = (n - 1) & hash]) == null)// map中表的长度同添加的key的hash值相与，得到添加元素存放角标，判断角标下是否有元素，没有的话直接新建链表存放
            tab[i] = newNode(hash, key, value, null);
        else {// 添加元素角标下已经有链表
            Node<K,V> e; K k;
            if (p.hash == hash && // 判断链表第一个元素的key同添加元素key是否相同，相同直接赋值给 e
                    ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;// e等于表中key同添加元素key相等的链表节点
            else if (p instanceof TreeNode)// 当前的链表是红黑树，使用红黑树查找e
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            else {
                for (int binCount = 0; ; ++binCount) {// 遍历链表
                    if ((e = p.next) == null) {// 判断链表next是否为空
                        p.next = newNode(hash, key, value, null); // 为空直接把元素添加到尾部
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st 链表长度大于等于8则转为红黑树
                            treeifyBin(tab, hash);
                        break;// 此时，e为null，并且元素尾插到链表中
                    }
                    if (e.hash == hash && // 链表中的元素的key 同添加元素的key相同，直接break
                            ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;// 用来循环的，相当于p=p.next
                }
            }
            if (e != null) { // existing mapping for key e不等于null是因为map中有相同的key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)// 不是ifAbsent 或者旧值为null
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        if (++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }
```

### get方法解析

根据key的hash值同数组长度减一相与获取数组角标，判断角标下链表首个元素key是否相同，相同返回，不相同就判断链表类型，根据类型遍历链表，找key相同的元素，找到返回

```java
 public V get(Object key) {
        Node<K,V> e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }

    final Node<K,V> getNode(int hash, Object key) {
        Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (first = tab[(n - 1) & hash]) != null) { // 判断角标下是否有元素
            if (first.hash == hash && // always check first node equals方法检查第一个角标的key同查询的key是否相等
                ((k = first.key) == key || (key != null && key.equals(k))))
                return first;
            if ((e = first.next) != null) { //根据类型遍历元素使用equals方法判断是否为同一元素
                if (first instanceof TreeNode)
                    return ((TreeNode<K,V>)first).getTreeNode(hash, key);
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }	
```

### remove方法解析

```java
/**
 * Implements Map.remove and related methods.
 *  找角标，找equals方法相同的两个key
 *  移除角标
 * @param hash hash for key
 * @param key the key
 * @param value the value to match if matchValue, else ignored
 * @param matchValue if true only remove if value is equal 只移除value相等的
 * @param movable if false do not move other nodes while removing false的话移除元素后不移动数组
 * @return the node, or null if none
 */
final Node<K,V> removeNode(int hash, Object key, Object value,
                           boolean matchValue, boolean movable) {
    Node<K,V>[] tab; Node<K,V> p; int n, index;
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (p = tab[index = (n - 1) & hash]) != null) {
        Node<K,V> node = null, e; K k; V v;
        if (p.hash == hash &&
            ((k = p.key) == key || (key != null && key.equals(k))))
            node = p;
        else if ((e = p.next) != null) {
            if (p instanceof TreeNode)
                node = ((TreeNode<K,V>)p).getTreeNode(hash, key);
            else {
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key ||
                         (key != null && key.equals(k)))) {
                        node = e;
                        break;
                    }
                    p = e;
                } while ((e = e.next) != null);
            }
        }
        if (node != null && (!matchValue || (v = node.value) == value ||
                             (value != null && value.equals(v)))) {
            if (node instanceof TreeNode)
                ((TreeNode<K,V>)node).removeTreeNode(this, tab, movable);
            else if (node == p)
                tab[index] = node.next;
            else
                p.next = node.next;
            ++modCount;
            --size;
            afterNodeRemoval(node);
            return node;
        }
    }
    return null;
}
```

## 辅助方法

### tableSizeFor

对map的容量进行设置，map的容量不能大于最大容量，并且是大于当前容量的最小二进制数

```
/** 返回比cap大的最小的2的n次方的数字，如果cap大于最大容量，就让他等于最大容量
     * Returns a power of two size for the given target capacity.
     */
    static final int tableSizeFor(int cap) {
        // 减一防止cap本身就是二进制数，这样就会使得最后容量本来是8，最后变成16
        int n = cap - 1;
        // n二进制最高位肯定是1，将所有位数都或成1
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        // n+1就是二进制，判断是否大于最大容量从而进行设置
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
```

### hash(Object key)

调用**Object.hashCode**方法获取key的hashcode并使高16位同低16位异或使得当数组长度较短时hashcode高低位都能参与运算

```
static final int hash(Object key) {
    int h;// 高16位同低16位异或，让高16位也参与到获取数组下标的运算中
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

### resize()

数组初始化或扩容

```java
 /**
     * Initializes or doubles table size.  If null, allocates in
     * accord with initial capacity target held in field threshold.
     * Otherwise, because we are using power-of-two expansion, the
     * elements from each bin must either stay at same index, or move
     * with a power of two offset in the new table.
     * 数组为空则初始化数组
     *      数组为空，阈值不为空，说明是带参map构造，直接将旧阈值作为数组长度，然后new新数组
     *      数组为空，阈值为空，直接使用默认容量及负载因子，然后new新数组
     * 数组不为空则数组扩容
     *      判断数组长度是否大于数组最大容量，大于最大容量则将阈值设置为Integer最大值，否则容量阈值均double
     * 原有数组不为空时，扩容后需要重新赋值新数组
     *      链表为红黑树，则调用红黑树对元素重新排列
     *      链表不为红黑树，判断链表长度，
     *          只有一个元素的话直接元素key的hash值同新长度减一相与获取存放角标，添加进去
     *          有多个元素的话key的hash值同旧长度相与是否等于0，等于0就放到原有角标下，否则放到原有角标加原数组长度下面
     * @return the table
     */
    final Node<K,V>[] resize() {
        Node<K,V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        if (oldCap > 0) {// 老数组不为空时,说明map已经初始化过了
            if (oldCap >= MAXIMUM_CAPACITY) {// 大于默认最大容量时直接将阈值设置为Integer.MAX_VALUE
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }// 容量duoble，阈值double
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                     oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1; // double threshold
        }// 数组为空，但是阈值不为空，说明是带容量初始化的map，将阈值赋值给数组长度
        else if (oldThr > 0) // initial capacity was placed in threshold
            newCap = oldThr;
        else {               // zero initial threshold signifies using defaults 无参初始化的map
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        if (newThr == 0) {
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                      (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        @SuppressWarnings({"rawtypes","unchecked"})
        Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
        table = newTab;
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                Node<K,V> e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null)
                        newTab[e.hash & (newCap - 1)] = e;
                    else if (e instanceof TreeNode)
                        ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                    else { // preserve order
                        Node<K,V> loHead = null, loTail = null;// 小头 小尾
                        Node<K,V> hiHead = null, hiTail = null;// 大头大 尾
                        Node<K,V> next;
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {// 同oldCap与只有0和1两种，0是低位，1是高位
                                if (loTail == null)// 尾巴为null的时候，头赋值给e
                                    loHead = e;// 用于保存第一个元素
                                else
                                    loTail.next = e;// 尾巴不为null的时候，头赋值给e
                                loTail = e;// 尾巴next
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }
```

