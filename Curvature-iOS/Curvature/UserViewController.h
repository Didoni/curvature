//
//  UserViewController.h
//  Curvature
//
//  Created by Shailesh Prakash on 13/11/2014.
//  Copyright (c) 2014 MyOxygen. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreData/CoreData.h>
#import <QuartzCore/QuartzCore.h>
#import "Trial.h"
#import "User.h"
#import "HorizontalTableView.h"
#import "MDRadialProgressView.h"

typedef enum {
    CEventTrialStarted = 0x1,
    CEventTrialStopped = 0x2,
    CEventTrialPaused = 0x3,
    CEventTrialResumed = 0x4,
    CEventTrialReStarted = 0x5,
    CEventUserTrialEnded = 0x6,
    
    CEventStartTrial = 0x1,
    CEventStopTrial = 0x2,
    CEventPauseTrial = 0x3,
    CEventResumeTrial = 0x4,
    CEventReStartTrial = 0x5,
    CEventEndUserTrial = 0x6,
    
    CEventSendTrialList = 0x1 << 4,
    CEventUpdateTrialList = 0x1 << 5,
    CEventSendAllUserTrials = 0x1 << 6,
} CEvent;

@interface UserViewController : UIViewController <NSFetchedResultsControllerDelegate, UITableViewDataSource>
@property (strong, nonatomic) NSFetchedResultsController *fetchedResultsController;
@property (strong, nonatomic) NSManagedObjectContext *managedObjectContext;

@property (strong, nonatomic) IBOutlet UIView *timerViewContainer;
@property (strong, nonatomic) IBOutlet UILabel *userType;
@property (strong, nonatomic) IBOutlet UIButton *trialBtn;
@property (strong, nonatomic) IBOutlet UILabel *left;
@property (strong, nonatomic) IBOutlet UILabel *right;
@property (strong, nonatomic) IBOutlet UILabel *tableHeader;
@property (strong, nonatomic) IBOutlet UIButton *start;
@property (strong, nonatomic) IBOutlet UIBarButtonItem *rewind;
@property (strong, nonatomic) IBOutlet UIBarButtonItem *forward;
@property (strong, nonatomic) IBOutlet UIBarButtonItem *pause;
@property (strong, nonatomic) IBOutlet UIBarButtonItem *refresh;
@property (strong, nonatomic) IBOutlet UIBarButtonItem *showList;
@property (strong, nonatomic) IBOutlet UIToolbar *toolbar;
@property (strong, nonatomic) IBOutlet UIImageView *trialType;
@property (strong, nonatomic) IBOutlet UITableView *trialHistory;
@property (strong, nonatomic) IBOutlet UITableView *audioFiles;
@property (strong, nonatomic) IBOutlet HorizontalTableView *container;
@property (strong, nonatomic) IBOutlet MDRadialProgressView *timerView;
- (IBAction)skip:(UIButton *)segment;
- (IBAction)startTimer:(UIButton *)btn;
- (IBAction)finish:(UIButton *)btn;
- (IBAction)showTrialHistory;
- (IBAction)showAudioOptions;
- (IBAction)rewind:(UIBarButtonItem *)btn;
- (IBAction)forward:(UIBarButtonItem *)btn;
- (IBAction)pause:(UIBarButtonItem *)btn;
- (IBAction)refresh:(UIBarButtonItem *)btn;
@end
