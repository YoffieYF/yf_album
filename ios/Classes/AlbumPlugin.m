#import "AlbumPlugin.h"
#import "WFAlbumViewController.h"
#import "WFAlbumEvent.h"
#import "WFTakePhotoAndVideoManager.h"

typedef NS_ENUM(NSUInteger, PluginMethod) {
    ///单选自带压缩效果，截剪不支持gif
    PluginMethodOpenAlbum           = 0,
    /// addCrop: true 拍照加裁剪, 直接返回裁剪后的路径
    /// addCrop: false 单纯拍照，返回 AssetEntity
    PluginMethodTakePhoto           = 1,
    // 获取相册最近50张图片
    PluginMethodGetLatestMediaFile  = 2,
    // 图片压缩
    PluginMethodMediaCompress       = 3,
    // 图片预览
    PluginMethodImagesPreview       = 4,
    // 加载历史
    PluginMethodAddHistoryFiles     = 5,
};

@interface AlbumPlugin()
@property (strong, nonatomic) NSDictionary *methodMap;
@property (strong, nonatomic) WFTakePhotoAndVideoManager * takePhotoManager;
@property (strong, nonatomic) WFAlbumViewController *albumViewController;
@end

@implementation AlbumPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    FlutterMethodChannel* channel = [FlutterMethodChannel
                                     methodChannelWithName:@"flutter.plugin/customAlbum"
                                     binaryMessenger:[registrar messenger]];
    AlbumPlugin* instance = [self initAlbumPlugin];
    [registrar addMethodCallDelegate:instance channel:channel];
}

+ (AlbumPlugin *)initAlbumPlugin {
    AlbumPlugin* instance = [[AlbumPlugin alloc] init];
    instance.methodMap = @{
        @"openAlbum":@(PluginMethodOpenAlbum),
        @"takePhoto":@(PluginMethodTakePhoto),
        
        @"getLatestMediaFile":@(PluginMethodGetLatestMediaFile),
        @"mediaCompress":@(PluginMethodMediaCompress),
        @"imagesPreview":@(PluginMethodImagesPreview),
        @"addHistoryFiles":@(PluginMethodAddHistoryFiles),
    };
    return instance;
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    NSNumber *methodType = self.methodMap[call.method];
    if (methodType) {
        switch (methodType.intValue) {
            case PluginMethodOpenAlbum:
                [self openAlbum:result];
                break;
            case PluginMethodTakePhoto:
                [self takePhoto:result];
                break;
            case PluginMethodGetLatestMediaFile:
                [self getLatestMediaFile:result];
                break;
            case PluginMethodMediaCompress:
                [self mediaCompress:result];
                break;
            case PluginMethodImagesPreview:
                [self imagesPreview:result];
                break;
            case PluginMethodAddHistoryFiles:
                [self addHistoryFiles:result];
                break;
            default:
                NSAssert(NO, @"The method requires an implementation ！");
                break;
        }
    } else {
        result(FlutterMethodNotImplemented);
    }
}

-(void)openAlbum:(FlutterResult)result {
    [QTSub(self, WFAlbumSendImageEvent) next:^(WFAlbumSendImageEvent *event) {
        
    }];
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    WFAlbumViewController *vc = [[WFAlbumViewController alloc]init];
    vc.modalPresentationStyle = UIModalPresentationFullScreen;
    UINavigationController *nv = [[UINavigationController alloc]init];
    nv.modalPresentationStyle = UIModalPresentationFullScreen;
    nv.navigationBarHidden = YES;
    [nv setViewControllers:@[vc] animated:NO];
//    [window.rootViewController.navigationController pushViewController:vc animated:YES];
    [window.rootViewController presentViewController:nv animated:YES completion:nil];
}

-(void)takePhoto:(FlutterResult)result {
    self.takePhotoManager = [[WFTakePhotoAndVideoManager alloc] initWithSystemSelectType:systemSelectTypePhoto];
    [self.takePhotoManager takePhoto];
    self.takePhotoManager.sendCompressPhotosBlock = ^(NSArray *modelArray) {

    };
}

-(void)getLatestMediaFile:(FlutterResult)result {
    
}

-(void)mediaCompress:(FlutterResult)result {
}

-(void)imagesPreview:(FlutterResult)result {
}

-(void)addHistoryFiles:(FlutterResult)result {
    //[weakSelf.albumTool reloadBrowserWithHistorySources:medias paramSeqId:seqId];
}


- (WFAlbumViewController *)albumViewController {
    if (!_albumViewController) {
        _albumViewController = [[WFAlbumViewController alloc]init];
    }
    return _albumViewController;
}

@end
