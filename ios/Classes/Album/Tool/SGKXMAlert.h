

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface SGKXMAlert : NSObject

/*
 *  展示提示(仅限取消和确定)，取消或者确定后能进行操作
 */
+(UIAlertController *)alertControllerWithTitle:(NSString *) title message:(NSString *) message cancelHandler:(void(^)(UIAlertAction * cancelAction))cancel confirmHandler:(void(^)(UIAlertAction * confirmAction))confirm;
/**
 *  展示提示
 *
 *  @param title            标题
 *  @param message          内容
 *  @param leftActionTitle  左边按钮标题
 *  @param rightActionTitle 右边按钮标题
 *  @param cancel           左边按钮事件
 *  @param confirm          右边按钮事件
 *
 */
+(UIAlertController *)alertControllerWithTitle:(NSString *) title message:(NSString *) message leftActionTitle:(NSString *)leftActionTitle rightActionTitle:(NSString *)rightActionTitle cancelHandler:(void(^)(UIAlertAction * cancelAction))cancel confirmHandler:(void(^)(UIAlertAction * confirmAction))confirm;

@end

NS_ASSUME_NONNULL_END
