//
//  UICustom.h
//  FriendsAndFamily
//
//  Created by Shailesh Prakash on 16/04/2014.
//  Copyright (c) 2014 MyOxygen. All rights reserved.
//

#import <UIKit/UIKit.h>
#define BOLD_FONT @"OpenSans-Bold"
#define REGULAR_FONT @"OpenSans"
#define LIGHT_FONT @"OpenSans-Light"
#define SEMI_BOLD_FONT @"OpenSans-Semibold"

typedef NS_ENUM (int8_t, AppColor) {
    AppColorGreen,
    AppColorBlue,
    AppColorLiteOrange,
    AppColorDarkOrange,
    AppColorLightGray,
    AppColorWhite,
    AppColorTextBlack,
    AppColorTextGray,
    AppColorTextLightGray
};


typedef NS_ENUM (int8_t, AppFont) {
    AppFontRegularText,
    AppFontMediunText,
    AppFontLargeText,
    AppFontSmallText,
    AppFontHeaderSmallText,
    AppFontHeaderText,
    AppFontButtonText,
    AppFontBoldButtonText,
    AppFontScaledBoldButtonText
};
@interface UIView (shake)
- (void)shakeX;
@end
@interface UICustom : NSObject
+ (UIColor *)appColor:(AppColor)color;
+ (UIFont *)appFont:(AppFont)appFont;
+ (UInt32)hexColor:(AppColor)color;
+ (UIImage *)capture:(UIView *)view;
@end
@interface GradientBackgroundView : UIView
@property NSString *text;
@end
@interface UICustomCellBackground : UIView
@end
@interface UIMapShadow : UIView
@property (nonatomic, strong) UIBezierPath *path;
@end
@interface UICustomLabel : UILabel
@end
@interface UIMapLabel : UILabel
@end
@interface UICustomTextView : UITextView
@end
@interface UICustomButton : UIButton
@end
@interface UIRoundBorderButton : UICustomButton
@end
