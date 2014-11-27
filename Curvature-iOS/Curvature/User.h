//
//  User.h
//  Curvature
//
//  Created by Shailesh Prakash on 23/11/2014.
//  Copyright (c) 2014 MyOxygen. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Trial;

@interface User : NSManagedObject

@property (nonatomic) int16_t userId;
@property (nonatomic, retain) NSOrderedSet *trials;
@end

@interface User (CoreDataGeneratedAccessors)

- (void)insertObject:(Trial *)value inTrialsAtIndex:(NSUInteger)idx;
- (void)removeObjectFromTrialsAtIndex:(NSUInteger)idx;
- (void)insertTrials:(NSArray *)value atIndexes:(NSIndexSet *)indexes;
- (void)removeTrialsAtIndexes:(NSIndexSet *)indexes;
- (void)replaceObjectInTrialsAtIndex:(NSUInteger)idx withObject:(Trial *)value;
- (void)replaceTrialsAtIndexes:(NSIndexSet *)indexes withTrials:(NSArray *)values;
- (void)addTrialsObject:(Trial *)value;
- (void)removeTrialsObject:(Trial *)value;
- (void)addTrials:(NSOrderedSet *)values;
- (void)removeTrials:(NSOrderedSet *)values;
@end
