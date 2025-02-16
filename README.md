# Text Editor Implementation

A text editor implementation using [libGDX](https://libgdx.com/), a powerful Java game development framework. This project demonstrates the implementation of a basic text editor with a gap buffer data structure for efficient text manipulation.

## Features

- Basic text editing capabilities
- Cursor navigation using arrow keys
- Text selection support with mouse interaction
- Multi-line text selection and manipulation
- Gap buffer implementation for efficient text insertion and deletion
- Support for special characters and numbers
- Multi-line text editing with line tracking
- Real-time text rendering with proper spacing
- Font customization with FreeType font support

## Implementation Details

The project uses the following key components:

- `TextEditor`: Main editor component handling text rendering, input processing, and UI management
- `GapBuffer`: Custom implementation of a gap buffer data structure for efficient text manipulation
- `Node`: Data structure for storing individual characters and their properties
- `Main`: Application entry point and lifecycle management

The editor is built with a clean architecture that separates concerns:
- Text manipulation logic is handled by the gap buffer
- UI and input handling is managed by the text editor component
- Application lifecycle is managed by the main class

## Controls

- **Arrow Keys**: Navigate the cursor
- **Backspace**: Delete characters
- **Enter**: New line
- **Mouse**: 
  - Click to position cursor
  - Click and drag to select text
- **Regular Keys**: Type text (supports uppercase with Shift/Caps Lock)
- **Special Characters**: Supports period, comma, space, and other common symbols

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3.

## Building and Running

This project uses [Gradle](https://gradle.org/) to manage dependencies. You can run the project using the following commands:

```bash
# Run the application
./gradlew lwjgl3:run

# Build a runnable JAR
./gradlew lwjgl3:jar
```

The built JAR can be found at `lwjgl3/build/libs`.

## Development

### Gradle Tasks

- `build`: Builds sources and archives of every project.
- `clean`: Removes `build` folders.
- `test`: Runs unit tests (if any).
- `lwjgl3:run`: Starts the application.

For project-specific tasks, use the `[project]:` prefix (e.g., `core:clean`).

## Requirements

- Java 8 or higher
- Gradle (included wrapper)
- FreeType font support (included in dependencies)
