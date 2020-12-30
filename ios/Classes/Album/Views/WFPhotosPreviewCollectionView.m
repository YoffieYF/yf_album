//
//  WFPhotosPreviewCollectionView.m
//  Wolf
//
//  Created by Yoffie on 2020/8/24.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import "WFPhotosPreviewCollectionView.h"
#import "Masonry.h"
#import "YBImageBrowser.h"
#import "YBIBUtilities.h"
#import "WFMacro.h"


@interface WFPhotosPreviewCollectionView ()<YBImageBrowserDelegate>

@property (nonatomic, strong) NSMutableArray *dataArr;

@property (nonatomic, strong) YBImageBrowser *browser;

@end


@implementation WFPhotosPreviewCollectionView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setupViews];
    }
    return self;
}

#pragma mark - SetupViews
- (void)setupViews {
     [self.browser showToView:self containerSize:CGSizeMake(WF_SCREEN_WIDTH, WF_SCREEN_HEIGHT)];
}

#pragma mark - Function

#pragma mark --- YBImageBrowserDelegate
- (void)yb_imageBrowser:(YBImageBrowser *)imageBrowser pageChanged:(NSInteger)page data:(id<YBIBDataProtocol>)data{
    if (self.delegate && [self.delegate respondsToSelector:@selector(imageBrowser: pageChanged: data:)]) {
        [self.delegate imageBrowser:self pageChanged:page data:self.albumItemModels[page]];
    }
}

#pragma mark - Parameter Get
- (YBImageBrowser *)browser {
    if (!_browser) {
        _browser = [YBImageBrowser new];
        _browser.dataSourceArray = self.dataArr;
        _browser.currentPage = self.showIndex;
        _browser.backgroundColor = [UIColor redColor];
        _browser.delegate = self;
        _browser.supportedOrientations = UIInterfaceOrientationPortrait;
        WF_BlockWeakSelf(weakSelf, self);
        _browser.tapBlock = ^{
            if (weakSelf.delegate && [weakSelf.delegate respondsToSelector:@selector(tapImage:)]) {
                [weakSelf.delegate tapImage:weakSelf];
            }
        };
        // 关闭入场和出场动效
        _browser.defaultAnimatedTransition.showType = YBIBTransitionTypeNone;
        _browser.defaultAnimatedTransition.hideType = YBIBTransitionTypeNone;
    }
    return _browser;
}

- (NSMutableArray *)dataArr {
    if (!_dataArr) {
        _dataArr = [[NSMutableArray alloc]init];
    }
    return _dataArr;
}

#pragma mark - Parameter Set
- (void)setAlbumItemModels:(NSMutableArray<WFAlbumItemModel *> *)albumItemModels {
    _albumItemModels = albumItemModels;
    [albumItemModels enumerateObjectsUsingBlock:^(WFAlbumItemModel*  _Nonnull albumItemModel, NSUInteger idx, BOOL * _Nonnull stop) {
        PHAsset *obj = albumItemModel.coverAsset;
        if (obj.mediaType == PHAssetMediaTypeImage) {
            YBIBImageData *data = [YBIBImageData new];
            data.interactionProfile.disable = YES;  //关闭手势交互
            data.imagePHAsset = obj;
            data.allowSaveToPhotoAlbum = NO;
            YBIBImageLayout *layout = data.defaultLayout;
            layout.verticalFillType = YBIBImageFillTypeFullWidth; // 宽度优先填充
            [self.dataArr addObject:data];
        }
    }];
    self.browser.dataSourceArray = self.dataArr;
    [self.browser reloadData];
}

- (void)setShowIndex:(NSInteger)showIndex {
    _showIndex = showIndex;
    self.browser.currentPage = showIndex;
}

@end
