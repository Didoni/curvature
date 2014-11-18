#ifdef GL_ES
  precision highp float;
#endif
  
attribute vec4 vertexPosition;

varying vec4 normal;
varying vec4 wPos;
varying vec4 color;

uniform mat4 modelViewProjectionMatrix;
uniform mat4 modelMatrix;

#define N_TRANS _N_TRANS_

#define PI 3.1415926535897932384626433832795

uniform float alphaValue;
uniform int colouring;
uniform float heightGain;
uniform float heightDiv;
uniform float minNegColor;
uniform float maxNegColor;
uniform float minPosColor;
uniform float maxPosColor;
uniform vec3 tPos[N_TRANS];
uniform vec3 tNorm[N_TRANS];
uniform vec4 tSpecs[N_TRANS];


float getAmpAt(vec3 point){
    
    vec2 pre = vec2(0.0);
    vec2 tmp;

    for(int i = 0; i < N_TRANS; ++i){
        vec3 diffVec = point - tPos[i];
        float dist = length(diffVec);
      
        //tSpecs[i].x -> k
        //tSpecs[i].y -> amp
        //tSpecs[i].z -> phase
        //tSpecs[i].w -> w
        
        float kd = tSpecs[i].x * dist;
        float cosKD = cos(kd);
        float sinKD = sin(kd);
       
        float amp = tSpecs[i].y;
        float cosP = cos(tSpecs[i].z);
        float sinP = sin(tSpecs[i].z);
        float cosKDdist = cosKD / dist;
        float sinKDdist = sinKD / dist;
        
        tmp = amp * vec2(cosKDdist, sinKDdist);
        pre.x += tmp.x*cosP - tmp.y*sinP;
        pre.y += tmp.x*sinP + tmp.y*cosP;
    }

    return length(pre);
}

vec3 getNormalAt(vec3 px0, vec3 px1, vec3 py0, vec3 py1){
    
    float x = (getAmpAt(px0) - getAmpAt(px1)) * heightGain;
    float y = (getAmpAt(py0) - getAmpAt(py1)) * heightGain;

    vec3 vx = normalize ( vec3( distance(px0, px1) , 0 , x) );
    vec3 vy = normalize ( vec3(0.0, distance(py0, py1), y) );
    return cross(vx,vy);
}

void main()
{
    wPos = modelMatrix * vertexPosition;
    vec3 w = vec3(wPos);

    float amplitude =  getAmpAt(w);
    float divL = 1.0 / heightDiv;

    vec3 px0 = vec3( modelMatrix * (vertexPosition + vec4(-divL, 0.0, 0.0, 0.0)) );
    vec3 px1 = vec3( modelMatrix * (vertexPosition + vec4(divL,  0.0, 0.0, 0.0)) );
    vec3 py0 = vec3( modelMatrix * (vertexPosition + vec4(0.0, -divL, 0.0, 0.0)) );
    vec3 py1 = vec3( modelMatrix * (vertexPosition + vec4(0.0, divL, 0.0, 0.0)) );

    vec3 amplitudeNormal = getNormalAt(px0, px1, py0, py1);

    vec4 modVertexPosition = vertexPosition;
    modVertexPosition.z = amplitude * heightGain;

    gl_Position = modelViewProjectionMatrix * modVertexPosition;
    normal = modelMatrix * vec4( amplitudeNormal , 0.0);
    
    vec3 colNoAlpha;
    if (amplitude > 0.0){ 
        float col = clamp( (amplitude-minPosColor)/(maxPosColor-minPosColor) , 0.0, 1.0);
        if(colouring == 1){ //red
            float value = col;
            colNoAlpha = vec3(value, 0.0, 0.0); 
        }else if(colouring == 2){ //16 periods red
            float value = cos(2*PI*16.0*col)*0.5+0.5;
            colNoAlpha = vec3(value, 0.0, 0.0); 
        }else if(colouring == 3){ //cosine fire
            colNoAlpha = vec3(0.5) + cos( vec3(col*PI+PI) - vec3(-1.0,0.0,1.0) ); 
        }else{ //linear fire gradient
            colNoAlpha = vec3(col*3.0, col*3.0 - 1.0, col*3.0 - 2.0); 
        }
    }else{ 
        float col = clamp( (amplitude-minNegColor)/(maxNegColor-minNegColor) , 0.0, 1.0);
        col = 1.0 - col;
        if(colouring == 1){ //blue
            float value = col;
            colNoAlpha = vec3(0.0, 0.0, value);
        }else if(colouring == 2){ //16 periods blue
            float value = cos(2*PI*16.0*col)*0.5+0.5;
            colNoAlpha = vec3(0.0, 0.0, value);
        }else if(colouring == 3){ //cosine ice
            colNoAlpha = vec3(0.5) + cos( vec3(col*PI+PI) - vec3(1.0,0.0,-1.0) ); 
        }else{ //linear ice gradient
            colNoAlpha = vec3(col*3.0 - 2.0, col*3.0 - 1.0, col*3.0); 
        }
    }

    color = vec4(colNoAlpha, alphaValue);    
}