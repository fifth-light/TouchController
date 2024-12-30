# common-fabric 模块

这个模块其实不是一个真正意义上的 Gradle 项目，只是集中了一些使用到了 Fabric Loader 的类的代码到一个地方，
避免这些代码在每个 Fabric 的子项目中频繁复制。具体编译时使用了 sourceSet 的魔法，让其他的模块使用这里面的
代码。
