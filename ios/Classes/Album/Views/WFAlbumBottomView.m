//
//  WFAlbumBottomView.m
//  Wolf
//
//  Created by Yoffie on 2020/8/24.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import "WFAlbumBottomView.h"
#import "WFMacro.h"


@interface WFAlbumBottomView ()

@property (nonatomic, strong) UIButton *previewButton;
@property (nonatomic, strong) UIButton *originButton;
@property (nonatomic, strong) UIButton *sendButton;

@end


@implementation WFAlbumBottomView

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self setupViews];
    }
    return self;
}

#pragma mark - SetupViews
- (void)setupViews {
    self.backgroundColor = [UIColor blackColor];
    
    [self addSubview:self.previewButton];
    [self.previewButton mas_makeConstraints:^(MASConstraintMaker *make) {
        CGFloat tabbarH = (WF_IS_iPhoneX ? 94. : 60);
        CGFloat top = (tabbarH - 40) * 0.5;
        make.left.mas_equalTo(self).offset(10);
        make.top.mas_equalTo(self).offset(top);
        make.height.mas_offset(40);
        make.width.mas_offset(80);
    }];
    
    [self addSubview:self.originButton];
    [self.originButton mas_makeConstraints:^(MASConstraintMaker *make) {
        CGFloat tabbarH = (WF_IS_iPhoneX ? 94. : 60);
        CGFloat left = (WF_SCREEN_WIDTH - 100)*0.5;
        CGFloat top = (tabbarH - 40) * 0.5;
        make.left.mas_equalTo(self).offset(left);
        make.top.mas_equalTo(self).offset(top);
        make.height.mas_offset(40);
        make.width.mas_offset(100);
    }];
    
    [self addSubview:self.sendButton];
    [self.sendButton mas_makeConstraints:^(MASConstraintMaker *make) {
        CGFloat tabbarH = (WF_IS_iPhoneX ? 94. : 60);
        CGFloat top = (tabbarH - 40) * 0.5;
        make.right.mas_equalTo(self).offset(-10);
        make.top.mas_equalTo(self).offset(top);
        make.height.mas_offset(40);
        make.width.mas_offset(80);
    }];
}

#pragma mark - Function
- (void) hidePreviewBtn {
    self.previewButton.hidden = YES;
}

- (void)sendImagesHandler:(UIButton *)sender {
    if (self.delegate && [self.delegate respondsToSelector:@selector(sendImagesHandler:)]) {
        [self.delegate sendImagesHandler:self];
    }
}

- (void)clickImagesPreview:(UIButton *)sender {
    if (self.delegate && [self.delegate respondsToSelector:@selector(clickImagesPreview:)]) {
        [self.delegate clickImagesPreview:self];
    }
}

- (void)chooseOriginalPhoto:(UIButton *)sender {
    sender.selected = !sender.selected;
    if (self.delegate && [self.delegate respondsToSelector:@selector(chooseOriginalPhoto:imagesIsOrginal:)]) {
        [self.delegate chooseOriginalPhoto:self imagesIsOrginal:sender.selected];
    }
}

- (UIButton *)previewButton {
    if (!_previewButton) {
        _previewButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_previewButton setTitle:@"预览" forState:UIControlStateNormal];
        [_previewButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _previewButton.titleLabel.font = [UIFont systemFontOfSize:17];
        [_previewButton addTarget:self action:@selector(clickImagesPreview:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _previewButton;
}

- (UIButton *)originButton {
    if (!_originButton) {
        _originButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_originButton setImage:[WFMacro getBundleImageWithName:@"icon_origin_photo_unselected"] forState:UIControlStateNormal];
        [_originButton setImage:[WFMacro getBundleImageWithName:@"icon_origin_photo_selected"] forState:UIControlStateSelected];
        [_originButton setTitle:@"原图" forState:UIControlStateNormal];
        [_originButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _originButton.titleLabel.font = [UIFont systemFontOfSize:14];
        [_originButton setImageEdgeInsets:UIEdgeInsetsMake(0, -5, 0, 0)];
        [_originButton addTarget:self action:@selector(chooseOriginalPhoto:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _originButton;
}

- (UIButton *)sendButton {
    if (!_sendButton) {
        _sendButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_sendButton setTitle:@"发送" forState:UIControlStateNormal];
        [_sendButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _sendButton.titleLabel.font = [UIFont systemFontOfSize:15];
        [_sendButton setBackgroundColor: WF_STR_AHEX(@"#742F0F")];
        _sendButton.layer.cornerRadius = 8.f;
        _sendButton.layer.masksToBounds = YES;
        _sendButton.layer.shadowColor = [UIColor colorWithRed:255/255.0 green:223/255.0 blue:0/255.0 alpha:0.1].CGColor;
        _sendButton.layer.shadowOffset = CGSizeMake(0,6.7);
        _sendButton.layer.shadowOpacity = 1;
        [_sendButton addTarget:self action:@selector(sendImagesHandler:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _sendButton;
}

#pragma mark - Parameter Set
- (void)setImagesIsOrginal:(BOOL)imagesIsOrginal {
    self.originButton.selected = imagesIsOrginal;
}

@end
