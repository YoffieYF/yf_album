//
//  WFMacro.h
//  album
//
//  Created by Yoffie on 2020/9/10.
//

#import <Foundation/Foundation.h>
#import "Masonry.h"
#import "UIView+WFLayout.h"
#import "UIColor+WFHex.h"


/**
 * 弱引用定义
 */
#define WF_BlockWeakSelf(weakSelf, self)   __weak typeof(self) weakSelf = self

/**
 * 强引用定义
 */
#define WF_BlockStrongSelf(strongSelf, weakSelf)   __strong typeof(weakSelf) strongSelf = weakSelf

/**
*  颜色
*/
#define WF_RGB(r,g,b)                   [UIColor colorWithRed:r / 255.f green:g / 255.f blue:b / 255.f alpha:1.f]
#define WF_RGB_A(r,g,b,a)               [UIColor colorWithRed:r / 255.f green:g / 255.f blue:b / 255.f alpha:a]
#define WF_0X_Color_hex(hex)            RGBA((float)((hex & 0xFF0000) >> 16),(float)((hex & 0xFF00) >> 8),(float)(hex & 0xFF),1.f)
#define WF_0X_Color_hexA(hex,a)         RGBA((float)((hex & 0xFF0000) >> 16),(float)((hex & 0xFF00) >> 8),(float)(hex & 0xFF),a)
#define WF_STR_AHEX(COLOR)              [UIColor colorWithHexString:COLOR]
#define WF_STR_HEX_A(COLOR,A)           [[UIColor colorWithHexString:COLOR] colorWithAlphaComponent:A]


// System Version
#define WF_SYSTEM_VERSION                             ([[[UIDevice currentDevice] systemVersion] floatValue])
#define WF_SYSTEM_VERSION_EQUAL_TO(v)                 ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] == NSOrderedSame)
#define WF_SYSTEM_VERSION_HIGHER_THAN(v)              ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] == NSOrderedDescending)
#define WF_SYSTEM_VERSION_EQUAL_TO_OR_HIGHER_THAN(v)  ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] != NSOrderedAscending)
#define WF_SYSTEM_VERSION_LOWER_THAN(v)               ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] == NSOrderedAscending)
#define WF_SYSTEM_VERSION_EQUAL_TO_OR_LOWER_THAN(v)   ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] != NSOrderedDescending)


///=============================================================================
/// @name 屏幕坐标、尺寸相关
///=============================================================================
//判断是否iPhone X
#define WF_IS_iPhoneX              ([UIScreen instancesRespondToSelector:@selector(currentMode)] ? CGSizeEqualToSize(CGSizeMake(1125, 2436), [[UIScreen mainScreen] currentMode].size) : NO)
// Screen
#define WF_SCREEN_SIZE                 [[UIScreen mainScreen] bounds].size
#define WF_SCREEN_WIDTH                [[UIScreen mainScreen] bounds].size.width
#define WF_SCREEN_HEIGHT               [[UIScreen mainScreen] bounds].size.height
// 状态栏高度
#define WF_StatusBarHeight        (IS_iPhoneX ? 44.f : 20.f)
// 顶部导航栏高度
#define WF_NavigationBarHeight    44.f
// 顶部安全距离
#define WF_SafeAreaTopHeight      (IS_iPhoneX ? 88.f : 64.f)
// 底部安全距离
#define WF_SafeAreaBottomHeight   (IS_iPhoneX ? 34.f : 0.f)
// Tabbar高度
#define WF_TabbarHeight           49.f
// 去除上下导航栏剩余中间视图高度
#define WF_ContentHeight           (kScreenHeight - kSafeAreaTopHeight - kSafeAreaBottomHeight - kTabbarHeight)
///=============================================================================


///=============================================================================
/// @name NSLog相关
///=============================================================================
#ifdef DEBUG
#define WF_Log(...) NSLog(@"%s 第%d行 \n %@\n\n",__func__,__LINE__,[NSString stringWithFormat:__VA_ARGS__])
#else
#define NSLog(...)
#endif
///=============================================================================

NS_ASSUME_NONNULL_BEGIN

@interface WFMacro : NSObject
+ (UIImage *) getBundleImageWithName:(NSString*)name;
@end

NS_ASSUME_NONNULL_END
