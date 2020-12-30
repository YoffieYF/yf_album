//
//  WFAlbumChooseView.h
//  Wolf
//
//  Created by Yoffie on 2020/8/19.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "WFAlbumModel.h"


@class WFAlbumChooseView;


@protocol WFAlbumChooseViewDelegate <NSObject>

@optional
- (void)selectAlbum:(WFAlbumChooseView *) albumChooseView withSelectAlbumModel:(WFAlbumModel *) selectModel withOldAlbumModel:(WFAlbumModel *) oldModel;

@end


@interface WFAlbumChooseView : UIView

@property(nonatomic, strong) NSMutableArray<WFAlbumModel *> * albumArray;
@property (nonatomic, weak) id<WFAlbumChooseViewDelegate>delegate;

- (void)showInView:(UIView *)view;
- (void)dismiss;

@end

