//
//  WFAlbumCollectionCell.m
//  Wolf
//
//  Created by Yoffie on 2020/8/20.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import "WFAlbumCollectionCell.h"
//#import "AutolayoutUtils.h"
#import "WFAlbumTool.h"
#import "WFMacro.h"


@interface WFAlbumCollectionCell ()

@property (nonatomic, strong) UIImageView *iconImageView;
@property (nonatomic, strong) UIButton *indexBtn;;
@property (nonatomic, strong) UIImageView *indexImageView;

@end


@implementation WFAlbumCollectionCell

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        [self setupView];
    }
    return self;
}

#pragma mark - SetupView
- (void) setupView {
    [self.contentView addSubview:self.iconImageView];
    [self.iconImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(self.contentView);
        make.centerY.mas_equalTo(self.contentView);
        make.width.height.mas_equalTo(self.contentView);
    }];
    
    [self.contentView addSubview:self.indexBtn];
    [self.indexBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(self.contentView);
        make.top.mas_equalTo(self.contentView);
        make.height.width.mas_equalTo(28);
    }];
    
    [self.contentView insertSubview:self.indexImageView belowSubview:self.indexBtn];
    [self.indexImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.center.mas_equalTo(self.indexBtn);
        make.height.width.mas_equalTo(20);
    }];
}

#pragma mark - Function
-(void)indexBtnClick:(UIButton*) sender {
    if (self.selectItemBlock) {
        self.selectItemBlock(self.asset);
    }
}

- (void)resetViews:(PHAsset *)asset withSelectAsset:(NSMutableArray*)selectedPhAssets{
    [[WFAlbumTool shareInstance] getVideoImageFromPHAsset:asset forSize:CGSizeMake(200, 200) completedResultBack:^(UIImage *image) {
        self.iconImageView.image = image;
    }];
    if ([selectedPhAssets containsObject:asset]) {
        NSString *indexStr = @([selectedPhAssets indexOfObject:asset] + 1).description;
        [self.indexBtn setTitle:indexStr forState:UIControlStateNormal];
        self.indexImageView.image = [WFMacro getBundleImageWithName:@"icon_photo_selected"];
      
    } else {
        self.indexImageView.image = [WFMacro getBundleImageWithName:@"icon_photo_unselected"];
        [self.indexBtn setTitle:@"" forState:UIControlStateNormal];
    }
}

#pragma mark - Parameter Get
- (UIImageView *)iconImageView {
    if (!_iconImageView) {
        _iconImageView = [[UIImageView alloc] init];
        _iconImageView.clipsToBounds = YES;
        _iconImageView.layer.masksToBounds = YES;
        _iconImageView.contentMode = UIViewContentModeScaleAspectFill;
    }
    return _iconImageView;
}

- (UIButton *)indexBtn {
    if (!_indexBtn) {
        _indexBtn = [[UIButton alloc] init];
        [_indexBtn setTitle:@"" forState:UIControlStateNormal];
        [_indexBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _indexBtn.titleLabel.font = [UIFont systemFontOfSize:14];
        [_indexBtn addTarget:self action:@selector(indexBtnClick:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _indexBtn;
}

- (UIImageView *)indexImageView {
    if (!_indexImageView) {
        _indexImageView = [[UIImageView alloc] init];
        _indexImageView.image = [WFMacro getBundleImageWithName:@"icon_photo_unselected"];
    }
    return _indexImageView;
}

#pragma mark - Parameter Set
- (void)setAsset:(PHAsset *)asset {
    _asset = asset;
}

@end


