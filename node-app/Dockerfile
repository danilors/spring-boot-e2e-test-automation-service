# Use official Node.js image
FROM node:20-alpine

WORKDIR /app

COPY package.json package-lock.json* ./
RUN npm install

COPY . .

# Run tests and generate report
RUN npm run test

CMD ["ls", "-l", "/app/test-report.html"]