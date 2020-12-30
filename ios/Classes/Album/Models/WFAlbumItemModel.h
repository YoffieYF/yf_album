//
//  WFAlbumItemModel.h
//  Wolf
//
//  Created by Yoffie on 2020/8/21.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Photos/Photos.h>


@interface WFAlbumItemModel : NSObject

@property (nonatomic, strong) PHAsset *coverAsset; //相片
@property (nonatomic, copy) NSString *indexStr; //相册中的位置
@property (nonatomic, assign) BOOL selected; //是否被选中
@property (nonatomic, assign) BOOL showSelectImage; //UI控制：是否显示右上角的选中图标

//获取最后一个位置 如果没有返回0
+ (NSString *)getIndexWithModels:(NSMutableArray<WFAlbumItemModel*>*)models;

//重新排序选中的位置
+ (NSMutableArray<WFAlbumItemModel*>*)sortIndexWithModels:(NSMutableArray<WFAlbumItemModel*>*)models;

//获取选中的model
+ (NSMutableArray<WFAlbumItemModel*>*)getSelectModels:(NSMutableArray<WFAlbumItemModel*>*)models;

//获取选中的Asset
+ (NSMutableArray<PHAsset*>*)getSelectModelsAsset:(NSMutableArray<WFAlbumItemModel*>*)models;

//根据asset查找位置
+ (NSInteger) findIndexInModels:(NSMutableArray<WFAlbumItemModel*>*)models withPhasset:(PHAsset *)asset;

@end

