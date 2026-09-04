#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;

in vec2 currentTexCoord;
in vec2 nextTexCoord;
in float frameBlend;

out vec4 fragColor;

void main() {
    vec4 currentFrame = texture(Sampler0, currentTexCoord);
    vec4 nextFrame = texture(Sampler0, nextTexCoord);
    vec4 color = mix(currentFrame, nextFrame, frameBlend) * ColorModulator;
    if (color.a == 0.0) {
        discard;
    }
    fragColor = color;
}
