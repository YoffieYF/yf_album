//
//  WFAlbumCollectionView.h
//  Wolf
//
//  Created by Yoffie on 2020/8/20.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "WFAlbumModel.h"


@class WFAlbumCollectionView;


@protocol WFAlbumCollectionViewDelegate <NSObject>

@optional
- (void)tapAlbumImage:(WFAlbumCollectionView *)albumCollectionView curWFAlbumModel:(WFAlbumModel *)model imageIndex:(NSInteger) imageIndex;

@end


@interface WFAlbumCollectionView : UIView

@property (nonatomic, strong) WFAlbumModel *albumModel;
@property (nonatomic, weak) id<WFAlbumCollectionViewDelegate> delegate;

//Function
- (NSMutableArray<PHAsset*> *) getSelectArraym;
- (void)setSelectArraymWithData: (NSMutableArray<PHAsset*> *)data;

@end

