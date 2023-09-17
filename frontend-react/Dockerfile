# dockerfile for 'frontend-react'

# Use the official Node.js image
FROM node:20.5.1

# Set the working directory
WORKDIR /app

# Copy only the dependency files
COPY package*.json ./

# Install the dependencies
RUN npm install

# Copy the remaining files
COPY . .

# Expose port for the application
EXPOSE 3000

# Start the application
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]
