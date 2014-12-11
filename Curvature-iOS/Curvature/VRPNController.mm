//
//  VRPNController.m
//  Curvature
//
//  Created by Shailesh Prakash on 03/12/2014.
//  Copyright (c) 2014 MyOxygen. All rights reserved.
//

#import "VRPNController.h"
#import <sys/time.h>
#import "vrpn/vrpn_Tracker.h"


#define PI 3.14159265
using namespace std;

#define MAX_ANGLE 12
#define DIFFERENCE_H 0.001
#define MIN_SERVO_VALUE 1250
#define MILLIS_PER_TRIAL 20000

//#define EXPERIMENT_1 1
//#define EXPERIMENT_2 1
#define EXPERIMENT_3 1
//#define EVALUATION_1 1

//#define EXPERIMENT_KEYBOARD 1


#ifdef EXPERIMENT_3
static MultiVariableInterp2D *mInterpMars;
static MultiVariableInterp2D *mInterpEarth;
static MultiVariableInterp2D *mInterpJupiter;
static MultiVariableInterp2D *mInterpSaturn;
static FILE *fileExp3;
#endif

#ifdef EVALUATION_1
static MultiVariableInterp2D *mInterpMars;
static MultiVariableInterp2D *mInterpEarth;
static MultiVariableInterp2D *mInterpJupiter;
static MultiVariableInterp2D *mInterpSaturn;
static FILE *positionTracking = NULL;
static FILE *trialsFile = NULL;

std::vector <int> pairA;
std::vector <int> pairB;
int amountOfTrials = 0;

static int id;
#define NAME_LENGTH 32
static char eName[NAME_LENGTH];
enum eCondition {
    conditionRealOnlyCapture = 1,
    conditionVirtualNoAskFS = 2,
    conditionReal = 3,
    conditionVirtual = 4
};
static eCondition condition = conditionRealOnlyCapture;
static int trial = 0;
static bool firstPair = true;

static int timerCount = 0;
static long timerNextPrint = 0;
static long timerLimit = 0;

#endif


float coordinateX = 0, coordinateZ = 0;
static float mPitch = 0;
static float mRoll = 0;
static float mYaw = 0;
static float rx = 0;
static float ry = 0;

static float zoffsets[4] = { 0.025, 0.0, -0.025, -0.05 };
static float xoffsets[4] = { -0.01, 0.0, -0.01, -0.03 };

//1 to 11
static const float cNumberToCurv[] = { -1.4, -0.6, -0.4, 0, 0.4, 0.6, 1.4 };
void VRPN_CALLBACK handle_pos(void *, const vrpn_TRACKERCB t);


long getMillisTime() {
    timeval time;
    gettimeofday(&time, NULL);
    return (time.tv_sec * 1000) + (time.tv_usec / 1000);
}

void rotateAroundOrigin(float angle, float& x, float& y) {
    const float cos = cosf(angle);
    const float sin = sinf(angle);
    const float newX = cos * x - sin * y;
    const float newY = sin * x + cos * y;
    x = newX;
    y = newY;
}

void sendBytesTest(short theta, short fi) {
    COMToolkit::sendByte((theta >> 8) & 255);
    COMToolkit::sendByte(theta & 255);
    COMToolkit::sendByte((fi >> 8) & 255);
    COMToolkit::sendByte(fi & 255);
}

void send4ServosPackedIn9Bytes(short t0, short f0, short t1, short f1, short t2, short f2, short t3, short f3) {
    //printf("Values before %d %d %d %d %d %d %d %d\n", t0, f0, t1, f1, t2, f2, t3, f3);
    
    t0 -= MIN_SERVO_VALUE; f0 -= MIN_SERVO_VALUE;
    t1 -= MIN_SERVO_VALUE; f1 -= MIN_SERVO_VALUE;
    t2 -= MIN_SERVO_VALUE; f2 -= MIN_SERVO_VALUE;
    t3 -= MIN_SERVO_VALUE; f3 -= MIN_SERVO_VALUE;
    
    unsigned char significantBits = 0;
    significantBits |= (t0 & (1 << 8)) >> 8;
    significantBits |= (f0 & (1 << 8)) >> 7;
    significantBits |= (t1 & (1 << 8)) >> 6;
    significantBits |= (f1 & (1 << 8)) >> 5;
    significantBits |= (t2 & (1 << 8)) >> 4;
    significantBits |= (f2 & (1 << 8)) >> 3;
    significantBits |= (t3 & (1 << 8)) >> 2;
    significantBits |= (f3 & (1 << 8)) >> 1;
    
    COMToolkit::sendByte(t0 & 255); COMToolkit::sendByte(f0 & 255);
    COMToolkit::sendByte(t1 & 255); COMToolkit::sendByte(f1 & 255);
    COMToolkit::sendByte(t2 & 255); COMToolkit::sendByte(f2 & 255);
    COMToolkit::sendByte(t3 & 255); COMToolkit::sendByte(f3 & 255);
    COMToolkit::sendByte(significantBits);
}

void handshake() {
    short key = 255;
    COMToolkit::sendByte(key & 255);
}

float sphereFunction(float coordX, float coordZ, float curvature) {
    const float radious = 1.0f / curvature;
    const float r2 = radious * radious;
    
    return sqrtf(r2 - coordX * coordX - coordZ * coordZ);
}

float calcFiniteDiference(float coordX, float coordZ, float rotation, float xOffset, float zOffset, float param, float (*func)(float, float, float)) {
    float p0x = xOffset;
    float p0y = zOffset;
    float p1x =  -xOffset;
    float p1y =  -zOffset;
    
    rotateAroundOrigin(rotation, p0x, p0y);
    rotateAroundOrigin(rotation, p1x, p1y);
    
    p0x += coordX;
    p0y += coordZ;
    p1x += coordX;
    p1y += coordZ;
    
    const float p0 = func(p0x, p0y, param);
    const float p1 = func(p1x, p1y, param);
    return (p0 - p1) / (xOffset + zOffset) / 2.0f;
    
    /*
     const float p0 = func(coordX + xOffset*2, coordZ + zOffset*2, param);
     const float p1 = func(coordX + xOffset, coordZ + zOffset, param);
     const float p2 = func(coordX - xOffset, coordZ - zOffset, param);
     const float p3 = func(coordX - xOffset*2, coordZ - zOffset*2, param);
     return (-p0 + 8*p1 - 8*p2 + p3) / (xOffset+zOffset) / 12.0f;
     */
}

void findAnglesFinite(float coordX, float coordZ, float rotation, float& phi, float& theta, float curvature) {
    float sign = 1.0f;
    
    if (curvature == 0.0f) {
        phi = 0.0f;
        theta = 0.0f;
        return;
    }
    else if (curvature < 0.0f) {
        curvature = -curvature;
        sign = -1.0f;
    }
    
    phi = sign * -atanf(calcFiniteDiference(coordX, coordZ, rotation, DIFFERENCE_H, 0, curvature, sphereFunction)) * 180.f / PI;
    theta = sign * atanf(calcFiniteDiference(coordX, coordZ, rotation, 0, DIFFERENCE_H, curvature, sphereFunction)) * 180.f / PI;
    
    int offsetTheta = 0;
    int offsetPhi = 0;
    theta = theta + offsetTheta;
    phi = phi + offsetPhi;
    
    if (theta > MAX_ANGLE) {
        theta = MAX_ANGLE;
    }
    else if (theta < -MAX_ANGLE) {
        theta = -MAX_ANGLE;
    }
    if (phi > MAX_ANGLE) {
        phi = MAX_ANGLE;
    }
    else if (phi < -MAX_ANGLE) {
        phi = -MAX_ANGLE;
    }
}

void findCurve(float curv) {
    float xTemp;
    float zTemp;
    float thetaPlanets[4] = { 0, 0, 0, 0 };//Sat, Mars, Earth, Jup
    float phiPlanets[4] = { 0, 0, 0, 0 };
    
    const float rotRad = mYaw / 180.0f * PI;
    
    if (curv != 0) {
        for (int i = 0; i < 4; i++) {
            xTemp = xoffsets[i];
            zTemp = zoffsets[i];
            rotateAroundOrigin(rotRad, xTemp, zTemp);
            xTemp += coordinateX;
            zTemp += coordinateZ;
            
            findAnglesFinite(xTemp, zTemp, rotRad, phiPlanets[i], thetaPlanets[i], curv);
        }
    }
    
    rx = thetaPlanets[1];
    ry = phiPlanets[1];
    
    int sxJ, syJ, sxE, syE, sxM, syM, sxS, syS;
    
    mInterpJupiter->query(phiPlanets[3], thetaPlanets[3], sxJ, syJ);
    mInterpEarth->query(phiPlanets[2], thetaPlanets[2], sxE, syE);
    mInterpMars->query(phiPlanets[1], thetaPlanets[1], sxM, syM);
    mInterpSaturn->query(phiPlanets[0], thetaPlanets[0], sxS, syS);
    
    //cout<<"x "<<coordinateX<<" z "<<coordinateZ<<" Theta "<<thetaPlanets[2]<<" phi "<<phiPlanets[2]<<" Sx"<<sxE<<" Sy "<<syE<<endl;
    //cout<<"x "<<coordinateX<<" z "<<coordinateZ<<endl;
    //cout<<"Sx "<<sxJ<<" , "<<sxE<<" , "<<sxM<<" , "<<sxS<<" Sz "<<syJ<<" , "<<syE<<" , "<<syM<<" , "<<syS<<endl;
    //cout<<"tt "<<phiPlanets[1]<<" "<<thetaPlanets[1]<<"Jup "<<sxJ<<" "<<syJ<<" Earth "<<sxE<<" "<<syE<<" Mars "<<sxM<<" "<<syM<<" Sat "<<sxS<<" "<<syS<<endl;
    
    handshake();
    /*
     sendBytesTest((short)sxJ,(short)syJ);
     sendBytesTest((short)sxE,(short)syE);
     sendBytesTest((short)sxM,(short)syM);
     sendBytesTest((short)sxS,(short)syS);
     */
    send4ServosPackedIn9Bytes(sxJ, syJ, sxE, syE, sxM, syM, sxS, syS);
}

static float getRoll(float x, float y, float z, float w, bool reprojectAxis) {
    if (reprojectAxis) {
        float fTy  = 2.0f * y;
        float fTz  = 2.0f * z;
        float fTwz = fTz * w;
        float fTxy = fTy * x;
        float fTyy = fTy * y;
        float fTzz = fTz * z;
        
        return float(atan2(fTxy + fTwz, 1.0f - (fTyy + fTzz)));
    }
    else {
        return float(atan2(2 * (x * y + w * z), w * w + x * x - y * y - z * z));
    }
}

//-----------------------------------------------------------------------
static float getPitch(float x, float y, float z, float w,  bool reprojectAxis) {
    if (reprojectAxis) {
        float fTx  = 2.0f * x;
        float fTz  = 2.0f * z;
        float fTwx = fTx * w;
        float fTxx = fTx * x;
        float fTyz = fTz * y;
        float fTzz = fTz * z;
        
        return float(atan2(fTyz + fTwx, 1.0f - (fTxx + fTzz)));
    }
    else {
        return float(atan2(2 * (y * z + w * x), w * w - x * x - y * y + z * z));
    }
}

//-----------------------------------------------------------------------
static float getYaw(float x, float y, float z, float w,  bool reprojectAxis) {
    if (reprojectAxis) {
        float fTx  = 2.0f * x;
        float fTy  = 2.0f * y;
        float fTz  = 2.0f * z;
        float fTwy = fTy * w;
        float fTxx = fTx * x;
        float fTxz = fTz * x;
        float fTyy = fTy * y;
        
        return float(atan2(fTxz + fTwy, 1.0f - (fTxx + fTyy)));
    }
    else {
        // internal version
        return float(asin(-2 * (x * z - w * y)));
    }
}

void VRPN_CALLBACK handle_pos(void *, const vrpn_TRACKERCB t) {
    //printf("Tracker Position:(%.4f,%.4f,%.4f) Orientation:(%.2f,%.2f,%.2f,%.2f)\n",t.pos[0], t.pos[1], t.pos[2],t.quat[0], t.quat[1], t.quat[2], t.quat[3]);
    
    const float pitch = getPitch(t.quat[0], t.quat[1], t.quat[2], t.quat[3], false) * 180.0f / PI;
    const float roll = getRoll(t.quat[0], t.quat[1], t.quat[2], t.quat[3], false) * 180.0f / PI;
    const float yaw = getYaw(t.quat[0], t.quat[1], t.quat[2], t.quat[3], false) * 180.0f / PI;
    
    mPitch = pitch;
    mRoll = roll;
    mYaw = yaw;
    coordinateX = t.pos[2] + 0.0146991f;
    coordinateZ = t.pos[0] - 0.0097213f;
    
#ifdef EVALUATION_1
    //time stamp x z y, rz rx ry
    if (positionTracking != NULL) { //multiple access problems?
        fprintf(positionTracking, "%ld,%f,%f,%f,%f,%f,%f\n", getMillisTime(), t.pos[2], t.pos[0], t.pos[1], mYaw, mPitch, mRoll);
    }
#endif
    
#ifdef EXPERIMENT_3
    fprintf(fileExp3, "%ld,%f,%f,%f,%f\n", getMillisTime(), rx, ry, mPitch, mRoll);
    //printf("%ld,%f,%f,%f,%f\n", getMillisTime(), rx, ry, mPitch, mRoll);
#endif
}

float interp(float a, float b, float p) {
    return a + (b - a) * p;
}

void initRandomSeed() {
    srand(time(NULL));
}

int intRandom(int min, int max) {
    return min + rand() % (max - min);
}

float unitRandom() {
    return (float)rand() / (float)RAND_MAX;
}

float rangeRandom(float fLow, float fHigh) {
    return (fHigh - fLow) * unitRandom() + fLow;
}

class Point2D
{
public:
    int x, y;
    inline Point2D(int px, int py) : x(px), y(py) {
    }
};


void readFingerOffsets() {
    const int nFingers = 4;
    int holes[4];
    for (int i = 0; i < nFingers; ++i) {
        printf("Holes below %d: ", (i + 1));
        scanf("%d", &holes[i]);
    }
    for (int i = 0; i < nFingers; ++i) {
        xoffsets[i] += (holes[i] - holes[1]) * 0.005f;
    }
}

#ifdef EVALUATION_1

void closePositionTrackingFile() {
    if (positionTracking != NULL) {
        FILE *aux = positionTracking;
        positionTracking = NULL;
        fclose(aux);
    }
}

void openPositionTrackingFile() {
    closePositionTrackingFile();
    
    char tmpStr[256];
    sprintf(tmpStr, "%s_%d_%d_pos_%d_%s.txt", eName, id, condition, trial, firstPair ? "first" : "second");
    positionTracking = fopen(tmpStr, "w");
}

void readPairsFile(const char *pairsFile, int useValue) {
    pairA.clear();
    pairB.clear();
    
    FILE *f = fopen(pairsFile, "rb");
    if (f == NULL) {
        printf("ERROR opening file %s", pairsFile);
        return;
    }
    
    int exp, a, b;
    
    while (fscanf(f, "%d,%d,%d\n", &exp, &a, &b) > 0) {
        if (exp == useValue) {
            pairA.push_back(a);
            pairB.push_back(b);
        }
    }
    amountOfTrials = pairA.size();
    
    fclose(f);
}

void startCountDown() {
    timerCount = 0;
    long t = getMillisTime();
    timerNextPrint = t;
    timerLimit = t + MILLIS_PER_TRIAL;
}

//returns false if the countdown should be stopped, additionally it places the remainingMillis (if any) to finish
bool tickCountDown(long& remainingMillis, bool& keyPressed) {
    keyPressed = false;
    long t = getMillisTime();
    if (t > timerNextPrint) {
        printf("%d..", timerCount);
        timerCount++;
        timerNextPrint += 1000;
    }
    
    bool enterPressed = false;
    if (kbhit()) {
        int code = _getch();
        if (code == 13) {//Enter key
            enterPressed = true;
            keyPressed = true;
        }
    }
    
    if (t >= timerLimit || enterPressed) {
        long diff = timerLimit - t;
        if (diff < 0) {
            diff = 0;
        }
        remainingMillis = diff;
        return false;
    }
    
    return true;
}

int getKeyWithoutEnter() {
    return _getch();
}

#endif



int Main(int argc, char *argv[]) {
    initRandomSeed();
    
    vrpn_Connection *connection;
    char connectionName[128];
    int port = 3883;
    
    sprintf(connectionName, "localhost:%d", port);
    connection = vrpn_get_connection_by_name(connectionName);
    vrpn_Tracker_Remote *tracker;
#ifndef EVALUATION_1
    tracker = new vrpn_Tracker_Remote("testingSlope", connection);
    tracker->register_change_handler(NULL, handle_pos);
#endif
    
    // Arduino port
    COMToolkit::connect("\\\\.\\COM15");
    
    
    //first experiment: send a grid of values to the servo and read back the values
#ifdef EXPERIMENT_1
    bool shuffle = true;
    int N = 39;
    char fileName[] = "Mars_Shuffled5_18_18.csv";
    const int minX = 1250, maxX = 1750;
    const int minY = 1250, maxY = 1750;
    //const int minX = 1397, maxX = 1635;
    //const int minY = 1346, maxY = 1506;
    
    //generate the grid with the values to send to the arduino (sx,sy)
    std::vector <Point2D> points;
    std::vector <int> permutations;
    int index = 0;
    for (int ix = 0; ix <= N; ++ix) {
        for (int iy = 0; iy <= N; ++iy) {
            const int sx = (int)interp(minX, maxX, ix / (float)N);
            const int sy = (int)interp(minY, maxY, iy / (float)N);
            points.push_back(Point2D(sx, sy));
            permutations.push_back(index);
            index++;
        }
    }
    
    
    if (shuffle) {
        for (int i = permutations.size() - 1; i >= 0; --i) {
            int target = intRandom(0, i + 1);
            const int tmp = permutations[i];
            permutations[i] = permutations[target];
            permutations[target] = tmp;
        }
    }
    
    FILE *f = fopen(fileName, "w");
#endif
    
    //second experiment: test the interpolators
#ifdef EXPERIMENT_2
    
    const int N = 400;
    
    MultiVariableInterp2D *interpolator;
    char calibFile[] = "earth_calib_18.csv";
    char outputFile[] = "earth_delunay_18.csv";
    
    FILE *f = fopen(outputFile, "w");
    //interpolator = new MVILinear(calibFile);
    //interpolator = new MVIInverseWeight(calibFile, 4);
    interpolator = new MVIDelunayLinear(calibFile);
    
    const float minrX = -18, maxrX = 18;
    const float minrY = -18, maxrY = 18;
#endif
    
    //third experiment desired and real rx,ry on real time
#ifdef EXPERIMENT_3
    char calibFileJupiter[] = "jupiter_calib_18.csv";
    char calibFileMars[] = "mars_calib_18.csv";
    char calibFileEarth[] = "earth_calib_18.csv";
    char calibFileSaturn[] = "saturn_calib_18.csv";
    mInterpJupiter = new MVIDelunayLinear(calibFileJupiter);
    mInterpMars = new MVIDelunayLinear(calibFileMars);
    mInterpEarth = new MVIDelunayLinear(calibFileEarth);
    mInterpSaturn = new MVIDelunayLinear(calibFileSaturn);
    char fileName[] = "tracking_hand.csv";
    
    coordinateX = 0;
    coordinateZ = 0;
    
    fileExp3 = fopen(fileName, "w");
    //init time
    
    
    while (0) {//!kbhit()) {
        tracker->mainloop();
        connection->mainloop();
        sleep(5);
        tracker->mainloop();
        connection->mainloop();
        sleep(5);
        findCurve(3);
    }
    
#endif
    
    //first evaluation
#ifdef EVALUATION_1
    //init interpolators
    char calibFileJupiter[] = "jupiter_calib_18.csv";
    char calibFileMars[] = "MARS.csv";
    char calibFileEarth[] = "earth_calib_18.csv";
    char calibFileSaturn[] = "saturn_calib_18.csv";
    mInterpJupiter = new MVIDelunayLinear(calibFileJupiter);
    mInterpMars = new MVIDelunayLinear(calibFileMars);
    mInterpEarth = new MVIDelunayLinear(calibFileEarth);
    mInterpSaturn = new MVIDelunayLinear(calibFileSaturn);
    
    //read pairs file
    char pairsFile[] = "experiments.csv";
    readPairsFile(pairsFile, 1);
    id = intRandom(0, 999999999); //random id
    printf("---- Evaluation 1 with %d trials ID %d\n", amountOfTrials, id);
    
    //ask for initial information
    
    printf("Enter the name of the participant: ");
    scanf("%s", eName);
    printf("Enter condition (1=onlyCapture 2=virtualNoAsk 3=real 4=virtual): ");
    scanf("%d", &condition);
    printf("Starting trial (1): ");
    do {
        scanf("%d", &trial);
    }
    while (trial <1 || trial> amountOfTrials);
    firstPair = true;
    
    
    if (condition == conditionVirtualNoAskFS || condition == conditionVirtual) {
        readFingerOffsets(); //read the offsets of the fingers if it were needed
        
        //reading tracker on the device
        tracker = new vrpn_Tracker_Remote("testingSlope", connection);
        tracker->register_change_handler(NULL, handle_pos);
    }
    else {
        tracker = new vrpn_Tracker_Remote("handTracking", connection);
        tracker->register_change_handler(NULL, handle_pos);
    }
    
    //open the tracking file
    openPositionTrackingFile();
    
    //opening the trials file if it were needed (in append mode just in case)
    if (condition == conditionReal || condition == conditionVirtual) {
        char tmpStr[256];
        sprintf(tmpStr, "%s_%d_%d_trials.txt", eName, id, condition);
        trialsFile = fopen(tmpStr, "w+");
    }
    
    if (condition == conditionRealOnlyCapture) {
        printf("Only capturing data\n");
    }
    
    bool stop = false;
    bool countDown = false;
    float curvature = 3;
    long remainingMillisA, remainingMillisB;
    
    while (!stop) {
        if (condition == conditionRealOnlyCapture) {
            stop = kbhit();
            tracker->mainloop();
            connection->mainloop();
            Sleep(5);
        }
        else if (condition == conditionVirtualNoAskFS || condition == conditionReal || condition == conditionVirtual) {
            if (countDown) {
                if (condition == conditionVirtualNoAskFS || condition == conditionVirtual) {
                    findCurve(curvature);
                }
                long rm;
                bool keyPressed;
                countDown = tickCountDown(rm, keyPressed);
                
                if (!countDown) {
                    if (!keyPressed) {
                        printf("\n\nSTOP!!!! STOP!!!!STOP!!!!STOP!!!!STOP!!!!STOP!!!!STOP!!!!\n\n");
                    }
                    
                    if (firstPair) {
                        remainingMillisA = rm;
                    }
                    else { remainingMillisB = rm; }
                    
                    if ((!firstPair) && (condition == conditionReal || condition == conditionVirtual)) {
                        char ch;
                        do {
                            printf("\n\nAsk first or second (1 or 2): ");
                            ch = getKeyWithoutEnter();
                        }
                        while (ch != '1' &&  ch != '2');
                        int firstOrSecond = ch - '0';
                        float curvA = cNumberToCurv[pairA[trial - 1]];
                        float curvB = cNumberToCurv[pairB[trial - 1]];
                        //trial firstOrSecond curvA curvB remainingTimeA remainingTimeB timestamp
                        fprintf(trialsFile, "%d,%d,%f,%f,%ld,%ld,%ld\n", trial, firstOrSecond,
                                curvA, curvB, remainingMillisA, remainingMillisB, getMillisTime());
                        fflush(trialsFile);
                    }
                    
                    //increase pair, trial, end of eval?
                    if (firstPair) {
                        firstPair = false;
                    }
                    else {
                        trial++;
                        firstPair = true;
                        if (trial > amountOfTrials) {
                            stop = true;
                            break;
                        }
                    }
                }
            }
            else {
                int curvNumber = firstPair ? pairA[trial - 1] : pairB[trial - 1];
                curvature = cNumberToCurv[curvNumber];
                const float curvA = cNumberToCurv[pairA[trial - 1]];
                const float curvB = cNumberToCurv[pairB[trial - 1]];
                int nextTrial = trial < amountOfTrials ? trial : 0;
                const float nextCurvA = cNumberToCurv[pairA[nextTrial]];
                const float nextCurvB = cNumberToCurv[pairB[nextTrial]];
                
                //read the command
                char command;
                do {
                    if (firstPair) {
                        printf("\n\n[[%.1f]]    %.1f       %.1f   %.1f  Trial %d/%d %s",  curvA, curvB, nextCurvA, nextCurvB, trial, amountOfTrials, firstPair ? "first" : "second");
                    }
                    else {
                        printf("\n\n  %.1f    [[%.1f]]     %.1f   %.1f  Trial %d/%d %s",  curvA, curvB, nextCurvA, nextCurvB, trial, amountOfTrials, firstPair ? "first" : "second");
                    }
                    
                    command = getKeyWithoutEnter();
                }
                while (command != 'c' && command != 'b');
                
                if (command == 'b') { // go back to the trial
                    printf("\n");
                    char aOrB = 'a';
                    do {
                        printf("trial and (a/b) ");
                        scanf("%d %c", &trial, &aOrB);
                    }
                    while (trial <1 || trial> amountOfTrials || (aOrB != 'a' && aOrB != 'b'));
                    firstPair = aOrB == 'a';
                }
                else {
                    printf("\n");
                    if (condition == conditionReal || condition == conditionVirtual) {
                        openPositionTrackingFile();
                    }
                    
                    startCountDown();
                    countDown = true;
                }
            }
            
            tracker->mainloop();
            connection->mainloop();
            Sleep(5);
        }
    }
    
#endif
    
    
#ifdef EXPERIMENT_1
    Sleep(1200);
    for (int i = 0; i < points.size(); ++i) {
        const int permIndex = permutations[i];
        const Point2D& pointToSend = points[permIndex];
#ifdef EXPERIMENT_KEYBOARD
        int sx, sy;
        printf("Please input sx sy values: ");
        scanf("%d %d", &sx, &sy);
        sendBytesTest(sx, sy);
#else
        send4ServosPackedIn9Bytes(pointToSend.x, pointToSend.y, pointToSend.x, pointToSend.y, pointToSend.x, pointToSend.y, pointToSend.x, pointToSend.y);
#endif
        Sleep(600);
        tracker->mainloop();
        connection->mainloop();
        Sleep(400);
        tracker->mainloop();
        connection->mainloop();
        const float rx = mPitch;
        const float ry = mRoll;
        fprintf(f, "%d,%d,%d,%f,%f\n", permIndex, pointToSend.x, pointToSend.y, rx, ry);
        
#ifdef EXPERIMENT_KEYBOARD
        printf("Index %d, servoX %d, servoY %d, Pitch %.4f,Roll %.4f\n", permIndex, sx, sy, rx, ry);
#else
        printf("Index %d, servoX %d, servoY %d, Pitch %f,Roll %f\n", permIndex, pointToSend.x, pointToSend.y, rx, ry);
#endif
    }
#endif
    
#ifdef EXPERIMENT_2
    Sleep(1200);
    for (int i = 0; i < N; ++i) {
        const float targetRX = rangeRandom(minrX, maxrX);
        const float targetRY = rangeRandom(minrY, maxrY);
#ifdef EXPERIMENT_KEYBOARD
        printf("Please enter rx ry values: ");
        scanf("%f %f", &targetRX, &targetRY);
#endif
        int sx, sy;
        interpolator->query(targetRX, targetRY, sx, sy);
        sendBytesTest(sx, sy);
        Sleep(600);
        tracker->mainloop();
        connection->mainloop();
        Sleep(400);
        tracker->mainloop();
        connection->mainloop();
        const float realRx = mPitch;
        const float realRy = mRoll;
        fprintf(f, "%d,%d,%f,%f,%f,%f\n", sx, sy, targetRX, targetRY, realRx, realRy);
        const float errorRX = abs(targetRX - realRx);
        const float errorRY = abs(targetRY - realRy);
        printf("%d %d,%d,%f,%f,%f,%f  errRX %.4f   errRY %.4f \n", i, sx, sy, targetRX, targetRY, realRx, realRy, errorRX, errorRY);
    }
#endif
    
#ifdef EXPERIMENT_1
    fclose(f);
#endif
#ifdef EXPERIMENT_2
    fclose(f);
    delete interpolator;
#endif
    
#ifdef EXPERIMENT_3
    fclose(fileExp3);
    delete mInterpMars;
    delete mInterpEarth;
    delete mInterpJupiter;
    delete mInterpSaturn;
#endif
    
#ifdef EVALUATION_1
    closePositionTrackingFile();
    if (trialsFile != NULL) {
        fclose(trialsFile);
    }
    delete mInterpMars;
    delete mInterpEarth;
    delete mInterpJupiter;
    delete mInterpSaturn;
#endif
    
    return 0;
}