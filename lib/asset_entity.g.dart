// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'asset_entity.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

AssetEntity _$AssetEntityFromJson(Map<String, dynamic> json) {
  return AssetEntity(
      fileName: json['fileName'] as String,
      w: json['w'] as int,
      h: json['h'] as int,
      path: json['path'] as String,
      coverPath: json['coverPath'] as String,
      type: _$enumDecodeNullable(_$AssetEntityTypeEnumMap, json['type']),
      isOriginal: json['isOriginal'] as int,
      videoDuration: json['videoDuration'] as int);
}

Map<String, dynamic> _$AssetEntityToJson(AssetEntity instance) =>
    <String, dynamic>{
      'fileName': instance.fileName,
      'w': instance.w,
      'h': instance.h,
      'path': instance.path,
      'coverPath': instance.coverPath,
      'type': _$AssetEntityTypeEnumMap[instance.type],
      'isOriginal': instance.isOriginal,
      'videoDuration': instance.videoDuration
    };

T _$enumDecode<T>(Map<T, dynamic> enumValues, dynamic source) {
  if (source == null) {
    throw ArgumentError('A value must be provided. Supported values: '
        '${enumValues.values.join(', ')}');
  }
  return enumValues.entries
      .singleWhere((e) => e.value == source,
          orElse: () => throw ArgumentError(
              '`$source` is not one of the supported values: '
              '${enumValues.values.join(', ')}'))
      .key;
}

T _$enumDecodeNullable<T>(Map<T, dynamic> enumValues, dynamic source) {
  if (source == null) {
    return null;
  }
  return _$enumDecode<T>(enumValues, source);
}

const _$AssetEntityTypeEnumMap = <AssetEntityType, dynamic>{
  AssetEntityType.IMAGE: 'IMAGE',
  AssetEntityType.VIDEO: 'VIDEO'
};
