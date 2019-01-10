UEMeasureTool [![platform](https://img.shields.io/badge/platform-android-brightgreen.svg)](https://developer.android.com/index.html) [![license](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/eleme/UETool/blob/master/LICENSE)
======

>**Show/edit any view's attributions, any app.**

![UEMeasureTool](pic/1.png)

## 介绍

UEMeasureTool 扩展自[UETool](https://github.com/eleme/UETool)实现，底层基于[VirtualApp](https://github.com/asLody/VirtualApp)的实现。可以作用于**任何App**在屏幕上显示的 view，比如 Activity/Fragment/Dialog/PopupWindow 等等。

目前 UEMeasureTool 提供以下功能：

- 移动屏幕上的任意 view，如果重复选中一个 view，将会选中其父 view
- 查看/修改常用控件的属性，比如修改 TextView 的文本内容、文本大小、文本颜色等等
- 有的时候 UETool 为你选中的 view 并不是你想要的，你可以选择打开 ValidView，然后选中你需要的 View
- 显示两个 view 的相对位置关系
- 显示网格栅栏，方便查看控件是否对齐

## 效果
![UEMeasureTool](pic/2.gif)


## Thanks
[UETool](https://github.com/eleme/UETool)

[VirtualApp](https://github.com/asLody/VirtualApp)

## License

[MIT](http://opensource.org/licenses/MIT)