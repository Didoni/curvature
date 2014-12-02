//
//  AppDelegate.m
//  Curvature
//
//  Created by Shailesh Prakash on 13/11/2014.
//  Copyright (c) 2014 MyOxygen. All rights reserved.
//

#import "AppDelegate.h"
#import <AVFoundation/AVAudioSession.h>
#include <CoreGraphics/CGGeometry.h>
#include <dispatch/dispatch.h>
#import "DetailViewController.h"
#import "UserViewController.h"


@interface AppDelegate ()

@end

@implementation AppDelegate
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    [self checkDocuments];
    UINavigationController *navigationController = (UINavigationController *)self.window.rootViewController;
    UserViewController *controller = (UserViewController *)navigationController.topViewController;
    controller.managedObjectContext = self.managedObjectContext;
    //[navigationController.navigationBar setBackgroundImage:[UIImage imageNamed:@"orangeStrip"]                                                   forBarMetrics:UIBarMetricsDefault];
    navigationController.navigationController.navigationBar.translucent = YES;
    [UIApplication sharedApplication].idleTimerDisabled = YES;
    return YES;
}

- (void)checkDocuments {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *path = [documentsDirectory stringByAppendingPathComponent:@"experiments.csv"];
    NSFileManager *fileManager = [NSFileManager defaultManager];
    [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayback error:nil];
    if (![fileManager fileExistsAtPath:path]) {
        [fileManager copyItemAtPath:[[NSBundle mainBundle] pathForResource:@"sound1" ofType:@"caf"] toPath:[documentsDirectory stringByAppendingPathComponent:@"sound1.caf"] error:NULL];
        [fileManager copyItemAtPath:[[NSBundle mainBundle] pathForResource:@"sound2" ofType:@"caf"] toPath:[documentsDirectory stringByAppendingPathComponent:@"sound2.caf"] error:NULL];
        [fileManager copyItemAtPath:[[NSBundle mainBundle] pathForResource:@"track1" ofType:@"caf"] toPath:[documentsDirectory stringByAppendingPathComponent:@"track1.caf"] error:NULL];
        [fileManager copyItemAtPath:[[NSBundle mainBundle] pathForResource:@"track2" ofType:@"caf"] toPath:[documentsDirectory stringByAppendingPathComponent:@"track2.caf"] error:NULL];
        [fileManager copyItemAtPath:[[NSBundle mainBundle] pathForResource:@"track3" ofType:@"caf"] toPath:[documentsDirectory stringByAppendingPathComponent:@"track3.caf"] error:NULL];
        [fileManager copyItemAtPath:[[NSBundle mainBundle] pathForResource:@"drum" ofType:@"caf"] toPath:[documentsDirectory stringByAppendingPathComponent:@"drum.caf"] error:NULL];
        [fileManager copyItemAtPath:[[NSBundle mainBundle] pathForResource:@"ding" ofType:@"mp3"] toPath:[documentsDirectory stringByAppendingPathComponent:@"ding.mp3"] error:NULL];
        [fileManager copyItemAtPath:[[NSBundle mainBundle] pathForResource:@"duck" ofType:@"caf"] toPath:[documentsDirectory stringByAppendingPathComponent:@"duck.caf"] error:NULL];
        [fileManager copyItemAtPath:[[NSBundle mainBundle] pathForResource:@"tong" ofType:@"mp3"] toPath:[documentsDirectory stringByAppendingPathComponent:@"tong.mp3"] error:NULL];
        [fileManager copyItemAtPath:[[NSBundle mainBundle] pathForResource:@"White Noise" ofType:@"mp3"] toPath:[documentsDirectory stringByAppendingPathComponent:@"White Noise.mp3"] error:NULL];
        [fileManager copyItemAtPath:[[NSBundle mainBundle] pathForResource:@"experiments" ofType:@"csv"] toPath:[documentsDirectory stringByAppendingPathComponent:@"experiments.csv"] error:NULL];
    }
    
    NSString *version = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
    BOOL set = [[NSUserDefaults standardUserDefaults] boolForKey:version];
    if (!set) {
        [[NSUserDefaults standardUserDefaults] setBool:YES forKey:version];
        [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"reset"];
        [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"trial_type"];
        [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"sound"];
        [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"trial_user"];
        [[NSUserDefaults standardUserDefaults] setInteger:20 forKey:@"trial_time"];
        [[NSUserDefaults standardUserDefaults] setInteger:100 forKey:@"vrpn_refresh"];
        [[NSUserDefaults standardUserDefaults] setBool:NO forKey:@"vrpn"];
        [[NSUserDefaults standardUserDefaults] synchronize];
    }
}

- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    // Saves changes in the application's managed object context before the application terminates.
    [self saveContext];
}

#pragma mark - Core Data stack

@synthesize managedObjectContext = _managedObjectContext;
@synthesize managedObjectModel = _managedObjectModel;
@synthesize persistentStoreCoordinator = _persistentStoreCoordinator;

- (NSURL *)applicationDocumentsDirectory {
    // The directory the application uses to store the Core Data store file. This code uses a directory named "ac.didoni.Curvature" in the application's documents directory.
    return [[[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory inDomains:NSUserDomainMask] lastObject];
}

- (NSManagedObjectModel *)managedObjectModel {
    // The managed object model for the application. It is a fatal error for the application not to be able to find and load its model.
    if (_managedObjectModel != nil) {
        return _managedObjectModel;
    }
    NSURL *modelURL = [[NSBundle mainBundle] URLForResource:@"Curvature" withExtension:@"momd"];
    _managedObjectModel = [[NSManagedObjectModel alloc] initWithContentsOfURL:modelURL];
    return _managedObjectModel;
}

- (NSPersistentStoreCoordinator *)persistentStoreCoordinator {
    // The persistent store coordinator for the application. This implementation creates and return a coordinator, having added the store for the application to it.
    if (_persistentStoreCoordinator != nil) {
        return _persistentStoreCoordinator;
    }
    
    // Create the coordinator and store
    
    _persistentStoreCoordinator = [[NSPersistentStoreCoordinator alloc] initWithManagedObjectModel:[self managedObjectModel]];
    NSURL *storeURL = [[self applicationDocumentsDirectory] URLByAppendingPathComponent:@"Curvature.sqlite"];
    NSError *error = nil;
    NSString *failureReason = @"There was an error creating or loading the application's saved data.";
    if (![_persistentStoreCoordinator addPersistentStoreWithType:NSSQLiteStoreType configuration:nil URL:storeURL options:nil error:&error]) {
        // Report any error we got.
        NSMutableDictionary *dict = [NSMutableDictionary dictionary];
        dict[NSLocalizedDescriptionKey] = @"Failed to initialize the application's saved data";
        dict[NSLocalizedFailureReasonErrorKey] = failureReason;
        dict[NSUnderlyingErrorKey] = error;
        error = [NSError errorWithDomain:@"YOUR_ERROR_DOMAIN" code:9999 userInfo:dict];
        // Replace this with code to handle the error appropriately.
        // abort() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
        abort();
    }
    
    return _persistentStoreCoordinator;
}

- (NSManagedObjectContext *)managedObjectContext {
    // Returns the managed object context for the application (which is already bound to the persistent store coordinator for the application.)
    if (_managedObjectContext != nil) {
        return _managedObjectContext;
    }
    
    NSPersistentStoreCoordinator *coordinator = [self persistentStoreCoordinator];
    if (!coordinator) {
        return nil;
    }
    _managedObjectContext = [[NSManagedObjectContext alloc] init];
    [_managedObjectContext setPersistentStoreCoordinator:coordinator];
    return _managedObjectContext;
}

#pragma mark - Core Data Saving support

- (void)saveContext {
    NSManagedObjectContext *managedObjectContext = self.managedObjectContext;
    if (managedObjectContext != nil) {
        NSError *error = nil;
        if ([managedObjectContext hasChanges] && ![managedObjectContext save:&error]) {
            // Replace this implementation with code to handle the error appropriately.
            // abort() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
            NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
            abort();
        }
    }
}

@end
