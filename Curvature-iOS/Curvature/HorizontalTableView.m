//
//  HorizontalTableView.m
//  Scroller
//
//  Created by Martin Volerich on 5/22/10.
//  Copyright 2010 Martin Volerich - Bill Bear Technologies. All rights reserved.
//

// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
// documentation files (the "Software"), to deal in the Software without restriction, including without
// limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
// the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
// conditions:
//
// The above copyright notice and this permission notice shall be included in all copies or substantial
// portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
// LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
// EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
// AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
// OR OTHER DEALINGS IN THE SOFTWARE.

#import <QuartzCore/QuartzCore.h>
#import "HorizontalTableView.h"

#define kColumnPoolSize 3

@interface HorizontalTableView() <UIScrollViewDelegate>

@property (nonatomic, retain) NSMutableArray *pageViews;
@property (nonatomic) NSUInteger physicalPageIndex;
@property (nonatomic, retain) NSMutableArray *columnPool;
@property (nonatomic, retain) NSMutableArray* pageIndicators;

- (void)prepareView;
- (void)layoutPages;
- (void)currentPageIndexDidChange;
- (NSUInteger)numberOfPages;
- (void)layoutPhysicalPage:(NSUInteger)pageIndex;
- (UIView *)viewForPhysicalPage:(NSUInteger)pageIndex;
- (void)removeColumn:(NSInteger)index;

@end


@implementation HorizontalTableView

@synthesize pageViews=_pageViews;
@synthesize scrollView=_scrollView;
@synthesize currentPageIndex=_currentPageIndex;
@synthesize delegate=_delegate;
@synthesize columnPool=_columnPool;
@synthesize pageIndicators;


- (void)refreshData {
    for (UIView* view in self.pageViews)
        if( (id)view != [NSNull null] )
            [view removeFromSuperview];
    self.pageViews = [NSMutableArray array];
	// to save time and memory, we won't load the page views immediately
	NSUInteger numberOfPhysicalPages = [self numberOfPages];
	for (NSUInteger i = 0; i < numberOfPhysicalPages; ++i)
		[self.pageViews addObject:[NSNull null]];
    
    for( unsigned int u = 0; u < [pageIndicators count]; ++u )
    {
        UIView* view = [pageIndicators objectAtIndex: u];
        [view removeFromSuperview];
    }
    [pageIndicators removeAllObjects];
    const float pageIconDim = 8;
    const float startX = (self.frame.size.width * 0.5f) - (pageIconDim * numberOfPhysicalPages);
    for( unsigned int u = 0; u < numberOfPhysicalPages; ++u )
    { 
        UIView* view = [[UIView alloc] initWithFrame: CGRectMake( startX + (pageIconDim * u * 2.5f), self.frame.size.height - (pageIconDim) -15, pageIconDim, pageIconDim )];
        view.layer.cornerRadius = pageIconDim/2.f;
        //view.layer.borderWidth = 0.25f;
        //view.layer.borderColor = [UIColor lightGrayColor].CGColor;
        [self addSubview: view];
        [pageIndicators addObject: view];
    }
    
    [self setNeedsLayout];
}

- (void)resetContentOffset
{
    self.scrollView.contentOffset = CGPointZero;
}

-(void)setHidePageIndicator:(BOOL)hide
{
    if(_hidePageIndicator!=hide){
        for (UIView *view in pageIndicators) {
            view.hidden = hide;
        }
        _hidePageIndicator = hide;
    }
}

- (NSUInteger)numberOfPages {
	NSInteger numPages = 0;
    if (_delegate)
        numPages = [_delegate numberOfColumnsForTableView:self];
    return numPages;
}

- (UIView *)viewForPhysicalPage:(NSUInteger)pageIndex {
	NSParameterAssert(pageIndex >= 0);
	NSParameterAssert(pageIndex < [self.pageViews count]);
	
	UIView *pageView = nil;
	if ([self.pageViews objectAtIndex:pageIndex] == [NSNull null]) {
        
        if (_delegate) {
            pageView = [_delegate tableView:self viewForIndex:pageIndex];
            [self.pageViews replaceObjectAtIndex:pageIndex withObject:pageView];
            if( [self.pageViews count] > 1 )
            {
                int dir = pageIndex > 0 ? 1 : -1;
                UIView* neighbourView = [self.pageViews objectAtIndex:pageIndex - dir];
                NSInteger otherNeighbour = pageIndex - (-dir);
                if( (id)neighbourView == [NSNull null] && otherNeighbour >= 0 && otherNeighbour < [self.pageViews count] )
                {
                    neighbourView = [self.pageViews objectAtIndex: otherNeighbour];
                }
                [self.scrollView insertSubview:pageView belowSubview: neighbourView];
            }
            else
            {
                [self.scrollView addSubview:pageView];
            }
            //DLog(@"View loaded for page %d", pageIndex);
        }
	} else {
		pageView = [self.pageViews objectAtIndex:pageIndex];
	}
	return pageView;
}

- (CGSize)pageSize {
    CGRect rect = self.scrollView.bounds;
	return rect.size;
}

- (CGFloat)columnWidth {
    if (!_columnWidth) {
        if (_delegate) {
            CGFloat width = [_delegate columnWidthForTableView:self];
            _columnWidth = [NSNumber numberWithFloat:width];
        }
    }
    return [_columnWidth floatValue];
    
}

- (BOOL)isPhysicalPageLoaded:(NSUInteger)pageIndex {
	return [self.pageViews objectAtIndex:pageIndex] != [NSNull null];
}

- (void)layoutPhysicalPage:(NSUInteger)pageIndex {
	UIView *pageView = [self viewForPhysicalPage:pageIndex];
    CGFloat viewWidth = pageView.bounds.size.width;
	CGSize pageSize = [self pageSize];
    
    CGRect rect = CGRectMake(viewWidth * pageIndex, 0, viewWidth, pageSize.height);
	pageView.frame = rect;
}

- (void)awakeFromNib {
    pageIndicators = [NSMutableArray array];
    [self prepareView];
}

- (void)queueColumnView:(UIView *)vw {
    if ([self.columnPool count] >= kColumnPoolSize) {
        return;
    }
    [self.columnPool addObject:vw];
}

- (UIView *)dequeueColumnView {
    UIView *vw = [self.columnPool lastObject];
    if (vw) {
        [self.columnPool removeLastObject];
        //DLog(@"Supply from reuse pool");
    }
    return vw;
}

- (void)removeColumn:(NSInteger)index {
    if ([self.pageViews objectAtIndex:index] != [NSNull null]) {
        //DLog(@"Removing view at position %d", index);
        UIView *vw = [self.pageViews objectAtIndex:index];
        [self queueColumnView:vw];
        [vw removeFromSuperview];
        [self.pageViews replaceObjectAtIndex:index withObject:[NSNull null]];
    }
}

- (void)currentPageIndexDidChange {
    CGSize pageSize = [self pageSize];
    CGFloat columnWidth = [self columnWidth];
    _visibleColumnCount = pageSize.width / columnWidth + 2;
    
    NSInteger leftMostPageIndex = -1;
    NSInteger rightMostPageIndex = 0;
    
    for (NSInteger i = -2; i < _visibleColumnCount; i++) {
        NSInteger index = _currentPhysicalPageIndex + i;
        if (index < [self.pageViews count] && (index >= 0)) {
            [self layoutPhysicalPage:index];
            if (leftMostPageIndex < 0)
                leftMostPageIndex = index;
            rightMostPageIndex = index;
        }
    }
    
    // clear out views to the left
    for (NSInteger i = 0; i < leftMostPageIndex; i++) {
        [self removeColumn:i];
    }
    
    // clear out views to the right
    for (NSInteger i = rightMostPageIndex + 1; i < [self.pageViews count]; i++) {
        [self removeColumn:i];
    }
    for( unsigned int u = 0; u < [pageIndicators count]; ++u )
    {
        ((UIView*)[pageIndicators objectAtIndex: u]).alpha = 0.5f;
    }
    bool swap = self.currentPageIndex == 0;
    [UIView animateWithDuration:swap?0.4f:0.2f animations:^{
        // set current page
        NSInteger count = [pageIndicators count];
        for( unsigned int u = 0; u < count; ++u )
        {
            UIView* view = [pageIndicators objectAtIndex: u];
            if( swap )
            {
                view.backgroundColor = u != self.currentPageIndex ? self.otherPageColor : self.currentPageColor;
            }
            else
            {
                view.backgroundColor = u != self.currentPageIndex ? self.otherPageColor : self.currentPageColor;
            }
            view.alpha = 1;
        }
    }];
}

- (void)layoutPages {
    CGSize pageSize = self.bounds.size;
	self.scrollView.contentSize = CGSizeMake([self.pageViews count] * [self columnWidth], pageSize.height);
}

- (id)init {
    self = [super init];
    if (self) {
        [self prepareView];
    }
    return self;
}

- (void)prepareView {
	_columnPool = [[NSMutableArray alloc] initWithCapacity:kColumnPoolSize];
    _columnWidth = nil;
    //[self setClipsToBounds:YES];
    
    self.autoresizesSubviews = YES;
    
    UIScrollView *scroller = [[UIScrollView alloc] init];
    CGRect rect = self.bounds;
    scroller.frame = rect;
    scroller.backgroundColor = [UIColor clearColor];
    scroller.pagingEnabled = YES;
	scroller.delegate = self;
    scroller.autoresizesSubviews = YES;
    scroller.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleRightMargin | UIViewAutoresizingFlexibleLeftMargin;
    
	scroller.showsHorizontalScrollIndicator = NO;
	scroller.showsVerticalScrollIndicator = NO;
    scroller.alwaysBounceVertical = NO;
    scroller.pagingEnabled = YES;
    scroller.bounces = NO;
    self.scrollView = scroller;
	[self addSubview:scroller];
    scroller = nil;
    
}

- (NSUInteger)physicalPageIndex {
    NSUInteger page = floorf((self.scrollView.contentOffset.x + ([_columnWidth floatValue] * 0.5f)) / [self columnWidth]);
    return page;
}

- (void)setPhysicalPageIndex:(NSUInteger)newIndex {
	self.scrollView.contentOffset = CGPointMake(newIndex * [self pageSize].width, 0);
}


#pragma mark -
#pragma mark UIScrollViewDelegate methods

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    //DLog(@"Did Scroll");
	
	NSUInteger newPageIndex = self.physicalPageIndex;
	if (newPageIndex == _currentPhysicalPageIndex) return;
	_currentPhysicalPageIndex = newPageIndex;
	_currentPageIndex = newPageIndex;
	
	[self currentPageIndexDidChange];

    [self.delegate endedScrolling];
    //CGSize rect = [self.scrollView contentSize];
    //DLog(@"CSize = %@", NSStringFromCGSize(rect));
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    return (interfaceOrientation != UIInterfaceOrientationPortraitUpsideDown);
}


- (void)layoutSubviews {
    [self layoutPages];
    [self currentPageIndexDidChange];
}

- (void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation {
	// adjust frames according to the new page size - this does not cause any visible changes
	[self layoutPages];
	self.physicalPageIndex = _currentPhysicalPageIndex;
	
	// unhide
	for (NSUInteger pageIndex = 0; pageIndex < [self.pageViews count]; ++pageIndex)
		if ([self isPhysicalPageLoaded:pageIndex])
			[self viewForPhysicalPage:pageIndex].hidden = NO;
	
    self.scrollView.contentSize = CGSizeMake([self.pageViews count] * [self columnWidth], [self pageSize].height);
    
    [self currentPageIndexDidChange];
}

- (NSUInteger)currentPageIndex
{
    return _currentPhysicalPageIndex;
}

- (void)setCurrentPageIndex:(NSUInteger)currentPageIndex
{
	if (currentPageIndex == (int)_currentPhysicalPageIndex) return;
    [UIView animateWithDuration:.4f animations:^{
        [self.scrollView scrollRectToVisible:CGRectMake(currentPageIndex * [self pageSize].width, 0, [self pageSize].width, [self pageSize].height) animated:NO];
    }];
}

@end
