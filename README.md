# TextSizeHelper [ ![Download](https://api.bintray.com/packages/pokercc/android/text-size-helper/images/download.svg) ](https://bintray.com/pokercc/android/text-size-helper/_latestVersion)
**动态修改app或单个activity 字体大小的帮助类**
特性:
 * 不需要重启，立即生效
 * 低侵入性，几行代码即可集成

集成步骤:
1. gradle 引入依赖
```groovy
implementation 'pokercc.android:textSizeHelper:${last_version}'

```
2. app级别的集成

2.1 在application 中初始化
```java
AppTextSizeHelper.init(this);
```

2.2 改变字体大小
```java
AppTextSizeHelper.onFontScaled(context, 1.5f);// 字体放大50%

```
3. activity级别的集成（选用）
3.1 在attachBaseContext方法中创建ActivityTextSizeHelper对象
```java
private ActivityTextSizeHelper textSizeHelper;
@Override
protected void attachBaseContext(Context context){
     super.attachBaseContext(newBase);
     textSizeHelper = ActivityTextSizeHelper(this);
}

```
3.2 在onResume方法中调用onResume方法，跟踪view树
```java
@Override
protected void onResume() {
    super.onResume()
    textSizeHelper.onResume();
    
 }
```
3.3 修改activity.getResource方法的实现
```java
@Override
public Resources getResources(){
    return textSizeHelper.getProxyResource(super.getResources());
}
```
3.4 改变字体大小
```java
textSizeHelper.onFontScaled(1.5f);// 字体放大50%
```

已知bug:
* 增大activity的字体后，重启activity，设置了排除(use_dp)的TextView字体也会变大 