import 'package:json_annotation/json_annotation.dart';

part 'asset_entity.g.dart';

enum AssetEntityType { IMAGE, VIDEO }

@JsonSerializable()
class AssetEntity {
  String fileName; //文件名称
  int w; //宽
  int h; //高
  String path; //文件路径
  String coverPath; //视频封面图：如果是AssetEntityModelType == IMAGE，那就是path
  AssetEntityType type; //IMAGE：表示图片 VIDEO：表示视频
  int isOriginal; //是否是原图发送，0表示不是，1表示是
  int videoDuration; // 视频时长

  AssetEntity(
      {this.fileName,
      this.w,
      this.h,
      this.path,
      this.coverPath,
      this.type,
      this.isOriginal,
      this.videoDuration});

  factory AssetEntity.fromJson(Map<String, dynamic> json) => _$AssetEntityFromJson(json);

  Map<String, dynamic> toJson() => _$AssetEntityToJson(this);
}
