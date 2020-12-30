//
//  WFTakePhotoAndVideoManager.h
//  Wolf
//
//  Created by Yoffie on 2020/8/31.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import <Foundation/Foundation.h>


typedef NS_ENUM(NSUInteger, systemSelectType) {
    systemSelectTypePhoto,
    systemSelectTypeVideo,
    systemSelectTypePhtotoAndVideo
};

@interface WFTakePhotoAndVideoManager : NSObject
- (instancetype)initWithSystemSelectType:(systemSelectType)systemSelectType;

- (void)takePhoto;

@property(nonatomic, copy) void(^sendCompressPhotosBlock)(NSArray *modelArray);

@end

