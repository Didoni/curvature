//
//  AppDelegate.h
//  Curvature
//
//  Created by Shailesh Prakash on 13/11/2014.
//  Copyright (c) 2014 MyOxygen. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreData/CoreData.h>
#include "VRPN.h"
@interface AppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) UIWindow *window;

@property (readonly, strong, nonatomic) NSManagedObjectContext *managedObjectContext;
@property (readonly, strong, nonatomic) NSManagedObjectModel *managedObjectModel;
@property (readonly, strong, nonatomic) NSPersistentStoreCoordinator *persistentStoreCoordinator;
@property (readonly, strong, nonatomic) VRPN *vrpn_client;
- (void)saveContext;
- (NSURL *)applicationDocumentsDirectory;


@end
