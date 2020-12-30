//
//  YBIBImageActionBar.h
//  Runner
//
//  Created by 显铭 on 2020/4/15.
//  Copyright © 2020 The Chromium Authors. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@class YBIBImageActionBar;
@protocol YBIBImageActionBarDelegate <NSObject>

- (void)yb_respondsToMoreButtonForView:(YBIBImageActionBar *)view;

- (void)yb_respondsToFileButtonForView:(YBIBImageActionBar *)view;

- (void)yb_respondsToView:(YBIBImageActionBar *)view originButton:(UIButton *)originalButton;

@end

@interface YBIBImageActionBar : UIView
@property(nonatomic, weak) id<YBIBImageActionBarDelegate> delegate;

- (void)yb_hideViewOriginButton:(BOOL)hide;

- (void)yb_resetViewOriginButtonState:(NSUInteger)imageDataSize;

@end

NS_ASSUME_NONNULL_END
