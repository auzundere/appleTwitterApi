# Public API  Test Suite

This Test Suite is Based on Java 8 Lenguage, uses TestNg Testing tool. It has Extent TestNG Reports

## Installation

Clone the repository from [git](https://github.com/auzundere/appleTwitterApi).

```bash
git clone https://github.com/auzundere/appleTwitterApi.git
```

## Usage
1. Go to the project folder
2. w/o test group specified:
```mvn
 mvn clean test (It will run smoke test)
```
3. With specific group names:
```
a. smoke
 mvn clean test -Dgroups=smoke
``` 
## Features
###### a. Flexible
###### b. Using Java 8, TestNG, RestAssured, Extent Reports
###### c. It has text and html logs
###### d. Visualized detailed Extent Reports based on the test results.
###### e. Ready for adding new test cases
###### f. It uses custom annotation that it stores testCaseId and testName
 
## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.