# LightProgressView + VolumeProgressView
![Build Status](https://img.shields.io/badge/build-passing-brightgreen)

[Englist](https://github.com/zhangchaojiong/VideoProgressView/blob/master/README.md) | [中文版本](https://github.com/zhangchaojiong/VideoProgressView/blob/master/README_zh.md)
>A creative brightness animation, which controls the brightness of the mobile phone screen by swiping up and down. The animation changes from the sun (the day is bright) to the moon (the night is dark), a very beautiful screen brightness animation!

## Design sketch
![2020-05-03 01_00_28.gif](https://github.com/zhangchaojiong/VideoProgressView/blob/master/image/2788235-f057d49c96baa63d.gif)          ![2020-05-05 18_09_07.gif](https://github.com/zhangchaojiong/VideoProgressView/blob/master/image/2788235-cd0a98eda6c1c711.gif)


## Usage：
* Setting up the dependency

```
implementation 'com.cj.videoprogressview:progressview:1.0.0'
```
* Simple use cases will look something like this:

```
<com.cj.videoprogressview.LightProgressView
        android:id="@+id/lpv"
        android:padding="10dp"
        android:background="@drawable/bg_center_window"
        android:layout_width="88dp"
        android:layout_height="88dp"
        app:lpv_halo_color="@android:color/white"
        app:lpv_moon_color="@android:color/white"
        app:lpv_halo_height="7dp"
        app:lpv_halo_width="2dp"/>
```

```
<com.cj.videoprogressview.VolumeProgressView
        android:id="@+id/vpv"
        android:layout_marginLeft="3dp"
        android:padding="10dp"
        android:background="@drawable/bg_center_window"
        android:layout_width="88dp"
        android:layout_height="88dp"
        app:vpv_halo_color="@android:color/white"
        app:vpv_halo_height="7dp"
        app:vpv_halo_width="2dp"
        />
```

## Styleable 

```
   <declare-styleable name="LightProgressView">
        //Halo length
        <attr format="dimension" name="lpv_halo_height"/> 
        //Halo thickness
        <attr format="dimension" name="lpv_halo_width"/>
        //Number of halos
        <attr format="integer" name="lpv_num_of_halo"/>
        //The curvature of the moon, the default value is 0.43f
        <attr format="float" name="lpv_magicnum"/>
        //The color of the moon
        <attr format="color" name="lpv_moon_color"/>
        //The color of the halo
        <attr format="color" name="lpv_halo_color"/>
    </declare-styleable>
```

```
    <declare-styleable name="VolumeProgressView">
    //Halo length
        <attr format="dimension" name="vpv_halo_height"/>
        //Halo thickness
        <attr format="dimension" name="vpv_halo_width"/>
        //Number of halos
        <attr format="integer" name="vpv_num_of_halo"/>
        //Customize pictures with low volume
        <attr format="integer" name="vpv_volume_low"/>
        //Customize pictures with medium volume
        <attr format="integer" name="vpv_volume_medium"/>
        //Customize pictures with high volume
        <attr format="integer" name="vpv_volume_high"/>
        //The color of the halo
        <attr format="color" name="vpv_halo_color"/>
    </declare-styleable>
```

## Deductive Reasoning
goto [wiki home](https://www.jianshu.com/p/55e7de12451d) 

## License

```
Copyright (C) 2020 chaojiong.zhang

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

