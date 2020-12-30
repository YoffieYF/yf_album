//
//  WFTakePhotoAndVideoManager.m
//  Wolf
//
//  Created by Yoffie on 2020/8/31.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import "WFTakePhotoAndVideoManager.h"
#import <AVFoundation/AVFoundation.h>
#import <CoreServices/CoreServices.h>
#import <MobileCoreServices/MobileCoreServices.h>
#import "WFAlbumTool.h"
#import "SGKXMAlert.h"
#import "WFMacro.h"

#define kKeyWindow    [UIApplication sharedApplication].keyWindow

@interface WFTakePhotoAndVideoManager ()<UIImagePickerControllerDelegate, UINavigationControllerDelegate>
@property(nonatomic, strong) UIImagePickerController * imagePickerVC;
@property(nonatomic, strong) NSArray * mediaTypes;
@end

@implementation WFTakePhotoAndVideoManager

- (instancetype)initWithSystemSelectType:(systemSelectType)systemSelectType {
    if (self = [super init]) {
        if (systemSelectType == systemSelectTypePhoto) {
            self.mediaTypes = @[(NSString *)kUTTypeImage];
        }else if (systemSelectType == systemSelectTypeVideo) {
            self.mediaTypes = @[(NSString *)kUTTypeVideo];
        }else {
            self.mediaTypes = @[(NSString *)kUTTypeImage, (NSString *)kUTTypeVideo];
        }
    }
    return self;
}


#pragma mark  拍照
- (void)takePhoto {

    [self checkVideoAuthorizationWhenCompletion:^(BOOL granted) {
        if (granted) {
            if ([UIImagePickerController isSourceTypeAvailable: UIImagePickerControllerSourceTypeCamera]) {
               
                self.imagePickerVC.mediaTypes = self.mediaTypes;
                self.imagePickerVC.modalPresentationStyle = UIModalPresentationOverCurrentContext;
                [kKeyWindow.rootViewController presentViewController:self.imagePickerVC animated:YES completion:nil];
                   
            }
        }
    }];
}


#pragma mark --- 检测权限
- (void)checkVideoAuthorizationWhenCompletion:(void(^)(BOOL granted))completion {
    AVAuthorizationStatus authStatus = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo];
    if (authStatus == AVAuthorizationStatusNotDetermined) {//第一次请求授权
        [AVCaptureDevice requestAccessForMediaType:AVMediaTypeVideo completionHandler:^(BOOL granted) {
            dispatch_async(dispatch_get_main_queue(), ^{
                completion(granted);
            });
        }];
    }else if (authStatus == AVAuthorizationStatusRestricted || authStatus == AVAuthorizationStatusDenied){//未授权
        [self userDeniedRecordRequest];
        completion(NO);
        
    }else{//已授权
        completion(YES);
    }
}

#pragma mark - - - 让用户去设置相机权限
- (void)userDeniedRecordRequest {
    UIAlertController *alert = [SGKXMAlert alertControllerWithTitle:@"设置相机权限" message:@"您还未开启相机，该功能需要开启相机权限" cancelHandler:^(UIAlertAction * _Nonnull cancelAction) {

    } confirmHandler:^(UIAlertAction * _Nonnull confirmAction) {
        //去设置权限
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:UIApplicationOpenSettingsURLString]];
    }];

    [kKeyWindow.rootViewController presentViewController:alert animated:YES completion:nil];
    
}


#pragma mark --- UIImagePickerControllerDelegate
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<UIImagePickerControllerInfoKey,id> *)info {
    [picker dismissViewControllerAnimated:YES completion:nil];
//    NSString *dataUTI = [info objectForKey:UIImagePickerControllerMediaType];
//    NSSLog(@"dataUTI==%@",dataUTI);
    UIImage *image = [info objectForKey:UIImagePickerControllerOriginalImage];
    WF_BlockWeakSelf(weakSelf, self);
    [[WFAlbumTool shareInstance] compressOriginalImage:image completionHandler:^(NSArray * _Nonnull modelArray) {
        if (weakSelf.sendCompressPhotosBlock) {
            weakSelf.sendCompressPhotosBlock(modelArray);
        }
    }];
    self.imagePickerVC.delegate = nil;
    self.imagePickerVC = nil;
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker {
    [picker dismissViewControllerAnimated:YES completion:nil];
    self.imagePickerVC.delegate = nil;
    self.imagePickerVC = nil;
}

#pragma mark --- getters
- (UIImagePickerController *)imagePickerVC {
    if (!_imagePickerVC) {
        _imagePickerVC = [[UIImagePickerController alloc] init];
        _imagePickerVC.sourceType = UIImagePickerControllerSourceTypeCamera;
        _imagePickerVC.delegate = self;
        _imagePickerVC.allowsEditing = NO;
        _imagePickerVC.videoMaximumDuration = 15;//视频最长拍摄时间
        _imagePickerVC.videoQuality = UIImagePickerControllerQualityTypeHigh;
    }
    return _imagePickerVC;
}

@end
