//
//  WFPhotosPreviewViewController.m
//  Wolf
//
//  Created by Yoffie on 2020/8/21.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import "WFPhotosPreviewViewController.h"
#import "WFPhotosPreviewView.h"
#import "WFAlbumEvent.h"
#import "WFMacro.h"
//#import "WFIMChatViewController.h"


@interface WFPhotosPreviewViewController () <WFPhotosPreviewViewDelegate>

@property (nonatomic, strong) WFPhotosPreviewView *photosPreviewView;

@end


@implementation WFPhotosPreviewViewController

@synthesize selectArraym = _selectArraym;

+ (instancetype)viewController {
    WFPhotosPreviewViewController *view = [WFPhotosPreviewViewController new];
    return view;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupViews];
    [self setViewsData];
}

- (void)dealloc {
    self.photosPreviewView.delegate = nil;
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    self.photosPreviewView.firstShowIndex = self.firstShowIndex;
}

- (void)setViewsData {
    self.photosPreviewView.selectArraym = self.selectArraym;
    self.photosPreviewView.firstShowIndex = self.firstShowIndex;
    self.photosPreviewView.type = self.type;
    self.photosPreviewView.albumItemModels = self.albumItemModels;
    self.photosPreviewView.imagesIsOrginal = self.imagesIsOrginal;
}

#pragma mark - SetupViews
- (void)setupViews {
    [self.view addSubview: self.photosPreviewView];
}

#pragma mark - Function

#pragma mark - WFPhotosPreviewViewDelegate
- (void)selectBtnClick:(WFPhotosPreviewView *)view withIndex:(NSInteger)index {
    WFAlbumItemModel *curModel;
    if(self.albumItemModels.count > index) {
        curModel = self.albumItemModels[index];
        curModel.selected = !curModel.selected;
    }
    if (curModel == nil) return;
    switch (self.type) {
        case WFPhotosPreviewViewControllerTypeAlbum: {
            NSMutableArray *selectArraymTmp = self.selectArraym;
            if (curModel.selected) {
                [selectArraymTmp addObject:curModel.coverAsset];
            } else {
                if ([selectArraymTmp containsObject:curModel.coverAsset]) {
                    [selectArraymTmp removeObject:curModel.coverAsset];
                }
            }
            NSMutableArray<WFAlbumItemModel*> *albumItemModels = [[NSMutableArray alloc]init];
            for (PHAsset *one in selectArraymTmp) {
                WFAlbumItemModel *model = [[WFAlbumItemModel alloc]init];
                model.selected = YES;
                model.coverAsset = one;
                [albumItemModels addObject:model];
            }
            [self.photosPreviewView reloadPreviewSelectView:albumItemModels];
            WFPhotosPreviewSelectEvent *event = [WFPhotosPreviewSelectEvent new];
            event.selectAlbums = selectArraymTmp;
            //[self.eventDispatcher dispatch:event]; //不知道为什么这种方式无效
            [QTEventBus.shared dispatch:event];
            break;
        }
        case WFPhotosPreviewViewControllerTypeSelect: {
            WFPhotosPreviewSelectEvent *event = [WFPhotosPreviewSelectEvent new];
            event.selectAlbums = [WFAlbumItemModel getSelectModelsAsset:self.albumItemModels];
            [QTEventBus.shared dispatch:event];
            [self.photosPreviewView reloadPreviewSelectView:self.albumItemModels];
            break;
        }
        case WFPhotosPreviewViewControllerTypeURL:
            break;
    }
}

- (void)sendImagesHandler:(WFAlbumBottomView *)view {
    NSMutableArray<PHAsset*> *selectAsset = [WFAlbumItemModel getSelectModelsAsset:self.albumItemModels];
    if (selectAsset.count <=0) {
        return;
    }
    [[WFAlbumTool shareInstance] sendMediasWithAssetArray:selectAsset imagesIsOrginal:self.imagesIsOrginal completionHandler:^(NSArray *modelArray) {
        WFAlbumSendImageEvent *event = [WFAlbumSendImageEvent new];
        event.models = modelArray;
        [QTEventBus.shared dispatch:event];
        [self dismissViewControllerAnimated:YES completion:nil];
    }];
}

- (void)chooseOriginalPhoto:(WFAlbumBottomView *)view imagesIsOrginal:(BOOL)imagesIsOrginal {
    self.imagesIsOrginal = imagesIsOrginal;
    WFPhotosPreviewOrginalSelectEvent *event = [WFPhotosPreviewOrginalSelectEvent new];
    event.imageIsOrginal = self.imagesIsOrginal;
    [QTEventBus.shared dispatch:event];
}

- (void)closePreview:(WFPhotosPreviewView *)view {
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark - Parameter Get
- (WFPhotosPreviewView *)photosPreviewView {
    if(!_photosPreviewView) {
        _photosPreviewView = [[WFPhotosPreviewView alloc]initWithFrame: CGRectMake(0, 0, WF_SCREEN_WIDTH, WF_SCREEN_HEIGHT)];
        _photosPreviewView.delegate = self;
    }
    return _photosPreviewView;
}

- (NSMutableArray<PHAsset *> *)selectArraym {
    if (!_selectArraym) {
        _selectArraym = [[NSMutableArray alloc]init];
    }
    return _selectArraym;
}

#pragma mark - Parameter Set

@end
