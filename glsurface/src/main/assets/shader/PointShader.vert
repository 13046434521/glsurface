attribute vec4 a_Position;
attribute vec4 a_Color;
uniform float a_PointSize;
varying vec4 v_Color;
void main() {
    gl_Position = vec4(a_Position.x,a_Position.y,0,1);
    gl_PointSize = a_PointSize;
    v_Color = a_Color;
}
