//
//  WFPhotosPreviewView.h
//  Wolf
//
//  Created by Yoffie on 2020/8/24.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "WFAlbumItemModel.h"
#import "WFAlbumTool.h"
#import "WFAlbumBottomView.h"
#import "WFPhotosPreviewCollectionView.h"
#import "WFPhotosPreviewSelectView.h"


@class WFPhotosPreviewView;


@protocol WFPhotosPreviewViewDelegate <NSObject, WFAlbumBottomViewDelegate>

@optional
- (void) selectBtnClick: (WFPhotosPreviewView *)view withIndex:(NSInteger)index;
- (void) closePreview: (WFPhotosPreviewView *)view;

@end


@interface WFPhotosPreviewView : UIView

@property (nonatomic, strong) NSMutableArray<WFAlbumItemModel*> *albumItemModels;
@property (nonatomic, strong) NSMutableArray<PHAsset*> *selectArraym;
@property (nonatomic, assign) NSInteger firstShowIndex;
@property (nonatomic, assign) WFPhotosPreviewViewControllerType type;
@property (nonatomic, weak) id<WFPhotosPreviewViewDelegate>delegate;
@property (nonatomic, assign) BOOL imagesIsOrginal;

- (void)reloadPreviewSelectView:(NSMutableArray<WFAlbumItemModel*> *)data;

@end

