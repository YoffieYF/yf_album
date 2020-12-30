//
//  WFAlbumChooseCell.h
//  Wolf
//
//  Created by Yoffie on 2020/8/19.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "WFAlbumModel.h"


static NSString *const kWFAlbumChooseCellIdentifier = @"kWFAlbumChooseCellIdentifier";


@interface WFAlbumChooseCell : UITableViewCell

@property(nonatomic, strong) WFAlbumModel *albumModel;

@end

