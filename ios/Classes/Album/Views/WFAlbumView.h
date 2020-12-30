//
//  WFAlbumView.h
//  Wolf
//
//  Created by Yoffie on 2020/8/18.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "WFAlbumModel.h"
#import "WFAlbumChooseView.h"
#import "WFAlbumCollectionView.h"
#import "WFAlbumBottomView.h"


@class WFAlbumView;


@protocol WFAlbumViewDelegate <NSObject, WFAlbumChooseViewDelegate, WFAlbumBottomViewDelegate, WFAlbumCollectionViewDelegate>

@optional
- (void)closeAlbum:(WFAlbumView *)albumView;

@end


@interface WFAlbumView : UIView

//Data
@property(nonatomic, strong) NSMutableArray<WFAlbumModel *> * albumArray;
@property (nonatomic, weak) id<WFAlbumViewDelegate>delegate;
@property (nonatomic, assign) BOOL imagesIsOrginal;

//Function
- (NSMutableArray<PHAsset*> *) getSelectArraym;
- (void)setSelectArraymWithData: (NSMutableArray<PHAsset*> *)data;

@end
