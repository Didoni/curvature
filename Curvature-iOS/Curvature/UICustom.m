//
//  UICustom.m
//  FriendsAndFamily
//
//  Created by Shailesh Prakash on 16/04/2014.
//  Copyright (c) 2014 MyOxygen. All rights reserved.
//

#import "UICustom.h"

#define k_meter_interval 0.1f
#define UIColorFromRGB(rgbValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.f green:((float)((rgbValue & 0xFF00) >> 8))/255.f blue:((float)(rgbValue & 0xFF))/255.f alpha:1.f]
#define UIColorFromARGB(argbValue) [UIColor colorWithRed:((float)((argbValue & 0xFF0000) >> 16))/255.f green:((float)((argbValue & 0xFF00) >> 8))/255.f blue:((float)(argbValue & 0xFF))/255.f alpha:((float)((argbValue & 0xFF000000) >> 24))/255.f]

@implementation UIView (shake)
- (void)shakeX {
    [self shakeXWithOffset:40.0 breakFactor:0.85 duration:1.5 maxShakes:5];
}
- (void)shakeY{
    CAKeyframeAnimation *animation = [CAKeyframeAnimation animationWithKeyPath:@"position"];
    [animation setDuration:2.f];
    NSMutableArray *keys = [NSMutableArray arrayWithCapacity:20];
    int infinitySec = 5;
    CGFloat aOffset = 31.f;
    CGFloat aBreakFactor = 0.80;
    while(aOffset > 0.01) {
        [keys addObject:[NSValue valueWithCGPoint:CGPointMake(self.center.x, self.center.y - aOffset)]];
        [keys addObject:[NSValue valueWithCGPoint:CGPointMake(self.center.x, self.center.y + aOffset)]];
        aBreakFactor = aBreakFactor*aBreakFactor;
        aOffset *= aBreakFactor;
        infinitySec--;
        if(infinitySec <= 0.01) {
            break;
        }
    }
    animation.values = keys;
    [self.layer addAnimation:animation forKey:@"position"];
}
- (void)shakeX:(NSUInteger)impulseLevel {
    impulseLevel = MIN(impulseLevel,5);
    CAKeyframeAnimation *animation = [CAKeyframeAnimation animationWithKeyPath:@"position"];
    [animation setDuration:k_meter_interval];
    NSMutableArray *keys = [NSMutableArray arrayWithCapacity:20];
    CGFloat aOffset = impulseLevel*1.25;
    [keys addObject:[NSValue valueWithCGPoint:CGPointMake(self.center.x - aOffset, self.center.y)]];
    aOffset *= 0.85;
    [keys addObject:[NSValue valueWithCGPoint:CGPointMake(self.center.x + aOffset, self.center.y)]];
    animation.values = keys;
    [self.layer addAnimation:animation forKey:@"position"];
}
- (void)shakeXWithOffset:(CGFloat)aOffset breakFactor:(CGFloat)aBreakFactor duration:(CGFloat)aDuration maxShakes:(UInt8)maxShakes
{
    CAKeyframeAnimation *animation = [CAKeyframeAnimation animationWithKeyPath:@"position"];
    [animation setDuration:aDuration];
    NSMutableArray *keys = [NSMutableArray arrayWithCapacity:20];
    int infinitySec = maxShakes;
    while(aOffset > 0.01) {
        [keys addObject:[NSValue valueWithCGPoint:CGPointMake(self.center.x - aOffset, self.center.y)]];
        aOffset *= aBreakFactor;
        [keys addObject:[NSValue valueWithCGPoint:CGPointMake(self.center.x + aOffset, self.center.y)]];
        aOffset *= aBreakFactor;
        infinitySec--;
        if(infinitySec <= 0) {
            break;
        }
    }
    animation.values = keys;
    [self.layer addAnimation:animation forKey:@"position"];
}
@end
@implementation UICustom
static UIColor *lightOrange, *darkOrange, *alphaGreen, *blue, *green,*lightGray,*textBlack,*textGray,*textLightGray,*white;
static UIFont *regular, *small, *medium, *large, *btnTxt, *boldBtnTxt, *header, *scaledBtn, *headerSmall;

+(void)initialize
{
    
    lightOrange = UIColorFromRGB(0xfdb90f);
    darkOrange = UIColorFromRGB(0xe67c27);
    alphaGreen = UIColorFromARGB(0x0863b32e);
    blue = UIColorFromRGB(0x0071bb);
    green = UIColorFromRGB(0x63b32e);
    lightGray = UIColorFromRGB(0xefefef);
    textBlack = UIColorFromRGB(0x0);
    textGray = UIColorFromRGB(0x1d1d1b);
    textLightGray = UIColorFromRGB(0xb5b5b5);
    white = UIColorFromRGB(0xffffff);
    
    regular = [UIFont fontWithName:REGULAR_FONT size:16.f];
    small = [UIFont fontWithName:REGULAR_FONT size:14.f];
    medium = [UIFont fontWithName:REGULAR_FONT size:18.f];
    large = [UIFont fontWithName:REGULAR_FONT size:23.f];
    btnTxt = [UIFont fontWithName:SEMI_BOLD_FONT size:16.f];
    boldBtnTxt = [UIFont fontWithName:BOLD_FONT size:14.f];
    scaledBtn = [UIFont fontWithName:BOLD_FONT size:23.f];
    header = [UIFont fontWithName:BOLD_FONT size:16.f];
    headerSmall= [UIFont fontWithName:SEMI_BOLD_FONT size:18.f];
}

+(UIFont*)appFont:(AppFont)appFont
{
    UIFont *font;
    switch (appFont) {
        case AppFontButtonText:
            font = btnTxt;
            break;
        case AppFontHeaderText:
            font = header;
            break;
        case AppFontLargeText:
            font = large;
            break;
        case AppFontMediunText:
            font = medium;
            break;
        case AppFontSmallText:
            font = small;
            break;
        case AppFontRegularText:
            font = regular;
            break;
        case AppFontBoldButtonText:
            font = boldBtnTxt;
            break;
        case AppFontHeaderSmallText:
            font = headerSmall;
            break;
        case AppFontScaledBoldButtonText:
            font = scaledBtn;
            break;
    }
    return font;
}

+(UInt32)hexColor:(AppColor)color
{
    UInt32 ucolor;
    switch (color) {
        case AppColorBlue:
            ucolor = 0x0071bb;
            break;
        case AppColorGreen:
            ucolor = 0x53a31e;
            break;
        case AppColorLightGray:
            ucolor = 0xefefef;
            break;
        case AppColorTextBlack:
            ucolor = 0x0;
            break;
        case AppColorTextGray:
            ucolor = 0x1d1d1b;
            break;
        case AppColorTextLightGray:
            ucolor = 0xb5b5b5;
            break;
        case AppColorWhite:
            ucolor = 0xffffff;
            break;
        case AppColorLiteOrange:
            ucolor = 0xfdb90f;
            break;
        case AppColorDarkOrange:
            ucolor = 0xe67c27;
            break;
    }
    return ucolor;
}

+(UIColor*)appColor:(AppColor)color
{
    UIColor *ucolor;
    switch (color) {
        case AppColorBlue:
            ucolor = blue;
            break;
        case AppColorGreen:
            ucolor = green;
            break;
        case AppColorLightGray:
            ucolor = lightGray;
            break;
        case AppColorTextBlack:
            ucolor = textBlack;
            break;
        case AppColorTextGray:
            ucolor = textGray;
            break;
        case AppColorTextLightGray:
            ucolor = textLightGray;
            break;
        case AppColorWhite:
            ucolor = white;
            break;
        case AppColorLiteOrange:
            ucolor = lightOrange;
            break;
        case AppColorDarkOrange:
            ucolor = darkOrange;
    }
    return ucolor;
}
+ (UIImage *) capture:(UIView *)view
{
    UIGraphicsBeginImageContextWithOptions(view.bounds.size, NO, [[UIScreen mainScreen] scale]);
    [view.layer renderInContext:UIGraphicsGetCurrentContext()];
    UIImage * img = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return img;
}
@end
@implementation UICustomLabel
-(void)awakeFromNib
{
    [super awakeFromNib];
    self.font = [UIFont fontWithName:![self.font.fontName hasSuffix:@"Bold"]?(![self.font.fontName hasSuffix:@"Light"]?REGULAR_FONT:LIGHT_FONT):BOLD_FONT size:self.font.pointSize];
}
@end
@implementation UIMapLabel
-(void)awakeFromNib
{
    [super awakeFromNib];
    self.font = [UIFont fontWithName:SEMI_BOLD_FONT size:self.font.pointSize];
}
@end
@implementation UICustomTextView
-(void)awakeFromNib
{
    [super awakeFromNib];
    //self.textContainerInset = UIEdgeInsetsMake(5.0f, 5.0f, 5.0f, 5.0f);
    self.font = [UIFont fontWithName:![self.font.fontName hasSuffix:@"Bold"]?REGULAR_FONT:BOLD_FONT size:self.font.pointSize];
}
@end

@implementation UICustomButton
-(void)awakeFromNib
{
    [super awakeFromNib];
    NSString *fontName = self.titleLabel.font.fontName;
    self.titleLabel.font = [UIFont fontWithName:![fontName hasSuffix:@"Bold"]?(![fontName hasSuffix:@"Light"]?REGULAR_FONT:LIGHT_FONT):BOLD_FONT size:self.titleLabel.font.pointSize];
}
@end
@implementation  UIRoundBorderButton
-(void)awakeFromNib
{
    [super awakeFromNib];
    self.layer.borderColor = [self titleColorForState:UIControlStateNormal].CGColor;
    self.layer.borderWidth = 1.f;
    self.layer.cornerRadius = self.tag > 0 ? CGRectGetWidth(self.frame)/2 : 5.f;
}
@end
@implementation GradientBackgroundView{
    CGGradientRef _gradientRef;
}
- (void)drawRect:(CGRect)rect
{
    CGContextRef context = UIGraphicsGetCurrentContext();
    size_t locationsCount = 2;
    CGFloat locations[2] = {0.0f, 1.0f};//
    CGFloat colors[8] = {0.792,0.525,0.039,1.f,.7255f,.7255f,.7255f,1.f};    //
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    CGGradientRef gradient = CGGradientCreateWithColorComponents(colorSpace, colors, locations, locationsCount);
    CGColorSpaceRelease(colorSpace);
    
    CGPoint center = CGPointMake(self.bounds.size.width/2, (self.bounds.size.height)/2 - 76.f);//Set offset to center of radial - 76.f);
    float radius = MIN(self.bounds.size.width , self.bounds.size.height)*5;
    CGContextDrawRadialGradient (context, gradient, center, 0, center, radius, kCGGradientDrawsAfterEndLocation);
    CGGradientRelease(gradient);
}
@end
@implementation UICustomCellBackground

- (void)drawRect:(CGRect)rect
{
    [super drawRect:rect];
    // get the contect
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextClearRect(context, rect);
    CGContextSetStrokeColorWithColor(context, [UIColor whiteColor].CGColor);
    //now draw the rounded rectangle
    CGContextSetRGBFillColor(context, 0.0, 0.0, 0.0, 0.1);
    
    //since I need room in my rect for the shadow, make the rounded rectangle a little smaller than frame
    CGRect rrect = CGRectMake(10.f, 8.f, CGRectGetWidth(rect)-20.f, CGRectGetHeight(rect)- 11.5f);
    CGFloat radius = 5.f;
    // the rest is pretty much copied from Apples example
    CGFloat minx = CGRectGetMinX(rrect), midx = CGRectGetMidX(rrect), maxx = CGRectGetMaxX(rrect);
    CGFloat miny = CGRectGetMinY(rrect), midy = CGRectGetMidY(rrect), maxy = CGRectGetMaxY(rrect);
    {
        // set the shadow
        CGContextSetShadow(context, CGSizeMake(0.0, 2.0), 2.0);
        // Start at 1
        CGContextMoveToPoint(context, minx, midy);
        // Add an arc through 2 to 3
        CGContextAddArcToPoint(context, minx, miny, midx, miny, radius);
        // Add an arc through 4 to 5
        CGContextAddArcToPoint(context, maxx, miny, maxx, midy, radius);
        // Add an arc through 6 to 7
        CGContextAddArcToPoint(context, maxx, maxy, midx, maxy, radius);
        // Add an arc through 8 to 9
        CGContextAddArcToPoint(context, minx, maxy, minx, midy, radius);
        // Close the path
        CGContextClosePath(context);
        CGContextSetRGBFillColor(context, 1.f, 1.f, 1.f, 1.f);
        // Fill & stroke the path
        CGContextDrawPath(context, kCGPathFill);
    }
}
@end
@implementation UIMapShadow:UIView
- (void) drawRect:(CGRect)rect
{
    CGContextRef context = UIGraphicsGetCurrentContext();
    size_t locationsCount = 2;
    CGFloat locations[2] = {0.0f, 1.0f};
    CGFloat colors[8] = {.9255f,.9255f,.9255f,1.f,1.f,1.f,1.f,1.f};    //
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    CGGradientRef gradient = CGGradientCreateWithColorComponents(colorSpace, colors, locations, locationsCount);
    CGColorSpaceRelease(colorSpace);
    CGPoint center = CGPointMake(self.bounds.size.width/2, (self.bounds.size.height)/2);//Set offset to center of radial - 76.f);
    float radius = MIN(self.bounds.size.width , self.bounds.size.height);
    CGContextDrawRadialGradient (context, gradient, center, 0, center, radius, kCGGradientDrawsAfterEndLocation);
    CGGradientRelease(gradient);
}
@end