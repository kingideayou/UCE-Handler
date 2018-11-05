[![](https://jitpack.io/v/kingideayou/UCE-Handler.svg)](https://jitpack.io/#RohitSurwase/UCE-Handler) [![Project Status: Active – The project has reached a stable, usable state and is being actively developed.](http://www.repostatus.org/badges/latest/active.svg)](http://www.repostatus.org/#active) [![GitHub stars](https://img.shields.io/github/stars/kingideayou/UCE-Handler.svg?style=social&label=Star)](https://GitHub.com/kingideayou/UCE-Handler/stargazers)

# UCE Handler
#### 捕获应用的 UncaughtException，应用崩溃时提供崩溃的类名、方法名、行数、Error Log、Activity 路径、设备信息、应用信息...同时支持复制、分享和保存崩溃信息。方便在开发和测试时使用

## Features
* 监控 App 整个生命周期
* 优雅捕获所以 UncaughtException
* 崩溃时直接展示崩溃信息、格式化显示崩溃信息
* 复制、分享、保存崩溃日志
* 使用者可以拦截崩溃自行处理[如何配置](#自定义处理崩溃信息)

## 提供的崩溃日志信息
* Crash `ClassName`、`MethodName`、`LineNumber`、`CrashInfo`
* Device/mobile info.
* Application info.
* Crash log.
* Activity track. //optional
* All log files are placed in a separate folder.

### 示例图片
![Example Image](http://ww1.sinaimg.cn/mw690/6db4aff6gy1fvn1nrp5qnj21905g0qse.jpg)
![Example Animation](http://ww1.sinaimg.cn/mw690/6db4aff6gy1fvmyplertdg20ew0pn1kx.gif)

## 预览效果
Download the example app [here](https://github.com/kingideayou/UCE-Handler/raw/master/DemoApk)

# Setup
Project 根目录的 build.gradle 文件:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Application 或 Module 的 build.gradle 文件:

	dependencies {
            debugImplementation 'com.github.kingideayou.UCE-Handler:uce_handler:1.5.2'
	        releaseImplementation 'com.github.kingideayou.UCE-Handler:uce_handler_no_op:1.5.2'
	}

##### 在应用的 Application 类初始化:
* Initialize library using builder pattern.

		public class MyApplication extends Application {
		@Override public void onCreate() {
			...
			// Initialize UCE_Handler Library
			new UCEHandler.Builder(this).build();
		} }

##### Kotlin 初始化

        UCEHandler.Builder(applicationContext).build()

##### 自定义处理崩溃信息

    .setUCEHCallback(new UCECallback() {
        @Override
        public void exceptionInfo(@Nullable ExceptionInfoBean exceptionInfoBean) {
            Log.e("UCE-Handler", "exceptionInfo...");
        }

        @Override
        public void throwable(@Nullable Throwable throwable) {
            Log.e("UCE-Handler", "throwable...");
        }
    })

### Optional Parameters

| Optional Parameters | Default | Desc |
| ------ | ------ | ------ |
| `setUCEHEnabled(true/false)` | true | Enable/Disable UCE_Handler. |
| `setTrackActivitiesEnabled(true/false)` | false | Choose whether you want to track the flow of activities the user/tester has taken or not. |
| `setBackgroundModeEnabled(true/false)` | true | Choose if you want to catch exceptions while app is in background. |
| `setUCEHCallback(UCECallback)` | null | You can handle catch exception infos yourself. |

#### 注意：「保存异常信息」需要提供读取本地存储空间权限

## Authors & Contributers

* [**Rohit Surwase**](https://github.com/RohitSurwase) - *Initial work* - [API-Calling-Flow](https://github.com/RohitSurwase/API-Calling-Flow) , [AndroidDesignPatterns](https://github.com/RohitSurwase/AndroidDesignPatterns) , [News App Using Kotlin, MVP](https://github.com/RohitSurwase/News-Kotlin-MVP) ,  [Linkaive - Android App on Play Store](https://play.google.com/store/apps/details?id=com.rohitss.saveme)
* [**NeXT**](https://juejin.im/user/55f4419360b28e983c150d0e)

## Thanks To
* [UCE-Handler](https://github.com/RohitSurwase/UCE-Handler)
* [Recovery](https://github.com/Sunzxyong/Recovery)
* [leakcanary](https://github.com/square/leakcanary)

## License
Copyright © 2018 NeXT.

This project is licensed under the Apache License, Version 2.0 - see the [LICENSE.md](LICENSE.md) file for details