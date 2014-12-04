//
//  UserViewController.m
//  Curvature
//
//  Created by Shailesh Prakash on 13/11/2014.
//  Copyright (c) 2014 MyOxygen. All rights reserved.
//

#import "UserViewController.h"
#import "UICustom.h"
#import "Trial.h"
#import "MDRadialProgressTheme.h"
#import "AppDelegate.h"
#import "SoundManager.h"
#import "VRPN.h"
#import "NSDate+TimeAgo.h"
#include "CoreWebSocket.h"

#define MAX_TRAILS 500

@interface UserViewController () {
    NSString *noiseFile;
    UIFont *font;
    NSTimer *perSecTimer;
    NSUInteger counter;
    NSUInteger totalTrials;
    NSUInteger maxTimerVal;
    NSUInteger lTime;
    NSUInteger rTime;
    BOOL isCurrentModelA;
    BOOL runTimer;
    BOOL alertTimer;
    BOOL shouldShowDemo;
    BOOL dualDemo;
    BOOL leftSideOpen;
    BOOL rightSideOpen;
    UILabel *trialCounter;
    NSArray *files;
    BOOL _suspendInBackground;
    UIBackgroundTaskIdentifier _backgroundTask;
    
}
@property (nonatomic, retain) NSArray *views;
@end

@implementation UserViewController
@synthesize start;

BOOL lrType;
BOOL paused;
int trialsPlates[MAX_TRAILS * 3][3];
User *currentUser;
static UserViewController *_THIS;
WebSocketRef _webSocket;

- (void)viewDidLoad {
    [super viewDidLoad];
    _THIS = self;
    shouldShowDemo = YES;
    _rewind.enabled = NO;
    _refresh.enabled = NO;
    _pause.enabled = NO;
    _forward.enabled = YES;
    perSecTimer = nil;
    dualDemo = NO;
    trialCounter = _trialBtn.titleLabel;
    self.navigationItem.rightBarButtonItem.customView.hidden = YES;
    self.views = [[self.view viewWithTag:10] subviews];
    self.container.currentPageColor = [UICustom appColor:AppColorBlue];
    self.container.otherPageColor = [UICustom appColor:AppColorLightGray];
    [_container refreshData];
    [self.trialHistory registerClass:[UITableViewCell class] forCellReuseIdentifier:@"Cell"];
    maxTimerVal = [[NSUserDefaults standardUserDefaults] integerForKey:@"trial_time"];
    [self getExperiments];
    [self makeDial];
    ((AppDelegate *)[UIApplication sharedApplication].delegate).vrpnUpdater = self.tableHeader;
    currentUser = ((User *)self.fetchedResultsController.fetchedObjects.lastObject);
    [self changeTrialType:trialsPlates[currentUser.trials.count][2]];
    if (currentUser.trials.count >= totalTrials) {
        [self changeUser];
    }
    isCurrentModelA = YES;
    _left.alpha = 0;
    _right.alpha = 0;
    [_trialBtn setTitle:nil forState:UIControlStateNormal];
    font = [UIFont systemFontOfSize:42.0f];
    start.titleLabel.font = font;
    start.titleLabel.frame = start.frame;
    
    lrType = [[NSUserDefaults standardUserDefaults] boolForKey:@"trial_type"];
    [self appDidBecomeActive:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(appDidBecomeActive:) name:UIApplicationDidBecomeActiveNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(appWillInactive:) name:UIApplicationWillResignActiveNotification object:nil];
    [SoundManager sharedManager].allowsBackgroundMusic = YES;
    [[SoundManager sharedManager] prepareToPlay];
    [self restartSocket];
    _name.userInteractionEnabled = YES;
    [_name addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(changeName)]];
    NSManagedObjectContext *context = [self.fetchedResultsController managedObjectContext];
    BOOL changed = NO;
    for (User *remUser in self.fetchedResultsController.fetchedObjects) {
        if(remUser.trials.count == 0){
            changed = YES;
            [context deleteObject:remUser];
            [[NSUserDefaults standardUserDefaults] setObject:nil forKey:[NSString stringWithFormat:@"user-%d",currentUser.userId]];
        }
    }
    [[NSUserDefaults standardUserDefaults] synchronize];
    if(changed){
        [context save:nil];
    }
    NSString *name = [[NSUserDefaults standardUserDefaults] objectForKey:[NSString stringWithFormat:@"user-%d",currentUser.userId]];
    if(name.length){
        _name.text = name;
    }
}
-(void)changeName
{
    UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:nil message:@"Please enter your name" delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"Ok", nil] ;
    alertView.tag = 100;
    UITextField * alertTextField = [alertView textFieldAtIndex:0];
    alertTextField.text = _name.text;
    alertView.alertViewStyle = UIAlertViewStylePlainTextInput;
    [alertView show];
}
- (void)changeTrialType:(int)ltrialType {
    NSUInteger trialType = trialsPlates[currentUser.trials.count][2];
    if (trialType != ((Trial*)currentUser.trials.lastObject).trialType) {
        counter = 0;
    }
    if (counter == 0) {
        [[SoundManager sharedManager] playSound:@"ding.mp3"];
        _trialType.image = [UIImage imageNamed:trialType ? (trialType == 1 ? @"servoWhite" : @"joint") : @"tap"];
        UIView *view = [_trialType superview];
        CGRect frame = view.frame;
        BOOL single = (ltrialType == 2);
        if (single) {
            frame.origin.x = 90;
            frame.size.width = 140;
            ((UILabel *)[view viewWithTag:1]).text = @"First\nModel";
            _left.text = @"First\nModel";
            _right.text = @"Second\nModel";
        }
        else {
            frame.origin.x = 30;
            frame.size.width = 260;
            ((UILabel *)[view viewWithTag:1]).text = @"Left\nModel";
            _left.text = @"Left\nModel";
            _right.text = @"Right\nModel";
        }
        view.clipsToBounds = single;
        [UIView animateWithDuration:0.4 animations: ^{
            view.frame = frame;
            _left.transform = CGAffineTransformIdentity;
            _right.transform = CGAffineTransformIdentity;
            _left.alpha = 0;
            _right.alpha = 0;
            _trialType.transform = (single ? CGAffineTransformMakeTranslation(-10, 0) : CGAffineTransformIdentity);
        }];
    }
}

- (void)perSecondUpdate {
    if (runTimer) {
        if (isCurrentModelA) {
            lTime++;
        }else{
            rTime++;
        }
        _timerView.progressCounter += 1;
        if (_timerView.progressCounter == _timerView.progressTotal) {
            [[SoundManager sharedManager] playSound:@"tong.mp3"];
            [self doneCounting];
            [self stopTimer];
        }
    }
}

- (void)doneCounting {
    if (!isCurrentModelA) {
        NSString *one, *two;
        NSUInteger trialType = trialsPlates[currentUser.trials.count][2];
        if (trialType != 2) {
            one = @"Model Left";
            two = @"Model Right";
        }
        else {
            one = @"First Model";
            two = @"Second Model";
        }
        UIAlertView *decisionAlert = [[UIAlertView alloc] initWithTitle:@"Which model is more curved?" message:nil delegate:self cancelButtonTitle:nil otherButtonTitles:one, two, nil];
        decisionAlert.tag = 1;
        runTimer = NO;
        alertTimer = runTimer;
        [decisionAlert show];
    }
}

- (void)showAudioOptions {
    CGFloat offset;
    if (!leftSideOpen) {
        [_audioFiles reloadData];
    }
    if (rightSideOpen) {
        offset = 320;
        rightSideOpen = NO;
    }
    else {
        offset = leftSideOpen ? -160 : 160;
    }
    [UIView animateWithDuration:0.4f animations: ^{
        _container.transform = CGAffineTransformTranslate(_container.transform, offset, 0);
        UIView *bg = [self.view viewWithTag:99];
        bg.transform = CGAffineTransformTranslate(bg.transform, offset, 0);
    }];
    leftSideOpen = !leftSideOpen;
    _container.userInteractionEnabled = !leftSideOpen;
}

- (void)showTrialHistory {
    if (!rightSideOpen) {
        if (rx == 0) {
            _tableHeader.text = @"Ongoing Trials";
        }
        [_trialHistory reloadData];
        if (currentUser.trials.count) {
            [_trialHistory scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:currentUser.trials.count - 1 inSection:0]
                                 atScrollPosition:UITableViewScrollPositionMiddle animated:NO];
        }
        
    }
    CGFloat offset;
    if (leftSideOpen) {
        offset = -320;
        leftSideOpen = NO;
    }
    else {
        offset = rightSideOpen ? 160 : -160;
    }
    [UIView animateWithDuration:0.4f animations: ^{
        _container.transform = CGAffineTransformTranslate(_container.transform, offset, 0);
        UIView *bg = [self.view viewWithTag:99];
        bg.transform = CGAffineTransformTranslate(bg.transform, offset, 0);
    } completion: ^(BOOL finish) {
        if (currentUser.trials.count) {
        }
    }];
    rightSideOpen = !rightSideOpen;
    _container.userInteractionEnabled = !rightSideOpen;
}

- (IBAction)startTimer:(UIButton *)btn {
    [self emitEvent:CEventStartTrial];
    [[SoundManager sharedManager] playSound:@"ding.mp3"];
    btn.hidden = YES;
    runTimer = YES;
    uint cur = (uint)_container.currentPageIndex;
    _pause.enabled = YES;
    BOOL isTimer = (cur > 0 && cur % 2 == 0);
    if (isTimer) {
        _refresh.enabled = YES;
        UILabel *model = isCurrentModelA ? _left : _right;
        NSUInteger trialType = trialsPlates[currentUser.trials.count][2];
        if (isCurrentModelA) {
            lTime = 0;
        }else{
            rTime = 0;
        }
        
        if (trialType != 2) {
            CGRect frame = model.frame;
            _left.alpha = 0;
            _right.alpha = 0;
            model.frame = CGRectMake(30, -356 + 110, 200, 200);
            model.layer.cornerRadius = 100;
            model.alpha = 1;
            model.font = [UIFont fontWithDescriptor:[model.font fontDescriptor] size:model.font.pointSize * 2];
            _forward.enabled = NO;
            [UIView animateWithDuration:0.4 delay:0.4 options:UIViewAnimationOptionTransitionCrossDissolve animations: ^{
                model.transform = CGAffineTransformTranslate(CGAffineTransformMakeScale(.496, .496), isCurrentModelA ? -121 : 121, 356 + 79.5);
            } completion: ^(BOOL finish) {
                model.transform = CGAffineTransformIdentity;
                model.font = [UIFont fontWithDescriptor:[model.font fontDescriptor] size:model.font.pointSize / 2];
                model.layer.cornerRadius = CGRectGetWidth(frame) / 2;
                model.frame = frame;
                _forward.enabled = YES;
            }];
        }
        else {
            model.alpha = 1;
            model.transform = CGAffineTransformMakeTranslation(isCurrentModelA ? -120 : 0, 0);
            [UIView animateWithDuration:0.6 delay:0.2 options:UIViewAnimationOptionTransitionCrossDissolve animations: ^{
                model.transform = isCurrentModelA ? CGAffineTransformIdentity : CGAffineTransformTranslate(_left.transform, -120, 0);
            } completion: ^(BOOL finish) {
                _forward.enabled = YES;
                ((UILabel *)[[_left superview] viewWithTag:1]).text = !isCurrentModelA ? @"First\nModel" : @"Second\nModel";
            }];
        }
    }
}

- (void)stopTimer {
    [self emitEvent:CEventStopTrial];
    _pause.enabled = NO;
    start.hidden = NO;
    _left.alpha = 0;
    _right.alpha = 0;
    runTimer = NO;
    _timerView.progressCounter = 0;
    NSUInteger index = [_views indexOfObject:[_timerViewContainer superview]];
    switch (index) {
        case 8:
            _refresh.enabled = isCurrentModelA;
            isCurrentModelA = !isCurrentModelA;
            _timerView.theme.incompletedColor = [UICustom appColor:isCurrentModelA ? AppColorBlue : AppColorGreen];
            break;
            
        case 6:
            _refresh.enabled = NO;
            isCurrentModelA = !isCurrentModelA;
            [_trialBtn setTitle:[NSString stringWithFormat:@"Trial %2d", dualDemo ? 2 : 1] forState:UIControlStateNormal];
            _timerView.theme.incompletedColor = [UICustom appColor:isCurrentModelA ? AppColorBlue : AppColorGreen];
            break;
            
        case 4:
            _timerView.theme.incompletedColor = [UICustom appColor:isCurrentModelA ? AppColorGreen : AppColorBlue];
            break;
            
        case 2:
            _container.currentPageIndex += 1;
            break;
    }
}

- (void)forward:(UIBarButtonItem *)btn {
    NSUInteger cur = _container.currentPageIndex;
    BOOL isTimer = (cur > 0 && cur % 2 == 0);
    if (isTimer) {
        if (isCurrentModelA) {
            if (paused || runTimer) {
                runTimer = NO;
                [self stopTimer];
            }
            else {
                [self startTimer:start];
            }
        }
        else {
            if (paused || runTimer) {
                runTimer = NO;
                [self stopTimer];
                isCurrentModelA = NO;
                [self doneCounting];
            }
            else {
                [self startTimer:start];
            }
        }
        if (paused) {
            paused = NO;
            [self resetPause:_pause];
        }
    }
    else {
        _container.currentPageIndex = 8;
    }
}

- (void)rewind:(UIBarButtonItem *)btn {
    NSUInteger cur = _container.currentPageIndex;
    if (cur == 8) {
        UIAlertView *rewindAlert = [[UIAlertView alloc] initWithTitle:@"Abort Experiment" message:@"Do you want to abort this experiment?" delegate:self cancelButtonTitle:@"No" otherButtonTitles:((currentUser.trials.count) ? @"Yes, save user trials" : @"Yes"), ((currentUser.trials.count) ? @"Yes, clear user trial" : nil), nil];
        rewindAlert.tag = 10;
        alertTimer = runTimer;
        runTimer = NO;
        [rewindAlert show];
    }
    else {
        _container.currentPageIndex = 0;
    }
}

- (void)skip:(UIButton *)skip {
    if (!isCurrentModelA) {
        [self stopTimer];
    }
}

- (void)pause:(UIBarButtonItem *)btn {
    paused = !paused;
    start.hidden = YES;
    runTimer = !paused;
    [self resetPause:btn];
    [self emitEvent:paused?CEventPauseTrial:CEventResumeTrial];
}

- (void)resetPause:(UIBarButtonItem *)btn {
    NSMutableArray *items = [NSMutableArray arrayWithArray:_toolbar.items];
    for (NSObject *item in items) {
        if (btn == item) {
            UIBarButtonItem *i = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:paused ? UIBarButtonSystemItemPlay : UIBarButtonSystemItemPause target:self action:@selector(pause:)];
            _pause = i;
            [items replaceObjectAtIndex:[items indexOfObject:item] withObject:i];
            break;
        }
    }
    _toolbar.items = items;
}

- (void)refresh:(UIBarButtonItem *)btn {
    UIAlertView *refreshAlert = [[UIAlertView alloc] initWithTitle:@"Reset Trial" message:@"Do you want to reset this trial?" delegate:self cancelButtonTitle:@"No" otherButtonTitles:@"Yes", nil];
    refreshAlert.tag = 20;
    alertTimer = runTimer;
    runTimer = NO;
    [refreshAlert show];
}

- (IBAction)finish:(UIButton *)btn {
    NSUInteger userType = [[NSUserDefaults standardUserDefaults] integerForKey:@"trial_user"];
    _container.currentPageIndex = userType ? 8 : 0;
    _container.scrollView.scrollEnabled = YES;
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    runTimer = alertTimer;
    if (!alertView || alertView.tag == 1) {
        _left.alpha = 0;
        _right.alpha = 0;
        _timerView.theme.incompletedColor = [UICustom appColor:AppColorBlue];
        NSUInteger cur = _container.currentPageIndex;
        if (cur > 4) {
            if (cur == 6) {
                [_trialBtn setTitle:@"2" forState:UIControlStateNormal];
                UILabel *model = dualDemo ? _right : _left;
                [UIView animateWithDuration:0.4 animations: ^{
                    model.alpha = 1;
                }];
                if (dualDemo) {
                    dualDemo = NO;
                    _container.currentPageIndex += 1;
                }
                else {
                    dualDemo = YES;
                }
            }
            else {
                [self addTrial:!buttonIndex];
                if (currentUser.trials.count >= totalTrials) {
                    NSUInteger userType = [[NSUserDefaults standardUserDefaults] integerForKey:@"trial_user"];
                    [self changeTrialType:lrType?0:2];
                    if (userType) {
                        [[[UIAlertView alloc] initWithTitle:nil message:[NSString stringWithFormat:@"User %ld finished the experiment.", self.fetchedResultsController.fetchedObjects.count] delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil] show];
                        [self changeUser];
                    }
                    else {
                        _container.currentPageIndex += 1;
                        _container.scrollView.scrollEnabled = NO;
                    }
                }
                else {
                    int i = (int)currentUser.trials.count - 1;
                    [self changeTrialType:trialsPlates[i][3]];
                    [_trialBtn setTitle:[NSString stringWithFormat:@"Trial %2ld", counter + 1] forState:UIControlStateNormal];
                }
            }
        }
        else {
            _container.currentPageIndex += 1;
        }
        isCurrentModelA = YES;
    }
    else if (alertView.tag == 40) {
        if (buttonIndex) {
            for (User *user in self.fetchedResultsController.fetchedObjects) {
                [self.managedObjectContext deleteObject:user];
            }
            [self changeUser];
        }
    }else {
        if (buttonIndex) {
            if (alertView.tag == 100){
                if(!currentUser){
                    [self changeUser];
                }
                UITextField * alertTextField = [alertView textFieldAtIndex:0];
                NSString *text = [alertTextField.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
                
                [[NSUserDefaults standardUserDefaults] setObject:text forKey:[NSString stringWithFormat:@"user-%d",currentUser.userId]];
                [[NSUserDefaults standardUserDefaults] synchronize];
                if(text.length){
                    [self emitEvent:CEventUserName];
                }
                _name.text = text.length?text:@"Tap here to add name";
                return;
            }
            if (alertView.tag == 10) {
                shouldShowDemo = YES;
                if (buttonIndex == 2 && currentUser.trials.count) {
                    //by default it is saved, lets delete the user + trials
                    // delete is cascaded
                    [self.managedObjectContext deleteObject:currentUser];
                    currentUser = nil;
                }
                NSUInteger userType = [[NSUserDefaults standardUserDefaults] integerForKey:@"trial_user"];
                if (userType == 0) {
                    _container.currentPageIndex =  0;
                }
                else {
                    [self changeUser];
                }
            }
            if (!isCurrentModelA) {
                _left.alpha = 0;
                _right.alpha = 0;
                [UIView animateWithDuration:0.4 animations: ^{
                    _left.alpha = 1;
                }];
            }
            else {
                isCurrentModelA = NO;
            }
            [self stopTimer];
        }
        if (paused) {
            paused = NO;
            [self resetPause:_pause];
        }
    }
}

- (void)addTrial:(BOOL)selctedLeft {
    BOOL newUser = NO;
    if ([_views indexOfObject:[_timerViewContainer superview]] == 8) {
        NSManagedObjectContext *context = [self.fetchedResultsController managedObjectContext];
        NSUInteger userTrialCount = currentUser.trials.count;
        if (!currentUser || userTrialCount == totalTrials) {
            newUser = YES;
            currentUser = [NSEntityDescription insertNewObjectForEntityForName:@"User" inManagedObjectContext:context];
            currentUser.userId = currentUser.userId + 1;
        }
        else {
            currentUser = (User *)self.fetchedResultsController.fetchedObjects.lastObject;
        }
        
        Trial *trial = [NSEntityDescription insertNewObjectForEntityForName:@"Trial" inManagedObjectContext:context];
        trial.timeStamp = [[NSDate date] timeIntervalSinceReferenceDate];
        trial.selectedLeft = selctedLeft;
        NSUInteger index = currentUser.trials.count;
        trial.left = trialsPlates[index][0];
        trial.right = trialsPlates[index][1];
        trial.trialType = trialsPlates[index][2];
        trial.leftTime = lTime;
        trial.rightTime = rTime;
        lTime = rTime = 0;
        trial.trial = ++counter;
        [self emitEvent:CEventUserTrialEnded withData:trial];
        NSMutableOrderedSet *tempSet = [NSMutableOrderedSet orderedSetWithOrderedSet:currentUser.trials];
        [tempSet addObject:trial];
        currentUser.trials = tempSet;
        // Save the context.
        NSError *error = nil;
        if (![context save:&error]) {
            // Replace this implementation with code to handle the error appropriately.
            // abort() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
            NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
            abort();
        }
        if (newUser) {
            [self.fetchedResultsController performFetch:nil];
        }
        // NSString *message = [NSString stringWithFormat:@"{ user=%zd, trial=%zd, selected=%zd, unselected=%zd, trailOn='%@'}", currentUser.userId, trial.trial, trial.selected, trial.unselected, [NSDate dateWithTimeIntervalSinceReferenceDate:trial.timeStamp]];
        //[_webSocket send:message];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)changeUser {
    [[SoundManager sharedManager] playSound:@"duck"];
    NSManagedObjectContext *context = [self.fetchedResultsController managedObjectContext];
    for (User *remUser in self.fetchedResultsController.fetchedObjects) {
        if(remUser.trials.count == 0){
            [context deleteObject:remUser];
        }
    }
    currentUser = [NSEntityDescription insertNewObjectForEntityForName:@"User" inManagedObjectContext:context];
    currentUser.userId = ((User *)self.fetchedResultsController.fetchedObjects.lastObject).userId + 1;
    NSError *error = nil;
    if (![context save:&error]) {
        // Replace this implementation with code to handle the error appropriately.
        // abort() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
        abort();
    }
    [self.fetchedResultsController performFetch:nil];
    [self changeTrialType:lrType?0:2];
    [_trialBtn setTitle:@"Trial 1" forState:UIControlStateNormal];
    _name.text = @"Tap here to add name";
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
    NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc] initWithKey:@"userId" ascending:YES];
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

#pragma mark - Dial
- (void)makeDial {
    UIView *view = [self.view viewWithTag:99];
    view.layer.shadowOffset = CGSizeZero;
    view.layer.shadowOpacity = 0.75f;
    view.layer.shadowRadius = 10.0f;
    view.layer.shadowColor = [UICustom appColor:AppColorTextGray].CGColor;
    view.layer.shadowPath = [UIBezierPath bezierPathWithRect:self.view.layer.bounds].CGPath;
    view.clipsToBounds = NO;
    
    _timerView.progressTotal = maxTimerVal;
    _timerView.progressCounter = 0;
    _timerView.theme.completedColor = [UICustom appColor:AppColorLiteOrange];
    _timerView.theme.incompletedColor = [UICustom appColor:AppColorBlue];
    _timerView.theme.thickness = 12;
    _timerView.theme.sliceDividerHidden = YES;
    _timerView.theme.centerColor = [UIColor colorWithWhite:.92 alpha:1];
    _timerView.startingSlice = _timerView.progressTotal + 1;
    _timerView.clockwise = NO;
    _timerView.countTotal = YES;
    _timerView.theme.sliceDividerHidden = NO;
    _timerView.theme.sliceDividerThickness = 1;
    
    [_left superview].layer.cornerRadius = 5;
    [_left superview].layer.borderColor = [UIColor whiteColor].CGColor;
    [_left superview].layer.borderWidth = 0.5;
    
    
    [[_left superview] viewWithTag:1].layer.cornerRadius = CGRectGetWidth(_left.frame) / 2;
    [[_left superview] viewWithTag:1].layer.borderColor = _left.textColor.CGColor;
    [[_left superview] viewWithTag:1].layer.borderWidth = 0.5;
    [[_right superview] viewWithTag:2].layer.cornerRadius = CGRectGetWidth(_right.frame) / 2;
    [[_right superview] viewWithTag:2].layer.borderColor = _right.textColor.CGColor;
    [[_right superview] viewWithTag:2].layer.borderWidth = 0.5;
    
    _left.layer.cornerRadius = CGRectGetWidth(_left.frame) / 2;
    _left.layer.borderColor = _left.textColor.CGColor;
    _left.layer.borderWidth = 0.5;
    _right.layer.cornerRadius = CGRectGetWidth(_right.frame) / 2;
    _right.layer.borderColor = _right.textColor.CGColor;
    _right.layer.borderWidth = 0.5;
}

#pragma mark - Segues

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([[segue identifier] isEqualToString:@"showEdit"]) {
        [segue.destinationViewController setFetchedResultsController:self.fetchedResultsController];
        [segue.destinationViewController setManagedObjectContext:self.managedObjectContext];
    }
}

#pragma mark - horizontal slider view delegate
- (void)endedScrolling {
    counter = 0;
    NSUInteger cur = _container.currentPageIndex;
    BOOL isTimer = (cur > 0 && cur % 2 == 0);
    if (isTimer && cur != [_views indexOfObject:[_timerViewContainer superview]]) {
        _timerView.theme.completedColor = cur == 8 ? [UIColor clearColor] : [UICustom appColor:AppColorLiteOrange];
        _timerViewContainer.alpha = 0.f;
        [_views[cur] addSubview:_timerViewContainer];
        [UIView animateWithDuration:0.25 animations: ^{
            _timerViewContainer.alpha = 1.f;
        }];
    }
    _trialType.hidden = !isTimer;
    [_trialType superview].backgroundColor =  cur != 8?[UICustom appColor:AppColorDarkOrange]:[UIColor clearColor];
    _container.scrollView.scrollEnabled = !isTimer;
    NSUInteger userType = [[NSUserDefaults standardUserDefaults] integerForKey:@"trial_user"];
    _rewind.enabled = !userType && (cur != 1);
    _refresh.enabled = NO;
    _pause.enabled = NO;
    _forward.enabled = cur <= 8;
    self.navigationItem.rightBarButtonItem.customView.hidden = !isTimer;
    _container.hidePageIndicator = isTimer;
    start.hidden = NO;
    dualDemo = NO;
    runTimer = NO;
    _timerView.progressCounter = 0;
    _left.alpha = !isTimer;
    _right.alpha = 0;
    _timerView.theme.completedColor = cur == 8 ? [UIColor clearColor] : [UICustom appColor:AppColorLiteOrange];
    if (cur != 4) {
        isCurrentModelA = YES;
        _timerView.theme.incompletedColor = [UICustom appColor:AppColorBlue];
        [_trialBtn setTitle:[NSString stringWithFormat:@"Trial %2ld", (cur == 8) ? (currentUser.trials.count) + 1 : 1] forState:UIControlStateNormal];
        if (cur == 8) {
            shouldShowDemo = YES;
        }
    }
    else {
        isCurrentModelA = NO;
        [_trialBtn setTitle:@"Trial 1" forState:UIControlStateNormal];
        _timerView.theme.incompletedColor = [UICustom appColor:AppColorGreen];
    }
    if (shouldShowDemo && cur == 2) {
        UIView *window = ((AppDelegate *)[UIApplication sharedApplication].delegate).window;
        UIImageView *demo = [[UIImageView alloc] initWithFrame:window.bounds];
        demo.image = [UIImage imageNamed:@"demoOverlay"];
        demo.contentMode = UIViewContentModeScaleAspectFill;
        demo.alpha = 0.2;
        [window addSubview:demo];
        self.view.userInteractionEnabled = NO;
        [UIView animateWithDuration:0.7 delay:0.1 options:UIViewAnimationOptionTransitionCrossDissolve animations: ^{
            demo.alpha = 1;
        } completion:nil];
        demo.userInteractionEnabled = YES;
        [demo addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(removeOverlay:)]];
        UISwipeGestureRecognizer *reco = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(removeOverlay:)];
        reco.direction = (UISwipeGestureRecognizerDirectionLeft |
                          UISwipeGestureRecognizerDirectionRight |
                          UISwipeGestureRecognizerDirectionDown |
                          UISwipeGestureRecognizerDirectionUp);
        [demo addGestureRecognizer:reco];
        shouldShowDemo = NO;
    }
}

- (void)removeOverlay:(UIGestureRecognizer *)reco {
    UIImageView *demo = (UIImageView *)reco.view;
    demo.userInteractionEnabled = NO;
    demo.alpha = 0.0;
    demo.image = [UIImage imageNamed:@"startOverlay"];
    [UIView animateWithDuration:0.4 delay:0.2 options:UIViewAnimationOptionTransitionCrossDissolve animations: ^{
        demo.alpha = 1;
    } completion: ^(BOOL finish) {
        [UIView animateWithDuration:0.4 delay:.8 options:UIViewAnimationOptionTransitionCrossDissolve animations: ^{
            demo.alpha = 0;
        } completion: ^(BOOL finish) {
            self.view.userInteractionEnabled = YES;
            [demo removeFromSuperview];
        }];
    }];
}

- (NSInteger)numberOfColumnsForTableView:(HorizontalTableView *)tableView {
    return _views.count;
}

- (UIView *)tableView:(HorizontalTableView *)tableView viewForIndex:(NSInteger)index {
    NSUInteger cur = tableView.currentPageIndex;
    BOOL isTimer = (cur > 0 && cur % 2 == 0);
    if (isTimer && cur != [_views indexOfObject:[_timerViewContainer superview]]) {
        _timerViewContainer.alpha = 0.f;
        [_views[cur] addSubview:_timerViewContainer];
        [UIView animateWithDuration:0.25 animations: ^{
            _timerViewContainer.alpha = 1.f;
        }];
    }
    return _views[index];
}

- (CGFloat)columnWidthForTableView:(HorizontalTableView *)tableView {
    return CGRectGetWidth(self.view.frame);
}

#pragma mark - SRWebSocketDelegate

#pragma mark - Trial history Tableview
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return tableView == _trialHistory ? totalTrials : 4 + files.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Cell" forIndexPath:indexPath];
    int row = (int)indexPath.row;
    if (tableView == _trialHistory) {
        int type = 0;
        NSString *details = @"";
        cell.textLabel.numberOfLines = 2;
        cell.textLabel.font = _tableHeader.font;
        if (row < currentUser.trials.count) {
            Trial *trial = currentUser.trials[row];
            type  = trial.trialType;
            cell.textLabel.textColor = [UIColor whiteColor];
            cell.backgroundColor = [UICustom appColor:trial.selectedLeft ? AppColorBlue : AppColorGreen];
            cell.imageView.image = [UIImage imageNamed:type ? (type == 1 ? @"servoWhite" : @"jointWhite") : @"tap"];
            details = [NSString stringWithFormat:@" in %dsecs\n%@",(int)(trial.leftTime+trial.rightTime),[[NSDate dateWithTimeIntervalSinceReferenceDate:trial.timeStamp] timeAgo]];
        }
        else {
            cell.textLabel.textColor = [UIColor darkTextColor];
            cell.backgroundColor = [UIColor whiteColor];
            type = trialsPlates[row][2];
            cell.imageView.image = [UIImage imageNamed:type ? (type == 1 ? @"servo" : @"joint") : @"hand"];
        }
        cell.textLabel.text = [NSString stringWithFormat:@"%d, %d%@", trialsPlates[row][0], trialsPlates[row][1],details];
    }
    else {
        static NSString *icons[4] = { @"metal", @"punk", @"rock", @"electric" };
        static NSString *names[4] = { @"Silence", @"Random 1", @"Random 2", @"No music" };
        cell.imageView.image = [UIImage imageNamed:icons[row % 4]];
        NSString *text = row < files.count?files[row]:names[(row - files.count)%4];
        cell.textLabel.text = [text stringByReplacingOccurrencesOfString:@".mp3" withString:@""];
        cell.backgroundColor = row < files.count + 3 ? [UIColor colorWithWhite:0.92 alpha:1]:[UIColor colorWithWhite:0.8 alpha:1];
    }
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    if (tableView == _audioFiles) {
        int row = (int)indexPath.row;
        if(row < files.count){
            [[SoundManager sharedManager] playMusic:files[row] looping:YES];
        }else{
            switch (row - files.count) {
                default:
                case 3:
                    [[SoundManager sharedManager] stopMusic];
                    break;
                    
                case 0:
                    [[SoundManager sharedManager] playMusic:@"track3" looping:YES];
                    break;
                    
                case 1:
                    [[SoundManager sharedManager] playMusic:@"track1" looping:YES];
                    break;
                    
                case 2:
                    [[SoundManager sharedManager] playMusic:@"track2" looping:YES];
                    break;
            }
        }
        [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayback error:nil];
        [[AVAudioSession sharedInstance] setActive: YES error: nil];
    }
}

#pragma mark - detecting app state
- (void)appDidBecomeActive:(NSNotification *)notification {
    BOOL changed = NO;
    NSUInteger lmaxTimerVal = [[[NSUserDefaults standardUserDefaults] stringForKey:@"trial_time"]  intValue];
    BOOL reset = [[NSUserDefaults standardUserDefaults] boolForKey:@"reset"];
    if (reset) {
        [[NSUserDefaults standardUserDefaults] setBool:NO forKey:@"reset"];
        [[NSUserDefaults standardUserDefaults] synchronize];
    }
    NSManagedObjectContext *context = [self.fetchedResultsController managedObjectContext];
    BOOL dbChanged = NO;
    for (User *remUser in self.fetchedResultsController.fetchedObjects) {
        if(remUser.trials.count == 0){
            dbChanged = YES;
            [context deleteObject:remUser];
            [[NSUserDefaults standardUserDefaults] setObject:nil forKey:[NSString stringWithFormat:@"user-%d",currentUser.userId]];
        }
    }
    [[NSUserDefaults standardUserDefaults] synchronize];
    if(dbChanged){
        [context save:nil];
    }
    [self getExperimentData];
    if (lmaxTimerVal != maxTimerVal) {
        maxTimerVal = lmaxTimerVal;
        _container.currentPageIndex = 0;
        if (maxTimerVal > currentUser.trials.count) {
            [self changeUser];
        }
        changed = YES;
    }
    BOOL userType = [[NSUserDefaults standardUserDefaults] boolForKey:@"trial_user"];
    _showList.enabled = userType;
    _trialBtn.enabled = userType;
    if (userType != 0 && reset && (self.fetchedResultsController.fetchedObjects.count > 1 || ((User *)self.fetchedResultsController.fetchedObjects.lastObject).trials.count)) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Reset" message:@"Do you want to reset user data?" delegate:self cancelButtonTitle:@"No" otherButtonTitles:@"Yes", nil];
        alert.tag = 40;
        [alert show];
    }
    if (userType != 0 && _container.currentPageIndex != 8) {
        _container.currentPageIndex = 8;
        changed = YES;
    }
    [VRPN instance];
    [_trialHistory reloadData];
    if(!perSecTimer || changed){
        [perSecTimer invalidate];
        perSecTimer = [NSTimer scheduledTimerWithTimeInterval:1 target:self selector:@selector(perSecondUpdate) userInfo:nil repeats:YES];
        //runTimer = YES;
    }
    [self searchForMusic];
    [_audioFiles reloadData];
}

- (void)appWillInactive:(NSNotification *)notification {
    paused = runTimer?YES:NO;
    if([NSThread isMainThread] &&_backgroundTask == UIBackgroundTaskInvalid) {
        _backgroundTask = [[UIApplication sharedApplication] beginBackgroundTaskWithExpirationHandler: ^{
            [self endBackgroundTask];
        }];
    }
}
- (void)endBackgroundTask {
    if([NSThread isMainThread] &&_backgroundTask != UIBackgroundTaskInvalid && [[UIApplication sharedApplication] applicationState] == UIApplicationStateBackground) {
        paused = NO;
        [perSecTimer invalidate];
        perSecTimer = nil;
        runTimer = NO;
    }
}

- (void)searchForMusic {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSArray *filePathsArray = [[NSFileManager defaultManager] subpathsOfDirectoryAtPath:documentsDirectory error:nil];
    NSMutableArray *array = [NSMutableArray new];
    for (NSString *filePath in filePathsArray) {
        if ([[filePath pathExtension] isEqualToString:@"mp3"]) {
            NSString *lastPath = [filePath lastPathComponent];
            if(!( [lastPath isEqualToString:@"ding.mp3"] || [lastPath isEqualToString:@"tong.mp3"] ))
                [array addObject:lastPath];
        }
    }
    if (array.count) {
        files = [NSArray arrayWithArray:array];
    }
}
- (void)getExperimentData
{
    NSUInteger i = totalTrials;
    [self getExperiments];
    if ((i != totalTrials) && currentUser.trials.count) {
        [self changeUser];
    }else{
        [self changeTrialType:lrType?0:2];
    }
    
}
- (void)getExperiments {
    
    lrType = [[NSUserDefaults standardUserDefaults] boolForKey:@"trial_type"];
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSArray *data = [[[NSString stringWithContentsOfFile:[documentsDirectory stringByAppendingPathComponent:@"experiments.csv"] encoding:NSUTF8StringEncoding error:nil] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] componentsSeparatedByString:@"\n"];
    int i = 0;
    memset(trialsPlates, 0, sizeof(int)*MAX_TRAILS*9);
    totalTrials = 0;
    for (NSString *row in data) {
        NSString *cleaned = [row stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
        NSArray *cols = [cleaned componentsSeparatedByString:@","];
        if (cleaned.length && cols.count >= 3) {
            int type = [cols[0] intValue];
            if ((type == 2 && !lrType) || (type !=2 && lrType)) {
                trialsPlates[i][2] = type;
                trialsPlates[i][0] = [cols[1] intValue];
                trialsPlates[i++][1] = [cols[2] intValue];
            }
        }
    }
    totalTrials = i;
    [_trialHistory reloadData];
}

-(void)restartSocket
{
    if(_webSocket){
        WebSocketRelease(_webSocket);
    }
    _webSocket = WebSocketCreateWithHostAndPort(NULL, kWebSocketHostAny, 6001, NULL);
    _webSocket->callbacks.didClientReadCallback = remote_action;
    if (_webSocket) {
        NSLog(@"Running on %@", WebSocketGetIpAddress(_webSocket));
    }else{
        WebSocketRelease(_webSocket);
    }
}
-(void)emitEvent:(CEvent)event{
    [self emitEvent:event withData:nil];
}
-(void)emitEvent:(CEvent)event withData:(Trial*)trial
{
    NSUInteger userTrialCount = currentUser.trials.count;
    char index = (uint)userTrialCount;
    index = trialsPlates[index][isCurrentModelA?0:1];
    char type = (char)trialsPlates[index][2];
    char leftOrRight;
    WebSocketRef websocket = _webSocket;
    for (CFIndex i = 0; i < WebSocketGetClientCount(websocket); ++i) {
        WebSocketClientRef client = WebSocketGetClientAtIndex(websocket, i);
        switch (event) {
            case CEventChangeTrialType:
                WebSocketClientWriteWithFormat(client, CFSTR("%d,%d"), event,type,lrType);
                break;
            case CEventUserTrialEnded:
                WebSocketClientWriteWithFormat(client, CFSTR("%d,%d,%d,%d,%.0f,%.0f"), event,type,index,trial.selectedLeft,trial.leftTime,trial.rightTime);
                break;
            case CEventUserName:{
                NSString *name = [[NSUserDefaults standardUserDefaults] objectForKey:[NSString stringWithFormat:@"user-%d",currentUser.userId]];
                WebSocketClientWriteWithFormat(client, CFSTR("%d, , ,%@"), event,name);
            }
                break;
            default:
                leftOrRight = (char)isCurrentModelA;
                WebSocketClientWriteWithFormat(client, CFSTR("%d,%d,%d,%d"), event,type,index,leftOrRight);
                break;
        }
    }
    NSLog(@"sending data: %d%d%d", event,type,trialsPlates[index][isCurrentModelA?0:1]);
}

void remote_action(WebSocketRef self, WebSocketClientRef client, CFStringRef value) {
    if (value) {
        CFIndex length = CFStringGetLength(value);
        CFIndex maxSize = CFStringGetMaximumSizeForEncoding(length,
                                                            kCFStringEncodingUTF8);
        char *buffer = (char *)malloc(maxSize);
        if (maxSize && CFStringGetCString(value, buffer, maxSize,
                                          kCFStringEncodingUTF8)) {
            int action = buffer[0];
            switch (action & 7) {
                case CEventStartTrial: [_THIS startTimer:nil];break;
                case CEventStopTrial: [_THIS stopTimer];break;
                case CEventPauseTrial: if(!paused)[_THIS pause:_THIS.pause]; break;
                case CEventResumeTrial: if(paused)[_THIS pause:_THIS.pause]; break;
                case CEventReStartTrial: [_THIS refresh];  break;
                case CEventEndUserTrial:[_THIS  changeUser]; break;
                case CEventSkipTrial:
                    [_THIS  forward:_THIS.forward];
                    break;
            }
            if (action & CEventSendTrialList) {
                NSMutableString *csv = [NSMutableString new];
                NSString *name = _THIS.name.text;
                if(!name.length||[name isEqualToString:@"Tap here to add name"]){
                    name = @"";
                }
                [csv appendFormat:@"%d,%d,%@",CEventSendTrialList,lrType,name];
                for (Trial *trial in currentUser.trials) {
                    [csv appendFormat:@"\n%d,%d,%d,%d,%.0f,%.0f",trial.trialType,trial.left,trial.right,trial.selectedLeft,trial.leftTime,trial.rightTime];
                }
                WebSocketClientWriteWithFormat(client, (__bridge CFStringRef)[csv stringByTrimmingCharactersInSet:[NSCharacterSet newlineCharacterSet]]);
            }
            if (action & CEventUpdateTrialList) {
                BOOL valid = NO;
                NSString *data = [[((__bridge NSString *)value) substringFromIndex:1] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
                if(data.length){
                    for (NSString *row in [data componentsSeparatedByString:@"\n"]) {
                        NSArray *cols = [[row stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] componentsSeparatedByString:@","];
                        if (cols.count>=3) {
                            valid = YES;
                            break;
                        }
                    }
                }
                if (valid) {
                    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
                    NSString *documentsDirectory = [paths objectAtIndex:0];
                    NSString *filePath = [documentsDirectory stringByAppendingPathComponent:@"experiments.csv"];
                    bool  written = [data writeToFile:filePath atomically:YES encoding:NSUTF8StringEncoding error:nil];
                    if(written){
                        [_THIS getExperimentData];
                    }
                }
            }
            if (action & CEventChangeTrialType) {
                if(maxSize>1){
                    NSArray *vals = [((__bridge NSString*)value) componentsSeparatedByString:@","];
                    if(vals.count >= 2){
                        [[NSUserDefaults standardUserDefaults] setBool:[vals[1] boolValue] forKey:@"trial_type"];
                        [[NSUserDefaults standardUserDefaults] synchronize];
                        [_THIS getExperimentData];
                    }
                }
                [_THIS emitEvent:CEventChangeTrialType];
            }
            if (action & CEventSendAllUserTrials) {
                NSMutableString *writeString = [NSMutableString stringWithCapacity:0]; //don't worry about the capacity, it will expand as necessary
                [writeString appendFormat:@"%d,User,Trial type,Trial,Left,Time taken,Right,Time taken,Trial On,Output",CEventSendAllUserTrials];
                NSArray *dataArray = _THIS.fetchedResultsController.fetchedObjects;
                for (User *user in dataArray) {
                    NSString *name = [[NSUserDefaults standardUserDefaults] objectForKey:[NSString stringWithFormat:@"user-%d",user.userId]];
                    if(!name.length){
                        name = [NSString stringWithFormat:@"User %d",user.userId];
                    }
                    for (Trial *trial in user.trials) {
                        NSString *type = trial.trialType ? (trial.trialType == 1 ? @"servo" : @"joint") : @"touch";
                        [writeString appendFormat:@"\n%@,%@,%d,%d,%f,%d,%f,%@,%@",name , type, trial.trial, trial.left, trial.leftTime, trial.right, trial.rightTime, [NSDate dateWithTimeIntervalSinceReferenceDate:trial.timeStamp], trial.selectedLeft ? @"Left" : @"Right"];
                    }
                }
                WebSocketRef websocket =  _webSocket;
                for (CFIndex i = 0; i < WebSocketGetClientCount(websocket); ++i) {
                    WebSocketClientRef client = WebSocketGetClientAtIndex(websocket, i);
                    WebSocketClientWriteWithFormat(client, CFSTR("%@"), writeString);
                }
            }
        }
    }
}
@end
