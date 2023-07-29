
# Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
# Click nbfs://nbhost/SystemFileSystem/Templates/Other/Dockerfile to edit this template

FROM alpine:latest

# Install openjdk 11 and ffmpeg
RUN apk add --no-cache openjdk11 ffmpeg

# Copy the files to the container
RUN mkdir -p /app/audiobot/
COPY audiobot/ /app/audiobot/

# Run the jar file
CMD ["java", "-jar", "/app/audiobot/audiobot.jar"]
