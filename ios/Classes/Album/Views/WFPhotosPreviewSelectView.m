//
//  WFPhotosPreviewSelectView.m
//  Wolf
//
//  Created by Yoffie on 2020/8/27.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import "WFPhotosPreviewSelectView.h"
#import "WFPhotosPreviewSelectCollectionCell.h"
#import "WFMacro.h"


@interface WFPhotosPreviewSelectView ()<UICollectionViewDelegate,UICollectionViewDataSource,UICollectionViewDelegateFlowLayout>

@property (nonatomic, strong) UICollectionView *collectionView;

@end


@implementation WFPhotosPreviewSelectView

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
    
    self.collectionView.frame = CGRectMake(0, 20, WF_SCREEN_WIDTH, self.height - 40);
    [self addSubview:self.collectionView];
}

#pragma mark - Function
//Public
- (void) collectionReload {
    [self.collectionView reloadData];
}

#pragma mark  UICollectionViewDelegate,UICollectionViewDataSource
-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.selectModels.count;
}

-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    WFPhotosPreviewSelectCollectionCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:kWFPhotosPreviewSelectCollectionCellIdentifier forIndexPath:indexPath];
    WFAlbumItemModel *model = self.selectModels[indexPath.item];
    cell.model = model;
    return cell;
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    if (self.delegate && [self.delegate respondsToSelector:@selector(tapImageItem: withModel:)]) {
        WFAlbumItemModel *model = self.selectModels[indexPath.item];
        [self.delegate tapImageItem:self withModel:model];
    }
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
    CGFloat itemW = (collectionView.bounds.size.width - 20)/4.f;
    return CGSizeMake(itemW, itemW);
}

- (UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout insetForSectionAtIndex:(NSInteger)section {
    return UIEdgeInsetsMake(0, 10, 0, 10);
}

- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumLineSpacingForSectionAtIndex:(NSInteger)section {
    return 10;
}

- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumInteritemSpacingForSectionAtIndex:(NSInteger)section {
    return 0;
}

#pragma mark - Parameter Get
-(UICollectionView *)collectionView {
    if (_collectionView == nil) {
        UICollectionViewFlowLayout *flowLayout = [[UICollectionViewFlowLayout alloc] init];
        flowLayout.scrollDirection = UICollectionViewScrollDirectionHorizontal;
        _collectionView = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:flowLayout];
        _collectionView.delegate = self;
        _collectionView.dataSource = self;
        _collectionView.backgroundColor = WF_STR_AHEX(@"#000000");
        _collectionView.showsVerticalScrollIndicator = NO;
        _collectionView.showsHorizontalScrollIndicator = NO;
        [_collectionView registerClass:[WFPhotosPreviewSelectCollectionCell class] forCellWithReuseIdentifier:kWFPhotosPreviewSelectCollectionCellIdentifier];
    }
    return _collectionView;
}

#pragma mark - Parameter Set
- (void)setSelectModels:(NSMutableArray<WFAlbumItemModel *> *)selectModels {
    _selectModels = selectModels;
    self.hidden = (selectModels.count == 0);
    [self.collectionView reloadData];
}

@end
