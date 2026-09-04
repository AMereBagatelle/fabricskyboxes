#version 150

in vec3 Position;
in vec2 UV0;
in ivec2 UV1;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec2 currentTexCoord;
out vec2 nextTexCoord;
out float frameBlend;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    currentTexCoord = UV0;
    nextTexCoord = vec2(UV1) / 32767.0;
    frameBlend = Color.r;
}
