//
//  SGKXMALert.m

#import "SGKXMAlert.h"

@implementation SGKXMAlert

+(UIAlertController *)alertControllerWithTitle:(NSString *) title message:(NSString *) message cancelHandler:(void(^)(UIAlertAction * cancelAction))cancel confirmHandler:(void(^)(UIAlertAction * confirmAction))confirm{
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:title message:message preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction *action01 = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction *action) {
        cancel(action);
    }];
    
    UIAlertAction *action02 = [UIAlertAction actionWithTitle:@"确认"style:UIAlertActionStyleDestructive handler:^(UIAlertAction *action) {
        confirm(action);
    }];
    
    [alertController addAction:action01];
    [alertController addAction:action02];
    return alertController;
}

+(UIAlertController *)alertControllerWithTitle:(NSString *) title message:(NSString *) message leftActionTitle:(NSString *)leftActionTitle rightActionTitle:(NSString *)rightActionTitle cancelHandler:(void(^)(UIAlertAction * cancelAction))cancel confirmHandler:(void(^)(UIAlertAction * confirmAction))confirm{
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:title message:message preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction *action01 = [UIAlertAction actionWithTitle:leftActionTitle style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
        cancel(action);
    }];
    
    UIAlertAction *action02 = [UIAlertAction actionWithTitle:rightActionTitle style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
        confirm(action);
    }];
    
    [alertController addAction:action01];
    [alertController addAction:action02];
    return alertController;
}



@end
