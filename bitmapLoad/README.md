# BitmapLoad - 智能图片加载工具

## 概述

BitmapLoad 是一个专为 Android 设计的智能图片加载工具，能够根据 View 的大小自动加载对应尺寸的图片，有效避免内存浪费。采用链式调用 API 设计，使用简单，功能强大。

## 主要特性

### 🚀 智能尺寸适配
- 自动检测 View 尺寸
- 智能计算采样率
- 避免加载过大的图片

### 🎨 自定义图片处理
- 提供 BitmapProcessor 接口
- 支持圆角、裁切、滤镜等效果
- 可组合多个处理效果
- 支持条件处理

### 🔧 灵活的配置选项
- 占位图/错误图
- 加载监听器
- 异步加载（基于Executor + Handler）
- 任务取消支持

### 📱 支持的类型
- 资源ID (Int) - 从应用的drawable资源加载
- 本地文件路径 (String) - 从设备本地存储加载

## 快速开始

### 基本使用

```kotlin
// 从资源加载
BitmapLoad.with(context)
    .load(R.drawable.image)
    .into(imageView)

// 从本地文件路径加载
BitmapLoad.with(context)
    .load("/storage/emulated/0/Pictures/image.jpg")
    .into(imageView)
```

### 高级配置

```kotlin
BitmapLoad.with(context)
    .load(R.drawable.image)
    .placeholder(R.drawable.placeholder)
    .error(R.drawable.error)
    .processor(customProcessor)
    .listener(object : BitmapLoadListener {
        override fun onSuccess(bitmap: Bitmap) {
            // 加载成功
        }
        override fun onError(exception: Exception) {
            // 加载失败
        }
    })
    .into(imageView)
```

### 任务取消

```kotlin
val bitmapLoad = BitmapLoad.with(context)
    .load(R.drawable.image)
    .into(imageView)

// 取消加载任务
bitmapLoad.cancel()
```

## API 参考

### 核心方法

| 方法 | 描述 | 参数 |
|------|------|------|
| `with(context)` | 创建 BitmapLoad 实例 | Context |
| `load(source)` | 设置图片源 | Int(resId)/String(path) |
| `into(view)` | 加载到指定 View | View |
| `cancel()` | 取消加载任务 | - |

### 配置方法

| 方法 | 描述 | 参数 |
|------|------|------|
| `placeholder(resId/drawable)` | 设置占位图 | Int/Drawable |
| `error(resId/drawable)` | 设置错误图 | Int/Drawable |
| `processor(processor)` | 设置自定义图片处理器 | BitmapProcessor |
| `listener(listener)` | 设置加载监听器 | BitmapLoadListener |

## 自定义图片处理

### BitmapProcessor 接口

```kotlin
interface BitmapProcessor {
    fun process(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap
}
```

### 内置处理器示例

```kotlin
// 圆角处理
val cornerProcessor = CornerRadiusProcessor(20f)

// 居中裁剪
val cropProcessor = CenterCropProcessor()

// 居中适应
val insideProcessor = CenterInsideProcessor()

// 圆形处理
val circleProcessor = CircleProcessor()

// 边框处理
val borderProcessor = BorderProcessor(5, Color.BLACK)

// 模糊处理
val blurProcessor = BlurProcessor(10f)

// 灰度处理
val grayscaleProcessor = GrayscaleProcessor()
```

### 组合处理器

```kotlin
// 组合多个处理效果
val compositeProcessor = CompositeProcessor(
    CornerRadiusProcessor(15f),
    BorderProcessor(3, Color.RED)
)

BitmapLoad.with(context)
    .load(R.drawable.image)
    .processor(compositeProcessor)
    .into(imageView)
```

### 条件处理器

```kotlin
// 根据条件选择不同的处理方式
val conditionalProcessor = ConditionalProcessor(
    condition = { bitmap, targetWidth, targetHeight ->
        bitmap.width > targetWidth * 2 || bitmap.height > targetHeight * 2
    },
    trueProcessor = CenterCropProcessor(),
    falseProcessor = CenterInsideProcessor()
)
```

## 使用场景

### 1. 列表项图片加载
```kotlin
// RecyclerView 适配器中的使用
override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = items[position]
    
    BitmapLoad.with(context)
        .load(item.imagePath)
        .placeholder(R.drawable.placeholder)
        .processor(CornerRadiusProcessor(8f))
        .into(holder.imageView)
}
```

### 2. 头像加载
```kotlin
BitmapLoad.with(context)
    .load(user.avatarPath)
    .placeholder(R.drawable.default_avatar)
    .processor(CircleProcessor())
    .into(avatarImageView)
```

### 3. 背景图片加载
```kotlin
BitmapLoad.with(context)
    .load(R.drawable.background)
    .processor(CenterCropProcessor())
    .into(backgroundView)
```

### 4. 本地图片加载
```kotlin
BitmapLoad.with(context)
    .load("/storage/emulated/0/Pictures/large_image.jpg")
    .placeholder(R.drawable.loading)
    .error(R.drawable.error)
    .processor(CenterInsideProcessor())
    .listener(object : BitmapLoadListener {
        override fun onSuccess(bitmap: Bitmap) {
            // 处理成功加载的图片
        }
        override fun onError(exception: Exception) {
            // 处理加载失败
        }
    })
    .into(imageView)
```

## 性能优化

### 1. 智能采样
- 根据目标尺寸自动计算采样率
- 避免加载过大的图片到内存

### 2. 异步加载
- 使用 Executor + Handler 进行异步操作
- 线程池管理，性能更好
- 不阻塞主线程
- 支持任务取消

### 3. 内存管理
- 自动回收不需要的 Bitmap
- 智能尺寸适配减少内存占用

## 技术实现

### 异步处理架构
- **线程池**: 使用 `Executors.newCachedThreadPool()` 管理后台线程
- **主线程切换**: 使用 `Handler(Looper.getMainLooper())` 自动切换到主线程
- **任务取消**: 支持取消正在进行的加载任务
- **生命周期管理**: 防止内存泄漏和无效回调

### 图片处理架构
- **接口设计**: 提供 `BitmapProcessor` 接口供外部实现
- **组合模式**: 支持多个处理器组合使用
- **条件处理**: 支持根据条件选择不同的处理方式
- **扩展性**: 易于添加新的图片处理效果

### 优势
- 适用于所有Android版本
- 线程池复用，性能更好
- 自动内存管理
- 支持任务取消
- 高度可扩展的图片处理

## 注意事项

1. **权限要求**：本地文件加载需要存储权限
2. **内存管理**：大量图片加载时注意内存使用
3. **错误处理**：设置合适的错误图和监听器
4. **任务取消**：在适当时候调用cancel()避免无效操作
5. **支持类型**：只支持资源ID和本地文件路径，不支持网络图片
6. **处理器实现**：自定义处理器时注意内存管理和性能

## 依赖要求

```kotlin
// 无需额外依赖，使用Android原生API
// 适用于所有Android版本
```

## 许可证

本项目采用 MIT 许可证，详见 LICENSE 文件。

## 贡献

欢迎提交 Issue 和 Pull Request 来改进这个项目。

## 更新日志

### v1.0.0
- 初始版本发布
- 支持资源ID和本地文件路径
- 智能尺寸适配
- 自定义图片处理接口
- 基于Executor + Handler的异步加载
- 支持任务取消
- 移除缓存机制，简化架构
- 提供BitmapProcessor接口供外部扩展
