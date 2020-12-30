//
//  WFAlbumCollectionView.m
//  Wolf
//
//  Created by Yoffie on 2020/8/20.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import "WFAlbumCollectionView.h"
#import "WFAlbumCollectionCell.h"
#import "WFMacro.h"


@interface WFAlbumCollectionView () <UICollectionViewDelegate,UICollectionViewDataSource,UICollectionViewDelegateFlowLayout,UINavigationControllerDelegate,UIImagePickerControllerDelegate>

@property (nonatomic, strong) UICollectionView *collectionView;
@property (nonatomic, strong) NSMutableArray<PHAsset*> *phArraym;
@property (nonatomic, strong) NSMutableArray<PHAsset*> *selectArraym;

@end


@implementation WFAlbumCollectionView

- (instancetype)init{
    self = [super init];
    if (self) {
        [self setupViews];
    }
    return self;
}

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self setupViews];
    }
    return self;
}

- (void)dealloc {
    self.collectionView.delegate = nil;
    self.collectionView.dataSource = nil;
}

#pragma mark - SetupViews
- (void)setupViews {
    self.backgroundColor = WF_STR_AHEX(@"#000000");
    [self addSubview:self.collectionView];
    [self.collectionView setFrame:self.bounds];
}

#pragma mark - Function
//Public
- (NSMutableArray<PHAsset *> *)getSelectArraym {
    return self.selectArraym;
}

- (void)setSelectArraymWithData: (NSMutableArray<PHAsset*> *)data {
    self.selectArraym = data;
    [self.collectionView reloadData];
}

//Privately
- (void)selectImageWithAsset:(PHAsset *)asset {
    if ([self.selectArraym containsObject:asset]) {
        [self.selectArraym removeObject:asset];
    } else {
        if (self.selectArraym.count >= 9) {
            //                    [weakSelf makeToast:@"最多可选择9张" duration:.8 position:CSToastPositionCenter];
            return;
        }
        [self.selectArraym addObject:asset];
    }
    [self.collectionView reloadData];
}

#pragma mark  UICollectionViewDelegate,UICollectionViewDataSource
-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.phArraym.count;
}

-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    WFAlbumCollectionCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:kWFAlbumCollectionCellIdentifier forIndexPath:indexPath];
    PHAsset *asset = self.phArraym[indexPath.item];
    cell.asset = asset;
    [cell resetViews:asset withSelectAsset:self.selectArraym];
    WF_BlockWeakSelf(weakSelf, self);
    [cell setSelectItemBlock:^(PHAsset *asset) {
        [weakSelf selectImageWithAsset:asset];
    }];
    return cell;
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    if (self.delegate && [self.delegate respondsToSelector:@selector(tapAlbumImage: curWFAlbumModel: imageIndex:)]) {
        [self.delegate tapAlbumImage:self curWFAlbumModel:self.albumModel imageIndex:indexPath.item];
    }
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
    return CGSizeMake((WF_SCREEN_WIDTH - 2*5)/4, (WF_SCREEN_WIDTH - 2*5)/4);
}

- (UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout insetForSectionAtIndex:(NSInteger)section {
    return UIEdgeInsetsMake(2, 2, 2, 2);
}

- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumLineSpacingForSectionAtIndex:(NSInteger)section {
    return 2.f;
}

- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumInteritemSpacingForSectionAtIndex:(NSInteger)section {
    return 2.f;
}

#pragma mark - Parameter Get
-(UICollectionView *)collectionView {
    if (_collectionView == nil) {
        UICollectionViewFlowLayout *flowLayout = [[UICollectionViewFlowLayout alloc] init];
        _collectionView = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:flowLayout];
        _collectionView.delegate = self;
        _collectionView.dataSource = self;
        _collectionView.backgroundColor = WF_STR_AHEX(@"#000000");
        _collectionView.showsVerticalScrollIndicator = NO;
        _collectionView.showsHorizontalScrollIndicator = NO;
        [_collectionView registerClass:[WFAlbumCollectionCell class] forCellWithReuseIdentifier:kWFAlbumCollectionCellIdentifier];
    }
    return _collectionView;
}

- (NSMutableArray<PHAsset *> *)selectArraym {
    if (!_selectArraym) {
        _selectArraym = [[NSMutableArray alloc] init];
    }
    return _selectArraym;
}

#pragma mark - Parameter Set
- (void)setAlbumModel:(WFAlbumModel *)albumModel {
    if (!albumModel) {
        return;
    }
    _albumModel = albumModel;
    _phArraym = albumModel.collectionPhotos.mutableCopy;
    [self.collectionView reloadData];
}

@end
