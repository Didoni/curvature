//
//  Trial.h
//  Curvature
//
//  Created by Shailesh Prakash on 23/11/2014.
//  Copyright (c) 2014 MyOxygen. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class User;

@interface Trial : NSManagedObject

@property (nonatomic) int16_t left;
@property (nonatomic) NSTimeInterval timeStamp;
@property (nonatomic) int16_t trial;
@property (nonatomic) int16_t right;
@property (nonatomic) int16_t trialType;
@property (nonatomic) float leftTime;
@property (nonatomic) float rightTime;
@property (nonatomic) BOOL selectedLeft;
@property (nonatomic, retain) User *user;

@end
