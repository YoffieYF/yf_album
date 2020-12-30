//
//  VideoCompressManager.h
//  Runner
//
//  Created by 显铭 on 2020/4/8.
//  Copyright © 2020 The Chromium Authors. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <AVFoundation/AVFoundation.h>


@interface VideoCompressManager : NSObject

+ (void)compressVideoWithAsset:(AVURLAsset *_Nullable)asset withBiteRate:(NSNumber * _Nullable)outputBiteRate withFrameRate:(NSNumber * _Nullable)outputFrameRate withVideoWidth:(NSNumber * _Nullable)outputWidth withVideoHeight:(NSNumber * _Nullable)outputHeight outputPath:(NSString *_Nullable)outputPath compressComplete:(void(^_Nullable)(id _Nonnull responseObjc))compressComplete;

@end

