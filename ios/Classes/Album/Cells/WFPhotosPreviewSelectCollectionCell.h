//
//  WFPhotosPreviewSelectCollectionCell.h
//  Wolf
//
//  Created by Yoffie on 2020/8/27.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Photos/Photos.h>
#import "WFAlbumItemModel.h"


static NSString *const kWFPhotosPreviewSelectCollectionCellIdentifier = @"kWFPhotosPreviewSelectCollectionCellIdentifier";


@interface WFPhotosPreviewSelectCollectionCell : UICollectionViewCell

@property (nonatomic, strong) WFAlbumItemModel *model;

@end

