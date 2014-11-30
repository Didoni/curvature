//
//  VRPN.mm
//  VRPNClient
//
//  Created by Daniel Wilches on 9/24/10.
//  Copyright 2010. All rights reserved.
//
//  You can modify this file as long as you let the information from where it was downloaded:
//     - http://wwwest.uniandes.edu.co/~d.wilches52/
//     - http://dwilches.blogspot.com/
//

#import "VRPN.h"
#import "vrpn_Tracker.h"

// This function is called once for every incoming VRPN tracker update
void VRPN_CALLBACK handle_tracker(void *userData, const vrpn_TRACKERCB a);

@implementation VRPN {
    NSString *host;
    NSUInteger refreshRateInMillisec;
}

static VRPN *vrpnClient = NULL;
- (VRPN *)vrpn_client {
    return vrpnClient;
}

- (void)checkVRPN {
}

+ (VRPN *)instance {
    if (!vrpnClient) {
        BOOL vrpn = [[NSUserDefaults standardUserDefaults] boolForKey:@"vrpn"];
        if (vrpn) {
            if (!vrpnClient) {
                vrpnClient = [[VRPN alloc] initWithHost:[[NSUserDefaults standardUserDefaults] stringForKey:@"remote_vrpn_host"]
                                        andRefreshRate :[[NSUserDefaults standardUserDefaults] integerForKey:@"vrpn_refresh"]];
            }
            [vrpnClient startListening];
        }
        else if (vrpnClient) {
            [vrpnClient stopListening];
        }
    }
    return vrpnClient;
}

// Tracker listener reference
vrpn_Tracker_Remote *vrpnTracker;

// Default initializer
- (id)initWithHost:(NSString *)_host andRefreshRate:(NSUInteger)_refreshRateInMillisec {
    host = _host;
    refreshRateInMillisec = MAX(10, MIN(60000, _refreshRateInMillisec));
    dispatchSource = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0, 0,
                                            dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0));
    // Setup params for creation of a recurring timer
    double interval = refreshRateInMillisec;
    dispatch_time_t startTime = dispatch_time(DISPATCH_TIME_NOW, 0);
    uint64_t intervalTime = (int64_t)(interval * NSEC_PER_MSEC);
    dispatch_source_set_timer(dispatchSource, startTime, intervalTime, 0);
    // Attach the block you want to run on the timer fire
    dispatch_source_set_event_handler(dispatchSource, ^{
        // Your code here
        vrpnTracker->mainloop();
        static float x = 0, y = 0;
        if (x != rx | y != ry) {
            dispatch_async(dispatch_get_main_queue(), ^{
            });
        }
    });
    return self;
}

// Starts polling for events
- (void)startListening {
    // Avoid problems when the programmer tries to listen twice at the same VRPN device
    if (vrpnTracker != NULL) {
        NSLog(@"Client is already listening for VRPN events.");
        return;
    }
    if (host) {
        vrpnTracker = new vrpn_Tracker_Remote([host cStringUsingEncoding:NSUTF8StringEncoding]);
        if (vrpnTracker != NULL) {
            vrpnTracker->register_change_handler(0, handle_tracker);
            dispatch_resume(dispatchSource);
            NSLog(@"Client is now listening for VRPN events.");
        }
    }
}

// Stops polling for events
- (void)stopListening {
    // Avoid trying to stop listening the same VRPN device twice.
    if (vrpnTracker == NULL)
        return;
    
    // First, invalidate the timer so it triggers no more.
    dispatch_suspend(dispatchSource);
    // Unregister the VRPN callback so we receive no further notifications
    vrpnTracker->unregister_change_handler(0, handle_tracker);
    delete vrpnTracker;
    vrpnTracker = NULL;
    NSLog(@"Client has stopped listening for VRPN events.");
}

@end
class Instruction
{
public:
    float getPhi() {
        Instruction instr = *this; return instr.phi;
    }
    
    float getTheta() {
        Instruction instr = *this; return instr.theta;
    }
    
    void setPhi(float input) {
        phi = input;
    }
    
    void setTheta(float input) {
        theta = input;
    }
    
private:
    float phi;
    float theta;
};

class Trial
{
public:
    int getReal() {
        Trial instr = *this; return instr.real;
    }
    
    int getVirt() {
        Trial instr = *this; return instr.virt;
    }
    
    void setVirt(int input) {
        virt = input;
    }
    
    void setReal(int input) {
        real = input;
    }
    
private:
    int real;
    int virt;
};
static float getRoll(float x, float y, float z, float w, bool reprojectAxis) {
    if (reprojectAxis) {
        // roll = atan2(localx.y, localx.x)
        // pick parts of xAxis() implementation that we need
        //			float fTx  = 2.0*x;
        float fTy  = 2.0f * y;
        float fTz  = 2.0f * z;
        float fTwz = fTz * w;
        float fTxy = fTy * x;
        float fTyy = fTy * y;
        float fTzz = fTz * z;
        
        // Vector3(1.0-(fTyy+fTzz), fTxy+fTwz, fTxz-fTwy);
        
        return float(atan2(fTxy + fTwz, 1.0f - (fTyy + fTzz)));
    }
    else {
        return float(atan2(2 * (x * y + w * z), w * w + x * x - y * y - z * z));
    }
}

//-----------------------------------------------------------------------
static float getPitch(float x, float y, float z, float w,  bool reprojectAxis) {
    if (reprojectAxis) {
        // pitch = atan2(localy.z, localy.y)
        // pick parts of yAxis() implementation that we need
        float fTx  = 2.0f * x;
        //			float fTy  = 2.0f*y;
        float fTz  = 2.0f * z;
        float fTwx = fTx * w;
        float fTxx = fTx * x;
        float fTyz = fTz * y;
        float fTzz = fTz * z;
        
        // Vector3(fTxy-fTwz, 1.0-(fTxx+fTzz), fTyz+fTwx);
        return float(atan2(fTyz + fTwx, 1.0f - (fTxx + fTzz)));
    }
    else {
        // internal version
        return float(atan2(2 * (y * z + w * x), w * w - x * x - y * y + z * z));
    }
}

//-----------------------------------------------------------------------
static float getYaw(float x, float y, float z, float w,  bool reprojectAxis) {
    if (reprojectAxis) {
        // yaw = atan2(localz.x, localz.z)
        // pick parts of zAxis() implementation that we need
        float fTx  = 2.0f * x;
        float fTy  = 2.0f * y;
        float fTz  = 2.0f * z;
        float fTwy = fTy * w;
        float fTxx = fTx * x;
        float fTxz = fTz * x;
        float fTyy = fTy * y;
        
        // Vector3(fTxz+fTwy, fTyz-fTwx, 1.0-(fTxx+fTyy));
        
        return float(atan2(fTxz + fTwy, 1.0f - (fTxx + fTyy)));
    }
    else {
        // internal version
        return float(asin(-2 * (x * z - w * y)));
    }
}

static Instruction angleArray[91][91][13];
static Trial sequenceArray[40];
static float mPitch = 0;
static float mRoll = 0;
static float mYaw = 0;

// This function is called once for every incoming VRPN tracker update
void VRPN_CALLBACK handle_tracker(void *userData, const vrpn_TRACKERCB t) {
    //printf("Tracker Position:(%.4f,%.4f,%.4f) Orientation:(%.2f,%.2f,%.2f,%.2f)\n",t.pos[0], t.pos[1], t.pos[2],t.quat[0], t.quat[1], t.quat[2], t.quat[3]);
    
    const float pitch = getPitch(t.quat[0], t.quat[1], t.quat[2], t.quat[3], false) * 180.0f / M_PI;
    const float roll = getRoll(t.quat[0], t.quat[1], t.quat[2], t.quat[3], false) * 180.0f / M_PI;
    const float yaw = getYaw(t.quat[0], t.quat[1], t.quat[2], t.quat[3], false) * 180.0f / M_PI;
    
    mPitch = pitch;
    mRoll = roll;
    mYaw = yaw;
    
#ifdef EXPERIMENT_3
    fprintf(fileExp3, "%ld,%f,%f,%f,%f\n", getMillisTime(), rx, ry, mPitch, mRoll);
    
    float coordinateX = t.pos[0] - 0.0084;
    float coordinateZ = t.pos[2] + 0.0107;
    findCurve(coordinateZ, coordinateX, curvatureNum);
#endif
}

void findCurve(float coordinateX, float coordinateZ, int curvature) {
    float thetaAngle, phiAngle;
    float r = 500;
    int x = floor(1000 * (coordinateX) + 0.5);
    int z = floor(1000 * (coordinateZ) + 0.5);
    if (x > 90) x = 90;
    if (x < -90) x = -90;
    if (z > 90) z = 90;
    if (z < -90) z = -90;
    
    
    if (z < 0) thetaAngle = 0 + angleArray[abs(x)][-z][curvature].getTheta();
    else thetaAngle = 0 - angleArray[abs(x)][z][curvature].getTheta();
    if (x < 0) phiAngle = 0 + angleArray[-x][abs(z)][curvature].getPhi();
    else phiAngle = 0 - angleArray[x][abs(z)][curvature].getPhi();
    
    rx = thetaAngle;
    ry = phiAngle;
    int offsetTheta = 4;
    int offsetPhi = 11;
    thetaAngle = thetaAngle + offsetTheta;
    phiAngle = phiAngle + offsetPhi;
    
    int sx, sy;
    //mInterp->query(thetaAngle, phiAngle, sx, sy);
    //std::cout << "x " << x << " z " << z << " Theta " << (thetaAngle - offsetTheta) << " phi " << (phiAngle - offsetPhi) << " Sx" << sx << " Sy " << sy << endl;
    //sendBytesTest((short)sy, (short)sx);
}
