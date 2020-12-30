//
//  WFPhotosPreviewViewController.h
//  Wolf
//
//  Created by Yoffie on 2020/8/21.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import <Photos/Photos.h>
#import "WFAlbumItemModel.h"
#import "WFAlbumTool.h"


@interface WFPhotosPreviewViewController : UIViewController

@property (nonatomic, strong) NSMutableArray<WFAlbumItemModel*> *albumItemModels;
@property (nonatomic, strong) NSMutableArray<PHAsset*> *selectArraym;
@property (nonatomic, assign) NSInteger firstShowIndex;
@property (nonatomic, assign) WFPhotosPreviewViewControllerType type;
@property (nonatomic, assign) BOOL imagesIsOrginal;

@end


