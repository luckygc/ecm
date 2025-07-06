# ecm

### code style

使用idea google-java-format插件,aosp风格.\
参考[flink](https://github.com/apache/flink).

1. 尽量使用不可变类型，可复用对象可以可变
2. 使用jspecify标注，默认非空，可空需要标注
3. 可空返回对象可以适当返回Optional
4. 易于测试，使用构造函数注入
5. 尽量避免多线程

java
1. 不要使用原始类型，除非必要或数组
2. 优先使用非捕获型lambda，不捕获外部变量，防止每调用一次就生成一个对象实例
3. 性能关键代码避免使用Java Streams
