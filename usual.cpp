#include <iostream>
#include <sys\utime.h>
#include <conio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>
#include <vector>
#include <fstream>
#include <Windows.h>
#include <stdlib.h>
#include <opencv\cv.h>
#include <opencv\highgui.h>
#include "COMToolkit.h"
#include <string.h>
#include "vrpn_Connection.h"
#include "quat\quat.h"
#include "MultiVariableInterp2D.h"
// Missing this file?  Get the latest VRPN distro at
#include "vrpn_Tracker.h"    //    ftp://ftp.cs.unc.edu/pub/packages/GRIP/vrpn
//F#include <glm/glm.hpp>
//#include "conio.h"           // for kbhit()
#define DEGREES_PER_RADIAN (180 / acos(-1.0))
#define PI 3.14159265
using namespace std;
using namespace cv;

//#define EXPERIMENT_1 1
//#define EXPERIMENT_2 1
#define EXPERIMENT_3 1

//#define EXPERIMENT_KEYBOARD 1

//#ifdef EXPERIMENT_3
static MultiVariableInterp2D* mInterpMars;
static MultiVariableInterp2D* mInterpEarth;
static MultiVariableInterp2D* mInterpJupiter;
static MultiVariableInterp2D* mInterpSaturn;
//#endif

bool stopped=true;
HANDLE hSerialIN = INVALID_HANDLE_VALUE; 
const char* wndname = "Object Detection";
clock_t begin_time;
int curvatureNum;
int x;
int trial;
int y;
int atstart = 0;
long startmilis=0;
float coordinateX, coordinateZ;


class Instruction{
public:
	float getPhi() {Instruction instr = *this; return instr.phi;};
	float getTheta() {Instruction instr = *this; return instr.theta;};
	void setPhi(float input) { 
		phi = input; } ; 
	void setTheta(float input) { 
		theta = input; } ; 

private:
	float phi;
	float theta;
}; 

class Trial{
public:
	int getReal() {Trial instr = *this; return instr.real;};
	int getVirt() {Trial instr = *this; return instr.virt;};
	void setVirt(int input) { 
		virt = input; } ; 
	void setReal(int input) { 
		real = input; } ; 

private:
	int real;
	int virt;
}; 

Instruction angleArray[91][91][13];
Trial sequenceArray[40];



//== Callback prototype ==--

// Arduino
HANDLE hDevice;

void VRPN_CALLBACK handle_pos (void *, const vrpn_TRACKERCB t);

long getMillisTime(){
	timeval time;
	gettimeofday(&time, NULL);
	return (time.tv_sec * 1000) + (time.tv_usec / 1000);
}





//== Main entry point ==--

void sendBytesTest(short theta, short fi) {
	//cout<<"mapping "<<theta<<" mapping Z "<<fi<<endl;
	//cout<<"size "<<sizeof(mapping)<<endl;
	//cout<<"second ";
	
	COMToolkit::sendByte((theta >>8) & 255);
	COMToolkit::sendByte(theta & 255);
	COMToolkit::sendByte((fi >>8) & 255);
	COMToolkit::sendByte(fi & 255);
}


void handshake () {
	short key = 255;
	COMToolkit::sendByte(key & 255);

}




static float rx;
static float ry;

void findAngles(float coordX, float coordZ, float& phi, float& theta, float radious) {
	const float rxy = sqrtf(radious*radious - coordX*coordX - coordZ*coordZ);

	phi = -atanf( -coordX / rxy ) * 180.f / PI;
	theta = atanf( -coordZ / rxy ) * 180.f / PI;

	int offsetTheta = 0;
	int offsetPhi = 0;
	theta = theta + offsetTheta;
	phi = phi + offsetPhi;

	if (theta > 20) { theta = 20;}
	else if (theta < -20) { theta = -20;}
	if (phi > 20) { phi = 20;}
	else if (phi < -20) { phi = -20;}
}

void findCurve() {
	float thetaAngle, phiAngle;
	const float curv = 2;
	const float radious = 1.0f / curv;

	float xTemp; 
	float zTemp;
	float thetaPlanets[4];//Sat, Mars, Earth, Jup
	float phiPlanets[4];
	float zoffsets[4] = {0.025, 0.0, -0.025, -0.05};
	float xoffsets[4] ={ -0.01, 0.0, -0.01, -0.03};

	coordinateX = coordinateX - 0.1;
	for (int i=0; i<4; i++) {
		xTemp = coordinateX + xoffsets[i];
		zTemp = coordinateZ + zoffsets[i];
		findAngles(xTemp, zTemp, phiPlanets[i], thetaPlanets[i], radious);
	}

	//findAngles(coordinateX, coordinateZ, phiAngle, thetaAngle, radious);
	rx = thetaPlanets[1];
	ry = phiPlanets[1];

	int sxJ,syJ, sxE,syE, sxM,syM, sxS,syS;
	
	mInterpJupiter->query(phiPlanets[3],thetaPlanets[3], sxJ,syJ);
	mInterpEarth->query(phiPlanets[2],thetaPlanets[2], sxE,syE);
	mInterpMars->query(phiPlanets[1],thetaPlanets[1], sxM,syM);
	mInterpSaturn->query(phiPlanets[0],thetaPlanets[0], sxS,syS);
	//cout<<"x "<<coordinateX<<" z "<<coordinateZ<<" Theta "<<(thetaAngle-offsetTheta)<<" phi "<<(phiAngle-offsetPhi)<<" Sx"<<sxJ<<" Sy "<<syJ<<endl;
	cout<<"tt "<<phiPlanets[1]<<" "<<thetaPlanets[1]<<"Jup "<<sxJ<<" "<<syJ<<" Earth "<<sxE<<" "<<syE<<" Mars "<<sxM<<" "<<syM<<" Sat "<<sxS<<" "<<syS<<endl;

	handshake();
	sendBytesTest((short)sxJ,(short)syJ);
	sendBytesTest((short)sxE,(short)syE);
	sendBytesTest((short)sxM,(short)syM);
	sendBytesTest((short)sxS,(short)syS);
	
}


void readFromMyFile(int curvature) {

	stringstream nameA, nameB;
	nameA<<"theta"<<curvature;
	nameB<<"phi"<<curvature;
	std::ifstream thetafile (nameA.str());
	std::ifstream phifile (nameB.str());

	string phi, theta;
	for( int i=0; i<91; i++) {
		getline(thetafile, theta);
		getline(phifile, phi);
		istringstream thetastream(theta);
		istringstream phistream(phi);
		for(int j=0; j< 91; j++) {
			string subtheta,subphi;
			thetastream >> subtheta;
			phistream >> subphi;
			angleArray[i][j][curvature].setTheta(atof(subtheta.c_str()));
			angleArray[i][j][curvature].setPhi(atof(subphi.c_str()));
		}
	}
}

void writeToFile(int curvature) {
	
	//write to file
	stringstream nameA,nameB;
	nameA<<"theta"<<curvature;
	nameB<<"phi"<<curvature;
	ofstream thetaFile, phiFile;
    thetaFile.open (nameA.str());
    phiFile.open (nameB.str());
	stringstream thetaVal,phiVal;

	for( int i=0; i<91; i++) {
		for(int j=0; j< 91; j++) {
			thetaVal<<angleArray[i][j][curvature].getTheta()<<" ";
			phiVal<<angleArray[i][j][curvature].getPhi()<<" ";
		}
		thetaVal<<"\n";
		phiVal<<"\n";
	}
	thetaFile<<thetaVal.str();
    thetaFile.close();
	phiFile<<phiVal.str();
    phiFile.close();
	//write to file
}

void mapCurve(float radius, int curvature) {

	float r = radius;
	if(r == 0) {
		for( int i=0; i<91; i++) {
			for(int j=0; j< 91; j++) {
				angleArray[i][j][curvature].setTheta((float) 0.0);
				angleArray[i][j][curvature].setPhi((float) 0.0);
			}
		}
	}
	else {
		for( int i=0; i<91; i++) {
		  for(int j=0; j< 91; j++) {
		
			float y = sqrt(r*r - i*i -j*j);

			float theta = asin(j/r)*180/PI;
			float tempCos = cos(theta*PI/180);
			float temp = tempCos*y/r;
			float phi = acos(y/(r*tempCos))*180/PI;
			float tempSin = asin(-i/(r*tempCos))*180/PI;
	
			float mappingTheta = (float) theta;
			float mappingPhi = (float) tempSin;
			//cout<<"mapX "<<x<<" map Y "<<y<<" map Z "<<z<<" temp "<< temp<<"tempcos "<<tempCos<<" Theta is "<<theta<<" phi is "<<phi<<" phi2check "<<tempSin<<" map "<<mapping<<" mapB "<<mappingB<<endl;
			cout<<"x "<<i<<" y "<<y<<" z "<<j<<" Theta "<<theta<<" phi "<<tempSin<<" map "<<mappingTheta<<" mapB "<<mappingPhi<<endl;
			if(mappingTheta <-20) mappingTheta=-20;
			if(mappingTheta > 20) mappingTheta =20;
			if (mappingPhi <-20) mappingPhi =-20;
			if(mappingPhi > 20) mappingPhi = 20;


			angleArray[i][j][curvature].setTheta((float)mappingTheta);
			angleArray[i][j][curvature].setPhi((float)mappingPhi);
	
		  }
		}
	}
	writeToFile(curvature);

}


static float getRoll(float x, float y, float z, float w, bool reprojectAxis)
	{
		if (reprojectAxis)
		{
			// roll = atan2(localx.y, localx.x)
			// pick parts of xAxis() implementation that we need
//			float fTx  = 2.0*x;
			float fTy  = 2.0f*y;
			float fTz  = 2.0f*z;
			float fTwz = fTz*w;
			float fTxy = fTy*x;
			float fTyy = fTy*y;
			float fTzz = fTz*z;

			// Vector3(1.0-(fTyy+fTzz), fTxy+fTwz, fTxz-fTwy);

			return float(atan2(fTxy+fTwz, 1.0f-(fTyy+fTzz)));

		}
		else
		{
			return float(atan2(2*(x*y + w*z), w*w + x*x - y*y - z*z));
		}
	}
    //-----------------------------------------------------------------------
static float getPitch(float x, float y, float z, float w,  bool reprojectAxis) 
	{
		if (reprojectAxis)
		{
			// pitch = atan2(localy.z, localy.y)
			// pick parts of yAxis() implementation that we need
			float fTx  = 2.0f*x;
//			float fTy  = 2.0f*y;
			float fTz  = 2.0f*z;
			float fTwx = fTx*w;
			float fTxx = fTx*x;
			float fTyz = fTz*y;
			float fTzz = fTz*z;

			// Vector3(fTxy-fTwz, 1.0-(fTxx+fTzz), fTyz+fTwx);
			return float(atan2(fTyz+fTwx, 1.0f-(fTxx+fTzz)));
		}
		else
		{
			// internal version
			return float(atan2(2*(y*z + w*x), w*w - x*x - y*y + z*z));
		}
	}
    //-----------------------------------------------------------------------
static float getYaw(float x, float y, float z, float w,  bool reprojectAxis) 
	{
		if (reprojectAxis)
		{
			// yaw = atan2(localz.x, localz.z)
			// pick parts of zAxis() implementation that we need
			float fTx  = 2.0f*x;
			float fTy  = 2.0f*y;
			float fTz  = 2.0f*z;
			float fTwy = fTy*w;
			float fTxx = fTx*x;
			float fTxz = fTz*x;
			float fTyy = fTy*y;

			// Vector3(fTxz+fTwy, fTyz-fTwx, 1.0-(fTxx+fTyy));

			return float(atan2(fTxz+fTwy, 1.0f-(fTxx+fTyy)));

		}
		else
		{
			// internal version
			return float(asin(-2*(x*z - w*y)));
		}
	}

static float mPitch = 0;
static float mRoll = 0;
static float mYaw = 0;
static FILE* fileExp3;

void VRPN_CALLBACK handle_pos (void *, const vrpn_TRACKERCB t)
{

	//printf("Tracker Position:(%.4f,%.4f,%.4f) Orientation:(%.2f,%.2f,%.2f,%.2f)\n",t.pos[0], t.pos[1], t.pos[2],t.quat[0], t.quat[1], t.quat[2], t.quat[3]);
	
	const float pitch = getPitch(t.quat[0], t.quat[1], t.quat[2], t.quat[3], false) * 180.0f / PI;
	const float roll = getRoll(t.quat[0], t.quat[1], t.quat[2], t.quat[3], false) * 180.0f / PI;
	const float yaw = getYaw(t.quat[0], t.quat[1], t.quat[2], t.quat[3], false) * 180.0f / PI;

	mPitch = pitch;
	mRoll = roll;
	mYaw = yaw;

#ifdef EXPERIMENT_3
	fprintf(fileExp3, "%ld,%f,%f,%f,%f\n", getMillisTime(), rx, ry, mPitch, mRoll);
	//printf("%ld,%f,%f,%f,%f\n", getMillisTime(), rx, ry, mPitch, mRoll);
	coordinateZ = t.pos[0]-0.0084;
	coordinateX = t.pos[2]+ 0.0107;
#endif

}

void initializeSequence() {
	srand ( time(NULL) );
	int sizeOfDict = 20;
	int occurenceMax= 5;
	const int curvatures[8] = {1,2,4,5,3,3,3,3};
	int curvatureNum = 8;
	int referencePosition = 4;
	int occurances[8];
	int occurancesVirt[8];

	for(int i=0;i<curvatureNum;i++){
		occurances[i] =0;
		occurancesVirt[i] =0;
	}

	for (int i=0; i<sizeOfDict; i++) {
		int RealIndex = rand() % curvatureNum;
		if(occurances[RealIndex] < occurenceMax) {
			cout<<"yes"<<endl;
			sequenceArray[i].setReal(curvatures[RealIndex]);
			occurances[RealIndex] ++;
		}
		else { i = i-1; cout<<"no"<<endl;}
	}

	for (int i=0; i<sizeOfDict; i++) {
		if (sequenceArray[i].getReal() == curvatures[referencePosition]) {
			int VirtIndex = rand() % referencePosition;
			while(occurancesVirt[VirtIndex] >=occurenceMax) {
				VirtIndex = rand() % referencePosition;
			}
			occurancesVirt[VirtIndex]++;
			sequenceArray[i].setVirt(curvatures[VirtIndex]);
		}
		else {
			sequenceArray[i].setVirt(curvatures[referencePosition]);
		}
	}

	for(int i=0; i< sizeOfDict; i++) {
		cout<<"ARRAY "<<i<<" real "<<sequenceArray[i].getReal()<<" virt "<<sequenceArray[i].getVirt()<<endl;
	}
	for(int i=0;i<curvatureNum;i++) {
		cout<<"Count "<<curvatures[i]<<" - "<<occurances[i]<<" virtual "<<occurancesVirt[i]<<endl;
	}
}

void initializeLookupTable() {
	readFromMyFile(6);
	readFromMyFile(5);
	readFromMyFile(4);
	readFromMyFile(3);
	readFromMyFile(2);
	readFromMyFile(1);
	readFromMyFile(0);

	//Experiment 2
	readFromMyFile(7);
	readFromMyFile(8);
	readFromMyFile(9);
	readFromMyFile(10);
	readFromMyFile(11);
	readFromMyFile(12);
	
	//mapCurve(714.286,6); // 1.4 
	//mapCurve(1666.667,5); // 0.6
	//mapCurve(2500,4); //0.4
	//mapCurve(0,3); //0
	//mapCurve(-2500,2); //-0.4
	//mapCurve(-1666.667,1); //- 0.6
	//mapCurve(-714.286,0); // -1.4

	//mapCurve(555.556,7); //1.8
	//mapCurve(500,8); //2
	//mapCurve(454.545,9); //2.2
	//mapCurve(384.615,10); //2.6
	//mapCurve(-294.117,12); // 3.4

	/*findCurve(70, 70);
	findCurve(-70, 70);
	findCurve(70, -70);
	findCurve(-70, -70);
	findCurve(0, 90);
	findCurve(0, -90);
	findCurve(90, 0);
	findCurve(-90, 0);
	findCurve(0, 0);
	int test;
	cout<<"enter "<<endl;
	cin>>test;*/
}

void fitCurve(float coordX, float coordY, float rotX, float rotY) {
	//calibrate input
	float phi = rotX;
	float theta = rotY;


	//calibrate input
	cout<<"fitting Curve"<<endl;
}

float interp(float a, float b, float p){
	return a + (b-a)*p;
}


void initRandomSeed() {
	srand(time(NULL));
}

int intRandom(int min, int max){
	return min + rand() % (max - min);
}

float unitRandom (){
	return (float)rand()/(float)RAND_MAX;
}

float rangeRandom (float fLow, float fHigh){
	return (fHigh-fLow)*unitRandom() + fLow;
}

 class Point2D{
 public:
	 int x,y;
	 inline Point2D(int px, int py) : x(px), y(py) { }
};



int main(int argc, char* argv[])
{
    vrpn_Connection *connection;
    char connectionName[128];
    int  port = 3883;

    sprintf(connectionName,"localhost:%d", port);
    connection = vrpn_get_connection_by_name(connectionName);
    vrpn_Tracker_Remote *tracker = new vrpn_Tracker_Remote("testingSlope", connection);
  	tracker->register_change_handler(NULL, handle_pos);

	//Library init
	initializeLookupTable();
	//initializeSequence();
	//Library



	// Arduino port
	COMToolkit::connect(L"\\\\.\\COM15");


	//first experiment
#ifdef EXPERIMENT_1
	bool shuffle = true;
	int N = 39;
	char fileName[] = "Jupiter_Shuffled1_18_18.csv";
	const int minX = 1250, maxX = 1750;
	const int minY = 1250, maxY = 1750;
	//const int minX = 1397, maxX = 1635;
	//const int minY = 1346, maxY = 1506;

	//generate the grid with the values to send to the arduino (sx,sy)
	std::vector<Point2D> points;
	std::vector<int> permutations;
	int index = 0;
	for (int ix = 0; ix <= N; ++ix) {
		for (int iy = 0; iy <= N; ++iy) {
			const int sx = (int) interp(minX, maxX, ix / (float) N);
			const int sy = (int) interp(minY, maxY, iy / (float) N);
			points.push_back( Point2D(sx, sy) );
			permutations.push_back(index);
			index++;
		}
	}
	

	if (shuffle){
		initRandomSeed();
		for(int i = permutations.size() - 1; i >= 0; --i){
			int target = intRandom(0,i+1);
			const int tmp = permutations[i];
			permutations[i] = permutations[target];
			permutations[target] = tmp;
		}
	}

	FILE* f = fopen(fileName, "w");
#endif
#ifdef EXPERIMENT_2
	//second experiment
	const int N = 400;
	
	MultiVariableInterp2D* interpolator;
	char calibFile[] = "earth_calib_18.csv";
	char outputFile[] = "earth_delunay_18.csv";
	
	FILE* f = fopen(outputFile, "w");
	//interpolator = new MVILinear(calibFile);
	//interpolator = new MVIInverseWeight(calibFile, 4);
	interpolator = new MVIDelunayLinear(calibFile);

	const float minrX = -18, maxrX = 18;
	const float minrY = -18, maxrY = 18;
#endif

#ifdef EXPERIMENT_3
	char calibFileJupiter[] = "jupiter_calib_18.csv";
	char calibFileMars[] = "mars_calib_18.csv";
	char calibFileEarth[] = "earth_calib_18.csv";
	char calibFileSaturn[] = "saturn_calib_18.csv";
	mInterpJupiter = new MVIDelunayLinear(calibFileJupiter);
	mInterpMars = new MVIDelunayLinear(calibFileMars);
	mInterpEarth = new MVIDelunayLinear(calibFileEarth);
	mInterpSaturn = new MVIDelunayLinear(calibFileSaturn);
	char fileName[] = "sytem_HighMed_18.csv";
	curvatureNum = 6;

	coordinateX = 0; 
	coordinateZ = 0;

	fileExp3 = fopen(fileName, "w");
	//init time
	
	begin_time = clock();
	trial = 0;

	//third experiement

	 while(!kbhit())
    {
		tracker->mainloop();
		connection->mainloop();
        Sleep(30);
		findCurve();
    }


#endif

	//handshake();
	begin_time = clock();
	stopped=false;

	tracker->mainloop(); 
	connection->mainloop();

#ifdef EXPERIMENT_1
	Sleep(1200);
	for(int i = 0; i < points.size() ; ++i )
    {
		
		const int permIndex = permutations[i];
		const Point2D& pointToSend = points[ permIndex ];
#ifdef EXPERIMENT_KEYBOARD
		int sx,sy;
		printf("input sx sy you ugly twat: ");
		scanf("%d %d", &sx, &sy);
		sendBytesTest(sx, sy);
#else
		sendBytesTest(pointToSend.x, pointToSend.y);
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
	for(int i = 0; i < N ; ++i )
    {
		const float targetRX = rangeRandom(minrX, maxrX);
		const float targetRY = rangeRandom(minrY, maxrY);
#ifdef EXPERIMENT_KEYBOARD
		printf("Enter the rx ry: ");
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
		printf("%d %d,%d,%f,%f,%f,%f  errRX %.4f   errRY %.4f \n", i, sx, sy, targetRX, targetRY, realRx, realRy,errorRX,errorRY);

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
#endif
	CloseHandle(hDevice);
	return 0;
}


