#ifdef GL_ES
  precision highp float;
#endif
  
attribute vec4 vertexPosition;
attribute vec4 vertexNormal;

varying vec4 normal;
varying vec4 wPos;
varying vec4 gradientColor;

uniform mat4 modelViewProjectionMatrix;
uniform mat4 modelMatrix;

#define PI 3.1415926535897932384626433832795

uniform int colouring;
uniform float t;
uniform float heightGain;
uniform float recommendedH;
uniform float minColor;
uniform float maxColor;

//TEMPLATE MYFUNC
/*
float myFunc(vec3 p){
    return sin(p.x + t * PI * 2.0);
}
*/

vec3 hsv2rgb(vec3 c){
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

vec3 getNormalAt(vec3 px0, vec3 px1, vec3 py0, vec3 py1){
    
    float x = (myFunc(px0) - myFunc(px1)) * heightGain;
    float y = (myFunc(py0) - myFunc(py1)) * heightGain;

    vec3 vx = normalize ( vec3( distance(px0, px1) , 0 , x) );
    vec3 vy = normalize ( vec3(0.0, distance(py0, py1), y) );
    return cross(vx,vy);
}

void main()
{
    wPos = modelMatrix * vertexPosition;
    vec3 w = vec3(wPos);

    float value =  myFunc(w);
    float divL = recommendedH;

    vec3 px0 = vec3( modelMatrix * (vertexPosition + vec4(-divL, 0.0, 0.0, 0.0)) );
    vec3 px1 = vec3( modelMatrix * (vertexPosition + vec4(divL,  0.0, 0.0, 0.0)) );
    vec3 py0 = vec3( modelMatrix * (vertexPosition + vec4(0.0, -divL, 0.0, 0.0)) );
    vec3 py1 = vec3( modelMatrix * (vertexPosition + vec4(0.0, divL, 0.0, 0.0)) );

    vec3 valueNormal = getNormalAt(px0, px1, py0, py1);

    vec4 modVertexPosition = vertexPosition;
    modVertexPosition.z = value * heightGain;

    gl_Position = modelViewProjectionMatrix * modVertexPosition;
    normal = modelMatrix * vec4( valueNormal , 0.0);
    
    vec3 colNoAlpha;
    float col = (value-minColor)/(maxColor-minColor);
    if(colouring == 1){ //fire&ice
        if (col > 0.0){
            colNoAlpha = vec3(0.5) + cos( vec3(col*PI+PI) - vec3(1.0,0.0,-1.0) ); 
        }else{
            colNoAlpha =  vec3(0.5) + cos( vec3(-col*PI+PI) - vec3(-1.0,0.0,1.0) ); 
        }
    }else if(colouring == 2){ //hue
        colNoAlpha = hsv2rgb(vec3(col,1.0,1.0));
    }else if(colouring == 3){ //brown to blue
        colNoAlpha = vec3( .5+.5*cos(6.2831*col+0.0), .5+.5*cos(6.2831*col+0.4), .5+.5*cos(6.2831*col+0.7) );
    }else if(colouring == 4){ //test1
        colNoAlpha = vec3(0.5+0.89*cos(6.2831855*col+0.0), 0.5+0.48000002*cos(25.132742*col+2.0734513), 0.5+0.38*cos(50.265484*col+4.1469026));
    }else{
        colNoAlpha = vec3(1.0,0.0,0.0);
    }

    gradientColor = vec4(colNoAlpha, 1.0);    
}