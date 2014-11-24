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
uniform float heightGain;
uniform float minColor;
uniform float maxColor;

vec3 hsv2rgb(vec3 c){
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main()
{
    wPos = modelMatrix * vertexPosition;
    gl_Position = modelViewProjectionMatrix * vertexPosition;
    normal = modelMatrix * vec4(vertexNormal.xyz, 0.0);
    
    float value =  vertexPosition.z / heightGain;

    vec3 colNoAlpha;
    float col = (value-minColor)/(maxColor-minColor);
    if(colouring == 0){ //fire&ice
        if (col > 0.0){
            colNoAlpha = vec3(0.5) + cos( vec3(col*PI+PI) - vec3(1.0,0.0,-1.0) ); 
        }else{
            colNoAlpha =  vec3(0.5) + cos( vec3(col*PI+PI) - vec3(-1.0,0.0,1.0) ); 
        }
    }else if(colouring == 1){ //hue
        colNoAlpha = hsv2rgb(vec3(col,1.0,1.0));
    }else if(colouring == 2){ //brown to blue
        colNoAlpha = vec3( .5+.5*cos(6.2831*col+0.0), .5+.5*cos(6.2831*col+0.4), .5+.5*cos(6.2831*col+0.7) );
    }else{ //test1
        colNoAlpha = vec3(0.5+0.89*cos(6.2831855*col+0.0), 0.5+0.48000002*cos(25.132742*col+2.0734513), 0.5+0.38*cos(50.265484*col+4.1469026));
    }

    gradientColor = vec4(colNoAlpha, 1.0);    
}