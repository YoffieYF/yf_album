//
//  WFAlbumModel.m
//  Wolf
//
//  Created by Yoffie on 2020/8/20.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import "WFAlbumModel.h"

@implementation WFAlbumModel

//获取选择的相册
+ (WFAlbumModel *) getSelectAlbumModelInModels:(NSMutableArray<WFAlbumModel*>*) models {
    WFAlbumModel *model = [[WFAlbumModel alloc] init];
    if (!models) {
        return model;
    }
    for (WFAlbumModel *one in models) {
        if (one.selected) {
            model = one;
        }
    }
    return model;
}

@end
