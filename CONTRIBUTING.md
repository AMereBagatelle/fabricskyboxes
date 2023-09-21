# Contributing Guidelines

## Code Style

When contributing source code to the project, please adhere to our code style guidelines. These guidelines are based on the [Google code style guidelines for Java](https://google.github.io/styleguide/javaguide.html), with a few minor adjustments as described below:

- Use 4 spaces for indentation, not tabs. Avoid lines that exceed 120 characters in length.
- Avoid deeply nested conditional logic and prefer breaking out into separate functions when possible.
    - If you find yourself using more than three levels of indentation, consider refactoring your code.
    - For rarely taken branches, keep them concise or break out into new methods, allowing better code optimization.
- Use `this` to qualify member and field access to avoid ambiguity in certain contexts.

We also provide [EditorConfig](https://editorconfig.org/) files, which most Java IDEs will automatically detect and use.

## Pull Requests

When submitting a pull request, please include a brief description of the changes you made and link to any relevant open issues that your pull request addresses. Additionally, ensure that your code is well-documented, especially in cases where it might not be immediately obvious, and that it follows our code style guidelines.

We value your contributions and appreciate your efforts in helping us maintain a high-quality codebase. Thank you for contributing to our project!