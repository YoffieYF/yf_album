//
//  WFAlbumBottomView.h
//  Wolf
//
//  Created by Yoffie on 2020/8/24.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import <UIKit/UIKit.h>

@class WFAlbumBottomView;

@protocol WFAlbumBottomViewDelegate <NSObject>

@optional
- (void)sendImagesHandler:(WFAlbumBottomView *)view;
- (void)clickImagesPreview:(WFAlbumBottomView *)view;
- (void)chooseOriginalPhoto:(WFAlbumBottomView *)view imagesIsOrginal:(BOOL)imagesIsOrginal;

@end

@interface WFAlbumBottomView : UIView

@property (nonatomic, assign) BOOL imagesIsOrginal;

@property (nonatomic, weak) id<WFAlbumBottomViewDelegate> delegate;

- (void) hidePreviewBtn;

@end

