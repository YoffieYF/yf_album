//
//  WFMacro.m
//  album
//
//  Created by Yoffie on 2020/9/10.
//

#import "WFMacro.h"

@implementation WFMacro
+ (UIImage *) getBundleImageWithName:(NSString*)name {
    NSURL *bundleURL = [[NSBundle mainBundle] URLForResource:@"WFAlbum" withExtension:@"bundle"];
    NSBundle *bundle = [NSBundle bundleWithURL:bundleURL];
    NSInteger scale = [[UIScreen mainScreen] scale];
    NSString *imgName = [NSString stringWithFormat:@"%@@%zdx.png", name,scale];
    NSString *path = [bundle pathForResource:imgName ofType:nil];
    return [UIImage imageWithContentsOfFile:path];
}
@end
