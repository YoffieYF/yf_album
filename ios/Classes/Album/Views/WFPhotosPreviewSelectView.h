//
//  WFPhotosPreviewSelectView.h
//  Wolf
//
//  Created by Yoffie on 2020/8/27.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Photos/Photos.h>
#import "WFAlbumItemModel.h"


@class WFPhotosPreviewSelectView;


@protocol WFPhotosPreviewSelectViewDelegate <NSObject>

- (void)tapImageItem:(WFPhotosPreviewSelectView *)view withModel:(WFAlbumItemModel *)model;

@end


@interface WFPhotosPreviewSelectView : UIView

@property (nonatomic, strong) NSMutableArray<WFAlbumItemModel*> *selectModels;
@property (nonatomic, weak) id<WFPhotosPreviewSelectViewDelegate> delegate;

- (void) collectionReload;

@end

