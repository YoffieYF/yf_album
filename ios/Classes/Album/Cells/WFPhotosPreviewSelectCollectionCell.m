//
//  WFPhotosPreviewSelectCollectionCell.m
//  Wolf
//
//  Created by Yoffie on 2020/8/27.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import "WFPhotosPreviewSelectCollectionCell.h"
#import "WFMacro.h"
#import "WFAlbumTool.h"


@interface WFPhotosPreviewSelectCollectionCell ()

@property (nonatomic, strong) UIImageView *imageView;
@property (nonatomic, strong) UIImageView *selectImageView;

@end


@implementation WFPhotosPreviewSelectCollectionCell

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self setupViews];
    }
    return self;
}

#pragma mark - SetupView
- (void)setupViews {
    [self.contentView addSubview:self.imageView];
    [self.imageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(self.contentView);
        make.centerY.mas_equalTo(self.contentView);
        make.width.height.mas_equalTo(self.contentView);
    }];
    
    [self.contentView addSubview:self.selectImageView];
    [self.selectImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(self.contentView).mas_offset(-2);
        make.top.mas_equalTo(self.contentView).mas_offset(2);
        make.height.width.mas_equalTo(22);
    }];
}

#pragma mark - Function

#pragma mark - Parameter Get
- (UIImageView *)imageView {
    if (!_imageView) {
        _imageView = [[UIImageView alloc] init];
        _imageView.clipsToBounds = YES;
        _imageView.layer.masksToBounds = YES;
        _imageView.contentMode = UIViewContentModeScaleAspectFill;
    }
    return _imageView;
}

- (UIView *)selectImageView {
    if (!_selectImageView) {
        _selectImageView = [[UIImageView alloc] init];
        _selectImageView.image = [WFMacro getBundleImageWithName:@"icon_photo_selected"];
    }
    return _selectImageView;
}

#pragma mark - Parameter Set
- (void)setModel:(WFAlbumItemModel *)model {
    _model = model;
    self.selectImageView.hidden = !model.showSelectImage;
    if (model.selected) {
        _selectImageView.image = [WFMacro getBundleImageWithName:@"icon_photo_selected"];
    } else {
        _selectImageView.image = [WFMacro getBundleImageWithName:@"icon_photo_unselected"];
    }
    [[WFAlbumTool shareInstance] getVideoImageFromPHAsset:model.coverAsset forSize:CGSizeMake(200, 200) completedResultBack:^(UIImage *image) {
        self.imageView.image = image;
    }];
}

@end
