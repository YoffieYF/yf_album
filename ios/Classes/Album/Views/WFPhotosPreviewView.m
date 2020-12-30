//
//  WFPhotosPreviewView.m
//  Wolf
//
//  Created by Yoffie on 2020/8/24.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import "WFPhotosPreviewView.h"
#import "WFMacro.h"


@interface WFPhotosPreviewView () <WFAlbumBottomViewDelegate, WFPhotosPreviewCollectionViewDelegate, WFPhotosPreviewSelectViewDelegate>

@property (nonatomic, assign) NSInteger curIndex;

@property (nonatomic, strong) UIView *topView;
@property (nonatomic, strong) UIButton *backBtn;
@property (nonatomic, strong) UILabel *indexStrLabel;
@property (nonatomic, strong) UIButton *selectBtn;
@property (nonatomic, strong) WFAlbumBottomView *albumBottomView;
@property (nonatomic, strong) WFPhotosPreviewCollectionView *photosPreviewCollectionView;
@property (nonatomic, strong) WFPhotosPreviewSelectView *photosPreviewSelectView;

@end


@implementation WFPhotosPreviewView

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self setupViews];
    }
    return self;
}

- (void)dealloc {
    _photosPreviewSelectView.delegate = nil;
}

#pragma mark - SetupViews
- (void)setupViews {
    self.backgroundColor = WF_STR_AHEX(@"#D8D8D8");
    
    [self addSubview:self.topView];
    [self.topView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.top.right.mas_equalTo(self);
        make.height.mas_equalTo(60);
    }];
    
    [self.topView addSubview:self.backBtn];
    [self.backBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(self.topView);
        make.bottom.mas_equalTo(self.topView).mas_offset(-5);
        make.width.mas_equalTo(43);
        make.height.mas_equalTo(30);
    }];
    
    [self.topView addSubview:self.indexStrLabel];
    [self.indexStrLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(43);
        make.centerY.mas_equalTo(self.backBtn);
        make.height.mas_equalTo(30);
    }];
    
    [self.topView addSubview:self.selectBtn];
    [self.selectBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(self.topView);
        make.centerY.mas_equalTo(self.backBtn);
        make.width.mas_equalTo(63);
        make.height.mas_equalTo(30);
    }];
    
    [self insertSubview:self.photosPreviewCollectionView belowSubview:self.topView];
    
    [self addSubview:self.albumBottomView];
    
    [self addSubview:self.photosPreviewSelectView];
}

#pragma mark - Function
//Public
- (void)reloadPreviewSelectView:(NSMutableArray<WFAlbumItemModel*> *)data {
     self.photosPreviewSelectView.selectModels = data;
}

//Private
- (void)selectBtnClick:(UIButton *)sender {
    sender.selected = !sender.selected;
    if (self.delegate && [self.delegate respondsToSelector:@selector(selectBtnClick:withIndex:)]) {
        [self.delegate selectBtnClick:self withIndex:self.curIndex];
    }
}

- (void)closePreview:(UIButton *)sender {
   if (self.delegate && [self.delegate respondsToSelector:@selector(closePreview:)]) {
        [self.delegate closePreview:self];
   }
}

#pragma mark - WFPhotosPreviewSelectViewDelegate
- (void)tapImageItem:(WFPhotosPreviewSelectView *)imageBrowser withModel:(WFAlbumItemModel *)model {
    NSInteger index = [WFAlbumItemModel findIndexInModels:self.albumItemModels withPhasset:model.coverAsset];
    self.curIndex = index;
    self.photosPreviewCollectionView.showIndex = self.curIndex;
}

#pragma mark - WFPhotosPreviewCollectionViewDelegate
- (void)imageBrowser:(WFPhotosPreviewCollectionView *)imageBrowser pageChanged:(NSInteger)page data:(WFAlbumItemModel *)data {
    self.selectBtn.selected = data.selected;
    self.curIndex = page;
    self.indexStrLabel.text = [NSString stringWithFormat:@"%d/%lu", (int)page + 1, (unsigned long)self.albumItemModels.count];
}

- (void)tapImage:(WFPhotosPreviewCollectionView *)view {
    self.albumBottomView.hidden = !self.albumBottomView.hidden;
    self.topView.hidden = !self.topView.hidden;
    if (self.photosPreviewSelectView.selectModels.count == 0) {
        self.photosPreviewSelectView.hidden = YES;
    } else {
        self.photosPreviewSelectView.hidden = !self.photosPreviewSelectView.hidden;
    }
}

#pragma mark - Parameter Get
- (UIView *)topView {
    if (!_topView) {
        _topView = [[UIView alloc]init];
        _topView.backgroundColor = [UIColor blackColor];
    }
    return _topView;
}

- (UIButton *)backBtn {
    if (!_backBtn) {
        _backBtn = [[UIButton alloc] init];
        [_backBtn setImage:[WFMacro getBundleImageWithName:@"img_back"] forState:UIControlStateNormal];
        [_backBtn addTarget:self action:@selector(closePreview:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _backBtn;
}

- (UILabel *)indexStrLabel {
    if (!_indexStrLabel) {
        _indexStrLabel = [[UILabel alloc]init];
        [_indexStrLabel setTextColor:[UIColor whiteColor]];
        _indexStrLabel.font = [UIFont systemFontOfSize:17];
    }
    return _indexStrLabel;
}

- (UIButton *)selectBtn {
    if (!_selectBtn) {
        _selectBtn = [[UIButton alloc]init];
        [_selectBtn setImage:[WFMacro getBundleImageWithName:@"icon_origin_photo_unselected"] forState:UIControlStateNormal];
        [_selectBtn setImage:[WFMacro getBundleImageWithName:@"icon_origin_photo_selected"] forState:UIControlStateSelected];
        [_selectBtn setTitle:@"选择" forState:UIControlStateNormal];
        [_selectBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _selectBtn.titleLabel.font = [UIFont systemFontOfSize:14];
        [_selectBtn setImageEdgeInsets:UIEdgeInsetsMake(0, -5, 0, 0)];
        [_selectBtn addTarget:self action:@selector(selectBtnClick:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _selectBtn;
}

- (WFPhotosPreviewCollectionView *)photosPreviewCollectionView {
    if (!_photosPreviewCollectionView) {
        _photosPreviewCollectionView = [[WFPhotosPreviewCollectionView alloc]initWithFrame:CGRectMake(0, 0, WF_SCREEN_WIDTH, WF_SCREEN_HEIGHT)];
        _photosPreviewCollectionView.delegate = self;
    }
    return _photosPreviewCollectionView;
}

- (WFAlbumBottomView *)albumBottomView {
    if (!_albumBottomView) {
        CGFloat tabbarH = (WF_IS_iPhoneX ? 94. : 60);
        CGFloat y = WF_SCREEN_HEIGHT - tabbarH;
        _albumBottomView = [[WFAlbumBottomView alloc]initWithFrame:CGRectMake(0, y, WF_SCREEN_WIDTH, tabbarH)];
        [_albumBottomView hidePreviewBtn];
    }
    return _albumBottomView;
}

- (WFPhotosPreviewSelectView *)photosPreviewSelectView {
    if (!_photosPreviewSelectView) {
        CGFloat tabbarH = (WF_IS_iPhoneX ? 94. : 60);
        CGFloat h = (WF_SCREEN_WIDTH - 40)/4.f + 40;
        CGFloat y = WF_SCREEN_HEIGHT - tabbarH - h;
        _photosPreviewSelectView = [[WFPhotosPreviewSelectView alloc]initWithFrame:CGRectMake(0, y, WF_SCREEN_WIDTH, h)];
        _photosPreviewSelectView.delegate = self;
    }
    return _photosPreviewSelectView;
}

#pragma mark - Parameter Set
- (void)setAlbumItemModels:(NSMutableArray<WFAlbumItemModel *> *)albumItemModels {
    _albumItemModels = albumItemModels;
    if (albumItemModels.count > 0) {
        self.selectBtn.selected = albumItemModels[0].selected;
    }
    self.photosPreviewCollectionView.albumItemModels = albumItemModels;
    self.photosPreviewCollectionView.showIndex = self.firstShowIndex;
    self.photosPreviewSelectView.selectModels = [WFAlbumItemModel getSelectModels:albumItemModels];
}

- (void)setImagesIsOrginal:(BOOL)imagesIsOrginal {
    self.albumBottomView.imagesIsOrginal = imagesIsOrginal;
}

- (void)setDelegate:(id<WFPhotosPreviewViewDelegate>)delegate {
    _delegate = delegate;
    _albumBottomView.delegate = delegate;
}

@end
