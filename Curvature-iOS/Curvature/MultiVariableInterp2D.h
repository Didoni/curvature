/*
 * MultiVariableInterp2D.h
 *
 *  Created on: 10 Nov 2014
 *      Author: Asier
 */

#ifndef MULTIVARIABLEINTERP2D_H_
#define MULTIVARIABLEINTERP2D_H_

#include <vector>

class MultiVariableInterp2D;
class MVILinear;
class MVIInverseWeight;
class MVIDelunayLinear;
class DelunayTriangle;

class MultiVariablePoint{
    friend class MultiVariableInterp2D;
    friend class MVILinear;
    friend class MVIInverseWeight;
    friend class MVIDelunayLinear;
    friend class DelunayTriangle;
public:
    inline MultiVariablePoint() : sx(0), sy(0), rx(0), ry(0) {}
    inline MultiVariablePoint(int pSx, int pSy) : sx(pSx), sy(pSy), rx(0), ry(0) {}
    inline MultiVariablePoint(int pSx, int pSy, float pRx, float pRy) : sx(pSx), sy(pSy), rx(pRx), ry(pRy) {}
protected:
    int sx, sy;
    float rx, ry;
};

class MultiVariableInterp2D {
    friend class MVILinear;
    friend class MVIInverseWeight;
    friend class MVIDelunayLinear;
public:
    virtual ~MultiVariableInterp2D();
    virtual void query(float rx, float ry, int& sx, int& sy) = 0;
    
    inline int getMinSx() const { return minSx; }
    inline int getMaxSx() const { return maxSx; }
    inline int getMinSy() const { return minSy; }
    inline int getMaxSy() const { return maxSy; }
    
    inline float getMinRx() const { return minRx; }
    inline float getMaxRx() const { return maxRx; }
    inline float getMinRy() const { return minRy; }
    inline float getMaxRy() const { return maxRy; }
protected:
    MultiVariableInterp2D(const char* fileName);
    void calcBoundaries();
    int minSx, maxSx, minSy, maxSy;
    float minRx, maxRx, minRy, maxRy;
    float distRx, distRy;
    std::vector<MultiVariablePoint> points;
private:
    void loadPoints(const char* fileName);
};

class MVILinear : public MultiVariableInterp2D{
public:
    MVILinear(const char* fileName);
    void query(float rx, float ry, int& sx, int& sy);
    
    static float linearInterp(float x1, float x2, float y1, float y2, float x);
};

class MVIInverseWeight : public MultiVariableInterp2D{
    friend class MVIDelunayLinear;
public:
    MVIInverseWeight(const char* fileName, float pParam = 2);
    virtual void query(float rx, float ry, int& sx, int& sy);
    
    static float distanceP(float x, float y, float param = 2.0);
private:
    const float param;
};

class DelunayTriangle{
    friend class MVIDelunayLinear;
public:
    inline DelunayTriangle(MultiVariablePoint *pa, MultiVariablePoint *pb, MultiVariablePoint *pc) : a(pa), b(pb), c(pc) {}
    
    float getMinX() const;
    float getMaxX() const;
    float getMinY() const;
    float getMaxY() const;
    
    bool pointInside(float rx, float ry) const;
    void interp(float rx, float ry, int& sx, int& sy) const;
protected:
    const MultiVariablePoint *a, *b, *c;
};

class MVIDelunayLinear : public MVIInverseWeight{ //delunay uses inverseWeight as the fallback algorithm
public:
    MVIDelunayLinear(const char* fileName, int pHashSize = 64);
    void query(float rx, float ry, int& sx, int& sy);
private:
    const int hashSize;
    std::vector<DelunayTriangle> triangles;
};

#endif /* MULTIVARIABLEINTERP2D_H_ */