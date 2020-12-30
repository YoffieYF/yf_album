//
//  WFAlbumModel.h
//  Wolf
//
//  Created by Yoffie on 2020/8/20.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Photos/Photos.h>
#import "WFAlbumItemModel.h"


@interface WFAlbumModel : NSObject

@property (nonatomic, copy) NSString * albumTitle; //相册名称
@property (nonatomic, strong) PHAsset * coverAsset; //相册封面图
@property (nonatomic, strong) NSArray * collectionPhotos; //相册中的相片
@property (nonatomic, assign) BOOL selected; //是否被选中

//获取当前选中的 WFAlbumModel
+ (WFAlbumModel *) getSelectAlbumModelInModels:(NSMutableArray<WFAlbumModel*>*) models;

@end

