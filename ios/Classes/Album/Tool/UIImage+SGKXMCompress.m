//
//  UIImage+SGKXMCompress.m
//  SuperGroup
//
//  Created by 陈伟光 on 2019/11/21.
//  Copyright © 2019 Cunlong Huo. All rights reserved.
//

#import "UIImage+SGKXMCompress.h"

@implementation UIImage (SGKXMCompress)

/**
 *  压缩图片
 *
 *  @param image       需要压缩的图片
 *  @param fImageBytes 希望压缩后的大小(以KB为单位)
 */
+ (void)compressImageFile:(UIImage *)image
               imageBytes:(CGFloat)fImageBytes
               imageBlock:(imageBlock)block
{
    fImageBytes = fImageBytes*1024;
    
    __block NSData *uploadImageData = nil;
    
    uploadImageData = UIImageJPEGRepresentation(image, 1);
    __block UIImage *imageCope = image;
    
    CGSize size = imageCope.size;
    CGFloat imageWidth = size.width;
    CGFloat imageHeight = size.height;
    
    if (uploadImageData.length <= fImageBytes) {
        block(image);
        return;
    }
    
    if (imageWidth < 1080.f || imageHeight < 1080.f) {
        CGFloat fImageScale = fImageBytes/uploadImageData.length;
        uploadImageData = UIImageJPEGRepresentation(image, fImageScale);
        imageCope = [[UIImage alloc] initWithData:uploadImageData];
        block(imageCope);
        return;
    }
    
    
    dispatch_async(dispatch_queue_create("CompressedImage", DISPATCH_QUEUE_SERIAL), ^{//尺寸压缩
    
        CGFloat fImageScale = 1.f;
        CGFloat fImageScale_w = 1080.f/imageWidth;
        CGFloat fImageScale_h = 1080.f/imageHeight;
        fImageScale = fImageScale_w > fImageScale_h? fImageScale_w:fImageScale_h;
        
        CGFloat dHeight = imageHeight*fImageScale;
        
        CGFloat dWidth = imageWidth*fImageScale;
        
        UIGraphicsBeginImageContext(CGSizeMake(dWidth, dHeight));
        [imageCope drawInRect:CGRectMake(0, 0, dWidth, dHeight)];
        imageCope = UIGraphicsGetImageFromCurrentImageContext();
        UIGraphicsEndImageContext();
        
        uploadImageData = UIImageJPEGRepresentation(imageCope, 1);
        
        if (uploadImageData.length > fImageBytes) {//质量压缩
            CGFloat scale = fImageBytes/uploadImageData.length;
            uploadImageData = UIImageJPEGRepresentation(imageCope, scale);
            imageCope = [[UIImage alloc] initWithData:uploadImageData];
        }
        
        dispatch_sync(dispatch_get_main_queue(), ^{
            block(imageCope);
        });
    });
}


/**
 *  压缩图片
 *
 *  @param imageData       需要压缩的图片数据
 *  @param fImageBytes 希望压缩后的大小(以KB为单位)
 */
+ (void)compressImageData:(NSData *)imageData
               imageBytes:(CGFloat)fImageBytes
               imageBlock:(imageDataBlock)block
{
    fImageBytes = fImageBytes*1024;
    
    __block NSData *uploadImageData = imageData;
    
    __block UIImage *imageCope = [UIImage imageWithData:imageData];
    
    CGSize size = imageCope.size;
    CGFloat imageWidth = size.width;
    CGFloat imageHeight = size.height;
    
    if (uploadImageData.length <= fImageBytes) {
        block(UIImageJPEGRepresentation(imageCope, 0.9));
        return;
    }
    
    if (imageWidth < 1080.f || imageHeight < 1080.f) {
        CGFloat fImageScale = fImageBytes/uploadImageData.length;
        uploadImageData = UIImageJPEGRepresentation(imageCope, fImageScale);
        block(uploadImageData);
        return;
    }
    
    
    dispatch_async(dispatch_queue_create("CompressedImage", DISPATCH_QUEUE_SERIAL), ^{//尺寸压缩
    
        CGFloat fImageScale_w = 1080.f/imageWidth;
        CGFloat fImageScale_h = 1080.f/imageHeight;
        CGFloat fImageScale = MAX(fImageScale_w, fImageScale_h);
        
        CGFloat dHeight = imageHeight*fImageScale;
        
        CGFloat dWidth = imageWidth*fImageScale;
        
        UIGraphicsBeginImageContext(CGSizeMake(dWidth, dHeight));
        [imageCope drawInRect:CGRectMake(0, 0, dWidth, dHeight)];
        imageCope = UIGraphicsGetImageFromCurrentImageContext();
        UIGraphicsEndImageContext();
        
        uploadImageData = UIImageJPEGRepresentation(imageCope, 1);
        
        if (uploadImageData.length > fImageBytes) {//质量压缩
            CGFloat scale = fImageBytes/uploadImageData.length;
            uploadImageData = UIImageJPEGRepresentation(imageCope, scale);
        }
        
        dispatch_sync(dispatch_get_main_queue(), ^{
            block(uploadImageData);
        });
    });
    
    
}


///压缩图片
/// @param imageData 原图片数据
/// @param minSize 压缩最小尺寸
/// @param fImageBytes 希望压缩后的大小(以KB为单位)

+ (void)compressImageData:(NSData *)imageData
             minImageSize:(CGSize)minSize
               imageBytes:(CGFloat)fImageBytes
               imageBlock:(imageDataBlock)block
{
    fImageBytes = fImageBytes*1024;
    
    __block NSData *uploadImageData = imageData;
    
    __block UIImage *imageCope = [UIImage imageWithData:imageData];
    
    CGSize size = imageCope.size;
    CGFloat imageWidth = size.width;
    CGFloat imageHeight = size.height;
    
    if (uploadImageData.length <= fImageBytes) {
        block(UIImageJPEGRepresentation(imageCope, 0.9));
        return;
    }
    
    if (imageWidth < minSize.width || imageHeight < minSize.height) {
        CGFloat fImageScale = fImageBytes/uploadImageData.length;
        uploadImageData = UIImageJPEGRepresentation(imageCope, fImageScale);
        block(uploadImageData);
        return;
    }
    
    
    dispatch_async(dispatch_queue_create("CompressedImage", DISPATCH_QUEUE_SERIAL), ^{//尺寸压缩
    
        CGFloat fImageScale_w = minSize.width/imageWidth;
        CGFloat fImageScale_h = minSize.height/imageHeight;
        CGFloat fImageScale = MAX(fImageScale_w, fImageScale_h);
        
        CGFloat dHeight = imageHeight*fImageScale;
        
        CGFloat dWidth = imageWidth*fImageScale;
        
        UIGraphicsBeginImageContext(CGSizeMake(dWidth, dHeight));
        [imageCope drawInRect:CGRectMake(0, 0, dWidth, dHeight)];
        imageCope = UIGraphicsGetImageFromCurrentImageContext();
        UIGraphicsEndImageContext();
        
        uploadImageData = UIImageJPEGRepresentation(imageCope, 1);
        
        if (uploadImageData.length > fImageBytes) {//质量压缩
            CGFloat scale = fImageBytes/uploadImageData.length;
            uploadImageData = UIImageJPEGRepresentation(imageCope, scale);
        }
        
        dispatch_sync(dispatch_get_main_queue(), ^{
            block(uploadImageData);
        });
    });
    
    
}




/// 压缩gif图片
/// @param gifData gif 数据
/// @param block 返回
+ (void)compressImageForGIF:(NSData *)gifData imageBlock:(imageDataBlock)block
{
    if (!gifData) {
      return;
    }
    dispatch_async(dispatch_queue_create("CompressedImage", DISPATCH_QUEUE_SERIAL), ^{
        
        CGImageSourceRef source = CGImageSourceCreateWithData((__bridge CFDataRef)gifData, NULL);
        size_t count = CGImageSourceGetCount(source);
        
        // 设置 gif 文件属性 (0:无限次循环)
        NSDictionary *fileProperties = [self filePropertiesWithLoopCount:0];
        
        NSString *tempFile = [NSTemporaryDirectory() stringByAppendingString:@"scallTemp.gif"];
        NSFileManager *manager = [NSFileManager defaultManager];
        if ([manager fileExistsAtPath:tempFile]) {
            [manager removeItemAtPath:tempFile error:nil];
        }
        NSURL *fileUrl = [NSURL fileURLWithPath:tempFile];
        CGImageDestinationRef destination = CGImageDestinationCreateWithURL((__bridge CFURLRef)fileUrl, CGImageSourceGetType(source), count, NULL);
        
        NSTimeInterval duration = 0.0f;
        for (size_t i = 0; i < count; i++) {
            CGImageRef imageRef = CGImageSourceCreateImageAtIndex(source, i, NULL);
            UIImage *image = [UIImage imageWithCGImage:imageRef];
            UIImage *scallImage = [self zipImageCope:image WithScallSize:CGSizeMake(1080, 1080)];
            
            NSTimeInterval delayTime = [self frameDurationAtIndex:i source:source];
            duration += delayTime;
            // 设置 gif 每帧画面属性
            NSDictionary *frameProperties = [self framePropertiesWithDelayTime:delayTime];
            CGImageDestinationAddImage(destination, scallImage.CGImage, (CFDictionaryRef)frameProperties);
            CGImageRelease(imageRef);
        }
        //合并，设置属性
        CGImageDestinationSetProperties(destination, (CFDictionaryRef)fileProperties);
        // Finalize the GIF
        if (!CGImageDestinationFinalize(destination)) {
            NSLog(@"Failed to finalize GIF destination");
            if (destination != nil) {
                CFRelease(destination);
            }
        }
        CFRelease(destination);
        CFRelease(source);
        dispatch_async(dispatch_get_main_queue(), ^{
            NSData *tempData = [NSData dataWithContentsOfFile:tempFile];
            block(tempData);
        });
        
    });
    
}


+ (UIImage *)zipImageCope:(UIImage *)imageCope WithScallSize:(CGSize)scallSize {
    
    CGFloat width = imageCope.size.width;
    CGFloat height = imageCope.size.height;

    CGFloat scaleFactor = 0.0;
    CGFloat scaledWidth = scallSize.width;
    CGFloat scaledHeight = scallSize.height;
    CGPoint thumbnailPoint = CGPointMake(0.0,0.0);
    
    if (width <= scaledWidth || height <= scaledHeight) {
        return imageCope;
    }

    CGFloat widthFactor = scaledWidth / width;
    CGFloat heightFactor = scaledHeight / height;
    
    scaleFactor = MAX(widthFactor, heightFactor);
    
    scaledWidth= width * scaleFactor;
    scaledHeight = height * scaleFactor;
    
    // center the image
    if (widthFactor > heightFactor)
    {
        thumbnailPoint.y = (scallSize.height - scaledHeight) * 0.5;
    }else if (widthFactor < heightFactor)
    {
        thumbnailPoint.x = (scallSize.width - scaledWidth) * 0.5;
    }
    
    CGRect rect;
    rect.origin = thumbnailPoint;
    rect.size = CGSizeMake(scaledWidth, scaledHeight);
    UIGraphicsBeginImageContext(rect.size);
    [imageCope drawInRect:rect];
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return  image;
}


+ (float)frameDurationAtIndex:(NSUInteger)index source:(CGImageSourceRef)source {
    float frameDuration = 0.f;
    CFDictionaryRef cfFrameProperties = CGImageSourceCopyPropertiesAtIndex(source, index, nil);
    NSDictionary *frameProperties = (__bridge NSDictionary *)cfFrameProperties;
    NSDictionary *gifProperties = frameProperties[(NSString *)kCGImagePropertyGIFDictionary];

    NSNumber *delayTimeUnclampedProp = gifProperties[(NSString *)kCGImagePropertyGIFUnclampedDelayTime];
    if (delayTimeUnclampedProp) {
        frameDuration = [delayTimeUnclampedProp floatValue];
    }
    else {
        NSNumber *delayTimeProp = gifProperties[(NSString *)kCGImagePropertyGIFDelayTime];
        if (delayTimeProp) {
          frameDuration = [delayTimeProp floatValue];
        }
    }

    if (frameDuration < 0.011f) {
        frameDuration = 0.100f;
    }
    CFRelease(cfFrameProperties);
    //  frameDuration += 0.1;
    return frameDuration;
}


+ (NSDictionary *)filePropertiesWithLoopCount:(int)loopCount {
  return @{(NSString *)kCGImagePropertyGIFDictionary:
             @{(NSString *)kCGImagePropertyGIFLoopCount: @(loopCount)}
           };
}

+ (NSDictionary *)framePropertiesWithDelayTime:(NSTimeInterval)delayTime {
  
  return @{(NSString *)kCGImagePropertyGIFDictionary:
             @{(NSString *)kCGImagePropertyGIFDelayTime: @(delayTime)},
           (NSString *)kCGImagePropertyColorModel:(NSString *)kCGImagePropertyColorModelRGB
           };
}


@end
