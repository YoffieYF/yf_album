//
//  VideoCompressManager.m
//  Runner
//
//  Created by 显铭 on 2020/4/8.
//  Copyright © 2020 The Chromium Authors. All rights reserved.
//

#import "SVProgressHUD.h"
#import "VideoCompressManager.h"
#import <faststarVideo/faststarVideo.h>
//#import "faststarVideo.h"

@implementation VideoCompressManager
/*
 * 自定义视频压缩
 * videoUrl 原视频url路径 必传
 * outputBiteRate 压缩视频至指定比特率(bps) 可传nil 默认480kbps
 * outputFrameRate 压缩视频至指定帧率 可传nil 默认25fps
 * outputWidth 压缩视频至指定宽度 可传nil 默认720
 * outputHeight 压缩视频至指定高度 可传nil 默认480
 * compressComplete 压缩后的视频信息回调 (id responseObjc) 可自行打印查看
 * outputPath 写入的路径
 **/
+ (void)compressVideoWithAsset:(AVURLAsset *)asset withBiteRate:(NSNumber * _Nullable)outputBiteRate withFrameRate:(NSNumber * _Nullable)outputFrameRate withVideoWidth:(NSNumber * _Nullable)outputWidth withVideoHeight:(NSNumber * _Nullable)outputHeight outputPath:(NSString *)outputPath compressComplete:(void(^)(id responseObjc))compressComplete {
    
    if (!asset) {
        [SVProgressHUD showErrorWithStatus:@"视频不能为空"];
        return;
    }
    
    NSInteger compressBiteRate = outputBiteRate ? [outputBiteRate integerValue] : 1080006;
    NSInteger compressFrameRate = outputFrameRate ? [outputFrameRate integerValue] : 25;
    NSInteger compressWidth = outputWidth ? [outputWidth integerValue] : 960;
    NSInteger compressHeight = outputHeight ? [outputHeight integerValue] : 544;
    
    //视频时长 S
//    CMTime time = [asset duration];
//    NSInteger seconds = ceil(time.value/time.timescale);
//    if (seconds < 3) {
//        [SVProgressHUD showErrorWithStatus:@"请上传3秒以上的视频"];
//        return;
//    }
    //压缩前原视频大小MB
//    unsigned long long fileSize = [[NSFileManager defaultManager] attributesOfItemAtPath:asset.URL.absoluteString error:nil].fileSize;
//    float fileSizeMB = fileSize / (1024.0*1024.0);
    //取出asset中的视频文件
    AVAssetTrack *videoTrack = [asset tracksWithMediaType:AVMediaTypeVideo].firstObject;

    //压缩前原视频宽高
    NSInteger videoWidth = videoTrack.naturalSize.width;
    NSInteger videoHeight = videoTrack.naturalSize.height;
    //压缩前原视频比特率
    NSInteger kbps = videoTrack.estimatedDataRate / 1024;
    //压缩前原视频帧率
//    NSInteger frameRate = [videoTrack nominalFrameRate];
//    NSLog(@"\noriginalVideo\nfileSize = %.2f MB,\n videoWidth = %ld,\n videoHeight = %ld,\n video bitRate = %ld\n, video frameRate = %ld", fileSizeMB, videoWidth, videoHeight, kbps, frameRate);

    //原视频比特率小于指定比特率则按照原视频比特率90%
    if (kbps < (compressBiteRate / 1024)) {
        compressBiteRate = kbps * 0.9 * 1024;
    }
    
    //创建视频文件读取者
    AVAssetReader *reader = [AVAssetReader assetReaderWithAsset:asset error:nil];
    //从指定文件读取视频
    AVAssetReaderTrackOutput *videoOutput = [AVAssetReaderTrackOutput assetReaderTrackOutputWithTrack:videoTrack outputSettings:[self configVideoOutput]];
    //取出原视频中音频详细资料
    AVAssetTrack *audioTrack = [asset tracksWithMediaType:AVMediaTypeAudio].firstObject;
    //从音频资料中读取音频
    AVAssetReaderTrackOutput *audioOutput = nil;
    if (audioTrack != nil) {
        audioOutput = [AVAssetReaderTrackOutput assetReaderTrackOutputWithTrack:audioTrack outputSettings:[self configAudioOutput]];
    }
    //将读取到的视频信息添加到读者队列中
    if ([reader canAddOutput:videoOutput]) {
        [reader addOutput:videoOutput];
    }
    //将读取到的音频信息添加到读者队列中
    if (audioOutput && [reader canAddOutput:audioOutput]) {
        [reader addOutput:audioOutput];
    }
    //视频文件写入者
    AVAssetWriter *writer = [AVAssetWriter assetWriterWithURL:[NSURL fileURLWithPath:outputPath] fileType:AVFileTypeMPEG4 error:nil];
    //根据指定配置创建写入的视频文件
    AVAssetWriterInput *videoInput = [AVAssetWriterInput assetWriterInputWithMediaType:AVMediaTypeVideo outputSettings:[self videoCompressSettingsWithBitRate:compressBiteRate withFrameRate:compressFrameRate withWidth:compressWidth WithHeight:compressHeight withOriginalWidth:videoWidth withOriginalHeight:videoHeight]];
    //对视频进行旋转处理矫正
    NSUInteger degress = [self degressFromVideoWithAsset:videoTrack];
    if (degress == 90) {
        videoInput.transform = CGAffineTransformMakeRotation(M_PI_2);
    }else if (degress == 180){
        videoInput.transform = CGAffineTransformMakeRotation(M_PI);
    }else if (degress == 270){
        videoInput.transform = CGAffineTransformMakeRotation(M_PI_2*3.0);
    }

    //根据指定配置创建写入的音频文件
    AVAssetWriterInput *audioInput = [AVAssetWriterInput assetWriterInputWithMediaType:AVMediaTypeAudio outputSettings:[self audioCompressSettings]];
    if ([writer canAddInput:videoInput]) {
        [writer addInput:videoInput];
    }
    if ([writer canAddInput:audioInput]) {
        [writer addInput:audioInput];
    }
//    [SVProgressHUD showWithStatus:@"视频压缩中..."];
    [reader startReading];
    [writer startWriting];
    [writer startSessionAtSourceTime:kCMTimeZero];
    
    //创建视频写入队列
    dispatch_queue_t videoQueue = dispatch_queue_create("Video Queue", DISPATCH_QUEUE_SERIAL);
    //创建音频写入队列
    dispatch_queue_t audioQueue = dispatch_queue_create("Audio Queue", DISPATCH_QUEUE_SERIAL);
    //创建一个线程组
    dispatch_group_t group = dispatch_group_create();
    //进入线程组
    dispatch_group_enter(group);
    //队列准备好后 usingBlock
    [videoInput requestMediaDataWhenReadyOnQueue:videoQueue usingBlock:^{
        BOOL completedOrFailed = NO;
        while ([videoInput isReadyForMoreMediaData] && !completedOrFailed) {
            CMSampleBufferRef sampleBuffer = [videoOutput copyNextSampleBuffer];
            if (sampleBuffer != NULL) {
                [videoInput appendSampleBuffer:sampleBuffer];
//                NSSLog(@"===%@===", sampleBuffer);
                CFRelease(sampleBuffer);
            } else {
                completedOrFailed = YES;
                [videoInput markAsFinished];
                dispatch_group_leave(group);
            }
        }
    }];
    dispatch_group_enter(group);
    //队列准备好后 usingBlock
    [audioInput requestMediaDataWhenReadyOnQueue:audioQueue usingBlock:^{
        BOOL completedOrFailed = NO;
        while ([audioInput isReadyForMoreMediaData] && !completedOrFailed) {
            CMSampleBufferRef sampleBuffer = [audioOutput copyNextSampleBuffer];
            if (sampleBuffer != NULL) {
                BOOL success = [audioInput appendSampleBuffer:sampleBuffer];
//                NSSLog(@"===%@===", sampleBuffer);
                CFRelease(sampleBuffer);
                completedOrFailed = !success;
            } else {
                completedOrFailed = YES;
            }
        }
        if (completedOrFailed) {
            [audioInput markAsFinished];
            dispatch_group_leave(group);
        }
    }];
    //完成压缩
    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        if ([reader status] == AVAssetReaderStatusReading) {
            [reader cancelReading];
        }
        switch (writer.status) {
            case AVAssetWriterStatusWriting:
            {
                [writer finishWritingWithCompletionHandler:^{
//                    NSSLog(@"视频压缩完成");
                    [FaststarVideoTool makeVideoFasterWithPath:outputPath];//视频moov前置
                    compressComplete(outputPath);
                }];
            }
                break;
            case AVAssetWriterStatusCancelled:
                break;
            case AVAssetWriterStatusFailed:
            {
                [writer finishWritingWithCompletionHandler:^{
//                    NSSLog(@"===error：%@===", writer.error);
                    compressComplete(writer.error);
                }];
            }
                break;
            case AVAssetWriterStatusCompleted:
            {
//                NSSLog(@"视频压缩完成");
                [writer finishWritingWithCompletionHandler:^{
                    [FaststarVideoTool makeVideoFasterWithPath:outputPath];//视频moov前置
                    compressComplete(outputPath);
                }];
            }
                break;
            default:
                break;
        }
    });
}


///判断视频旋转方向
+ (NSUInteger)degressFromVideoWithAsset:(AVAssetTrack *)videoTrack {
    
    NSUInteger degress = 0;
    CGAffineTransform t = videoTrack.preferredTransform;
    
    if (t.a == 0 && t.b == 1.0 && t.c == -1.0 && t.d == 0) {
        //Portrait
        degress = 90;
    }else if (t.a == 0 && t.b == -1.0 && t.c == 1.0 && t.d == 0){
        // PortraitUpsideDown
        degress = 270;
    }else if (t.a == 1.0 && t.b == 0 && t.c == 0 && t.d == 1.0){
        // LandscapeRight
        degress = 0;
    }else if (t.a == -1.0 && t.b == 0 && t.c == 0 && t.d == -1.0){
        // LandscapeLeft
        degress = 180;
    }
    
    return degress;

}


//视频编码设置
+ (NSDictionary *)videoCompressSettingsWithBitRate:(NSInteger)biteRate withFrameRate:(NSInteger)frameRate withWidth:(NSInteger)width WithHeight:(NSInteger)height withOriginalWidth:(NSInteger)originalWidth withOriginalHeight:(NSInteger)originalHeight{
    /*
     * AVVideoAverageBitRateKey： 比特率（码率）每秒传输的文件大小 kbps
     * AVVideoExpectedSourceFrameRateKey：帧率 每秒播放的帧数
     * AVVideoProfileLevelKey：画质水平
     **/
    NSInteger returnWidth = originalWidth > originalHeight ? width : height;
    NSInteger returnHeight = originalWidth > originalHeight ? height : width;
    
    NSDictionary *compressProperties = @{
                                         AVVideoAverageBitRateKey : @(biteRate),
                                         AVVideoExpectedSourceFrameRateKey : @(frameRate),
                                         AVVideoProfileLevelKey : AVVideoProfileLevelH264HighAutoLevel
                                         };
    if (@available(iOS 11.0, *)) {
        NSDictionary *compressSetting = @{
                                          AVVideoCodecKey : AVVideoCodecTypeH264,
                                          AVVideoWidthKey : @(returnWidth),
                                          AVVideoHeightKey : @(returnHeight),
                                          AVVideoCompressionPropertiesKey : compressProperties,
                                          AVVideoScalingModeKey : AVVideoScalingModeResizeAspect
                                          };
        return compressSetting;
    }else {
        NSDictionary *compressSetting = @{
                                          AVVideoCodecKey : AVVideoCodecH264,
                                          AVVideoWidthKey : @(returnWidth),
                                          AVVideoHeightKey : @(returnHeight),
                                          AVVideoCompressionPropertiesKey : compressProperties,
                                          AVVideoScalingModeKey : AVVideoScalingModeResizeAspect
                                          };
        return compressSetting;
    }
}


//音频编码设置
+ (NSDictionary *)audioCompressSettings{
    AudioChannelLayout stereoChannelLayout = {
        .mChannelLayoutTag = kAudioChannelLayoutTag_Stereo,
        .mChannelBitmap = kAudioChannelBit_Left,
        .mNumberChannelDescriptions = 0,
    };
    NSData *channelLayoutAsData = [NSData dataWithBytes:&stereoChannelLayout length:offsetof(AudioChannelLayout, mChannelDescriptions)];
    NSDictionary *audioCompressSettings = @{
                                            AVFormatIDKey : @(kAudioFormatMPEG4AAC),
                                            AVEncoderBitRateKey : @(128000),
                                            AVSampleRateKey : @(44100),
                                            AVNumberOfChannelsKey : @(2),
                                            AVChannelLayoutKey : channelLayoutAsData
                                            };
    return audioCompressSettings;
}


/** 音频解码设置 */
+ (NSDictionary *)configAudioOutput
{
    NSDictionary *audioOutputSetting = @{
                                         AVFormatIDKey: @(kAudioFormatLinearPCM)
                                         };
    return audioOutputSetting;
}



/** 视频解码设置 */
+ (NSDictionary *)configVideoOutput
{
    NSDictionary *videoOutputSetting = @{
                                         (__bridge NSString *)kCVPixelBufferPixelFormatTypeKey:[NSNumber numberWithUnsignedInt:kCVPixelFormatType_422YpCbCr8],
                                         (__bridge NSString *)kCVPixelBufferIOSurfacePropertiesKey:[NSDictionary dictionary]
                                         };
    
    return videoOutputSetting;
}


@end
