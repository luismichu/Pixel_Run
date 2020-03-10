#ifdef GL_ES
#define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;

void main() {
        vec3 color = texture2D(u_texture, v_texCoords).rgb;
        vec3 dark = vec3(color.r - 0.1, color.g - 0.1, color.b - 0.1);

        gl_FragColor = vec4(dark, 1.0);
}