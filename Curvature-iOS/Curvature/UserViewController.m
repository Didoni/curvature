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

#define MAX_TRAILS 30

@interface TCMessage : NSObject

- (id)initWithMessage:(NSString *)message fromMe:(BOOL)fromMe;

@property (nonatomic, retain, readonly) NSString *message;
@property (nonatomic, readonly)  BOOL fromMe;

@end
@interface UserViewController () {
    NSString *noiseFile;
    SRWebSocket *_webSocket;
    int trialsPlates[MAX_TRAILS * 3][2];
    UIFont *font;
    NSTimer *perSecTimer;
    
    NSUInteger maxTrials;
    NSUInteger maxTimerVal;
    NSUInteger trialType;
    AVAudioPlayer *player;
    User *currentUser;
    
    BOOL isCurrentModelA;
    BOOL runTimer;
    BOOL alertTimer;
    BOOL paused;
    BOOL shouldShowDemo;
    BOOL dualDemo;
    BOOL leftSideOpen;
    BOOL rightSideOpen;
    UILabel *trialCounter;
    NSArray *files;
}
@property (nonatomic, retain) NSArray *views;
@end

@implementation UserViewController
@synthesize start;

- (void)viewDidLoad {
    [super viewDidLoad];
    shouldShowDemo = YES;
    _rewind.enabled = NO;
    _refresh.enabled = NO;
    _pause.enabled = NO;
    _forward.enabled = YES;
    perSecTimer = nil;
    dualDemo = NO;
    memset(trialsPlates, 0, MAX_TRAILS * 6 * sizeof(int));
    trialCounter = _trialBtn.titleLabel;
    self.navigationItem.rightBarButtonItem.customView.hidden = YES;
    self.views = [[self.view viewWithTag:10] subviews];
    self.container.currentPageColor = [UICustom appColor:AppColorBlue];
    self.container.otherPageColor = [UICustom appColor:AppColorLightGray];
    [_container refreshData];
    [self.trialHistory registerClass:[UITableViewCell class] forCellReuseIdentifier:@"Cell"];
    maxTrials = [[NSUserDefaults standardUserDefaults] integerForKey:@"max_trial"];
    maxTimerVal = [[NSUserDefaults standardUserDefaults] integerForKey:@"trial_time"];
    if (maxTrials == 0) {
        [[NSUserDefaults standardUserDefaults] setInteger:24 forKey:@"max_trial"];
        [[NSUserDefaults standardUserDefaults] setInteger:20 forKey:@"trial_time"];
        maxTrials = 12;
        maxTimerVal = 20;
    }
    [self makeDial];
    currentUser = ((User *)self.fetchedResultsController.fetchedObjects.lastObject);
    if (currentUser.trials.count) {
        [self changeTrialType:((Trial *)currentUser.trials.lastObject).trialType];
    }
    if (currentUser.trials.count >= maxTrials * 3) {
        [self changeUser];
    }
    isCurrentModelA = YES;
    _left.alpha = 0;
    _right.alpha = 0;
    [_trialBtn setTitle:nil forState:UIControlStateNormal];
    font = [UIFont systemFontOfSize:42.0f];
    start.titleLabel.font = font;
    start.titleLabel.frame = start.frame;
    [self _reconnect];
    [self appDidBecomeActive:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(appDidBecomeActive:) name:UIApplicationDidBecomeActiveNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(appDidBecomeInactive:) name:UIApplicationWillResignActiveNotification object:nil];
    [SoundManager sharedManager].allowsBackgroundMusic = YES;
    [[SoundManager sharedManager] prepareToPlay];
    [SoundManager sharedManager].musicVolume = 1;
    //[SoundManager sharedManager].soundVolume = 1;
}

- (void)changeTrialType:(int)ltrialType {
    if (ltrialType != trialType) {
        if (ltrialType) {
            [[SoundManager sharedManager] playSound:@"ding"];
        }
        trialType = ltrialType;
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

- (void)_reconnect;
{
    BOOL canConnect = [[NSUserDefaults standardUserDefaults] objectForKey:@"connect"];
    NSString *server = [[NSUserDefaults standardUserDefaults] objectForKey:@"remote_host_url"];
    if (canConnect && server.length) {
        NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"ws://%@", server]];
        if (url) {
            _webSocket.delegate = nil;
            [_webSocket close];
            _webSocket = [[SRWebSocket alloc] initWithURLRequest:[NSURLRequest requestWithURL:url]];
            _webSocket.delegate = self;
            [_webSocket open];
        }
        else {
            [[[UIAlertView alloc] initWithTitle:@"Server settings are wrong!" message:nil delegate:nil cancelButtonTitle:nil otherButtonTitles:@"Model Left", @"Model Right", nil] show];
        }
    }
}

- (void)perSecondUpdate {
    if (runTimer) {
        _timerView.progressCounter += 1;
        if (_timerView.progressCounter == _timerView.progressTotal) {
            [[SoundManager sharedManager] playSound:@"tong"];
            [self doneCounting];
            [self stopTimer];
        }
    }
}

- (void)doneCounting {
    if (!isCurrentModelA) {
        NSString *one, *two;
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
        [_trialHistory reloadData];
        [_trialHistory scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:currentUser.trials.count - 1 inSection:0]
                             atScrollPosition:UITableViewScrollPositionMiddle animated:NO];
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
    [[SoundManager sharedManager] playSound:@"ding"];
    btn.hidden = YES;
    runTimer = YES;
    uint cur = (uint)_container.currentPageIndex;
    _pause.enabled = YES;
    if (cur == 8) {
        _refresh.enabled = YES;
        UILabel *model = isCurrentModelA ? _left : _right;
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
    if (_container.currentPageIndex == 8) {
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
                if (currentUser.trials.count >= (maxTrials * 3)) {
                    NSUInteger userType = [[NSUserDefaults standardUserDefaults] integerForKey:@"trial_user"];
                    [self changeTrialType:0];
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
                    [self changeTrialType:(int)(currentUser.trials.count / maxTrials)];
                    [_trialBtn setTitle:[NSString stringWithFormat:@"Trial %2ld", (currentUser.trials.count % maxTrials) + 1] forState:UIControlStateNormal];
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
    }
    else {
        if (buttonIndex) {
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
        if (!currentUser || userTrialCount == 3 * maxTrials) {
            newUser = YES;
            currentUser = [NSEntityDescription insertNewObjectForEntityForName:@"User" inManagedObjectContext:context];
            currentUser.userId = currentUser.userId + 1;
        }
        else {
            currentUser = (User *)self.fetchedResultsController.fetchedObjects.lastObject;
        }
        
        Trial *trial = [NSEntityDescription insertNewObjectForEntityForName:@"Trial" inManagedObjectContext:context];
        trial.timeStamp = [[NSDate date] timeIntervalSinceReferenceDate];
        uint index = (uint)((userTrialCount) % (maxTrials * 3));
        trial.trial = index + 1;
        trial.trialType = trialType;
        trial.selectedLeft = selctedLeft;
        trial.left = trialsPlates[index][0];
        trial.right = trialsPlates[index][1];
        
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
    [self changeTrialType:0];
    [_trialBtn setTitle:@"Trial 1" forState:UIControlStateNormal];
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
    
    int ltrialType = (int)[[NSUserDefaults standardUserDefaults] integerForKey:@"trial_type"];
    [self changeTrialType:ltrialType];
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
    _trialType.hidden = (cur != 8);
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
    _left.alpha = 0;
    _right.alpha = 0;
    _timerView.theme.completedColor = cur == 8 ? [UIColor clearColor] : [UICustom appColor:AppColorLiteOrange];
    if (cur != 4) {
        isCurrentModelA = YES;
        _timerView.theme.incompletedColor = [UICustom appColor:AppColorBlue];
        [_trialBtn setTitle:[NSString stringWithFormat:@"Trial %2ld", (cur == 8) ? (currentUser.trials.count % maxTrials) + 1 : 1] forState:UIControlStateNormal];
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

- (void)webSocketDidOpen:(SRWebSocket *)webSocket;
{
    NSLog(@"Websocket Connected");
}

- (void)webSocket:(SRWebSocket *)webSocket didFailWithError:(NSError *)error;
{
    NSLog(@":( Websocket Failed With Error %@", error);
    _webSocket = nil;
}

- (void)webSocket:(SRWebSocket *)webSocket didReceiveMessage:(id)message;
{
    [[[UIAlertView alloc] initWithTitle:@"Server says!" message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
    NSLog(@"Received \"%@\"", message);
}

- (void)webSocket:(SRWebSocket *)webSocket didCloseWithCode:(NSInteger)code reason:(NSString *)reason wasClean:(BOOL)wasClean;
{
    NSLog(@"WebSocket closed");
    _webSocket = nil;
}

- (void)webSocket:(SRWebSocket *)webSocket didReceivePong:(NSData *)pongPayload;
{
    NSLog(@"Websocket received pong");
}
#pragma mark - Trial history Tableview
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return tableView == _trialHistory ? maxTrials * 3 : 4 + files.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Cell" forIndexPath:indexPath];
    int row = (int)indexPath.row;
    if (tableView == _trialHistory) {
        int type = 0;
        if (row < currentUser.trials.count) {
            Trial *trial = currentUser.trials[row];
            type  = trial.trialType;
            cell.textLabel.textColor = [UIColor whiteColor];
            cell.backgroundColor = [UICustom appColor:trial.selectedLeft ? AppColorBlue : AppColorGreen];
            cell.imageView.image = [UIImage imageNamed:type ? (type == 1 ? @"servoWhite" : @"jointWhite") : @"tap"];
        }
        else {
            cell.textLabel.textColor = [UIColor darkTextColor];
            cell.backgroundColor = [UIColor whiteColor];
            type = row / maxTrials;
            cell.imageView.image = [UIImage imageNamed:type ? (type == 1 ? @"servo" : @"joint") : @"hand"];
        }
        cell.textLabel.text = [NSString stringWithFormat:@"%d, %d", trialsPlates[row][0], trialsPlates[row][1]];
    }
    else {
        static NSString *icons[4] = { @"metal", @"punk", @"rock", @"electric" };
        static NSString *names[4] = { @"Silence", @"Random 1", @"Random 2", @"No music" };
        cell.imageView.image = [UIImage imageNamed:icons[row % 4]];
        cell.textLabel.text = row < files.count?files[row]:[names[(row - files.count)%4]  stringByReplacingOccurrencesOfString:@".mp3" withString:@""];
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
    NSUInteger lmaxrials = [[NSUserDefaults standardUserDefaults] integerForKey:@"max_trial"];
    NSUInteger lmaxTimerVal = [[NSUserDefaults standardUserDefaults] integerForKey:@"trial_time"];
    BOOL reset = [[NSUserDefaults standardUserDefaults] boolForKey:@"reset"];
    if (reset) {
        [[NSUserDefaults standardUserDefaults] setBool:NO forKey:@"reset"];
        [[NSUserDefaults standardUserDefaults] synchronize];
    }
    if (lmaxrials > 0 && lmaxTimerVal > 0 && (lmaxrials != maxTimerVal || lmaxrials != maxTrials)) {
        maxTrials = lmaxrials;
        maxTimerVal = lmaxTimerVal;
        _container.currentPageIndex = 0;
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
    }
    if (trialsPlates[0][0] == 0) {
        uint st = arc4random() % (maxTrials + 1);
        NSMutableArray *array = [NSMutableArray new];
        for (int i = 0; i < maxTrials + 1; i++) {
            if (i != st) {
                [array addObject:@(i)];
            }
        }
        
        for (int i = 0; i < maxTrials; i++) {
            uint r = arc4random() % array.count;
            uint val = [[array objectAtIndex:r] intValue] + 1;
            uint val2 = st + 1;
            BOOL lr = arc4random() % 2;
            trialsPlates[i][lr] = val2;
            trialsPlates[i][1 - lr] = val;
            trialsPlates[i + maxTrials][lr] = val2;
            trialsPlates[i + maxTrials][1 - lr] = val;
            trialsPlates[i + maxTrials * 2][lr] = val2;
            trialsPlates[i + maxTrials * 2][1 - lr] = val;
            [array removeObject:[array objectAtIndex:r]];
        }
    }
    [_trialHistory reloadData];
    [self _reconnect];
    [perSecTimer invalidate];
    perSecTimer = [NSTimer scheduledTimerWithTimeInterval:1 target:self selector:@selector(perSecondUpdate) userInfo:nil repeats:YES];
    [self searchForMusic];
    [_audioFiles reloadData];
}

- (void)appDidBecomeInactive:(NSNotification *)notification {
    paused = YES;
}

- (void)searchForMusic {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSArray *filePathsArray = [[NSFileManager defaultManager] subpathsOfDirectoryAtPath:documentsDirectory error:nil];
    NSMutableArray *array = [NSMutableArray new];
    for (NSString *filePath in filePathsArray) {
        if ([[filePath pathExtension] isEqualToString:@"mp3"]) {
            [array addObject:[filePath lastPathComponent]];
        }
    }
    if (array.count) {
        files = [NSArray arrayWithArray:array];
    }
}

@end

@implementation TCMessage

@synthesize message = _message;
@synthesize fromMe = _fromMe;

- (id)initWithMessage:(NSString *)message fromMe:(BOOL)fromMe;
{
    self = [super init];
    if (self) {
        _fromMe = fromMe;
        _message = message;
    }
    
    return self;
}

@end
