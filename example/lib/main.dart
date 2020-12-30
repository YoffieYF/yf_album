import 'package:album/asset_entity.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:album/album.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  Album _album = Album();

  @override
  void initState() {
    super.initState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: <Widget>[
              SizedBox(height: 50),
              RaisedButton(
                onPressed: () async {
                  _album.checkPhotosPermission().then((value) async {
                    if (value) {
                      var result = await _album.openAlbumOfMultiple();
                      if (result != null) {
                        _album.mediaCompress(result, (item) {
                          print(item.toJson());
                        });
                      }
                    } else {
                      print("权限不够！");
                    }
                  });
                },
                child: Text("打开相册"),
              ),
              SizedBox(height: 50),
              RaisedButton(
                onPressed: () async {
                  List<String> list = [];
                  list.add(
                      "https://cdn-test.xinyan.tech/attachments/d3o77c9NPEvA8EXaWqTCdd/meet1592989665610.gif?s=600_338&size=4816621&c=567051");
                  list.add(
                      "https://cdn-test.xinyan.tech/attachments/d3o77c9NPEvA8EXaWqTCdd/meet1592989665610.gif?s=600_338&size=4816621&c=567051");
                  list.add(
                      "https://cdn-test.xinyan.tech/attachments/d3o77c9NPEvA8EXaWqTCdd/meet1592989665610.gif?s=600_338&size=4816621&c=567051");
                  list.add(
                      "https://cdn-test.xinyan.tech/attachments/d3o77c9NPEvA8EXaWqTCdd/meet1592989665610.gif?s=600_338&size=4816621&c=567051");
                  _album.imagesPreview(list, 2, () {
                    List<String> list2 = [];
                    list2.add(
                        "https://cdn-test.xinyan.tech/attachments/RYDn5rKLXjyRena9giegzM/Screenshot_2020-06-12-11-33-04-618_com.xinyan.meet.test.jpg?s=1080_2340&size=204883&c=fbf3c1");
                    list2.add(
                        "https://cdn-test.xinyan.tech/attachments/RYDn5rKLXjyRena9giegzM/Screenshot_2020-06-12-11-33-04-618_com.xinyan.meet.test.jpg?s=1080_2340&size=204883&c=fbf3c1");
                    _album.addHistoryFile(list2);
                  });
                },
                child: Text("大图预览"),
              ),
              SizedBox(height: 50),
              RaisedButton(
                onPressed: () async {
                  _album.checkCameraPermission().then((value) async {
                    if (value) {
                      var item = await _album.takePhoto();
                      if (item is String) {
                        print(item);
                      } else if (item is AssetEntity) {
                        print(item.toJson());
                      } else {
                        print("取消了照相！");
                      }
                    } else {
                      print("权限不够！");
                    }
                  });
                },
                child: Text("打开相机"),
              ),
              SizedBox(height: 50),
              RaisedButton(
                onPressed: () async {
                  _album.checkPhotosPermission().then((value) async {
                    if (value) {
                      var result = await _album.getLatestMediaFile(60);
                      if (result != null) {
                        for (int i = 0; i < result.length; ++i) {
                          print("$i  ${result[i].toJson()}");
                        }
                      }
                    } else {
                      print("权限不够！");
                    }
                  });
                },
                child: Text("获取相册前50个图片"),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
