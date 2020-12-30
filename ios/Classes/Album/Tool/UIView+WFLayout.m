//
//  UIView+RedPoint.m
//  kiwi
//
//  Created by lslin on 15/3/24.
//  Copyright (c) 2015年 com.mewe.party. All rights reserved.
//

#import "UIView+WFLayout.h"
#import <QuartzCore/QuartzCore.h>

@implementation UIView (WFLayout)

- (id)initWithParent:(UIView *)parent {
    self = [self initWithFrame:CGRectZero];
    
    if (!self)
        return nil;
    
    [parent addSubview:self];
    
    return self;
}

+ (id)viewWithParent:(UIView *)parent {
    return [[self alloc] initWithParent:parent];
}

- (CGPoint)position {
    return self.frame.origin;
}

- (void)setPosition:(CGPoint)position {
    CGRect rect = self.frame;
    rect.origin = position;
    [self setFrame:rect];
}

- (CGFloat)x
{
    return self.frame.origin.x;
}

- (void)setX:(CGFloat)x {
    CGRect rect = self.frame;
    rect.origin.x = x;
    [self setFrame:rect];
}

- (CGFloat)y
{
    return self.frame.origin.y;
}

- (void)setY:(CGFloat)y
{
    CGRect rect = self.frame;
    rect.origin.y = y;
    [self setFrame:rect];
}


- (CGFloat)left
{
    return self.frame.origin.x;
}

- (void)setLeft:(CGFloat)x
{
    CGRect frame = self.frame;
    frame.origin.x = x;
    self.frame = frame;
}

- (CGFloat)top {
    return self.frame.origin.y;
}

- (void)setTop:(CGFloat)y
{
    CGRect frame = self.frame;
    frame.origin.y = y;
    self.frame = frame;
}

- (CGFloat)right
{
    return self.frame.origin.x + self.frame.size.width;
}

- (void)setRight:(CGFloat)right
{
    CGRect frame = self.frame;
    frame.origin.x = right - frame.size.width;
    self.frame = frame;
}

- (CGFloat)bottom
{
    return self.frame.origin.y + self.frame.size.height;
}

- (void)setBottom:(CGFloat)bottom
{
    CGRect frame = self.frame;
    frame.origin.y = bottom - frame.size.height;
    self.frame = frame;
}

- (BOOL)visible {
    return !self.hidden;
}

- (void)setVisible:(BOOL)visible
{
    self.hidden=!visible;
}


- (void)removeAllSubViews
{
    for (NSInteger i = self.subviews.count - 1; i>=0; i--){
        UIView* subView = [self.subviews objectAtIndex:i];
        [subView removeFromSuperview];
    }
}

- (CGSize)size
{
    return [self frame].size;
}

- (void)setSize:(CGSize)size
{
    CGRect rect = self.frame;
    rect.size = size;
    [self setFrame:rect];
}

- (CGFloat)width
{
    return self.frame.size.width;
}

- (void)setWidth:(CGFloat)width
{
    CGRect rect = self.frame;
    rect.size.width = width;
    [self setFrame:rect];
}

- (CGFloat)height
{
    return self.frame.size.height;
}

- (void)setHeight:(CGFloat)height
{
    CGRect rect = self.frame;
    rect.size.height = height;
    [self setFrame:rect];
}

- (void)wf_centerHor:(UIView*)parentView
{
    self.left = ceilf((parentView.width - self.width)/ 2);
}

- (void)wf_centerVer:(UIView*)parentView
{
    self.top  = ceilf((parentView.height - self.height)/ 2);
}

- (void)wf_centerVer
{
    if (self.superview) {
        self.top  = ceilf((self.superview.height - self.height)/ 2);
    }
}

- (void)wf_centerHor
{
    if (self.superview) {
        self.left = ceilf((self.superview.width - self.width)/ 2);
    }
}

- (void)wf_center
{
    [self wf_centerHor];
    [self wf_centerVer];
}

- (void)wf_center:(UIView*)parentView
{
    [self wf_centerHor:parentView];
    [self wf_centerVer:parentView];
}

-(void)wf_setAnchorPoint:(CGPoint)point
{
    CGRect oldFrame = self.frame;
    self.layer.anchorPoint = point;
    self.frame = oldFrame;
}

@end
