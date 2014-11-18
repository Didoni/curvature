#ifdef GL_ES
	precision mediump float;
#endif

uniform vec4 colorMod;

void main()
{ 
    gl_FragColor = colorMod;			    
}