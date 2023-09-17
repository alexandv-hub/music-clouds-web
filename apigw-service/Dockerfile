# dockerfile for 'apigw-service'

# Use the official Java image
FROM openjdk:17-slim

# Set the working directory
WORKDIR /app

# Define JAR_FILE as an argument. This provides flexibility in specifying the JAR file during build-time.
ARG JAR_FILE=target/*.jar

# Copy the JAR file into the container
COPY ${JAR_FILE} app.jar

# Distributed tracing
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar .
ENV JAVA_TOOL_OPTIONS "-javaagent:./opentelemetry-javaagent.jar"

# Expose port for the application
EXPOSE 8000

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
