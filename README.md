# Android-class7
## 16231046 廖书昊
实现了播放暂停，进度条展示，横竖屏切换并保持屏幕比例为16:9。

播放本地视频先使用load选择视频，再点击play local播放，但遇到了一个问题，在重新设置了surfaceview后，虽然视频已经改变，但显示并未刷新，使用了invalidate()刷新整个view也没有解决问题，只有切回桌面再回来才能改变显示。
