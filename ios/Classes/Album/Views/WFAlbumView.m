//
//  WFAlbumView.m
//  Wolf
//
//  Created by Yoffie on 2020/8/18.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import "WFAlbumView.h"
#import "WFMacro.h"


@interface WFAlbumView()

//Data
@property (nonatomic, strong) NSString *centerTitleStr;

//UI
@property (nonatomic, strong) UIView *topView;
@property (nonatomic, strong) UIButton *cancelBtn;
@property (nonatomic, strong) UIView *topCenterView;
@property (nonatomic, strong) UILabel *centerTitleLab;
@property (nonatomic, strong) UIImageView *updownImgV;
@property (nonatomic, strong) UIButton *topCenterBtn;
@property (nonatomic, strong) WFAlbumChooseView *albumChooseView;
@property (nonatomic, strong) WFAlbumCollectionView *albumCollectionView;
@property (nonatomic, strong) UILabel *seperatorLine;
@property (nonatomic, strong) WFAlbumBottomView *albumBottomView;

@end


@implementation WFAlbumView

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self setupViews];
        
    }
    return self;
}

- (void)dealloc {
    self.albumChooseView.delegate = nil;
    self.albumBottomView.delegate = nil;
    self.albumCollectionView.delegate = nil;
}

#pragma mark - SetupViews
- (void)setupViews {
    self.backgroundColor = WF_STR_AHEX(@"#FF000000");
    
    [self addSubview:self.albumCollectionView];
    
    [self addSubview:self.albumChooseView];
    
    [self addSubview:self.topView];
    [self.topView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.top.right.mas_equalTo(self);
        make.height.mas_equalTo(60);
    }];
    
    [self.topView addSubview:self.cancelBtn];
    [self.cancelBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(self.topView).offset(10);
        make.bottom.mas_equalTo(-5);
        make.width.mas_equalTo(60);
        make.height.mas_equalTo(30);
    }];
    
    [self.topView addSubview:self.topCenterView];
    [self.topCenterView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(self.cancelBtn);
        make.centerX.mas_equalTo(self.topView);
        make.width.mas_equalTo(120);
        make.height.mas_equalTo(30);
    }];
    
    [self.topCenterView addSubview:self.centerTitleLab];
    [self.centerTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(self.topCenterView).offset(-11);
        make.centerY.mas_equalTo(self.topCenterView);
    }];
    
    [self.topCenterView addSubview:self.updownImgV];
    [self.updownImgV mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(_centerTitleLab.mas_right).offset(6);
        make.centerY.mas_equalTo(self.topCenterView);
        make.width.height.mas_equalTo(16);
    }];
    
    [self.topCenterView addSubview:self.topCenterBtn];
    [self.topCenterBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(self.topCenterView);
    }];
    
    [self.topView addSubview:self.seperatorLine];
    [self.seperatorLine mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.bottom.mas_equalTo(self.topView);
        make.height.mas_equalTo(0.5);
    }];
    
    [self insertSubview:self.albumBottomView belowSubview:self.albumChooseView];
}

#pragma mark - Function
//Public
- (NSMutableArray<PHAsset *> *)getSelectArraym {
    return [self.albumCollectionView getSelectArraym];
}

- (void)setSelectArraymWithData:(NSMutableArray<PHAsset *> *)data {
    [self.albumCollectionView setSelectArraymWithData:data];
}

//Privately
- (void)closeAlbum:(id) sender {
    self.albumChooseView.hidden = YES;
    if (self.delegate && [self.delegate respondsToSelector:@selector(closeAlbum:)]) {
        [self.delegate closeAlbum:self];
    }
}

- (void)topCenterBtnClick:(UIButton *) sender {
    sender.selected = !sender.selected;
    if (sender.selected) {
        [self.albumChooseView showInView:self];
    }else{
        [self.albumChooseView dismiss];
    }
    [self transformUpdownImgV:sender.selected];
}

- (void) transformUpdownImgV: (BOOL) isShow {
    if (isShow) {
        self.updownImgV.transform = CGAffineTransformMakeRotation(M_PI);
    } else {
        self.updownImgV.transform = CGAffineTransformIdentity;
    }
}

#pragma mark - Parameter Get
- (UIView *)topView {
    if(_topView == nil) {
        _topView = [[UIView alloc] init];
        _topView.backgroundColor = WF_STR_AHEX(@"#000000");
    }
    return _topView;
}

- (UIButton *)cancelBtn {
    if(_cancelBtn == nil) {
        _cancelBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_cancelBtn setTitle:@"取消" forState:UIControlStateNormal];
        [_cancelBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _cancelBtn.titleLabel.font = [UIFont systemFontOfSize:17];
        [_cancelBtn addTarget:self action:@selector(closeAlbum:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _cancelBtn;
}

- (UIView *)topCenterView {
    if(!_topCenterView) {
        _topCenterView = [[UIView alloc] init];
        _topCenterView.layer.backgroundColor = WF_STR_HEX_A(@"#989898", 1).CGColor;
        _topCenterView.layer.cornerRadius = 15.f;
        _topCenterView.layer.masksToBounds = YES;
    }
    return _topCenterView;
}

- (UILabel *)centerTitleLab {
    if(!_centerTitleLab) {
        _centerTitleLab = [[UILabel alloc] init];
        _centerTitleLab.font = [UIFont systemFontOfSize:17];
        _centerTitleLab.textColor = [UIColor whiteColor];
        _centerTitleLab.text = @"";
    }
    return _centerTitleLab;
}

- (UIImageView *)updownImgV {
    if (!_updownImgV) {
        _updownImgV = [[UIImageView alloc] init];
        _updownImgV.image = [WFMacro getBundleImageWithName:@"icon_unfold"];
    }
    return _updownImgV;
}

- (UIButton *)topCenterBtn {
    if (!_topCenterBtn) {
        _topCenterBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        _topCenterBtn.selected = NO;
        [_topCenterBtn addTarget:self action:@selector(topCenterBtnClick:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _topCenterBtn;
}

- (UILabel *)seperatorLine {
    if (!_seperatorLine) {
        _seperatorLine = [[UILabel alloc] init];
        _seperatorLine.backgroundColor = WF_STR_HEX_A(@"#000000", 1);
    }
    return _seperatorLine;
}

- (WFAlbumBottomView *)albumBottomView {
    if (!_albumBottomView) {
        CGFloat tabbarH = (WF_IS_iPhoneX ? 94. : 60);
        CGFloat y = WF_SCREEN_HEIGHT - tabbarH;
        _albumBottomView = [[WFAlbumBottomView alloc]initWithFrame:CGRectMake(0, y, WF_SCREEN_WIDTH, tabbarH)];
    }
    return _albumBottomView;
}

- (WFAlbumChooseView *)albumChooseView {
    if (!_albumChooseView) {
        _albumChooseView = [[WFAlbumChooseView alloc] initWithFrame:CGRectMake(0, -WF_SCREEN_HEIGHT, self.bounds.size.width, self.height - 60)];
    }
    return _albumChooseView;
}

- (WFAlbumCollectionView *)albumCollectionView {
    if (!_albumCollectionView) {
        _albumCollectionView = [[WFAlbumCollectionView alloc] initWithFrame:CGRectMake(0, 60, self.bounds.size.width, WF_SCREEN_HEIGHT - 60 - 40)];
    }
    return _albumCollectionView;
}

#pragma mark - Parameter Set
- (void)setAlbumArray:(NSMutableArray<WFAlbumModel *> *)albumArray {
    if(!albumArray) {
        return;
    }
    if(!_albumArray) {
        _albumArray = [[NSMutableArray alloc] init];
    }
    _albumArray = albumArray;
    self.albumChooseView.albumArray = _albumArray;
    WFAlbumModel *selectModel = [WFAlbumModel getSelectAlbumModelInModels:_albumArray];
    self.centerTitleLab.text = selectModel.albumTitle;
    self.albumCollectionView.albumModel = selectModel;
    self.topCenterBtn.selected = NO;
    [self.albumChooseView dismiss];
    [self transformUpdownImgV:NO];
}

- (void)setDelegate:(id<WFAlbumViewDelegate>)delegate {
    _delegate = delegate;
    _albumBottomView.delegate = delegate;
    _albumChooseView.delegate = delegate;
    _albumCollectionView.delegate = delegate;
}

- (void)setImagesIsOrginal:(BOOL)imagesIsOrginal {
    self.albumBottomView.imagesIsOrginal = imagesIsOrginal;
}

@end
