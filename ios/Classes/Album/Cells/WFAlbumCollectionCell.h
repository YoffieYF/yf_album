//
//  WFAlbumCollectionCell.h
//  Wolf
//
//  Created by Yoffie on 2020/8/20.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Photos/Photos.h>


static NSString *const kWFAlbumCollectionCellIdentifier = @"kWFAlbumCollectionCellIdentifier";


@interface WFAlbumCollectionCell : UICollectionViewCell

@property (nonatomic, copy) void(^selectItemBlock)(PHAsset *asset);

@property (nonatomic, strong) PHAsset *asset;

- (void)resetViews:(PHAsset *)asset withSelectAsset:(NSMutableArray*)selectedPhAssets;

@end

