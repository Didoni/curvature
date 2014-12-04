/*
 * MultiVariableInterp2D.cpp
 *
 *  Created on: 10 Nov 2014
 *      Author: Asier
 */

#include "MultiVariableInterp2D.h"

#include <stdio.h>
#include <stdlib.h>
#include <math.h>


static inline float max(float a, float b) {
    return a > b ? a : b;
}

static inline float min(float a, float b) {
    return a < b ? a : b;
}

static inline int max(int a, int b) {
    return a > b ? a : b;
}

static inline int min(int a, int b) {
    return a < b ? a : b;
}

static inline float mix(float a, float b, float p) {
    return a + (b - a) * p;
}

float roundf(float x) {
    return x >= 0.0f ? floorf(x + 0.5f) : ceilf(x - 0.5f);
}

MultiVariableInterp2D::MultiVariableInterp2D(const char *fileName) {
    loadPoints(fileName);
    calcBoundaries();
}

MultiVariableInterp2D::~MultiVariableInterp2D() {
}

void MultiVariableInterp2D::loadPoints(const char *fileName) {
    FILE *f = fopen(fileName, "rb");
    if (f == NULL) {
        printf("ERROR opening file %s", fileName);
        return;
    }
    
    int dump;
    MultiVariablePoint p;
    
    while (fscanf(f, "%d,%d,%d,%f,%f\n", &dump, &p.sx, &p.sy, &p.rx, &p.ry) > 0) {
        points.push_back(p);
    }
    
    fclose(f);
}

void MultiVariableInterp2D::calcBoundaries() {
    const int n = points.size();
    if (n == 0) {
        minSx = maxSx = minSy = maxSy = 0;
        minRx = maxRx = minRy = maxRy = 0.0f;
    }
    else {
        minSx = maxSx = points[0].sx;
        minSy = maxSy = points[0].sy;
        minRx = maxRx = points[0].rx;
        minRy = maxRy = points[0].ry;
        for (int i = 0; i < n; ++i) {
            const MultiVariablePoint& p = points[i];
            if (p.sx < minSx) {
                minSx = p.sx;
                minRx = p.rx;
            }
            else if (p.sx > maxSx) {
                maxSx = p.sx;
                maxRx = p.rx;
            }
            if (p.sy < minSy) {
                minSy = p.sy;
                minRy = p.ry;
            }
            else if (p.sy > maxSy) {
                maxSy = p.sy;
                maxRy = p.ry;
            }
        }
        
        distRx = maxRx - minRx;
        distRy = maxRy - minRy;
    }
}

/* Linear
 *
 *
 */
MVILinear::MVILinear(const char *fileName) : MultiVariableInterp2D(fileName) {
}

float MVILinear::linearInterp(float x1, float x2, float y1, float y2, float x) {
    const float proportion = (x - x1) / (x2 - x1);
    return mix(y1, y2, proportion);
}

void MVILinear::query(float rx, float ry, int& sx, int& sy) {
    sx = (int)roundf(linearInterp(minRx, maxRx, minSx, maxSx, rx));
    sy = (int)roundf(linearInterp(minRy, maxRy, minSy, maxSy, ry));
}

/* Inverse Weight Distance
 *
 *
 */
MVIInverseWeight::MVIInverseWeight(const char *fileName, float pParam) : MultiVariableInterp2D(fileName), param(pParam) {
}

float MVIInverseWeight::distanceP(float x, float y, float param) {
    return powf(sqrtf(x * x + y * y), param);
}

void MVIInverseWeight::query(float rx, float ry, int& sx, int& sy) {
    double totalInvDist = 0;
    double tx = 0, ty = 0;
    
    const int n = points.size();
    for (int i = 0; i < n; ++i) {
        const MultiVariablePoint& p = points[i];
        const double distance = distanceP(p.rx - rx, p.ry - ry, param);
        if (distance <= 0.0000001) {
            sx = p.sx;
            sy = p.sy;
            return;
        }
        const double invDist = 1.0 / distance;
        totalInvDist += invDist;
        tx += p.sx * invDist;
        ty += p.sy * invDist;
    }
    sx = (int)(tx / totalInvDist);
    sy = (int)(ty / totalInvDist);
}

/* Delunay Linear
 *
 */

float DelunayTriangle::getMinX() const {
    return min(min(a->rx, b->rx), c->rx);
}

float DelunayTriangle::getMaxX() const {
    return max(max(a->rx, b->rx), c->rx);
}

float DelunayTriangle::getMinY() const {
    return min(min(a->ry, b->ry), c->ry);
}

float DelunayTriangle::getMaxY() const {
    return max(max(a->ry, b->ry), c->ry);
}

static inline float sign(float p1x, float p1y, float p2x, float p2y, float p3x, float p3y) {
    return (p1x - p3x) * (p2y - p3y) - (p2x - p3x) * (p1y - p3y);
}

bool DelunayTriangle::pointInside(float rx, float ry) const {
    bool b1 = sign(rx, ry, a->rx, a->ry, b->rx, b->ry) < 0.0f;
    bool b2 = sign(rx, ry, b->rx, b->ry, c->rx, c->ry) < 0.0f;
    bool b3 = sign(rx, ry, c->rx, c->ry, a->rx, a->ry) < 0.0f;
    return ((b1 == b2) && (b2 == b3));
}

void DelunayTriangle::interp(float rx, float ry, int& sx, int& sy) const {
    const float det = (b->ry - c->ry) * (a->rx - c->rx) + (c->rx - b->rx) * (a->ry - c->ry);
    const float lamb1 = ((b->ry - c->ry) * (rx - c->rx) + (c->rx - b->rx) * (ry - c->ry)) / det;
    const float lamb2 = ((c->ry - a->ry) * (rx - c->rx) + (a->rx - c->rx) * (ry - c->ry)) / det;
    const float lamb3 = 1.0 - lamb1 - lamb2;
    
    
    //sx = (int) roundf(a->sx);
    //sy = (int) roundf(a->sy);
    sx = (int)roundf((float)a->sx * lamb1 + (float)b->sx * lamb2 + (float)c->sx * lamb3);
    sy = (int)roundf((float)a->sy * lamb1 + (float)b->sy * lamb2 + (float)c->sy * lamb3);
    
    /*
     float vx = rx - a->rx; float vy = ry - a->ry;
     float vbx = b->rx - a->rx; float vby = b->ry - a->ry;
     float vcx = c->rx - a->rx; float vcy = c->ry - a->ry;
     
     float db = (vbx * vx + vby * vy) / lengthV(vbx, vby);
     float dc = (vcx * vx + vcy * vy) / lengthV(vcx, vcy);
     
     float sbx = b->sx - a->sx;
     float sby = b->sy - a->sy;
     float scx = c->sx - a->sx;
     float scy = c->sy - a->sy;
     float slb = lengthV(sbx, sby);
     float slc = lengthV(scx, scy);
     sx = (int) roundf(a->sx + db * sbx / slb + dc * scx / slc);
     sy = (int) roundf(a->sy + db * sby / slb + dc * scy / slc);
     */
}

//TODO implement spatial hashing to speed up the search of the containing triangle
MVIDelunayLinear::MVIDelunayLinear(const char *fileName, int pHashSize) : MVIInverseWeight(fileName, 2.0), hashSize(pHashSize) {
    //create triangles
    const int P = points.size();
    const int N = (int)sqrtf(P);  //TOHACK give some warning if it is not a square grid NxN
    
    const int N_1 = N - 1;
    for (int iy = 0; iy < N_1; ++iy) {
        for (int ix = 0; ix < N_1; ++ix) {
            const int i = iy * N + ix; //current point
            const int j = i + N; //point in the next row
            triangles.push_back(DelunayTriangle(&points[i], &points[i + 1], &points[j]));
            triangles.push_back(DelunayTriangle(&points[i + 1], &points[j + 1], &points[j]));
        }
    }
}

void MVIDelunayLinear::query(float rx, float ry, int& sx, int& sy) {
    //search for the containing triangle
    for (int i = triangles.size() - 1; i >= 0; --i) {
        const DelunayTriangle& t = triangles[i];
        //if the point is inside, interpolate
        if (t.pointInside(rx, ry)) {
            t.interp(rx, ry, sx, sy);
            return;
        }
    }
    
    //fallback plan: use iwd
    MVIInverseWeight::query(rx, ry, sx, sy);
}
