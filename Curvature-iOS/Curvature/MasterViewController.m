//
//  MasterViewController.m
//  Curvature
//
//  Created by Shailesh Prakash on 13/11/2014.
//  Copyright (c) 2014 MyOxygen. All rights reserved.
//

#import "MasterViewController.h"
#import "DetailViewController.h"
#import "User.h"
#import "Trial.h"
#import "AppDelegate.h"
#import "UICustom.h"

@implementation MasterViewController {
    NSString *filePath;
}

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    AppDelegate *delegate = (AppDelegate *)[UIApplication sharedApplication].delegate;
    self.managedObjectContext = delegate.managedObjectContext;
    
    // Do any additional setup after loading the view, typically from a nib.
    [self.tableView registerClass:[UITableViewCell class] forCellReuseIdentifier:@"LogCell"];
    [self.fetchedResultsController performFetch:nil];
    NSString *fileName = @"Trial log.csv";
    //Check if cache folder exists
    NSArray *cachePathArray = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
    NSString *folderPath = [[cachePathArray lastObject] stringByAppendingPathComponent:@"/csvCache"];
    if (![[NSFileManager defaultManager] fileExistsAtPath:folderPath]) {
        [[NSFileManager defaultManager] createDirectoryAtPath:folderPath withIntermediateDirectories:NO attributes:nil error:nil];
    }
    filePath = [NSString stringWithFormat:@"%@/%@", folderPath, fileName];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)saveAsFile {
    NSMutableString *writeString = [NSMutableString stringWithCapacity:0]; //don't worry about the capacity, it will expand as necessary
    [writeString appendFormat:@"User,Trial type,Trial,Left,Time taken,Right,Time taken,Trial On,Output"];
    NSArray *dataArray = self.fetchedResultsController.fetchedObjects;
    for (User *user in dataArray) {
        NSString *name = [[NSUserDefaults standardUserDefaults] objectForKey:[NSString stringWithFormat:@"user-%d", user.userId]];
        if (!name.length) {
            name = [NSString stringWithFormat:@"User %d", user.userId];
        }
        for (Trial *trial in user.trials) {
            NSString *type = trial.trialType ? (trial.trialType == 1 ? @"servo" : @"joint") : @"touch";
            [writeString appendFormat:@"\n%@,%@,%d,%d,%f,%d,%f,%@,%@", name, type, trial.trial, trial.left, trial.leftTime, trial.right, trial.rightTime, [NSDate dateWithTimeIntervalSinceReferenceDate:trial.timeStamp], trial.selectedLeft ? @"Left" : @"Right"];
        }
    }
    [[NSFileManager defaultManager] createFileAtPath:filePath contents:[writeString dataUsingEncoding:NSUTF8StringEncoding] attributes:nil];
}

- (void)sync:(id)sender {
    [self saveAsFile];
    UIActivityViewController *activityVC = [[UIActivityViewController alloc] initWithActivityItems:@[[NSURL fileURLWithPath:filePath]] applicationActivities:nil];
    NSArray *excludeActivities = @[UIActivityTypeAddToReadingList,
                                   UIActivityTypeAssignToContact,
                                   UIActivityTypePostToFacebook,
                                   UIActivityTypePostToFlickr,
                                   UIActivityTypePostToTencentWeibo,
                                   UIActivityTypePostToTwitter,
                                   UIActivityTypePostToVimeo,
                                   UIActivityTypePostToWeibo,
                                   UIActivityTypeSaveToCameraRoll];
    activityVC.excludedActivityTypes = excludeActivities;
    [self presentViewController:activityVC animated:YES completion:nil];
    if ([activityVC respondsToSelector:@selector(popoverPresentationController)]) {
        // iOS 8+
        UIPopoverPresentationController *presentationController = [activityVC popoverPresentationController];
        
        presentationController.sourceView = sender; // if button or change to self.view.
    }
}

- (void)done {
    [self.navigationController dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark - Segues

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([[segue identifier] isEqualToString:@"showDetails"]) {
        NSIndexPath *indexPath = [self.tableView indexPathForSelectedRow];
        NSManagedObject *object = [[self fetchedResultsController] objectAtIndexPath:indexPath];
        [[segue destinationViewController] setDetailItem:object];
    }
}

#pragma mark - Table View

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    NSUInteger count = self.fetchedResultsController.fetchedObjects.count;
    return count;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    User *user = self.fetchedResultsController.fetchedObjects.count ? self.fetchedResultsController.fetchedObjects[section] : nil;
    return user.trials.count;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    User *user = self.fetchedResultsController.fetchedObjects.count ? self.fetchedResultsController.fetchedObjects[section] : nil;
    NSString *name = [[NSUserDefaults standardUserDefaults] objectForKey:[NSString stringWithFormat:@"user-%d", user.userId]];
    if (!name.length) {
        name = [NSString stringWithFormat:@"User %d", user.userId];
    }
    return name;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"LogCell" forIndexPath:indexPath];
    [self configureCell:cell atIndexPath:indexPath];
    return cell;
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the specified item to be editable.
    return YES;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        NSManagedObjectContext *context = [self.fetchedResultsController managedObjectContext];
        [context deleteObject:[self.fetchedResultsController objectAtIndexPath:indexPath]];
        
        NSError *error = nil;
        if (![context save:&error]) {
            // Replace this implementation with code to handle the error appropriately.
            // abort() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
            NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
            abort();
        }
    }
}

- (void)configureCell:(UITableViewCell *)cell atIndexPath:(NSIndexPath *)indexPath {
    User *user = self.fetchedResultsController.fetchedObjects[indexPath.section];
    int row = (int)indexPath.row;
    Trial *trial = user.trials.count ? user.trials[row] : nil;
    int type = 0;
    type  = trial.trialType;
    cell.textLabel.textColor = [UIColor whiteColor];
    cell.backgroundColor = [UICustom appColor:trial.selectedLeft ? AppColorBlue : AppColorGreen];
    cell.imageView.image = [UIImage imageNamed:type ? (type == 1 ? @"servoWhite" : @"jointWhite") : @"tap"];
    cell.textLabel.text = [NSString stringWithFormat:@"%d, %d", trial.left, trial.right];
    cell.detailTextLabel.text = [self getTrialTime:trial.timeStamp];
}

#pragma mark - Fetched results controller

- (void)controllerWillChangeContent:(NSFetchedResultsController *)controller {
    [self.tableView beginUpdates];
}

- (void)controller:(NSFetchedResultsController *)controller didChangeSection:(id <NSFetchedResultsSectionInfo> )sectionInfo
           atIndex:(NSUInteger)sectionIndex forChangeType:(NSFetchedResultsChangeType)type {
    switch (type) {
        case NSFetchedResultsChangeInsert:
            [self.tableView insertSections:[NSIndexSet indexSetWithIndex:sectionIndex] withRowAnimation:UITableViewRowAnimationFade];
            break;
            
        case NSFetchedResultsChangeDelete:
            [self.tableView deleteSections:[NSIndexSet indexSetWithIndex:sectionIndex] withRowAnimation:UITableViewRowAnimationFade];
            break;
            
        default:
            return;
    }
}

- (void)controller:(NSFetchedResultsController *)controller didChangeObject:(id)anObject
       atIndexPath:(NSIndexPath *)indexPath forChangeType:(NSFetchedResultsChangeType)type
      newIndexPath:(NSIndexPath *)newIndexPath {
    UITableView *tableView = self.tableView;
    
    switch (type) {
        case NSFetchedResultsChangeInsert:
            [tableView insertRowsAtIndexPaths:@[newIndexPath] withRowAnimation:UITableViewRowAnimationFade];
            break;
            
        case NSFetchedResultsChangeDelete:
            [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
            break;
            
        case NSFetchedResultsChangeUpdate:
            [self configureCell:[tableView cellForRowAtIndexPath:indexPath] atIndexPath:indexPath];
            break;
            
        case NSFetchedResultsChangeMove:
            [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
            [tableView insertRowsAtIndexPaths:@[newIndexPath] withRowAnimation:UITableViewRowAnimationFade];
            break;
    }
}

- (void)controllerDidChangeContent:(NSFetchedResultsController *)controller {
    [self.tableView endUpdates];
}

- (NSString *)getTrialTime:(NSTimeInterval)time;
{
    NSDateFormatter *dateFormater = [[NSDateFormatter alloc] init];
    NSDate *date = [NSDate dateWithTimeIntervalSinceReferenceDate:time];
    switch ([date daysDifferenceFrom:[NSDate date]]) {
        case 0:
            [dateFormater setDateFormat:@"'today' HH:mm"];
            break;
            
        case 1:
            [dateFormater setDateFormat:@"'yesterday' HH:mm"];
            break;
            
        case 3:
        case 2:
            [dateFormater setDateFormat:@"eeee HH:mm"];
            break;
            
        default:
            [dateFormater setDateFormat:@"dd MMM yyyy',' HH:mm"];
    }
    return [dateFormater stringFromDate:date];
}
- (NSFetchedResultsController *)fetchedResultsController {
    if (_fetchedResultsController != nil) {
        return _fetchedResultsController;
    }
    
    NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    // Edit the entity name as appropriate.
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"User" inManagedObjectContext:self.managedObjectContext];
    [fetchRequest setEntity:entity];
    
    // Set the batch size to a suitable number.
    [fetchRequest setFetchBatchSize:20];
    
    // Edit the sort key as appropriate.
    NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc] initWithKey:@"userId" ascending:NO];
    NSArray *sortDescriptors = @[sortDescriptor];
    
    [fetchRequest setSortDescriptors:sortDescriptors];
    
    // Edit the section name key path and cache name if appropriate.
    // nil for section name key path means "no sections".
    NSFetchedResultsController *aFetchedResultsController = [[NSFetchedResultsController alloc] initWithFetchRequest:fetchRequest managedObjectContext:self.managedObjectContext sectionNameKeyPath:nil cacheName:@"Master"];
    aFetchedResultsController.delegate = self;
    self.fetchedResultsController = aFetchedResultsController;
    
    NSError *error = nil;
    if (![self.fetchedResultsController performFetch:&error]) {
        // Replace this implementation with code to handle the error appropriately.
        // abort() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
        abort();
    }
    
    return _fetchedResultsController;
}

/*
 // Implementing the above methods to update the table view in response to individual changes may have performance implications if a large number of changes are made simultaneously. If this proves to be an issue, you can instead just implement controllerDidChangeContent: which notifies the delegate that all section and object changes have been processed.
 
 - (void)controllerDidChangeContent:(NSFetchedResultsController *)controller
 {
 // In the simplest, most efficient, case, reload the table view.
 [self.tableView reloadData];
 }
 */

@end
@implementation NSDate (Utils)

- (NSInteger)daysDifferenceFrom:(NSDate *)dateToCompare {
    // Start by removing the time components from the dates so we can compare the days correctly
    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSDateComponents *components = [calendar components:NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit fromDate:dateToCompare];
    [components setHour:0];
    [components setMinute:0];
    [components setSecond:0];
    NSDate *timelessDateToCompare = [calendar dateFromComponents:components];
    components = [calendar components:NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit fromDate:self];
    [components setHour:0];
    [components setMinute:0];
    [components setSecond:0];
    NSDate *timelessDate = [calendar dateFromComponents:components];
    // Calculate the number of full days between this item's date and the specified comparison date
    components = [calendar components:NSDayCalendarUnit fromDate:timelessDate toDate:timelessDateToCompare options:0];
    return [components day];
}

@end
