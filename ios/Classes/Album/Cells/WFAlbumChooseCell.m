//
//  WFAlbumChooseCell.m
//  Wolf
//
//  Created by Yoffie on 2020/8/19.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import "WFAlbumChooseCell.h"
#import "WFMacro.h"
#import "WFAlbumTool.h"



@interface WFAlbumChooseCell ()

@property (nonatomic, strong) UIImageView *coverImageView;
@property (nonatomic, strong) UILabel *coverNameLabel;
@property (nonatomic, strong) UILabel *numberLabel;
@property (nonatomic, strong) UIImageView *selectImageView;
@property (nonatomic, strong) UILabel *seperatorLine;

@end


@implementation WFAlbumChooseCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        self.backgroundColor = [UIColor clearColor];
        self.contentView.backgroundColor = [UIColor clearColor];
        
        [self setupView];
        
    }
    return self;
}

#pragma mark - SetupView
- (void)setupView {
    [self.contentView addSubview:self.coverImageView];
    [self.coverImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.top.mas_equalTo(0);
        make.width.mas_equalTo(60);
        make.height.mas_equalTo(60);
    }];
    
    [self.contentView addSubview:self.coverNameLabel];
    [self.coverNameLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.bottom.mas_equalTo(self.contentView);
        make.left.mas_equalTo(self.coverImageView.mas_right).offset(14);
    }];
    
    [self.contentView addSubview:self.numberLabel];
    [self.numberLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.bottom.mas_equalTo(self.contentView);
        make.left.mas_equalTo(self.coverNameLabel.mas_right).offset(5);
    }];
    
    [self.contentView addSubview:self.selectImageView];
    [self.selectImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(self.contentView);
        make.right.mas_equalTo(self.contentView).offset(-30);
        make.width.height.mas_equalTo(20);
    }];
    
    [self.contentView addSubview:self.seperatorLine];
    [self.seperatorLine mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.bottom.mas_equalTo(self.contentView);
        make.height.mas_equalTo(0.5);
    }];
}

#pragma mark - Parameter Get
- (UIImageView *)coverImageView {
    if (!_coverImageView) {
        _coverImageView = [[UIImageView alloc] init];
    }
    return _coverImageView;
}

- (UILabel *)coverNameLabel {
    if (!_coverNameLabel) {
        _coverNameLabel = [[UILabel alloc] init];
        [_coverNameLabel setTextColor:[UIColor whiteColor]];
        _coverNameLabel.font = [UIFont systemFontOfSize:17];
        _coverNameLabel.text = @"测试测试";
    }
    return _coverNameLabel;
}

- (UILabel *)numberLabel {
    if (!_numberLabel) {
        _numberLabel = [[UILabel alloc] init];
        [_numberLabel setTextColor:WF_STR_HEX_A(@"#FFFFFF",0.3)];
        _numberLabel.text = @"(102)";
        _numberLabel.font = [UIFont systemFontOfSize:17];
    }
    return _numberLabel;
}

- (UILabel *)seperatorLine {
    if (!_seperatorLine) {
        _seperatorLine = [[UILabel alloc] init];
        _seperatorLine.backgroundColor = WF_STR_AHEX(@"#464646");
    }
    return _seperatorLine;
}

- (UIImageView *)selectImageView {
    if (!_selectImageView) {
        _selectImageView = [[UIImageView alloc] init];
        _selectImageView.image = [UIImage imageNamed:@"icon_photo_check"];
    }
    return _selectImageView;
}

#pragma mark - Parameter Set
- (void)setAlbumModel:(WFAlbumModel *)albumModel {
    _albumModel = albumModel;
    WF_BlockWeakSelf(weakSelf, self);
     [[WFAlbumTool shareInstance] getVideoImageFromPHAsset:albumModel.coverAsset forSize:CGSizeMake(200, 200) completedResultBack:^(UIImage * _Nonnull image) {
           weakSelf.coverImageView.image = image;
       }];
    self.coverNameLabel.text = albumModel.albumTitle;
    self.numberLabel.text = [NSString stringWithFormat:@"(%lu)",(unsigned long)albumModel.collectionPhotos.count];
    self.selectImageView.image = albumModel.selected? [UIImage imageNamed:@"icon_photo_check"]:[UIImage imageNamed:@""];
}

@end
