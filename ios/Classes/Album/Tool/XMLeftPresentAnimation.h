//
//  XMLeftPresentAnimation.h
//  MyCustomAlbum
//
//  Created by 显铭 on 2020/3/23.
//  Copyright © 2020 显铭. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface XMLeftPresentAnimation : NSObject<UIViewControllerAnimatedTransitioning>
@property (nonatomic, assign) BOOL isPresent;

@end

NS_ASSUME_NONNULL_END
