//
//  VRPN.h
//  VRPNClient
//
//  Created by Daniel Wilches on 9/24/10.
//  Copyright 2010. All rights reserved.
//
//  You can modify this file as long as you let the information from where it was downloaded:
//     - http://wwwest.uniandes.edu.co/~d.wilches52/
//     - http://dwilches.blogspot.com/
//

#import <Foundation/Foundation.h>
static float radius;
static float rx;
static float ry;

@interface VRPN : NSObject
{
    dispatch_source_t dispatchSource;
}

// Default initiaizer
- (id)initWithHost:(NSString *)host andRefreshRate:(NSUInteger)refreshRateInMillisec;

// Starts polling for events
- (void)startListening;

// Stops polling for events
- (void)stopListening;

@end
