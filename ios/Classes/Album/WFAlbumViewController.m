//
//  WFAlbumViewController.m
//  Wolf
//
//  Created by Yoffie on 2020/8/18.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import "WFAlbumViewController.h"
#import "WFAlbumView.h"
#import "WFAlbumChooseView.h"
#import "WFMacro.h"
#import <Photos/Photos.h>
#import "WFAlbumTool.h"
#import "WFAlbumEvent.h"
#import "WFPhotosPreviewViewController.h"
#import "XMLeftPresentAnimation.h"


@interface WFAlbumViewController () <WFAlbumViewDelegate, UIViewControllerTransitioningDelegate>

@property (nonatomic, assign) BOOL imagesIsOrginal;

@property (nonatomic, strong) WFAlbumView *albumView;

@end


@implementation WFAlbumViewController

+ (instancetype)viewController {
    WFAlbumViewController *view = [WFAlbumViewController new];
    return view;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupViews];
    [self getAlbums];
    [self setupEvents];
}

- (void)dealloc {
    self.albumView.delegate = nil;
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
}

- (void)setupEvents {
    WF_BlockWeakSelf(weakSelf, self);
    
    [QTSub(self, WFPhotosPreviewSelectEvent) next:^(WFPhotosPreviewSelectEvent *event) {
        if (event.selectAlbums != nil) {
            [weakSelf.albumView setSelectArraymWithData:event.selectAlbums];
        }
    }];
    
    [QTSub(self, WFPhotosPreviewOrginalSelectEvent) next:^(WFPhotosPreviewOrginalSelectEvent *event) {
        weakSelf.imagesIsOrginal = event.imageIsOrginal;
    }];
    
    //    [QTSub(self, WFAlbumSendImageEvent) next:^(WFAlbumSendImageEvent *event) {
    //        [weakSelf goBack];
    //    }];
}

#pragma mark - SetupViews
- (void)setupViews {
    [self.view addSubview: self.albumView];
}

#pragma mark - Function
+ (void)getLatestMediaFile {
    NSMutableArray<WFAlbumModel *> *albumArray = [[NSMutableArray alloc] init];
    PHFetchOptions *option = [[PHFetchOptions alloc] init];
    option.sortDescriptors = @[[NSSortDescriptor sortDescriptorWithKey:@"creationDate" ascending:NO]];
    option.predicate = [NSPredicate predicateWithFormat:@"mediaType == %ld",PHAssetMediaTypeImage];
    
    //fetchAssetCollectionsWithType
    PHFetchResult *smartAlbums = [PHAssetCollection fetchAssetCollectionsWithType:PHAssetCollectionTypeSmartAlbum subtype:PHAssetCollectionSubtypeAlbumRegular options:nil];
    for (PHAssetCollection *collection in smartAlbums) {
        // 有可能是PHCollectionList类的的对象，过滤掉
        if (![collection isKindOfClass:[PHAssetCollection class]]) continue;
        // 过滤空相册
        if (collection.estimatedAssetCount <= 0) continue;
        PHFetchResult *fetchResult = [PHAsset fetchAssetsInAssetCollection:collection options:option];
        if (fetchResult.count > 0) {
            WFAlbumModel *model = [[WFAlbumModel alloc] init];
            model.albumTitle = collection.localizedTitle;
            NSMutableArray *arr = [NSMutableArray array];
            [fetchResult enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                //可通过此PHAsset用下边方法分别获取时常、地址及缩略图
                PHAsset *phAsset = (PHAsset *)obj;
                [arr addObject:phAsset];
                
            }];
            model.collectionPhotos = arr.copy;
            model.coverAsset = arr.firstObject;
            [albumArray addObject:model];
        }
    }
    
    [albumArray enumerateObjectsUsingBlock:^(WFAlbumModel*  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if ([obj.albumTitle isEqualToString:@"最近项目"]) {
            *stop = YES;
            [albumArray exchangeObjectAtIndex:0 withObjectAtIndex:idx];
        }
    }];
    
    WFAlbumModel *model = albumArray.firstObject;
    model.selected = YES;
    int num = 0;
    NSMutableArray<WFAssetEntityModel*> *assetEntityModel = [[NSMutableArray alloc]init];
    for (PHAsset *one in model.collectionPhotos) {
        
    }
    
}

- (void)goBack {
    [self dismissViewControllerAnimated:YES completion:nil];
}

//获取所有相册
- (void)getAlbums {
    NSMutableArray<WFAlbumModel *> *albumArray = [[NSMutableArray alloc] init];
    PHFetchOptions *option = [[PHFetchOptions alloc] init];
    option.sortDescriptors = @[[NSSortDescriptor sortDescriptorWithKey:@"creationDate" ascending:NO]];
    option.predicate = [NSPredicate predicateWithFormat:@"mediaType == %ld",PHAssetMediaTypeImage];
    
    //fetchAssetCollectionsWithType
    PHFetchResult *smartAlbums = [PHAssetCollection fetchAssetCollectionsWithType:PHAssetCollectionTypeSmartAlbum subtype:PHAssetCollectionSubtypeAlbumRegular options:nil];
    for (PHAssetCollection *collection in smartAlbums) {
        // 有可能是PHCollectionList类的的对象，过滤掉
        if (![collection isKindOfClass:[PHAssetCollection class]]) continue;
        // 过滤空相册
        if (collection.estimatedAssetCount <= 0) continue;
        PHFetchResult *fetchResult = [PHAsset fetchAssetsInAssetCollection:collection options:option];
        if (fetchResult.count > 0) {
            WFAlbumModel *model = [[WFAlbumModel alloc] init];
            model.albumTitle = collection.localizedTitle;
            NSMutableArray *arr = [NSMutableArray array];
            [fetchResult enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                //可通过此PHAsset用下边方法分别获取时常、地址及缩略图
                PHAsset *phAsset = (PHAsset *)obj;
                [arr addObject:phAsset];
                
            }];
            model.collectionPhotos = arr.copy;
            model.coverAsset = arr.firstObject;
            [albumArray addObject:model];
        }
    }
    
    [albumArray enumerateObjectsUsingBlock:^(WFAlbumModel*  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if ([obj.albumTitle isEqualToString:@"最近项目"]) {
            *stop = YES;
            [albumArray exchangeObjectAtIndex:0 withObjectAtIndex:idx];
        }
    }];
    
    WFAlbumModel *model = albumArray.firstObject;
    model.selected = YES;
    self.albumView.albumArray = albumArray;
}

#pragma mark --- UIViewControllerTransitioningDelegate
- (id<UIViewControllerAnimatedTransitioning>)animationControllerForPresentedController:(UIViewController *)presented presentingController:(UIViewController *)presenting sourceController:(UIViewController *)source {
    XMLeftPresentAnimation *leftPresentAni = [[XMLeftPresentAnimation alloc] init];
    leftPresentAni.isPresent = YES;
    return leftPresentAni;
}


- (id<UIViewControllerAnimatedTransitioning>)animationControllerForDismissedController:(UIViewController *)dismissed {
    XMLeftPresentAnimation *leftPresentAni = [[XMLeftPresentAnimation alloc] init];
    leftPresentAni.isPresent = NO;
    return leftPresentAni;
}

#pragma mark - WFAlbumViewDelegate
- (void)closeAlbum:(WFAlbumView *)albumView {
    [self goBack];
}

- (void)clickImagesPreview:(WFAlbumBottomView *)view {
    NSMutableArray<PHAsset*> *selectArraym = [self.albumView getSelectArraym];
    if (!selectArraym || selectArraym.count <=0) {
        return;
    }
    NSMutableArray<WFAlbumItemModel *> *albumItemModels = [[NSMutableArray alloc]init];
    for (PHAsset *one in selectArraym) {
        WFAlbumItemModel *model = [[WFAlbumItemModel alloc]init];
        model.coverAsset = one;
        model.selected = YES;
        model.showSelectImage = YES;
        model.indexStr = [NSString stringWithFormat:@"%lu", (unsigned long)[selectArraym indexOfObject:one] + 1];
        [albumItemModels addObject:model];
    }
    
    WFPhotosPreviewViewController *vc = [[WFPhotosPreviewViewController alloc] init];
    vc.transitioningDelegate = self;
    vc.modalPresentationStyle = UIModalPresentationFullScreen;
    vc.albumItemModels = albumItemModels;
    vc.selectArraym = selectArraym;
    vc.type = WFPhotosPreviewViewControllerTypeSelect;
    vc.imagesIsOrginal = self.imagesIsOrginal;
    //[self presentViewController:vc animated:YES completion:nil];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)chooseOriginalPhoto:(WFAlbumView *)albumView imagesIsOrginal:(BOOL)imagesIsOrginal{
    self.imagesIsOrginal = imagesIsOrginal;
}

- (void)sendImagesHandler:(WFAlbumView *)albumView {
    NSMutableArray<PHAsset*> *selectAsset = [self.albumView getSelectArraym];
    if (selectAsset.count <=0) {
        return;
    }
    WF_BlockWeakSelf(weakSelf, self);
    [[WFAlbumTool shareInstance] sendMediasWithAssetArray:selectAsset imagesIsOrginal:self.imagesIsOrginal completionHandler:^(NSArray *modelArray) {
        WFAlbumSendImageEvent *event = [WFAlbumSendImageEvent new];
        event.models = modelArray;
        [QTEventBus.shared dispatch:event];
        [weakSelf goBack];
    }];
}

- (void)tapAlbumImage:(WFAlbumCollectionView *)albumCollectionView curWFAlbumModel:(WFAlbumModel *)model imageIndex:(NSInteger)imageIndex{
    NSMutableArray<PHAsset*> *selectArraym = [self.albumView getSelectArraym];
    NSMutableArray<PHAsset*> *selectArraymTmp = selectArraym.mutableCopy;
    NSMutableArray<PHAsset*> *curAllArraym = (NSMutableArray<PHAsset*>*)model.collectionPhotos;
    NSMutableArray<WFAlbumItemModel *> *albumItemModels = [[NSMutableArray alloc]init];
    //albumItemModels先添加当前相册相片
    for (PHAsset *one in curAllArraym) {
        //判断是否选中
        bool selected = NO;
        if ([selectArraymTmp containsObject:one]) {
            selected = YES;
            //如果选中就从selectArraymTmp删除
            [selectArraymTmp removeObject:one];
        }
        WFAlbumItemModel *model = [[WFAlbumItemModel alloc]init];
        model.coverAsset = one;
        model.selected = selected;
        model.indexStr = [NSString stringWithFormat:@"%lu", (unsigned long)albumItemModels.count];
        [albumItemModels addObject:model];
    }
    //albumItemModels添加那些不在当前相册，但是被选中的照片
    for (PHAsset *one in selectArraymTmp) {
        WFAlbumItemModel *model = [[WFAlbumItemModel alloc]init];
        model.coverAsset = one;
        model.selected = YES;
        model.showSelectImage = NO;
        model.indexStr = [NSString stringWithFormat:@"%lu", (unsigned long)albumItemModels.count];
        [albumItemModels addObject:model];
    }
    
    WFPhotosPreviewViewController *vc = [[WFPhotosPreviewViewController alloc] init];
    vc.transitioningDelegate = self;
    vc.modalPresentationStyle = UIModalPresentationFullScreen;
    vc.albumItemModels = albumItemModels;
    vc.selectArraym = selectArraym;
    vc.firstShowIndex = imageIndex;
    vc.type = WFPhotosPreviewViewControllerTypeAlbum;
    vc.imagesIsOrginal = self.imagesIsOrginal;
    //[self presentViewController:vc animated:YES completion:nil];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)selectAlbum:(WFAlbumChooseView *)albumChooseView withSelectAlbumModel:(WFAlbumModel *)selectModel withOldAlbumModel:(WFAlbumModel *)oldModel {
    NSMutableArray<WFAlbumModel*> *albumArray = self.albumView.albumArray;
    if (albumArray != nil) {
        self.albumView.albumArray = albumArray;
    }
}

#pragma mark - Parameter Get
- (WFAlbumView *)albumView {
    if(_albumView == nil) {
        _albumView = [[WFAlbumView alloc]initWithFrame: CGRectMake(0, 0, WF_SCREEN_WIDTH, WF_SCREEN_HEIGHT)];
        _albumView.delegate = self;
    }
    return _albumView;
}

#pragma mark - Parameter Set
- (void)setImagesIsOrginal:(BOOL)imagesIsOrginal {
    _imagesIsOrginal = imagesIsOrginal;
    self.albumView.imagesIsOrginal = imagesIsOrginal;
}

@end
