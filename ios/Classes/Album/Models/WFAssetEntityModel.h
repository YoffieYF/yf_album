//
//  WFAssetEntityModel.h
//  Wolf
//
//  Created by Yoffie on 2020/8/28.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import <Foundation/Foundation.h>


typedef NS_ENUM(NSUInteger, EntityType) {
    EntityTypeImage,
    EntityTypeVideo,
};


@interface WFAssetEntityModel : NSObject<NSCoding>

@property(nonatomic, copy) NSString * fileName;//文件名
@property(nonatomic, assign) NSUInteger w;//宽度
@property(nonatomic, assign) NSUInteger h;//高度
@property(nonatomic, copy) NSString * path;//文件路径
@property(nonatomic, copy) NSString * coverPath;//封面图片路径
@property(nonatomic, copy) NSString * type;//类型
@property(nonatomic, assign) int isOriginal;//是否是原图发送，0表示不是，1表示是
@property(nonatomic, assign) BOOL isCompressSuccess;//是否压缩成功
@property(nonatomic, assign) int videoDuration;//视频时长

@end

