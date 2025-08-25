# Node.js TypeScript Vite Project with Jest

This project uses Vite with TypeScript, runs tests with Jest, and produces an HTML test report. Tests are run and reported both locally and in Docker.

## Scripts

- `npm run dev` — Start Vite dev server
- `npm run build` — Build the project
- `npm run test` — Run Jest tests and generate `test-report.html`

## Docker

Build and run tests in Docker:

```sh
# Build the Docker image
docker build -t node-ts-test .

# Run the container (tests will run and report will be generated)
docker run --rm -v $(pwd):/app node-ts-test
```

The test report will be available as `test-report.html` in the project root.
