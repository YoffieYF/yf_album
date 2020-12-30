//
//  WFPhotosPreviewCollectionView.h
//  Wolf
//
//  Created by Yoffie on 2020/8/24.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Photos/Photos.h>
#import "WFAlbumItemModel.h"


@class WFPhotosPreviewCollectionView;


@protocol WFPhotosPreviewCollectionViewDelegate <NSObject>

- (void)imageBrowser:(WFPhotosPreviewCollectionView *)view pageChanged:(NSInteger)page data:(WFAlbumItemModel *)data;
- (void)tapImage:(WFPhotosPreviewCollectionView *)view;

@end


@interface WFPhotosPreviewCollectionView : UIView

@property (nonatomic, strong) NSMutableArray<WFAlbumItemModel*> *albumItemModels;
@property (nonatomic, strong) NSMutableArray<PHAsset*> *selectArraym;
@property (nonatomic, assign) NSInteger showIndex;
@property (nonatomic, weak) id<WFPhotosPreviewCollectionViewDelegate> delegate;

@end

