import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

import 'asset_entity.dart';

class Album {
  static final Album _instance = new Album._internal();
  static Album get instance => _instance;
  Album._internal();

  static const int albumTypeMultiple = 0;
  static const int albumTypeRadioAddCrop = 1;
  static const int albumTypeSingle = 2;
  static const int takePhotoSingle = 1;
  static const int takePhotoAddCrop = 2;

  Function(AssetEntity) _compressCallBack;
  Function() _loadHistoryCallBack;

  MethodChannel _channel = const MethodChannel('flutter.plugin/customAlbum');

  Album() {
    _channel.setMethodCallHandler((MethodCall call) async {
      switch (call.method) {
        case "compressCompletion":
          if (_compressCallBack != null && call.arguments is Map) {
            _compressCallBack(AssetEntity.fromJson(Map<String, dynamic>.from(call.arguments)));
          }
          break;
        case "loadHistoryFiles":
          if (_loadHistoryCallBack != null) {
            _loadHistoryCallBack();
          }
          break;
      }
    });
  }

  Future<bool> checkPhotosPermission() async {
    return await checkPermission([Platform.isIOS ? Permission.photos : Permission.storage]);
  }

  Future<bool> checkCameraPermission() async {
    return await checkPermission([Platform.isIOS ? Permission.photos : Permission.camera]);
  }

  ///选择多张图片，返回原图路径
  Future<List<AssetEntity>> openAlbumOfMultiple() async {
    if (!await checkPhotosPermission()) return null;
    try {
      var result = await _channel.invokeMethod('openAlbum', albumTypeMultiple);
      if (result is List && result.isNotEmpty) {
        List<AssetEntity> itemList = [];
        for (var tempItem in result) {
          itemList.add(AssetEntity.fromJson(Map<String, dynamic>.from(tempItem)));
        }
        return itemList;
      }
    } catch (e) {}
    return null;
  }

  ///单选自带压缩效果，截剪不支持gif
  Future<String> openAlbumOfRadio({bool addCrop = false}) async {
    if (!await checkPhotosPermission()) return null;
    try {
      var result = await _channel.invokeMethod(
          'openAlbum', addCrop ? albumTypeRadioAddCrop : albumTypeSingle);
      if (result is String) {
        return result;
      }
    } catch (e) {}
    return null;
  }

  /// addCrop: true 拍照加裁剪, 直接返回裁剪后的路径
  /// addCrop: false 单纯拍照，返回 AssetEntity
  Future<dynamic> takePhoto({bool addCrop = false}) async {
    if (!await checkCameraPermission()) return null;
    try {
      var result =
          await _channel.invokeMethod('takePhoto', addCrop ? takePhotoAddCrop : takePhotoSingle);
      if (result is String) {
        return result;
      } else if (result is Map) {
        return AssetEntity.fromJson(Map<String, dynamic>.from(result));
      }
    } catch (e) {}
    return null;
  }

  Future<List<AssetEntity>> getLatestMediaFile([int count = 50]) async {
    if (!await checkPhotosPermission()) return null;
    try {
      var result = await _channel.invokeMethod('getLatestMediaFile', count);

      if (result is List && result.isNotEmpty) {
        List<AssetEntity> itemList = [];
        for (var tempItem in result) {
          itemList.add(AssetEntity.fromJson(Map<String, dynamic>.from(tempItem)));
        }
        return itemList;
      }
    } catch (e) {}
    return null;
  }

  void mediaCompress(List<AssetEntity> itemList, Function(AssetEntity) compressCallBack) async {
    if (!await checkPhotosPermission()) return;
    try {
      _compressCallBack = compressCallBack;
      List<Map<String, dynamic>> mapList = [];
      for (AssetEntity item in itemList) {
        mapList.add(item.toJson());
      }
      _channel.invokeMethod('mediaCompress', mapList);
    } catch (e) {}
    return null;
  }

  void imagesPreview(List<String> picArr, var index, Function() loadHistoryCallBack) async {
    try {
      _loadHistoryCallBack = loadHistoryCallBack;
      await _channel.invokeMethod('imagesPreview', {'data': picArr, 'idx': index});
    } catch (e) {}
  }

  Future<void> addHistoryFile(List<String> fileList) async {
    try {
      await _channel.invokeMethod('addHistoryFiles', fileList);
    } catch (e) {}
  }

  static Future<bool> checkPermission(List<Permission> permissions) async {
    bool isAllGranted = true;
    List<bool> grantedList = permissions.map((e) => false).toList();

    for (var i = 0; i < permissions.length; ++i) {
      var permission = await permissions[i].request();
      if (permission.isGranted) {
        grantedList[i] = true;
      } else {
        grantedList[i] = false;
      }
    }

    if (grantedList != null && grantedList.isNotEmpty) {
      grantedList.forEach((granted) {
        if (!granted) {
          // 存在一个没有授权，返回false，否则默认true
          isAllGranted = false;
        }
      });
    }
    return isAllGranted;
  }
}
