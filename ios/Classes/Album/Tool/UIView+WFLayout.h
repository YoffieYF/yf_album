//
//  UIView+RedPoint.h
//  kiwi
//
//  Created by lslin on 15/3/24.
//  Copyright (c) 2015年 com.mewe.party. All rights reserved.
//
#import <UIKit/UIKit.h>

@interface UIView (WFLayout)

- (id)initWithParent:(UIView *)parent;
+ (id)viewWithParent:(UIView *)parent;
-(void)removeAllSubViews;

// Position of the top-left corner in superview's coordinates
@property CGPoint position;
@property CGFloat x;
@property CGFloat y;
@property CGFloat top;
@property CGFloat bottom;
@property CGFloat left;
@property CGFloat right;

// makes hiding more logical
@property BOOL	visible;


// Setting size keeps the position (top-left corner) constant
@property CGSize size;
@property CGFloat width;
@property CGFloat height;

- (void)wf_centerHor:(UIView*)parentView;

- (void)wf_centerVer:(UIView*)parentView;

- (void)wf_center:(UIView*)parentView;

- (void)wf_centerVer;
- (void)wf_centerHor;
- (void)wf_center;

- (void)wf_setAnchorPoint:(CGPoint)point;

@end
