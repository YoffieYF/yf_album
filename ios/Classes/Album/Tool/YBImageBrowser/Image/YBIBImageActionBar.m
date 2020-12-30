//
//  YBIBImageActionBar.m
//  Runner
//
//  Created by 显铭 on 2020/4/15.
//  Copyright © 2020 The Chromium Authors. All rights reserved.
//

#import "YBIBImageActionBar.h"
#import "Masonry.h"

@interface YBIBImageActionBar ()
@property(nonatomic, strong) UIButton * moreOperationButton;
@property(nonatomic, strong) UIButton * fileButton;
//@property(nonatomic, strong) UIButton * viewOriginButton;//查看原图
@property(nonatomic, strong) CAGradientLayer * gl;
@end

@implementation YBIBImageActionBar

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self setupUI];
    }
    return self;
}

- (void)setupUI {
    
//    [self addSubview:self.fileButton];
    [self addSubview:self.moreOperationButton];
//    [self addSubview:self.viewOriginButton];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
//    CGFloat width = self.bounds.size.width, /* height = self.bounds.size.height,*/ buttonWidth = 44;
//    self.moreOperationButton.x = width - 58;
//    self.moreOperationButton.width = buttonWidth;
//    self.moreOperationButton.height = buttonWidth;
//    self.moreOperationButton.top = 76;
    
//    self.fileButton.x = width - 100;
//    self.fileButton.width = buttonWidth;
//    self.fileButton.height = buttonWidth;
//    self.fileButton.centerY = self.moreOperationButton.centerY;
    
//    self.viewOriginButton.x = 20;
//    self.viewOriginButton.centerY = self.moreOperationButton.centerY;
//    self.viewOriginButton.width = 130;
//    self.viewOriginButton.height = 35;
    
    // gradient
    self.gl.frame = self.bounds;
    self.gl.startPoint = CGPointMake(0.5, 0);
    self.gl.endPoint = CGPointMake(0.5, 1);
    self.gl.colors = @[(__bridge id)[UIColor colorWithRed:0/255.0 green:0/255.0 blue:0/255.0 alpha:0.0].CGColor, (__bridge id)[UIColor colorWithRed:0/255.0 green:0/255.0 blue:0/255.0 alpha:0.5].CGColor];
    self.gl.locations = @[@(0.0f), @(1.0f)];
    [self.layer insertSublayer:self.gl atIndex:0];
    
}

- (void)buttonClick:(UIButton *)sender {
    switch (sender.tag) {
        case 1001:
            [self.delegate yb_respondsToMoreButtonForView:self];
            break;
        case 1002:
            [self.delegate yb_respondsToFileButtonForView:self];
            break;
        case 1003:
            [self.delegate yb_respondsToView:self originButton:sender];
        default:
            break;
    }
}

- (void)yb_hideViewOriginButton:(BOOL)hide {
//    self.viewOriginButton.hidden = hide;
}


- (void)yb_resetViewOriginButtonState:(NSUInteger)imageDataSize {
    NSString *str = @"查看原图";
    if (imageDataSize > 0) {
        str = imageDataSize/1000.0 > 1? [NSString stringWithFormat:@"查看原图(%.1fM)",imageDataSize/1024.0]: [NSString stringWithFormat:@"查看原图(%ldk)",imageDataSize];
    }
//    [self.viewOriginButton setTitle:str forState:UIControlStateNormal];
    
//    CGRect rect = [str boundingRectWithSize:CGSizeMake(200, 20) options:NSStringDrawingUsesLineFragmentOrigin | NSStringDrawingUsesFontLeading attributes:@{NSFontAttributeName : [UIFont boldSystemFontOfSize:15]} context:nil];
//    self.viewOriginButton.width = rect.size.width + 15;
}


#pragma mark --- getters
- (UIButton *)moreOperationButton {
    if (!_moreOperationButton) {
        _moreOperationButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_moreOperationButton setImage:[UIImage imageNamed:@"icon_video_more"] forState:UIControlStateNormal];
        [_moreOperationButton addTarget:self action:@selector(buttonClick:) forControlEvents:UIControlEventTouchUpInside];
        _moreOperationButton.tag = 1001;
    }
    return _moreOperationButton;
}

- (UIButton *)fileButton {
    if (!_fileButton) {
        _fileButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_fileButton setImage:[UIImage imageNamed:@"icon_picture_white"] forState:UIControlStateNormal];
        [_fileButton addTarget:self action:@selector(buttonClick:) forControlEvents:UIControlEventTouchUpInside];
        _fileButton.tag = 1002;
    }
    return _fileButton;
}


#pragma mark - getters
//- (UIButton *)viewOriginButton {
//    if (!_viewOriginButton) {
//        _viewOriginButton = [UIButton buttonWithType:UIButtonTypeCustom];
//        _viewOriginButton.titleLabel.font = [UIFont boldSystemFontOfSize:15];
//        [_viewOriginButton setTitle:@"查看原图" forState:UIControlStateNormal];
//        [_viewOriginButton setTitleColor:UIColor.whiteColor forState:UIControlStateNormal];
//        _viewOriginButton.backgroundColor = [UIColor.blackColor colorWithAlphaComponent:0.4];
//        _viewOriginButton.layer.cornerRadius = 8.f;
//        _viewOriginButton.layer.borderColor = [UIColor whiteColor].CGColor;
//        _viewOriginButton.layer.borderWidth = 1.f;
//        _viewOriginButton.layer.masksToBounds = YES;
//        [_viewOriginButton addTarget:self action:@selector(buttonClick:) forControlEvents:UIControlEventTouchUpInside];
//        _viewOriginButton.tag = 1003;
//    }
//    return _viewOriginButton;
//}

- (CAGradientLayer *)gl {
    if (!_gl) {
        _gl = [CAGradientLayer layer];
    }
    return _gl;
}

@end
