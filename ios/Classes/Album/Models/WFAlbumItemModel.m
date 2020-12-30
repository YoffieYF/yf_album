//
//  WFAlbumCollectionCellModel.m
//  Wolf
//
//  Created by Yoffie on 2020/8/21.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import "WFAlbumItemModel.h"

@implementation WFAlbumItemModel

//获取选中的个数 如果没有返回0
+ (NSString *)getIndexWithModels:(NSMutableArray<WFAlbumItemModel*>*)models {
    int num = 0;
    if (!models) {
        return [NSString stringWithFormat:@"%d", num];
    }
    for (WFAlbumItemModel *one in models) {
        if (one.selected) {
            num += 1;
        }
    }
    return [NSString stringWithFormat:@"%d", num];
}

//重新排序选中的位置
+ (NSMutableArray<WFAlbumItemModel*>*)sortIndexWithModels:(NSMutableArray<WFAlbumItemModel*>*)models {
    int num = 0;
    if (!models) {
        return models;
    }
    for (WFAlbumItemModel *one in models) {
        one.indexStr = @"";
        if (one.selected) {
            num += 1;
            one.indexStr = [NSString stringWithFormat:@"%d", num];
        }
    }
    return models;
}

+ (NSMutableArray<WFAlbumItemModel*>*)getSelectModels:(NSMutableArray<WFAlbumItemModel*>*)models {
    NSMutableArray<WFAlbumItemModel*> *selectModels = [[NSMutableArray alloc]init];
    for (WFAlbumItemModel *one in models) {
        if (one.selected) {
            [selectModels addObject:one];
        }
    }
    return selectModels;
}

+ (NSMutableArray<PHAsset*>*)getSelectModelsAsset:(NSMutableArray<WFAlbumItemModel*>*)models {
    NSMutableArray<PHAsset*> *selectPHAsset = [[NSMutableArray alloc]init];
    for (WFAlbumItemModel *one in models) {
        if (one.selected) {
            [selectPHAsset addObject:one.coverAsset];
        }
    }
    return selectPHAsset;
}

+ (NSInteger) findIndexInModels:(NSMutableArray<WFAlbumItemModel*>*)models withPhasset:(PHAsset *)asset {
    NSInteger index = -1;
    for (WFAlbumItemModel *one in models) {
        if (one.coverAsset == asset) {
            index = [models indexOfObject:one];
            break;
        }
    }
    return index;
}

@end
