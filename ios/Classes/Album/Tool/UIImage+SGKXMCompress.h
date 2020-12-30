//
//  UIImage+SGKXMCompress.h
//  SuperGroup
//
//  Created by 陈伟光 on 2019/11/21.
//  Copyright © 2019 Cunlong Huo. All rights reserved.
//

#import <UIKit/UIKit.h>


typedef void(^imageBlock)(UIImage *compressedImage);
typedef void (^imageDataBlock)(NSData *compressImageData);

@interface UIImage (SGKXMCompress)
/**
 *  压缩图片
 *
 *  @param image       需要压缩的图片
 *  @param fImageBytes 希望压缩后的大小(以KB为单位)
 */
+ (void)compressImageFile:(UIImage *)image
                imageBytes:(CGFloat)fImageBytes
                imageBlock:(imageBlock)block;

/**
 *  压缩图片
 *
 *  @param imageData       需要压缩的图片
 *  @param fImageBytes 希望压缩后的大小(以KB为单位)
 */
+ (void)compressImageData:(NSData *)imageData
               imageBytes:(CGFloat)fImageBytes
               imageBlock:(imageDataBlock)block;

///压缩图片
/// @param imageData 原图片数据
/// @param minSize 压缩最小尺寸
/// @param fImageBytes 希望压缩后的大小(以KB为单位)

+ (void)compressImageData:(NSData *)imageData
             minImageSize:(CGSize)minSize
               imageBytes:(CGFloat)fImageBytes
               imageBlock:(imageDataBlock)block;

/// 压缩gif图片
/// @param gifData gif 数据
/// @param block 返回
+ (void)compressImageForGIF:(NSData *)gifData imageBlock:(imageDataBlock)block;

@end

