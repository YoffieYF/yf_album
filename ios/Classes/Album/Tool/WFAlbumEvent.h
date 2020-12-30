//
//  WFAlbumEvent.h
//  Wolf
//
//  Created by Yoffie on 2020/8/28.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QTEventBus.h"
#import "WFAssetEntityModel.h"


@interface WFAlbumEvent : NSObject

@end


@interface WFPhotosPreviewSelectEvent : NSObject<QTEvent>

@property (nonatomic, strong) NSMutableArray *selectAlbums;

@end


@interface WFPhotosPreviewOrginalSelectEvent : NSObject<QTEvent>

@property (nonatomic, assign) BOOL imageIsOrginal;

@end


@interface WFAlbumSendImageEvent : NSObject<QTEvent>

@property (nonatomic, assign) NSArray<WFAssetEntityModel*> *models;

@end


