//
//  WFAlbumTool.m
//  Wolf
//
//  Created by Yoffie on 2020/8/20.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import "WFAlbumTool.h"
#import <YYCache/YYCache.h>
#import "WFAssetEntityModel.h"
#import "UIImage+SGKXMCompress.h"
#import "VideoCompressManager.h"
#import "SVProgressHUD.h"
#import "WFMacro.h"


#define CacheName   @"picAndVideoCache"
#define LOCALDIR [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) lastObject]
#define CHAT_MEDIAS_DIR(string) [LOCALDIR stringByAppendingPathComponent:string]
#define BROWSER_IMAGE_DIR  @"browser.images"
#define DirKey @"Wolf"


@interface WFAlbumTool ()
@property(nonatomic, strong) YYCache * LCache;
@end


@implementation WFAlbumTool
static WFAlbumTool *_singleInstance = nil;

+ (instancetype)shareInstance
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        if (_singleInstance == nil) {
            _singleInstance = [[self alloc]init];
            _singleInstance.LCache = [YYCache cacheWithName:CacheName];
            [_singleInstance createDirectory];
        }
    });
    return _singleInstance;
}

+ (instancetype)allocWithZone:(struct _NSZone *)zone
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _singleInstance = [super allocWithZone:zone];
    });
    return _singleInstance;
}

-(id)copyWithZone:(NSZone *)zone{
    return _singleInstance;
}

-(id)mutableCopyWithZone:(NSZone *)zone{
    return _singleInstance;
}

/**
 通过phasset获取图片的原图数据
 
 @param mAsset 资源文件
 @param imageBlock 图片数据回传
 */
- (void)fetchImageWithAsset:(PHAsset*)mAsset imageBlock:(void(^)(NSData*))imageBlock {
    [[PHImageManager defaultManager] requestImageDataForAsset:mAsset options:nil resultHandler:^(NSData * _Nullable imageData, NSString * _Nullable dataUTI, UIImageOrientation orientation, NSDictionary * _Nullable info) {
        
        NSData *finalImageData = imageData;
        if ([dataUTI isEqualToString:@"public.heif"] || [dataUTI isEqualToString:@"public.heic"]) {//转成jpg格式
            CIImage *ciImage = [CIImage imageWithData:imageData];
            CIContext *context = [CIContext context];
            finalImageData = [context JPEGRepresentationOfImage:ciImage colorSpace:ciImage.colorSpace options:@{}];
        }
        // 直接得到最终的 NSData 数据
        if (imageBlock) {
            imageBlock(finalImageData);
        }
    }];
}

#pragma mark --- 获取缩略图
- (void)getVideoImageFromPHAsset:(PHAsset *)asset
                         forSize:(CGSize)size
             completedResultBack:(void (^)(UIImage *image))resultBack {
    PHImageRequestOptions *option = [[PHImageRequestOptions alloc] init];
    option.synchronous = YES;
    option.resizeMode = PHImageRequestOptionsResizeModeFast;
    option.deliveryMode = PHImageRequestOptionsDeliveryModeHighQualityFormat;
    option.networkAccessAllowed = YES;
    
    CGSize imageSize;
    imageSize = size;
    
    [[PHImageManager defaultManager] requestImageForAsset:asset targetSize:imageSize contentMode:PHImageContentModeAspectFill options:option resultHandler:^(UIImage * _Nullable result, NSDictionary * _Nullable info) {
        UIImage *iamge = result;
        resultBack(iamge);
    }];
    
}

///发送图片和视频
- (void)sendMediasWithAssetArray:(NSArray<PHAsset *> *)assetArray imagesIsOrginal:(BOOL)isOrginal completionHandler:(void(^)(NSArray *modelArray))completionHandler {
    
    [SVProgressHUD setDefaultMaskType:SVProgressHUDMaskTypeBlack];
    [SVProgressHUD showWithStatus:@"发送中"];
    
    NSMapTable *compressMapTable = [NSMapTable mapTableWithKeyOptions:NSPointerFunctionsWeakMemory valueOptions:NSPointerFunctionsStrongMemory];
    NSMutableArray *fileArr = [NSMutableArray array];
    NSUInteger totalCount = assetArray.count;
    __block NSUInteger finalCount = 0;
    
    @synchronized (self) {
        
        for (PHAsset *asset in assetArray) {
            @autoreleasepool {
                NSString *filename = [asset valueForKey:@"filename"];
                NSString *imageTypeName = @"";
                if ([filename hasSuffix:@"GIF"]) {//是否gif动图
                    imageTypeName = @".gif";
                }else{
                    imageTypeName = @".jpg";
                }
                
                if (isOrginal) {
                    NSString *keyStr = [NSString stringWithFormat:@"original_%@",filename];
                    if ([self.LCache containsObjectForKey:keyStr]) {
                        WFAssetEntityModel *model = (WFAssetEntityModel *)[self.LCache objectForKey:keyStr];
                        if ([model.type isEqualToString:@"IMAGE"]) {//图片
                            
                            model.path = [self.dirPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@%@", model.fileName,imageTypeName]];
                            model.coverPath = model.path;
                            
                            [fileArr addObject:model];
                            finalCount ++;
                            
                            if (finalCount >= totalCount) {
                                break;
                            }
                            continue;
                        }
                    }else if ([self.LCache containsObjectForKey:filename]){
                        WFAssetEntityModel *model = (WFAssetEntityModel *)[self.LCache objectForKey:filename];
                        if ([model.type isEqualToString:@"VIDEO"]) {//视频
                            
                            model.path = [self.dirPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.mp4", model.fileName]];
                            model.coverPath = [self.dirPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.jpg", model.fileName]];
                            
                            [fileArr addObject:model];
                            finalCount ++;
                            
                            if (finalCount >= totalCount) {
                                break;
                            }
                            continue;
                        }
                    }
                }else{
                    
                    if ([self.LCache containsObjectForKey:filename]) {
                        WFAssetEntityModel *model = (WFAssetEntityModel *)[self.LCache objectForKey:filename];
                        if ([model.type isEqualToString:@"IMAGE"]) {//图片
                            
                            model.path = [self.dirPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@%@", model.fileName,imageTypeName]];
                            model.coverPath = model.path;
                        }else{//视频
                            model.path = [self.dirPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.mp4", model.fileName]];
                            model.coverPath = [self.dirPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.jpg", model.fileName]];
                        }
                        
                        [fileArr addObject:model];
                        finalCount ++;
                        
                        if (finalCount >= totalCount) {
                            break;
                        }
                        continue;
                    }
                }
                
                //创建文件(获取缩略图)
                NSString *currentDateStr = [self currentDateAndTime];
                NSString *imgPath = [self.dirPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@%@", currentDateStr,imageTypeName]];
                
                WFAssetEntityModel *model = [[WFAssetEntityModel alloc] init];
                model.fileName = currentDateStr;
                model.type = (asset.mediaType == PHAssetMediaTypeImage)? @"IMAGE":@"VIDEO";
                if ([model.type isEqualToString:@"IMAGE"]) {
                    model.w = asset.pixelWidth;
                    model.h = asset.pixelHeight;
                }else{
                    model.w = asset.pixelWidth > asset.pixelHeight? 960:544;
                    model.h = asset.pixelWidth > asset.pixelHeight? 544:960;
                }
                model.isOriginal = ([model.type isEqualToString:@"IMAGE"] && isOrginal == YES)? 1:0;
                model.videoDuration = (int)asset.duration;//视频时长s
                //把缓存中没有的图片视频资源添加到maptable中
                [compressMapTable setObject:model forKey:asset];
                
                [self getVideoImageFromPHAsset:asset forSize:CGSizeZero completedResultBack:^(UIImage * _Nonnull image) {
                    if (![[NSFileManager defaultManager] fileExistsAtPath:imgPath]) {
                        BOOL isSuccess = [[NSFileManager defaultManager] createFileAtPath:imgPath contents:nil attributes:nil];
                        if (isSuccess) {
                            NSData *data = UIImageJPEGRepresentation(image, 0.9);
                            [data writeToFile:imgPath atomically:YES];
                            model.coverPath = imgPath;
                        }
                        else {
                         
                        }
                        
                        finalCount ++;
        
                    }
                }];
            }
        }
    }
    
    @synchronized (self) {
        //压缩处理
        if (compressMapTable.count > 0) {
            [self compressSourceFiles:compressMapTable imagesIsOrginal:isOrginal completionFinalHandler:^(NSArray *modelArray) {
                [fileArr addObjectsFromArray:modelArray];
                dispatch_async(dispatch_get_main_queue(), ^{
                    [SVProgressHUD dismiss];
                    completionHandler(fileArr);
                });
            }];
        } else {
            dispatch_async(dispatch_get_main_queue(), ^{
                [SVProgressHUD dismiss];
                completionHandler(fileArr);
            });
        }
    }
}

//压缩图片和视频
- (void)compressSourceFiles:(NSMapTable *)sourceFiles imagesIsOrginal:(BOOL)isOrginal completionFinalHandler:(void(^)(NSArray *modelArray))completionFinalHandler {
    NSEnumerator *enumerator = [sourceFiles keyEnumerator];
    __block NSMutableArray<WFAssetEntityModel<NSCoding> *> *finalModels = [[NSMutableArray alloc]init];
    __block NSUInteger totalCount = sourceFiles.count;
    __block NSUInteger finalCount = 0;
    for (PHAsset *asset in enumerator) {
        WFAssetEntityModel<NSCoding> *model = [sourceFiles objectForKey:asset];
        if (model == nil) {
            model = [[WFAssetEntityModel<NSCoding> alloc]init];
        }
        dispatch_async(dispatch_get_global_queue(0, 0), ^{
            NSString *filename = [asset valueForKey:@"filename"];
            NSString *imageTypeName = @"";
            if ([filename hasSuffix:@"GIF"]) {//是否gif动图
                imageTypeName = @".gif";
            }else{
                imageTypeName = @".jpg";
            }
            
            if (asset.mediaType == PHAssetMediaTypeImage) {//照片
                
                PHImageRequestOptions *option = [[PHImageRequestOptions alloc] init];
                option.synchronous = YES;
                option.networkAccessAllowed = YES;
                option.resizeMode = PHImageRequestOptionsResizeModeNone;
                
                [[PHImageManager defaultManager] requestImageDataForAsset:asset options:option resultHandler:^(NSData * _Nullable imageData, NSString * _Nullable dataUTI, UIImageOrientation orientation, NSDictionary * _Nullable info) {
                    NSData *finalImageData = imageData;
                    
                    if ([dataUTI isEqualToString:@"public.heif"] || [dataUTI isEqualToString:@"public.heic"]) {//转成jpg格式
                        CIImage *ciImage = [CIImage imageWithData:imageData];
                        CIContext *context = [CIContext context];
                        finalImageData = [context JPEGRepresentationOfImage:ciImage colorSpace:ciImage.colorSpace options:@{}];
                    }
                    if (isOrginal) {
                        WF_BlockWeakSelf(weakSelf, self);
                        //创建文件
                        NSString *imgPath = [self.dirPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@%@", model.fileName,imageTypeName]];
                        if ([[NSFileManager defaultManager] fileExistsAtPath:imgPath]) {
                            [[NSFileManager defaultManager] removeItemAtPath:imgPath error:nil];
                        }
                        
                        [finalImageData writeToFile:imgPath atomically:YES];
                        model.path = imgPath;
                        model.coverPath = imgPath;
                        model.isCompressSuccess = YES;
                        [finalModels addObject:model];
                        finalCount ++;
                        if (finalCount >= totalCount) {
                            completionFinalHandler(finalModels);
                        }
                        NSString *keyStr = [NSString stringWithFormat:@"original_%@",filename];
                        [weakSelf.LCache setObject:model forKey:keyStr withBlock:^{
                           
                        }];
                        
                    }
                    else{
                        //不是发送原图，进行压缩
                        WF_BlockWeakSelf(weakSelf, self);
                        if ([imageTypeName isEqualToString:@".gif"]) {//gif 图片
                            //创建文件
                            NSString *imgPath = [weakSelf.dirPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@%@", model.fileName,imageTypeName]];
                            //                             NSSLog(@"路径 = %@", imgPath);
                            if ([[NSFileManager defaultManager] fileExistsAtPath:imgPath]) {
                                [[NSFileManager defaultManager] removeItemAtPath:imgPath error:nil];
                            }
                            
                            [finalImageData writeToFile:imgPath atomically:YES];
                            model.path = imgPath;
                            model.coverPath = imgPath;
                            model.isCompressSuccess = YES;
                            [finalModels addObject:model];
                            finalCount ++;
                            if (finalCount >= totalCount) {
                                completionFinalHandler(finalModels);
                            }
                            [weakSelf.LCache setObject:model forKey:filename withBlock:^{
                            
                            }];
                            
                        }
                        else{
                            
                            [UIImage compressImageData:finalImageData imageBytes:1024.0 imageBlock:^(NSData * _Nonnull compressedImageData) {
                                
                                //创建文件
                                NSString *imgPath = [weakSelf.dirPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@%@", model.fileName,imageTypeName]];
                                //                                 NSSLog(@"路径 = %@", imgPath);
                                if ([[NSFileManager defaultManager] fileExistsAtPath:imgPath]) {
                                    [[NSFileManager defaultManager] removeItemAtPath:imgPath error:nil];
                                }
                                
                                [compressedImageData writeToFile:imgPath atomically:YES];
                                model.path = imgPath;
                                model.coverPath = imgPath;
                                model.isCompressSuccess = YES;
                                [finalModels addObject:model];
                                finalCount ++;
                                if (finalCount >= totalCount) {
                                    completionFinalHandler(finalModels);
                                }
                                [weakSelf.LCache setObject:model forKey:filename withBlock:^{
                                    //                                     NSSLog(@"插入缓存成功");
                                }];
                                
                            }];
                        }
                        
                    }
                }];
            }
            else{//视频
                WF_BlockWeakSelf(weakSelf, self);
                [self getVideoPathFromPHAsset:asset Complete:^(AVURLAsset *urlAsset, NSString *filePatch, NSInteger dTime) {
                    //创建文件
                    NSString *outputPath = [self.dirPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.mp4", model.fileName]];
                    [VideoCompressManager compressVideoWithAsset:urlAsset withBiteRate:nil withFrameRate:nil withVideoWidth:nil withVideoHeight:nil outputPath:outputPath compressComplete:^(id  _Nonnull responseObjc) {
                        
                        if ([responseObjc isKindOfClass:[NSError class]]) {//压缩失败
                            model.isCompressSuccess = NO;
                            [finalModels addObject:model];
                            finalCount ++;
                            if (finalCount >= totalCount) {
                                completionFinalHandler(finalModels);
                            }
                        }else{//压缩完成
                            model.path = (NSString *)responseObjc;
                            model.isCompressSuccess = YES;
                            [finalModels addObject:model];
                            finalCount ++;
                            if (finalCount >= totalCount) {
                                completionFinalHandler(finalModels);
                            }
                            [weakSelf.LCache setObject:model forKey:filename withBlock:^{
                        
                            }];
                        }
                        
                    }];
                    
                }];
            }
        });
    }
}

///对相机拍摄的图片和视频进行压缩处理
- (void)compressOriginalImage:(UIImage *)originalImage completionHandler:(void(^)(NSArray *modelArray))completionHandler {
    
    [SVProgressHUD setDefaultMaskType:SVProgressHUDMaskTypeBlack];
    [SVProgressHUD showWithStatus:@"发送中"];
    
    NSMutableArray *fileArr = [NSMutableArray array];
    NSData *data = UIImageJPEGRepresentation(originalImage, 1);
    
    WF_BlockWeakSelf(weakSelf, self);
    [UIImage compressImageData:data imageBytes:1024.0 imageBlock:^(NSData * _Nonnull compressedImageData) {
        //创建文件
        NSString *currentDateStr = [weakSelf currentDateAndTime];
        NSString *imgPath = [weakSelf.dirPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.jpg", currentDateStr]];

        if (![[NSFileManager defaultManager] fileExistsAtPath:imgPath]) {
            BOOL isSuccess = [[NSFileManager defaultManager] createFileAtPath:imgPath contents:nil attributes:nil];
            if (isSuccess) {
                [compressedImageData writeToFile:imgPath atomically:YES];
                
                WFAssetEntityModel *model = [[WFAssetEntityModel alloc] init];
                model.fileName = currentDateStr;
                model.w = originalImage.size.width;
                model.h = originalImage.size.height;
                model.path = imgPath;
                model.coverPath = imgPath;
                model.type = @"IMAGE";
                model.isOriginal = 0;
                
                [fileArr addObject:model];
                
                dispatch_async(dispatch_get_main_queue(), ^{
                    [SVProgressHUD dismiss];
                    completionHandler(fileArr);
                });
                
            }else {
                [SVProgressHUD dismiss];
            }
        }
    }];
}

#pragma mark --- 获取相册视频的本地地址
- (void)getVideoPathFromPHAsset:(PHAsset *)asset Complete:(void (^)(AVURLAsset *urlAsset ,NSString *filePatch,NSInteger dTime))result {
    PHVideoRequestOptions *options = [[PHVideoRequestOptions alloc] init];
    options.version = PHVideoRequestOptionsVersionOriginal;
    options.deliveryMode = PHVideoRequestOptionsDeliveryModeAutomatic;
    
    PHImageManager *manager = [PHImageManager defaultManager];
    [manager requestAVAssetForVideo:asset
                            options:options
                      resultHandler:^(AVAsset * _Nullable asset, AVAudioMix * _Nullable audioMix, NSDictionary * _Nullable info) {
        AVURLAsset *urlAsset = (AVURLAsset *)asset;
        CMTime   time = [asset duration];
        NSInteger seconds = ceil(time.value/time.timescale);
        result(urlAsset,urlAsset.URL.absoluteString,seconds%60);
    }];
    
}

#pragma mark --- 获取当前时间戳
- (NSString *)currentDateAndTime {
    
    NSDate *date = [NSDate date];
    NSTimeZone *zone = [NSTimeZone systemTimeZone];
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"YYYYMMddHHmmssSSS"];
    [dateFormatter setTimeZone:zone];
    NSString *dateString = [dateFormatter stringFromDate:date];
    return dateString;
}

#pragma mark --- 创建文件夹
- (void)createDirectory {
    //创建文件夹
    self.dirPath = [CHAT_MEDIAS_DIR(BROWSER_IMAGE_DIR) stringByAppendingPathComponent:DirKey];
    if ([[NSFileManager defaultManager] fileExistsAtPath:self.dirPath]) {
       
    }else{
        if ([[NSFileManager defaultManager] createDirectoryAtPath:self.dirPath withIntermediateDirectories:YES attributes:nil error:nil]) {
           
        }else {
          
        }
    }
}

@end
