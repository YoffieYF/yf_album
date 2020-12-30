//
//  WFAlbumTool.h
//  Wolf
//
//  Created by Yoffie on 2020/8/20.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Photos/Photos.h>


typedef NS_ENUM(NSInteger, WFPhotosPreviewViewControllerType) {
    WFPhotosPreviewViewControllerTypeAlbum, //相册预览
    WFPhotosPreviewViewControllerTypeSelect, //选中照片预览
    WFPhotosPreviewViewControllerTypeURL, //网络图片预览
};


@interface WFAlbumTool : NSObject

/// 图片文件夹
@property (nonatomic, copy) NSString *dirPath;

+ (instancetype)shareInstance;
/// 获取缩略图
- (void)getVideoImageFromPHAsset:(PHAsset *)asset forSize:(CGSize)size
             completedResultBack:(void (^)(UIImage *image))resultBack;


///发送图片和视频
- (void)sendMediasWithAssetArray:(NSArray<PHAsset *> *)assetArray imagesIsOrginal:(BOOL)isOrginal completionHandler:(void(^)(NSArray *modelArray))completionHandler;

/**
 通过phasset获取图片的原图数据
 
 @param mAsset 资源文件
 @param imageBlock 图片数据回传
 */
- (void)fetchImageWithAsset:(PHAsset*)mAsset imageBlock:(void(^)(NSData*))imageBlock;

/// 压缩图片
- (void)compressSourceFiles:(NSMapTable *)sourceFiles imagesIsOrginal:(BOOL)isOrginal completionFinalHandler:(void(^)(NSArray *modelArray))completionFinalHandler;

//对相机拍摄的图片和视频进行压缩处理
- (void)compressOriginalImage:(UIImage *)originalImage completionHandler:(void(^)(NSArray *modelArray))completionHandler;
@end

