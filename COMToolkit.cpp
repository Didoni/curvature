#include <windows.h>
#include "COMToolkit.h"
#include <iostream>
#include "pthread.h"
#include <list>
#include <vector>

using namespace std;

//Static variables to handle the connection. Only one copy of these variables exists in main memory, so only one connection will be managed by this code.
static HANDLE hSerial;
static DCB dcbSerialParams;
static COMMTIMEOUTS timeouts;
enum COM_States{INIT=0, READY, ERRORS};
int status=COM_States::INIT;

enum THREAD_States{T_INIT=0, T_RUNNING, T_STOPPED};
int THREAD_status=THREAD_States::T_INIT;
pthread_t xmosListeningThread;
pthread_mutex_t lockThread;
pthread_mutex_t notifyListeners;
std::vector<pthread_mutex_t>  syncSignalListeners;//Threads awaiting Sync signal

//Auxiliary methods to support the deffinition of the methods COMToolkit declares.
void createFile(wchar_t* fileName);
void setCommMask();
void defineControlSettings();
void setCommState();
void setTimeouts();
void setNoTimeouts();
void* xmosListeningThreadBody(void*);
void unlockAllThreadsAwaitingSyncSignal(){
	pthread_mutex_lock(&notifyListeners);
	for(int i=0;i<syncSignalListeners.size();i++)
		pthread_mutex_unlock(&(syncSignalListeners[i]));
	syncSignalListeners.clear();
	pthread_mutex_unlock(&notifyListeners);
	Sleep(1);
}

void COMToolkit::connect(wchar_t* portName){
	if(status!=INIT)
		return; //Already connected!

	//Configure the conneciton
	std::cout << "Connecting to" << portName << std::endl;
	createFile(portName);
	setCommMask();
	defineControlSettings();
	setCommState();
	setTimeouts();
	//Create the thread (will be initially locked)
	pthread_mutex_init(&lockThread,NULL);
	pthread_mutex_lock(&lockThread);
	pthread_mutex_init(&notifyListeners,NULL);
	pthread_create(&xmosListeningThread, NULL, xmosListeningThreadBody, NULL);

	if(anyErrors() == true){
		closeConnection();
		return;
	}
	else{
		status=READY;
		return;
	}
}
static bool listening=false;
void COMToolkit::startListeningSyncSignal(){
	if(listening)
		return;
	listening=true;
	setNoTimeouts();
	pthread_mutex_unlock(&lockThread);
}
void COMToolkit::stopListeningSyncSignal(){
	if(!listening)
		return;
	pthread_mutex_lock(&lockThread);
	setTimeouts();
	listening=false;
}

std::list<char> messagesRead;

unsigned  char COMToolkit::readByte(){
	if(status!=READY)
		return 0; //Either the connection has not bee initialized or there have been errors...
	Sleep(3);
	return 'r';
	/*
	if(!messagesRead.empty()){
		char result=*(messagesRead.begin());
		messagesRead.pop_front();
		return result;
	}
	unsigned char buffer[10];
	DWORD numOfBytesRead = 0;
	DWORD dwCommEvent;
	SetFilePointer(hSerial,0,0,FILE_BEGIN);
 	if (WaitCommEvent(hSerial, &dwCommEvent, NULL)) {
		if (ReadFile(hSerial, &buffer, 10, &numOfBytesRead, NULL)){
			// A byte has been read.
			//cout << "Received: " << buffer << endl ;
			if(numOfBytesRead>1)
				printf("Crap!!");
			if(buffer[0]=='x'){
				unlockAllThreadsAwaitingSyncSignal();
				printf("Read 'X'");
				return readByte();
			}
			else{ 
				printf("Read 'R'");
				return buffer[0];
			}
		}
		else{
			// An error occurred in the ReadFile call.
			cout << "An error occurred in the ReadFile call" << endl ;
			//break;
		}
	}
	else{
		// Error in WaitCommEvent.
		cout << "Error in WaitCommEvent" << endl ;
		//break;
	}
	return 0;*/
}


void COMToolkit::sendByte(unsigned char byte){
	if(status!=READY) {
		printf("COMToolkit::sendByte Error: status not ready!");
		return; //Either the connection has not bee initialized or there have been errors...
	}

	unsigned char buffer = 0;
	DWORD numOfBytesWritten = 0;
	buffer = (unsigned char)byte ;
	if(!WriteFile(hSerial, &buffer, sizeof(buffer), &numOfBytesWritten, NULL)){
		cout << "error writing" << endl ;
	}
	else{
		//printf("Sent %d\n", byte);
		//cout << "Sent: " << buffer << endl ;
	}
	SetFilePointer(hSerial,0,0,FILE_BEGIN);
}

// send an int as a string over the serial port (daniel.sp)
void COMToolkit::sendInt(int i)
{
	char buffer[64];				// int can have 10 digits, +/- sign, '\0'
	itoa(i, buffer, 10);			// convert to char/ASCII
	int index = 0;
	while (buffer[index] != '\0') {
		//printf("Sending %c to Arduino\n", buffer[index]);
		COMToolkit::sendByte(buffer[index]);
		index++;
	}
	COMToolkit::sendByte('\r');		// the line ending
	COMToolkit::sendByte('\n');
}

bool COMToolkit::anyErrors(){
	if(status == ERRORS){
		return true;
	}
	else{
		cout << "Connection successful." << endl ;
		return false;
	}
}

void COMToolkit::closeConnection(){

	if(status==INIT)
		return; //The connection has not been stablished yet!
	//Stop sync thread in case it is running
	THREAD_status=THREAD_States::T_STOPPED;
	startListeningSyncSignal();
	void* out;
	pthread_join(xmosListeningThread,&out);
	
	//Close connection
	if(CloseHandle(hSerial)){
		status = INIT;
		cout << "COM port closed" << endl;
	}
	else{
		cout << "Error closing COM port" << endl;
	}
}



void COMToolkit::awaitSyncSignal(){
	//Create a mutex
	pthread_mutex_t m;
	pthread_mutex_init(&m,NULL);
	pthread_mutex_lock(&m);
	//Insert it in listeneres queue
	pthread_mutex_lock(&notifyListeners);
	syncSignalListeners.push_back(m);
	pthread_mutex_unlock(&notifyListeners);
	//Wait on the mutex (the XMos thread will unlock all listeners qhen it receives the signal)
	pthread_mutex_lock(&m);
	//Release the resource.
	pthread_mutex_destroy(&m);

}

void* xmosListeningThreadBody(void*){
	THREAD_status=THREAD_States::T_RUNNING;
	
	return NULL;
	while(THREAD_status==THREAD_States::T_RUNNING){
		pthread_mutex_lock(&lockThread);
		unsigned char buffer = 0;
		DWORD numOfBytesRead = 0;
		DWORD dwCommEvent;
		SetFilePointer(hSerial,0,0,FILE_BEGIN); //I removed WaitComEvent, I think I do not need it...
 		if (ReadFile(hSerial, &buffer, 1, &numOfBytesRead, NULL)){
				if(numOfBytesRead>0)
					if( buffer=='x'){
						unlockAllThreadsAwaitingSyncSignal();
						/*int milis= (((int)IClock::instance().getTimeMiliseconds()*100)%199935)/100;
						static int min=2000, max=0;
						static int numX=0;
						numX++;
						if(numX>10){
							if(milis>max)max=milis;
							if(milis<min)min=milis;
						}
						printf("Read X at %d (err %d)", milis, max-min);*/
					}
					else{ 
						printf("Enqueued 'R'");
						messagesRead.push_back(buffer);
					}
				else{
					Sleep(1);
				}
				
		}
		else{
			// An error occurred in the ReadFile call.
			cout << "An error occurred in the ReadFile call" << endl ;
			//break;
		}
		pthread_mutex_unlock(&lockThread);
		Sleep(1);
	}	
	return NULL;

}

//UTILITY METHODS

void createFile(wchar_t* fileName){

	hSerial = CreateFile(fileName,
						GENERIC_READ | GENERIC_WRITE,
						0, 
						0,
						OPEN_EXISTING,
						FILE_ATTRIBUTE_NORMAL,
						0);

    if(hSerial==INVALID_HANDLE_VALUE)
    {
        if(GetLastError()==ERROR_FILE_NOT_FOUND)
        {
			//serial port does not exist. Inform user.
			cout << "The port does not exsist" << endl ;
			status = ERRORS;
        }

		//some other error occurred. Inform user.
		cout << "Invalid handle value" << endl ;
		status = ERRORS;
    }
}

void setCommMask(){
	if (!SetCommMask(hSerial, EV_RXCHAR)){
		// Error setting communications event mask.
		cout << "Error setting communications event mask" << endl ;
		status = ERRORS;
	}
}

void defineControlSettings(){
	FillMemory(&dcbSerialParams, sizeof(dcbSerialParams), 0);
	dcbSerialParams.DCBlength = sizeof(dcbSerialParams);

	if (!GetCommState(hSerial, &dcbSerialParams)) {
		//error getting state
		cout << "Error getting state" << endl ;
		status = ERRORS;
	}
	//Configuring the serial port: http://msdn.microsoft.com/en-us/library/aa450503.aspx
	/*dcbSerialParams.BaudRate=115200 ;
	dcbSerialParams.ByteSize=8;
	dcbSerialParams.fBinary=TRUE;				//It has to be binary
	dcbSerialParams.Parity=NOPARITY;			//No parity checking
	dcbSerialParams.fOutxCtsFlow=FALSE;			//No flow control
	dcbSerialParams.fOutxDsrFlow=FALSE;
	//dcbSerialParams.fDtrControl=DTR_CONTROL_DISABLE;
	dcbSerialParams.fTXContinueOnXoff = TRUE;     // XOFF continues Tx 
	dcbSerialParams.fOutX = FALSE;                // No XON/XOFF out flow control 
	dcbSerialParams.fInX = FALSE;                 // No XON/XOFF in flow control 
	dcbSerialParams.fErrorChar = FALSE;           // Disable error replacement 
	dcbSerialParams.fNull = FALSE;                // Disable null stripping 
	dcbSerialParams.fDsrSensitivity=FALSE;		//Receive ANY incomming bytes
	dcbSerialParams.StopBits=ONESTOPBIT;
	//dcbSerialParams.fRtsControl=RTS_CONTROL_DISABLE;*/
	
	//dcbSerialParams.BaudRate=CBR_9600;
	dcbSerialParams.BaudRate=CBR_115200;	
	dcbSerialParams.ByteSize=8;
	dcbSerialParams.StopBits=ONESTOPBIT;
	dcbSerialParams.Parity=NOPARITY;
}

void setCommState(){
	if(!SetCommState(hSerial, &dcbSerialParams)){
		//error setting serial port state
		cout << "Error setting serial port state" << endl ;
		status = ERRORS;
	}
}

void setTimeouts(){
	// From MSDN: Specifies the maximum acceptable time, in milliseconds, to elapse between the arrival of two characters on the communication line.
    timeouts.ReadIntervalTimeout=1; //50 //MAXWORD
	// From MSDN: Specifies the constant, in milliseconds, used to calculate the total timeout period for read operations.
    timeouts.ReadTotalTimeoutConstant=1; //50 //0
	// From MSDN: Specifies the multiplier, in milliseconds, used to calculate the total timeout period for read operations.
    timeouts.ReadTotalTimeoutMultiplier=1; //10 //0
	// From MSDN: Specifies the constant, in milliseconds, used to calculate the total timeout period for write operations.
    timeouts.WriteTotalTimeoutConstant=1; //50 //0
	// From MSDN: Specifies the multiplier, in milliseconds, used to calculate the total timeout period for write operations.
    timeouts.WriteTotalTimeoutMultiplier=1; //10 //0
	if(!SetCommTimeouts(hSerial, &timeouts)){
        //error occureed. Inform user
		cout << "Timeout error occurred." << endl ;
		status = ERRORS;
    }
}

void setNoTimeouts(){
	// From MSDN: Specifies the maximum acceptable time, in milliseconds, to elapse between the arrival of two characters on the communication line.
    timeouts.ReadIntervalTimeout=MAXDWORD; //50 //MAXWORD
	// From MSDN: Specifies the constant, in milliseconds, used to calculate the total timeout period for read operations.
    timeouts.ReadTotalTimeoutConstant=0; //50 //0
	// From MSDN: Specifies the multiplier, in milliseconds, used to calculate the total timeout period for read operations.
    timeouts.ReadTotalTimeoutMultiplier=0; //10 //0
	// From MSDN: Specifies the constant, in milliseconds, used to calculate the total timeout period for write operations.
    timeouts.WriteTotalTimeoutConstant=1; //50 //0
	// From MSDN: Specifies the multiplier, in milliseconds, used to calculate the total timeout period for write operations.
    timeouts.WriteTotalTimeoutMultiplier=1; //10 //0
	if(!SetCommTimeouts(hSerial, &timeouts)){
        //error occureed. Inform user
		cout << "Timeout error occurred." << endl ;
		status = ERRORS;
    }
}
