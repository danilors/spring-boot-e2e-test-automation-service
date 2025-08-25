module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'node',
  reporters: [
    'default',
    [ 'jest-html-reporter', {
      pageTitle: 'Test Report',
      outputPath: 'test-report.html',
      includeFailureMsg: true
    }]
  ]
};
